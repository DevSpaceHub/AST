/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : OverseasReservationBuyOrderService
 creation : 2024.6.29
 author : Yoonji Moon
 */

package com.devspacehub.ast.domain.my.reservationOrderInfo.service;

import com.devspacehub.ast.common.config.OpenApiProperties;
import com.devspacehub.ast.common.constant.*;
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
import com.devspacehub.ast.exception.error.BusinessException;
import com.devspacehub.ast.exception.error.InsufficientMoneyException;
import com.devspacehub.ast.exception.error.InvalidValueException;
import com.devspacehub.ast.exception.error.OpenApiFailedResponseException;
import com.devspacehub.ast.util.OpenApiRequest;
import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

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
@Slf4j
@Transactional(readOnly = true)
@Service
public class OverseasReservationBuyOrderService extends TradingService {
    private final ReservationOrderInfoRepository reservationOrderInfoRepository;

    @Value("${openapi.rest.header.transaction-id.overseas.buy-order}")
    private String txIdBuyOrder;
    @Value("${trading.overseas.cash-buy-order-amount-percent}")
    protected BigDecimal cashBuyOrderAmountPercent;
    @Value("${trading.overseas.split-buy-count}")
    protected BigDecimal splitBuyCount;

    public OverseasReservationBuyOrderService(OpenApiRequest openApiRequest, Notificator notificator,
                                              OrderTradingRepository orderTradingRepository, MyServiceFactory myServiceFactory,
                                              ReservationOrderInfoRepository reservationOrderInfoRepository) {
        super(openApiRequest, notificator, orderTradingRepository, myServiceFactory);
        this.reservationOrderInfoRepository = reservationOrderInfoRepository;
    }

    /**
     * 매수 주문을 요청한다.
     * @param openApiProperties OpenApi 요청 프로퍼티
     * @param openApiType OpenApi 타입
     * @return 주문 완료한 종목 정보 List
     */
    @Transactional
    @Override
    public List<OrderTrading> order(OpenApiProperties openApiProperties, OpenApiType openApiType) {
        List<ReservationStockItem> reservations = getValidReservationsForToday();

        if (reservations.isEmpty()) {
            LogUtils.notFoundDataError("금일 예약 매수할 종목 데이터");
            return Collections.emptyList();
        }

        List<OrderTrading> orderedItems = new ArrayList<>();
        OrderTrading orderTrading;
        for (ReservationStockItem reservation : reservations) {
            try {
                this.validateValue(reservation);
                this.sufficientDepositCheck(reservation, getMyDeposit(reservation));

                orderTrading = this.buy(reservation, openApiProperties, openApiType);
            } catch(BusinessException ex) {
                log.warn("code = {}, message = '{}'", ex.getResultCode(), ex.getMessage());
                continue;
            }

            this.updateLatestOrderNumber(orderTrading, reservation.getReservationSeq());
            this.orderApiResultProcess(orderTrading);
            orderedItems.add(orderTrading);
        }

        return orderedItems;
    }

    /**
     * 매수 주문하고 결과 값을 Entity로 변환하여 반환한다.
     * @param reservationItem 요청 파라미터
     * @param openApiProperties OpenApi 요청 프로퍼티
     * @param openApiType  OpenApi 요청 타입
     * @return OrderTrading 타입의 주문 데이터
     * @throws OpenApiFailedResponseException OpenApi 실패 응답인 경우
     */
    private OrderTrading buy(ReservationStockItem reservationItem, OpenApiProperties openApiProperties, OpenApiType openApiType) {
        StockItemDto.Overseas orderRequestStockItem = this.prepareOrderRequestInfo(reservationItem);
        StockOrderApiResDto apiResponse = (StockOrderApiResDto) this.callOrderApi(openApiProperties, orderRequestStockItem, openApiType, txIdBuyOrder);

        if (apiResponse.isFailed()) {
            throw new OpenApiFailedResponseException(openApiType, apiResponse.getMessage());
        }

        return OrderTrading.from(orderRequestStockItem, apiResponse, txIdBuyOrder);
    }

    /**
     * 예약 종목 데이터 validation check
     * @param reservation 예약 종목 데이터
     * @return 유효하면 True
     */
    protected void validateValue(ReservationStockItem reservation) {
        if (StringUtils.isBlank(reservation.getItemCode())) {
            throw new InvalidValueException(ResultCode.DATA_IS_BLANK_ERROR);
        }
        if (BigDecimalUtil.isLessThanOrEqualTo(reservation.getOrderPrice(), BigDecimal.valueOf(0))) {
            throw new InvalidValueException(ResultCode.INVALID_VALUE, String.format("주문가: %s", reservation.getOrderPrice()));
        }
        if (reservation.getOrderQuantity() <= 0) {
            throw new InvalidValueException(ResultCode.INVALID_VALUE, String.format("주문 수량: %s", reservation.getOrderQuantity()));
        }
        if (!reservation.getExchangeCode().isOverseas()) {
            throw new InvalidValueException(ResultCode.INVALID_VALUE, String.format("거래소 코드: %s", reservation.getExchangeCode()));
        }
    }

    /**
     * 매수 주문을 위한 주문가, 주문 수량을 세팅한다.
     * @param reservation 예약 주식 정보
     * @return 매수 주문 위해 세팅된 정보
     */
     protected StockItemDto.Overseas prepareOrderRequestInfo(ReservationStockItem reservation) {
        StockItemDto.Overseas overseasStockItem = reservation.getStockItem().castToOverseas();
        BigDecimal adjustedOrderPrice = this.prepareOrderPrice(overseasStockItem.getOrderPrice());

        return StockItemDto.Overseas.builder()
                .orderDivision(overseasStockItem.getOrderDivision())
                .itemNameKor(reservation.getItemNameKor())
                .exchangeCode(overseasStockItem.getExchangeCode())
                .itemCode(reservation.getItemCode())
                .orderPrice(adjustedOrderPrice)
                .orderQuantity(reservation.getOrderQuantity())
                .build();
    }

    /**
     * 호가에 맞게 주문가 조정한다.
     * @param orderPrice DB에서 조회한 주문가
     * @return 호가 조정된 주문가
     */
    protected BigDecimal prepareOrderPrice(BigDecimal orderPrice) {
        BigDecimal adjustedOrderPrice = BigDecimalUtil.setScale(orderPrice, DecimalScale.getOrderPriceDecimalScale(orderPrice));

        if (BigDecimalUtil.isLessThanOrEqualTo(adjustedOrderPrice, BigDecimal.valueOf(0))) {
            throw new InvalidValueException(ResultCode.INVALID_VALUE, String.format("주문가 : %s", orderPrice));
        }
        return adjustedOrderPrice;
    }

    /**
     * 나의 예수금 조회를 요청한다.
     * @param reservation 해외 예약 종목 정보 Dto
     * @return 예수금
     */
    private BigDecimal getMyDeposit(ReservationStockItem reservation) {
        StockItemDto.Overseas overseasStockItem = reservation.getStockItem().castToOverseas();
        return myServiceImpl().getBuyOrderPossibleCash(MyServiceRequestDto.Overseas.from(overseasStockItem.getItemCode(), overseasStockItem.getOrderPrice(), overseasStockItem.getExchangeCode()));
    }

    /**
     * 예수금과 주문할 총 가격을 비교하여 주문 가능한지 체크한다.
     * @param reservation 해외 예약 종목 정보 Dto
     * @return 주문 가능한지 여부 boolean 값
     */
    protected void sufficientDepositCheck(ReservationStockItem reservation, BigDecimal myDeposit) {
        StockItemDto overseasStockItem = reservation.getStockItem();
        BigDecimal totalAmount = BigDecimalUtil.multiplyBigDecimalWithNumber(overseasStockItem.getOrderPrice(), overseasStockItem.getOrderQuantity());
        if (BigDecimalUtil.isLessThan(myDeposit, totalAmount)) {
            throw new InsufficientMoneyException(String.format("totalAmount: %s, myDeposit: %s", totalAmount, myDeposit));
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
    protected List<ReservationStockItem> getValidReservationsForToday() {
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
    public <T extends StockItemDto> WebClientCommonResDto callOrderApi(OpenApiProperties openApiProperties, @Validated T stockItem, OpenApiType openApiType, String transactionId) {
        Consumer<HttpHeaders> headers = OverseasStockOrderApiReqDto.setHeaders(openApiProperties.getOauth(), transactionId);
        OverseasStockOrderApiReqDto reqDto = OverseasStockOrderApiReqDto.from(openApiProperties, stockItem);

        return openApiRequest.httpPostRequest(openApiType, headers, reqDto);
    }

    /**
     * 주문 거래 완료한 Entity들을 이력 저장한다.
     * @param orderTradingInfos List 타입의 주문 완료된 OrderTrading Entity
     * @return 테이브에 저장 완료된 List 타입의 OrderTrading Entity
     */
    @Transactional
    @Override
    public List<OrderTrading> saveOrderInfos(List<OrderTrading> orderTradingInfos) {
        if (orderTradingInfos.isEmpty()) {
            return Collections.emptyList();
        }
        return orderTradingRepository.saveAll(orderTradingInfos);
    }

    /**
     * 주문 거래 후 결과를 처리한다.
     * @param orderTrading 종목 정보 Dto
     */
    @Override
    public void orderApiResultProcess(OrderTrading orderTrading) {
        LogUtils.tradingOrderSuccess(OVERSEAS_STOCK_RESERVATION_BUY_ORDER, orderTrading.getItemNameKor());
        notificator.sendMessage(MessageContentDto.OrderResult.fromOne(OVERSEAS_STOCK_RESERVATION_BUY_ORDER, getAccountStatus(), orderTrading));
    }

    /**
     * 최신 주문번호로 업데이트한다.
     * @param orderResult 주문 OpenApi 응답 Dto
     * @param reservationItemSeq 예약 종목 Seq
     */
    @Transactional
    public void updateLatestOrderNumber(OrderTrading orderResult, Long reservationItemSeq) {
        Optional<ReservationOrderInfo> optionalReservationOrderInfo = reservationOrderInfoRepository.findById(reservationItemSeq);
        if (optionalReservationOrderInfo.isEmpty()) {
            LogUtils.notFoundDataError(String.format("예약 매수 seq (%d)에 해당하는 데이터", reservationItemSeq));
            return;
        }

        ReservationOrderInfo reservationOrderInfo = optionalReservationOrderInfo.get();
        reservationOrderInfo.updateOrderNumber(orderResult.getOrderNumber());
        reservationOrderInfo.updateMetaData(LocalDateTime.now());
    }
}
