/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : ReservationBuyOrderServiceImpl
 creation : 2024.3.20
 author : Yoonji Moon
 */

package com.devspacehub.ast.domain.my.reservationOrderInfo.service;

import com.devspacehub.ast.common.config.OpenApiProperties;
import com.devspacehub.ast.common.constant.MarketType;
import com.devspacehub.ast.common.constant.OpenApiType;
import com.devspacehub.ast.common.constant.StockPriceUnit;
import com.devspacehub.ast.common.utils.BigDecimalUtil;
import com.devspacehub.ast.common.utils.LogUtils;
import com.devspacehub.ast.domain.marketStatus.dto.StockItemDto;
import com.devspacehub.ast.domain.marketStatus.service.MarketStatusService;
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
import com.devspacehub.ast.domain.orderTrading.dto.DomesticStockOrderExternalReqDto;
import com.devspacehub.ast.domain.orderTrading.dto.StockOrderApiResDto;
import com.devspacehub.ast.domain.orderTrading.service.TradingService;
import com.devspacehub.ast.exception.error.BusinessException;
import com.devspacehub.ast.exception.error.OpenApiFailedResponseException;
import com.devspacehub.ast.util.OpenApiRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static com.devspacehub.ast.common.constant.CommonConstants.ORDER_DIVISION;
import static com.devspacehub.ast.common.constant.OpenApiType.DOMESTIC_STOCK_RESERVATION_BUY_ORDER;
import static com.devspacehub.ast.common.constant.ProfileType.*;
import static com.devspacehub.ast.domain.marketStatus.dto.CurrentStockPriceExternalResDto.*;

/**
 * 주문 서비스 - 예약 매수
 */
@Slf4j
@Transactional(readOnly = true)
@Service
public class ReservationBuyOrderServiceImpl extends TradingService {
    private final ReservationOrderInfoRepository reservationOrderInfoRepository;
    private final MarketStatusService marketStatusService;
    @Value("${openapi.rest.header.transaction-id.domestic.buy-order}")
    private String transactionId;

    public ReservationBuyOrderServiceImpl(OpenApiRequest openApiRequest, Notificator notificator,
                                          OrderTradingRepository orderTradingRepository, MyServiceFactory myServiceFactory,
                                          ReservationOrderInfoRepository reservationOrderInfoRepository, MarketStatusService marketStatusService) {
        super(openApiRequest, notificator, orderTradingRepository, myServiceFactory);
        this.reservationOrderInfoRepository = reservationOrderInfoRepository;
        this.marketStatusService = marketStatusService;
    }

    /**
     * 유효한 예약 매수 종목 데이터를 예약 매수 주문한다.
     * @param openApiProperties
     * @param openApiType
     * @return
     */
    @Transactional
    @Override
    public List<OrderTrading> order(OpenApiProperties openApiProperties, OpenApiType openApiType) {
        // 1. 예약 종목들 조회
        List<ReservationOrderInfo> reservationOrderInfos = getValidReservationsForToday();

        if (reservationOrderInfos.isEmpty()) {
            LogUtils.notFoundDataError("금일 예약 매수할 종목 데이터");
            return new ArrayList<>();
        }

        // 2. 매수 종목 선택 및 주문
        List<OrderTrading> orderTradings = new ArrayList<>();
        OrderTrading orderTrading;
        for (ReservationStockItem reservationItem : pickStockItems(reservationOrderInfos)) {
            try {
                orderTrading = this.buy(reservationItem, openApiProperties, openApiType);
            } catch(BusinessException ex) {
                log.warn("code = {}, message = '{}'", ex.getResultCode(), ex.getMessage());
                continue;
            }

            this.updateLatestOrderNumber(orderTrading, reservationItem.getReservationSeq());
            this.orderApiResultProcess(orderTrading);
            orderTradings.add(orderTrading);
        }
        return orderTradings;
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
        StockOrderApiResDto apiResponse = callOrderApi(openApiProperties, reservationItem.getStockItem(), openApiType, transactionId);
        if (apiResponse.isFailed()) {
            throw new OpenApiFailedResponseException(openApiType, apiResponse.getMessage());
        }

        return OrderTrading.from(reservationItem.getStockItem(), apiResponse, transactionId);
    }

    /**
     * 유효한 예약 종목 데이터를 조회하여 반환한다.
     * @return 유효한 예약 종목 Entity
     */
    protected List<ReservationOrderInfo> getValidReservationsForToday() {
        return reservationOrderInfoRepository.findValidAll(LocalDate.now());
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

    /**
     * 매수 주문할 종목들 선택한다.
     * - 현재가 시세 조회
     * - 호가 단위에 맞게 조정
     * - 충분한 예수금 있는지, 하한가보다 높은지 체크
     * @param reservationOrderInfos
     */
    public List<ReservationStockItem> pickStockItems(List<ReservationOrderInfo> reservationOrderInfos) {
        Map<Long, ReservationOrderInfo> itemCodeReservationOrderInfoMap = reservationOrderInfos.stream()
                .collect(Collectors.toMap(ReservationOrderInfo::getSeq, reservationOrderInfo -> reservationOrderInfo));

        // 현재가 시세 조회 API
        Map<Long, CurrentStockPriceInfo> itemCodeResponseMap = reservationOrderInfos.stream()
                .collect(Collectors.toMap(ReservationOrderInfo::getSeq, orderInfo -> marketStatusService.getCurrentStockPrice(orderInfo.getItemCode())));

        List<ReservationStockItem> pickedStockItems = new ArrayList<>();
        for (Long seq : itemCodeReservationOrderInfoMap.keySet()) {
            ReservationOrderInfo currReservationOrderInfo = itemCodeReservationOrderInfoMap.get(seq);

            // 호가 단위 조정
            BigDecimal adjustedOrderPrice = StockPriceUnit.intOrderPriceCuttingByPriceUnit(currReservationOrderInfo.getOrderPrice());
            currReservationOrderInfo.updateOrderPrice(adjustedOrderPrice);
            // 하한가 비교
            if (BigDecimalUtil.isLessThan(currReservationOrderInfo.getOrderPrice(), itemCodeResponseMap.get(seq).getStockLowerLimitPrice())) {
                continue;
            }
            // 예수금 체크
            BigDecimal myDeposit = getMyService().getBuyOrderPossibleCash(MyServiceRequestDto.Domestic.from(currReservationOrderInfo.getItemCode(), adjustedOrderPrice, ORDER_DIVISION));
            BigDecimal purchaseAmount = BigDecimalUtil.multiplyBigDecimalWithNumber(adjustedOrderPrice, currReservationOrderInfo.getOrderQuantity());
            if (BigDecimalUtil.isLessThan(myDeposit, purchaseAmount)) {
                LogUtils.insufficientAmountError(DOMESTIC_STOCK_RESERVATION_BUY_ORDER, currReservationOrderInfo.getKoreanItemName(), myDeposit);
                continue;
            }

            currReservationOrderInfo.subtractConcludedQuantity(currReservationOrderInfo.getConclusionQuantity());
            pickedStockItems.add(ReservationStockItem.ofDomestic(currReservationOrderInfo));
        }
        log.info("[{}] 최종 선택된 주식 종목 갯수 : {}", DOMESTIC_STOCK_RESERVATION_BUY_ORDER.getDiscription(), pickedStockItems.size());
        return pickedStockItems;
    }

    /**
     * Market Type에 따라 MyService 구현체를 반환한다.
     * @return MyService 구현체
     */
    private MyService getMyService() {
        return myServiceFactory.resolveService(MarketType.DOMESTIC);
    }

    @Override
    public <T extends StockItemDto> StockOrderApiResDto callOrderApi(OpenApiProperties openApiProperties, T stockItem, OpenApiType openApiType, String transactionId) {
        Consumer<HttpHeaders> httpHeaders = DomesticStockOrderExternalReqDto.setHeaders(openApiProperties.getOauth(), transactionId);
        DomesticStockOrderExternalReqDto bodyDto = DomesticStockOrderExternalReqDto.from(openApiProperties, stockItem);

        return (StockOrderApiResDto) openApiRequest.httpPostRequest(openApiType, httpHeaders, bodyDto);
    }

    @Override
    public List<OrderTrading> saveOrderInfos(List<OrderTrading> orderTradingInfos) {
        if (!orderTradingInfos.isEmpty()) {
            return orderTradingRepository.saveAll(orderTradingInfos);
        }
        return Collections.emptyList();
    }

    @Override
    public void orderApiResultProcess(OrderTrading orderTrading) {
        LogUtils.tradingOrderSuccess(DOMESTIC_STOCK_RESERVATION_BUY_ORDER, orderTrading.getItemNameKor());
        notificator.sendMessage(MessageContentDto.OrderResult.fromOne(DOMESTIC_STOCK_RESERVATION_BUY_ORDER, getAccountStatus(), orderTrading));
    }
}
