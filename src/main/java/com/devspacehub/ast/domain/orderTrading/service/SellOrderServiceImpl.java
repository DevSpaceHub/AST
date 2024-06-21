/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : SellOrderServiceImpl
 creation : 2024.1.6
 author : Yoonji Moon
 */

package com.devspacehub.ast.domain.orderTrading.service;

import com.devspacehub.ast.common.config.OpenApiProperties;
import com.devspacehub.ast.common.constant.MarketType;
import com.devspacehub.ast.common.constant.OpenApiType;
import com.devspacehub.ast.common.dto.WebClientCommonResDto;
import com.devspacehub.ast.common.utils.LogUtils;
import com.devspacehub.ast.domain.marketStatus.dto.StockItemDto;
import com.devspacehub.ast.domain.my.service.MyServiceFactory;
import com.devspacehub.ast.domain.my.stockBalance.dto.response.StockBalanceApiResDto;
import com.devspacehub.ast.domain.my.service.MyService;
import com.devspacehub.ast.domain.notification.Notificator;
import com.devspacehub.ast.domain.notification.dto.MessageContentDto;
import com.devspacehub.ast.domain.orderTrading.OrderTrading;
import com.devspacehub.ast.domain.orderTrading.OrderTradingRepository;
import com.devspacehub.ast.domain.orderTrading.dto.DomesticStockOrderExternalReqDto;
import com.devspacehub.ast.domain.orderTrading.dto.StockOrderApiResDto;
import com.devspacehub.ast.util.OpenApiRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import static com.devspacehub.ast.common.constant.CommonConstants.*;
import static com.devspacehub.ast.common.constant.OpenApiType.DOMESTIC_STOCK_SELL_ORDER;
import static com.devspacehub.ast.common.constant.ProfileType.getAccountStatus;

/**
 * 국내 주식 주문 서비스 구현체 - 매도
 */
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class SellOrderServiceImpl extends TradingService {
    private final OpenApiRequest openApiRequest;
    private final OrderTradingRepository orderTradingRepository;
    private final Notificator notificator;
    private final MyServiceFactory myServiceFactory;

    @Value("${trading.domestic.indicator.stop-loss-sell-ratio}")
    private Float stopLossSellRatio;

    @Value("${trading.domestic.indicator.profit-sell-ratio}")
    private Float profitSellRatio;
    @Value("${openapi.rest.header.transaction-id.domestic.sell-order}")
    private String transactionId;
    private static final int MARKET_START_HOUR = 9;
    private static final int MARKET_END_HOUR = 16;

    /**
     * 매도 주문
     * - 국내주식주문 API 호출
     */
    @Override
    public List<OrderTrading> order(OpenApiProperties openApiProperties, OpenApiType openApiType) {
        // 1. 주식 잔고 조회
        StockBalanceApiResDto myStockBalance = (StockBalanceApiResDto) myServiceImpl().getMyStockBalance();

        // 2. 주식 선택 후 매도 주문 (손절매도 & 수익매도)
        List<OrderTrading> orderTradings = new ArrayList<>();
        for (StockItemDto item : pickStockItems(myStockBalance, transactionId)) {
            StockOrderApiResDto result = callOrderApi(openApiProperties, item, DOMESTIC_STOCK_SELL_ORDER, transactionId);
            OrderTrading orderTrading = OrderTrading.from(item, result, transactionId);
            orderTradings.add(orderTrading);

            orderApiResultProcess(result, orderTrading);
        }
        return orderTradings;
    }

    /**
     * 주문 API 호출 메서드 호출
     * @param openApiProperties
     * @param stockItem
     * @param openApiType
     * @param transactionId
     * @return DomesticStockOrderExternalResDto
     */
    @Override
    public <T extends StockItemDto> StockOrderApiResDto callOrderApi(OpenApiProperties openApiProperties, T stockItem, OpenApiType openApiType, String transactionId) {

        Consumer<HttpHeaders> httpHeaders = DomesticStockOrderExternalReqDto.setHeaders(openApiProperties.getOauth(), transactionId);
        DomesticStockOrderExternalReqDto bodyDto = DomesticStockOrderExternalReqDto.from(openApiProperties, stockItem);

        return (StockOrderApiResDto) openApiRequest.httpPostRequest(openApiType, httpHeaders, bodyDto);
    }

    /**
     * 알고리즘에 따라 거래할 종목 선택 및 매도 금액&수량 결정
     * - 현재가 시세 조회 API 호출 -> 매도 금액 결정
     */
    public List<StockItemDto> pickStockItems(StockBalanceApiResDto stockBalanceResponse, String transactionId) {
        List<StockItemDto> pickedStockItems = new ArrayList<>();

        for (StockBalanceApiResDto.MyStockBalance myStockBalance : stockBalanceResponse.getMyStockBalance()) {
            if (isStockItemSellOrderable(myStockBalance, transactionId)) {
                pickedStockItems.add(StockItemDto.Domestic.of(myStockBalance));
            }
        }
        return pickedStockItems;
    }

    /**
     * 주식 매도할 수 있는 종목인지 체크.
     * @param myStockBalance 주식잔고조회 Api 응답 Dto
     * @param transactionId 매도 transactionId
     * @return 매도 주문할 수 있는 종목이면 True를 반환한다.
     */
    protected boolean isStockItemSellOrderable(StockBalanceApiResDto.MyStockBalance myStockBalance, String transactionId) {
        // 이미 체결된 주식인지 체크 (KIS : 체결 + 2일동안 0으로 응답함)
        if (0 == myStockBalance.getHoldingQuantity()) {
            return false;
        }

        if (isEvaluateProfitLossRateBetweenProfitAndStopLossPercent(myStockBalance.getEvaluateProfitLossRate())) {
            return false;
        }
        return isNewOrder(myStockBalance.getItemCode(), transactionId,
                LocalDateTime.of(LocalDate.now(), LocalTime.of(MARKET_START_HOUR, 0, 0)),
                LocalDateTime.of(LocalDate.now(), LocalTime.of(MARKET_END_HOUR, 0, 0))
        );
    }

    /**
     * 해당 종목의 평균 손익률을 손익절 지표와 비교하여 반환한다.
     * 수익 매도 : 평균 손익률 > 10%
     * 손절 매도 : 평균 손익률 < -5%
     * @param evaluateProfitLossRate 평가 손익률
     * @return 평가손익률이 손익절 지표와 수익절 지표 사이에 있으면 True를 반환한다. 그 반대(수익이거나 손절)면 False.
     */
    protected boolean isEvaluateProfitLossRateBetweenProfitAndStopLossPercent(Float evaluateProfitLossRate) {
        return stopLossSellRatio <= evaluateProfitLossRate && evaluateProfitLossRate <= profitSellRatio;  // 수익 매도 or 손절 매도
    }

    /**
     * 매도 주문 정보 저장
     * @param orderTradingInfos 다수의 매도 주문 정보 Entity
     * @return 저장된 다수의 Entity
     */
    @Transactional
    @Override
    public List<OrderTrading> saveOrderInfos(List<OrderTrading> orderTradingInfos) {
        if (!orderTradingInfos.isEmpty()) {
            return orderTradingRepository.saveAll(orderTradingInfos);
        }
        return Collections.emptyList();
    }

    /**
     * 매수됐지만 체결되지 않은 종목은 주문하지 않는다.
     * 매수됐던 이력도 없다면 주문할 수 있다.
     * @param itemCode
     * @param transactionId
     * @return
     */
    @Override
    public boolean isNewOrder(String itemCode, String transactionId, LocalDateTime marketStart, LocalDateTime marketEnd){
        // 주문 가능 수량 초과 시 주문 불가.
        return 0 == orderTradingRepository.countByItemCodeAndOrderResultCodeAndTransactionIdAndRegistrationDateTimeBetween(
                itemCode, OPENAPI_SUCCESS_RESULT_CODE, transactionId, marketStart, marketEnd);
    }

    /**
     * 매도 주문 후 로그 출력 및 메세지 전송 요청
     * @param result 주문 결과 Dto
     * @param orderTrading 매도 주문 정보 Entity
     * @param <T> WebClientCommonResDto를 상속하는 클래스
     */
    @Override
    public <T extends WebClientCommonResDto> void orderApiResultProcess(T result, OrderTrading orderTrading) {
        if (result.isSuccess()) {
            LogUtils.tradingOrderSuccess(DOMESTIC_STOCK_SELL_ORDER, orderTrading.getItemNameKor());
            notificator.sendMessage(MessageContentDto.OrderResult.fromOne(
                    DOMESTIC_STOCK_SELL_ORDER, getAccountStatus(), orderTrading));
        } else {
            LogUtils.openApiFailedResponseMessage(DOMESTIC_STOCK_SELL_ORDER, result.getMessage(), result.getMessageCode());
        }
    }

    /**
     * Market 타입에 따라 적절한 MyService 구현체를 조회한다.
     * @return MyService 구현체
     */
    private MyService myServiceImpl() {
        return myServiceFactory.resolveService(MarketType.DOMESTIC);
    }
}
