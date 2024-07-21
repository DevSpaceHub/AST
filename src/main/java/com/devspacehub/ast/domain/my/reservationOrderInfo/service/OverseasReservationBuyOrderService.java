/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : OverseasReservationBuyOrderService
 creation : 2024.6.29
 author : Yoonji Moon
 */

package com.devspacehub.ast.domain.my.reservationOrderInfo.service;

import com.devspacehub.ast.common.config.OpenApiProperties;
import com.devspacehub.ast.common.constant.DecimalScale;
import com.devspacehub.ast.common.constant.ExchangeCode;
import com.devspacehub.ast.common.constant.MarketType;
import com.devspacehub.ast.common.constant.OpenApiType;
import com.devspacehub.ast.common.dto.WebClientCommonResDto;
import com.devspacehub.ast.common.utils.BigDecimalUtil;
import com.devspacehub.ast.common.utils.LogUtils;
import com.devspacehub.ast.domain.marketStatus.dto.StockItemDto;
import com.devspacehub.ast.domain.my.dto.MyServiceRequestDto;
import com.devspacehub.ast.domain.my.reservationOrderInfo.ReservationOrderInfo;
import com.devspacehub.ast.domain.my.reservationOrderInfo.ReservationOrderInfoRepository;
import com.devspacehub.ast.domain.my.reservationOrderInfo.dto.ReservationStockItem;
import com.devspacehub.ast.domain.my.service.MyService;
import com.devspacehub.ast.domain.my.service.MyServiceFactory;
import com.devspacehub.ast.domain.notification.Notificator;
import com.devspacehub.ast.domain.notification.dto.MessageContentDto;
import com.devspacehub.ast.domain.orderTrading.OrderTrading;
import com.devspacehub.ast.domain.orderTrading.OrderTradingRepository;
import com.devspacehub.ast.domain.orderTrading.dto.OverseasStockOrderApiReqDto;
import com.devspacehub.ast.domain.orderTrading.dto.StockOrderApiResDto;
import com.devspacehub.ast.domain.orderTrading.service.TradingService;
import com.devspacehub.ast.util.OpenApiRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import static com.devspacehub.ast.common.constant.OpenApiType.OVERSEAS_STOCK_RESERVATION_BUY_ORDER;
import static com.devspacehub.ast.common.constant.ProfileType.getAccountStatus;

/**
 * TradingService 구현체인, 해외 예약 주문 서비스
 */
@RequiredArgsConstructor
@Service
public class OverseasReservationBuyOrderService extends TradingService {
    private final OpenApiRequest openApiRequest;
    private final ReservationOrderInfoRepository reservationOrderInfoRepository;
    private final OrderTradingRepository orderTradingRepository;
    private final MyServiceFactory myServiceFactory;
    private final Notificator notificator;

    @Value("${openapi.rest.header.transaction-id.overseas.buy-order}")
    private String txIdBuyOrder;
    @Value("${trading.overseas.cash-buy-order-amount-percent}")
    protected BigDecimal cashBuyOrderAmountPercent;
    @Value("${trading.overseas.split-buy-count}")
    protected BigDecimal splitBuyCount;

    /**
     * 매수 주문을 요청한다.
     * @param openApiProperties OpenApi 요청 프로퍼티
     * @param openApiType OpenApi 타입
     * @return 주문 완료한 종목 정보 List
     */
    @Override
    public List<OrderTrading> order(OpenApiProperties openApiProperties, OpenApiType openApiType) {
        List<ReservationStockItem.Overseas> reservations = getValidReservationsForToday();

        if (reservations.isEmpty()) {
            return Collections.emptyList();
        }

        List<OrderTrading> orderedItems = new ArrayList<>();
        for (ReservationStockItem.Overseas reservation : reservations) {
            BigDecimal myDeposit = getMyDeposit(reservation);
            if (!this.hasSufficientDeposit(reservation, myDeposit)) {
                continue;
            }
            StockItemDto stockItem = this.prepareOrder(reservation, myDeposit);

            StockOrderApiResDto apiResponse = (StockOrderApiResDto) this.callOrderApi(openApiProperties, stockItem, openApiType, txIdBuyOrder);

            OrderTrading orderTrading = OrderTrading.from(stockItem, apiResponse, txIdBuyOrder);

            this.updateLatestOrderNumber(apiResponse, reservation.getReservationSeq());
            this.orderApiResultProcess(apiResponse, orderTrading);
            orderedItems.add(orderTrading);
        }

        return orderedItems;
    }

    /**
     * 매수 주문을 위한 주문가, 주문 수량을 세팅한다.
     * @param reservation 예약 주식 정보
     * @param myDeposit 예수금
     * @return 매수 주문 위해 세팅된 정보
     */
    private StockItemDto prepareOrder(ReservationStockItem.Overseas reservation, BigDecimal myDeposit) {
        StockItemDto stockItem = reservation.getStockItem();

        BigDecimal adjustedOrderPrice = BigDecimalUtil.setScale(reservation.getStockItem().getOrderPrice(), DecimalScale.getOrderPriceDecimalScale(reservation.getStockItem().getOrderPrice()));
        stockItem.setOrderPrice(adjustedOrderPrice);
        stockItem.setOrderQuantity(calculateOrderQuantity(myDeposit, adjustedOrderPrice));
        return stockItem;
    }

    /**
     * 나의 예수금 조회를 요청한다.
     * @param reservation 해외 예약 종목 정보 Dto
     * @return 예수금
     */
    private BigDecimal getMyDeposit(ReservationStockItem.Overseas reservation) {
        return myServiceImpl().getBuyOrderPossibleCash(MyServiceRequestDto.Overseas.from(reservation.getStockItem().getItemCode(), reservation.getStockItem().getOrderPrice(), reservation.getExchangeCode()));
    }

    /**
     * 예수금과 주문할 총 가격을 비교하여 주문 가능한지 체크한다.
     * @param reservation 해외 예약 종목 정보 Dto
     * @return 주문 가능한지 여부 boolean 값
     */
    protected boolean hasSufficientDeposit(ReservationStockItem.Overseas reservation, BigDecimal myDeposit) {
        if (BigDecimalUtil.isLessThanOrEqualTo(BigDecimalUtil.multiplyBigDecimalWithNumber(reservation.getStockItem().getOrderPrice(), reservation.getStockItem().getOrderQuantity()), myDeposit)) {
            return true;
        } else {
            LogUtils.insufficientAmountError(OpenApiType.OVERSEAS_STOCK_RESERVATION_BUY_ORDER, reservation.getStockItem().getItemNameKor(), myDeposit);
            return false;
        }
    }

    /**
     * 시장 타입에 따라 MyService 구현체 반환한다.
     * @return MyService 구현체
     */
    private MyService myServiceImpl() {
        return myServiceFactory.resolveService(MarketType.OVERSEAS);
    }

    /**
     * 오늘 기준 유효한 예약 종목 데이터를 조회하여 반환한다.
     * @return 유효한 해외 종목 Dto들
     */
    protected List<ReservationStockItem.Overseas> getValidReservationsForToday() {
        return reservationOrderInfoRepository.findValidAllByExchangeCodes(LocalDate.now(), ExchangeCode.longCodeOverseas());
    }

    /**
     * 주문 API 호출 요청한다.
     * @param openApiProperties OpenApi 요청 프로퍼티
     * @param stockItem 주식 종목 정보
     * @param openApiType OpenApi 타입
     * @param transactionId 트랜잭션 ID
     * @param <T> StockItemDto 상속 타입
     * @return OpenApi 요청의 응답 Dto
     */
    @Override
    public <T extends StockItemDto> WebClientCommonResDto callOrderApi(OpenApiProperties openApiProperties, T stockItem, OpenApiType openApiType, String transactionId) {
        Consumer<HttpHeaders> headers = OverseasStockOrderApiReqDto.setHeaders(openApiProperties.getOauth(), transactionId);
        OverseasStockOrderApiReqDto reqDto = OverseasStockOrderApiReqDto.from(openApiProperties, stockItem);

        return (StockOrderApiResDto) openApiRequest.httpPostRequest(openApiType, headers, reqDto);
    }

    /**
     * 주문 거래 완료한 Entity들을 이력 저장한다.
     * @param orderTradingInfos List 타입의 주문 완료된 OrderTrading Entity
     * @return 테이브에 저장 완료된 List 타입의 OrderTrading Entity
     */
    @Override
    public List<OrderTrading> saveOrderInfos(List<OrderTrading> orderTradingInfos) {
        if (orderTradingInfos.isEmpty()) {
            return Collections.emptyList();
        }
        return orderTradingRepository.saveAll(orderTradingInfos);
    }

    /**
     * 주문 거래 후 결과를 처리한다.
     * @param result       매수/매도 주문 응답 Dto
     * @param orderTrading 종목 정보 Dto
     * @param <T> WebClientCommonResDto 구현체
     */
    @Override
    public <T extends WebClientCommonResDto> void orderApiResultProcess(T result, OrderTrading orderTrading) {
        if (result.isSuccess()) {
            LogUtils.tradingOrderSuccess(OVERSEAS_STOCK_RESERVATION_BUY_ORDER, orderTrading.getItemNameKor());
            notificator.sendMessage(MessageContentDto.OrderResult.fromOne(
                    OVERSEAS_STOCK_RESERVATION_BUY_ORDER, getAccountStatus(), orderTrading));
        } else {
            LogUtils.openApiFailedResponseMessage(OVERSEAS_STOCK_RESERVATION_BUY_ORDER, result.getMessage(), result.getMessageCode());
        }
    }

    /**
     * 예수금의 특정 퍼센트와 주문가를 이용여 알고리즘을 이용해 매수 수량 구한다.
     * 1. 예수금에서 cashBuyOrderAmountPercent 만큼의 비율에 해당하는 금액만 고려한다.
     * 2. 1번 결과를 분할 매수할 수량으로 나눈다.
     * 3. 2번 결과를 주문가로 나누어 최종 주문 수량을 구한다.
     * @param myDeposit 예수금
     * @param orderPrice 주문가
     * @return 소수점 버려진 int 타입의 매수 수량
     */
    public int calculateOrderQuantity(BigDecimal myDeposit, BigDecimal orderPrice) {
        BigDecimal xPercentOfMyCash = myDeposit.multiply(BigDecimalUtil.percentageToDecimal(cashBuyOrderAmountPercent));
        BigDecimal resultDividedBySplitBuyCount = BigDecimalUtil.divide(xPercentOfMyCash, splitBuyCount, DecimalScale.FOUR.getCode());
        return BigDecimalUtil.divide(resultDividedBySplitBuyCount, orderPrice, DecimalScale.ZERO.getCode()).intValue();
    }

    /**
     * 최신 주문번호로 업데이트한다.
     * @param result 주문 OpenApi 응답 Dto
     * @param reservationItemSeq 예약 종목 Seq
     */
    public void updateLatestOrderNumber(StockOrderApiResDto result, Long reservationItemSeq) {
        if (result.isFailed()) {
            return;
        }
        Optional<ReservationOrderInfo> optionalReservationOrderInfo = reservationOrderInfoRepository.findById(reservationItemSeq);
        if (optionalReservationOrderInfo.isEmpty()) {
            LogUtils.notFoundDataError(String.format("예약 매수 seq (%d)에 해당하는 데이터", reservationItemSeq));
            return;
        }

        ReservationOrderInfo reservationOrderInfo = optionalReservationOrderInfo.get();
        reservationOrderInfo.updateOrderNumber(result.getOutput().getOrderNumber());
        reservationOrderInfo.updateMetaData(LocalDateTime.now());
    }
}
