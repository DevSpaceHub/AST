/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : SellOrderServiceImpl
 creation : 2024.1.6
 author : Yoonji Moon
 */

package com.devspacehub.ast.domain.orderTrading.service;

import com.devspacehub.ast.common.config.OpenApiProperties;
import com.devspacehub.ast.common.dto.WebClientCommonResDto;
import com.devspacehub.ast.domain.marketStatus.dto.StockItemDto;
import com.devspacehub.ast.domain.my.dto.response.StockBalanceExternalResDto;
import com.devspacehub.ast.domain.orderTrading.OrderTrading;
import com.devspacehub.ast.domain.orderTrading.OrderTradingRepository;
import com.devspacehub.ast.domain.orderTrading.dto.DomesticStockOrderExternalReqDto;
import com.devspacehub.ast.domain.orderTrading.dto.DomesticStockOrderExternalResDto;
import com.devspacehub.ast.openApiUtil.OpenApiRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static com.devspacehub.ast.common.constant.CommonConstants.ORDER_DIVISION;
import static com.devspacehub.ast.common.constant.OpenApiType.DOMESTIC_STOCK_SELL_ORDER;

/**
 * 주식 주문 서비스 구현체 - 매도
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class SellOrderServiceImpl extends TradingService {
    private final OpenApiRequest openApiRequest;
    private final OpenApiProperties openApiProperties;
    private final OrderTradingRepository orderTradingRepository;

    @Value("${openapi.rest.header.transaction-id.sell-order}")
    private String txIdSellOrder;

    @Value("${trading.stop-loss-sell-ratio}")
    private Float stopLossSellRatio;

    @Value("${trading.profit-sell-ratio}")
    private Float profitSellRatio;
    private static final Float COMMISSION_RATE = 0.2f;

    /**
     * TODO 매도 주문
     * - 국내주식주문 API 호출
     */
    @Override
    public DomesticStockOrderExternalResDto order(StockItemDto stockItem) {
        // 1. 조건 체크
        Consumer<HttpHeaders> httpHeaders = DomesticStockOrderExternalReqDto.setHeaders(openApiProperties.getOauth(), txIdSellOrder);
        DomesticStockOrderExternalReqDto bodyDto = DomesticStockOrderExternalReqDto.builder()
                .accntNumber(openApiProperties.getAccntNumber())
                .accntProductCode(openApiProperties.getAccntProductCode())
                .stockCode(stockItem.getStockCode())
                .orderDivision(stockItem.getOrderDivision())
                .orderQuantity(String.valueOf(stockItem.getOrderQuantity()))
                .orderPrice(String.valueOf(stockItem.getOrderPrice()))
                .build();

        return (DomesticStockOrderExternalResDto) openApiRequest.httpPostRequest(DOMESTIC_STOCK_SELL_ORDER, httpHeaders, bodyDto);
    }

    /**
     * 알고리즘에 따라 거래할 종목 선택 및 매수 금액&수량 결정
     * - 현재가 시세 조회 API 호출 -> 매수 금액 결정
     * @param resDto
     * @return
     */
    public List<StockItemDto> pickStockItems(WebClientCommonResDto resDto) {
        StockBalanceExternalResDto stockBalanceResponse = (StockBalanceExternalResDto) resDto;
        List<StockItemDto> pickedStockItems = new ArrayList<>();

        for (StockBalanceExternalResDto.Output1 myStockBalance : stockBalanceResponse.getOutput1()) {
            Float evaluateEarningRate = floatValueOf(myStockBalance.getEvaluateEarningRate());    // TODO 손실일 경우 값이 어떻게 넘어오는지 확인 필요
            if (checkIsSellStockItem(evaluateEarningRate)) {
                // TODO 매도 금액 결정 위해 현재가 시세 조회 API 필요

                pickedStockItems.add(StockItemDto.builder()
                                .stockCode(myStockBalance.getStockCode())
                                .orderDivision(ORDER_DIVISION)
                                .orderQuantity("")    // TODO 수량, 주문 논의 예정
                                .orderPrice("")
                        .build());
            }
        }
        return pickedStockItems;
    }

    private boolean checkIsSellStockItem(Float evaluateEarningRate) {
        if (evaluateEarningRate - COMMISSION_RATE > profitSellRatio) {  // 수익 매도  ex) 11.0-0.2 = 10.8 > 10%
            return true;
        }
        if (evaluateEarningRate - COMMISSION_RATE < stopLossSellRatio) { // 손절 매도  ex) 5.0-0.2 = 4.8 < -5.0%
            return true;
        }
        return false;
    }

    private Float floatValueOf(String strRate) {
        if (strRate.startsWith("-")) {
            return Float.valueOf(strRate.substring(1)) * -1.0f;
        }
        return Float.valueOf(strRate);
    }

    @Override
    public void saveInfos(List<OrderTrading> orderTradingInfos) {
        orderTradingRepository.saveAll(orderTradingInfos);
    }
}
