/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : SellOrderServiceImpl
 creation : 2024.1.6
 author : Yoonji Moon
 */

package com.devspacehub.ast.domain.orderTrading.service;

import com.devspacehub.ast.common.config.OpenApiProperties;
import com.devspacehub.ast.common.constant.OpenApiType;
import com.devspacehub.ast.common.dto.WebClientCommonResDto;
import com.devspacehub.ast.common.utils.LogUtils;
import com.devspacehub.ast.domain.marketStatus.dto.StockItemDto;
import com.devspacehub.ast.domain.my.stockBalance.dto.response.StockBalanceExternalResDto;
import com.devspacehub.ast.domain.my.service.MyService;
import com.devspacehub.ast.domain.notification.Notificator;
import com.devspacehub.ast.domain.notification.dto.MessageContentDto;
import com.devspacehub.ast.domain.orderTrading.OrderTrading;
import com.devspacehub.ast.domain.orderTrading.OrderTradingRepository;
import com.devspacehub.ast.domain.orderTrading.dto.DomesticStockOrderExternalReqDto;
import com.devspacehub.ast.domain.orderTrading.dto.DomesticStockOrderExternalResDto;
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
import java.util.List;
import java.util.function.Consumer;

import static com.devspacehub.ast.common.constant.CommonConstants.*;
import static com.devspacehub.ast.common.constant.OpenApiType.DOMESTIC_STOCK_SELL_ORDER;
import static com.devspacehub.ast.common.constant.ProfileType.getAccountStatus;

/**
 * 주식 주문 서비스 구현체 - 매도
 */
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class SellOrderServiceImpl extends TradingService {
    private final OpenApiRequest openApiRequest;
    private final OrderTradingRepository orderTradingRepository;
    private final Notificator notificator;
    private final MyService myService;

    @Value("${trading.stop-loss-sell-ratio}")
    private Float stopLossSellRatio;

    @Value("${trading.profit-sell-ratio}")
    private Float profitSellRatio;

    /**
     * 매도 주문
     * - 국내주식주문 API 호출
     */
    @Override
    public List<OrderTrading> order(OpenApiProperties openApiProperties, OpenApiType openApiType, String transactionId) {
        // 1. 주식 잔고 조회
        StockBalanceExternalResDto myStockBalance = myService.getMyStockBalance();


        // 2. 주식 선택 후 매도 주문 (손절매도 & 수익매도)
        List<OrderTrading> orderTradings = new ArrayList<>();
        for (StockItemDto item : pickStockItems(myStockBalance, transactionId)) {
            DomesticStockOrderExternalResDto result = callOrderApi(openApiProperties, item, DOMESTIC_STOCK_SELL_ORDER, transactionId);
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
    public DomesticStockOrderExternalResDto callOrderApi(OpenApiProperties openApiProperties, StockItemDto stockItem, OpenApiType openApiType, String transactionId) {

        Consumer<HttpHeaders> httpHeaders = DomesticStockOrderExternalReqDto.setHeaders(openApiProperties.getOauth(), transactionId);
        DomesticStockOrderExternalReqDto bodyDto = DomesticStockOrderExternalReqDto.from(openApiProperties, stockItem);

        return (DomesticStockOrderExternalResDto) openApiRequest.httpPostRequest(openApiType, httpHeaders, bodyDto);
    }

    /**
     * 알고리즘에 따라 거래할 종목 선택 및 매도 금액&수량 결정
     * - 현재가 시세 조회 API 호출 -> 매도 금액 결정
     */
    public List<StockItemDto> pickStockItems(WebClientCommonResDto resDto, String transactionId) {
        StockBalanceExternalResDto stockBalanceResponse = (StockBalanceExternalResDto) resDto;
        List<StockItemDto> pickedStockItems = new ArrayList<>();

        for (StockBalanceExternalResDto.MyStockBalance myStockBalance : stockBalanceResponse.getMyStockBalance()) {
            if (!isStockItemSellOrderable(myStockBalance, transactionId)) {
                continue;
            }

            pickedStockItems.add(StockItemDto.of(myStockBalance));
        }
        return pickedStockItems;
    }

    /**
     * 주식 매도할 수 있는 종목인지 체크.
     * @param myStockBalance
     * @return
     */
    protected boolean isStockItemSellOrderable(StockBalanceExternalResDto.MyStockBalance myStockBalance, String transactionId) {
        // 이미 체결된 주식인지 체크 (KIS : 체결 + 2일동안 0으로 응답함)
        if (0 == Integer.parseInt(myStockBalance.getHoldingQuantity())) {
            return false;
        }

        if (isEvaluateProfitLossRateBetweenProfitAndStopLossPercent(myStockBalance.getEvaluateProfitLossRate())) {
            return false;
        }
        return isNewOrder(myStockBalance.getItemCode(), transactionId);
    }

    /**
     * 평균 손익률과 비교. 수익/손절 매도 지표 사이 값이면 True. 그 반대(수익이거나 손절)면 False.
     * 수익 매도 : 평균 손익률 > 10%
     * 손절 매도 : 평균 손익률 < -5%
     * @param evaluateProfitLossRateStr
     * @return
     */
    protected boolean isEvaluateProfitLossRateBetweenProfitAndStopLossPercent(String evaluateProfitLossRateStr) {
        Float evaluateProfitLossRate = Float.valueOf(evaluateProfitLossRateStr);

        return stopLossSellRatio <= evaluateProfitLossRate && evaluateProfitLossRate <= profitSellRatio;  // 수익 매도 or 손절 매도
    }

    @Transactional
    @Override
    public void saveOrderInfos(List<OrderTrading> orderTradingInfos) {
        if (!orderTradingInfos.isEmpty()) {
            orderTradingRepository.saveAll(orderTradingInfos);
        }
    }

    /**
     * 매수됐지만 체결되지 않은 종목은 주문하지 않는다.
     * 매수됐던 이력도 없다면 주문할 수 있다.
     * @param itemCode
     * @param transactionId
     * @return
     */
    @Override
    public boolean isNewOrder(String itemCode, String transactionId){
        // 주문 가능 수량 초과 시 주문 불가.
        return 0 == orderTradingRepository.countByItemCodeAndOrderResultCodeAndTransactionIdAndRegistrationDateTimeBetween(
                itemCode, OPENAPI_SUCCESS_RESULT_CODE, transactionId,
                LocalDateTime.of(LocalDate.now(), LocalTime.of(0, 0, 0)),
                LocalDateTime.of(LocalDate.now(), LocalTime.of(23, 59, 59))
        );
    }

    @Override
    public void orderApiResultProcess(DomesticStockOrderExternalResDto result, OrderTrading orderTrading) {
        if (result.isSuccess()) {
            LogUtils.tradingOrderSuccess(DOMESTIC_STOCK_SELL_ORDER, orderTrading.getItemNameKor());
            notificator.sendMessage(MessageContentDto.OrderResult.fromOne(
                    DOMESTIC_STOCK_SELL_ORDER, getAccountStatus(), orderTrading));
        } else {
            LogUtils.openApiFailedResponseMessage(DOMESTIC_STOCK_SELL_ORDER, result.getMessage(), result.getMessageCode());
        }
    }
}
