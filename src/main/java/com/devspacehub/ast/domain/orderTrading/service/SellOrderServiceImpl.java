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
import com.devspacehub.ast.domain.orderTrading.OrderTrading;
import com.devspacehub.ast.domain.orderTrading.OrderTradingRepository;
import com.devspacehub.ast.domain.orderTrading.dto.DomesticStockOrderExternalReqDto;
import com.devspacehub.ast.domain.orderTrading.dto.DomesticStockOrderExternalResDto;
import com.devspacehub.ast.util.OpenApiCall;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Consumer;

import static com.devspacehub.ast.common.constant.OpenApiType.DOMESTIC_STOCK_SELL_ORDER;

/**
 * 주식 주문 서비스 구현체 - 매도
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class SellOrderServiceImpl extends TradingService {
    private final OpenApiCall openApiCall;
    private final OpenApiProperties openApiProperties;
    private final OrderTradingRepository orderTradingRepository;

    @Value("${openapi.rest.header.transaction-id.sell-order}")
    private String txIdSellOrder;
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
        WebClientCommonResDto response = openApiCall.httpPostRequest(DOMESTIC_STOCK_SELL_ORDER, httpHeaders, bodyDto);


        return null;
    }

    @Override
    public void saveInfos(List<OrderTrading> orderTradingInfos) {

    }
}
