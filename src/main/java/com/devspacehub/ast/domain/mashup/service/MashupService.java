/*
 © 2023 devspacehub, Inc. All rights reserved.

 name : MashupService
 creation : 2023.12.11
 author : Yoonji Moon
 */

package com.devspacehub.ast.domain.mashup.service;

import com.devspacehub.ast.common.config.OpenApiProperties;
import com.devspacehub.ast.common.constant.TokenType;
import com.devspacehub.ast.domain.oauth.service.OAuthService;
import com.devspacehub.ast.domain.orderTrading.OrderTrading;
import com.devspacehub.ast.domain.orderTrading.OrderTradingServiceFactory;
import com.devspacehub.ast.domain.orderTrading.service.TradingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.devspacehub.ast.common.constant.OpenApiType.*;

/**
 * The type Mashup service.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MashupService {
    private final OAuthService oAuthService;
    private final OrderTradingServiceFactory orderTradingServiceFactory;
    private final OpenApiProperties openApiProperties;

    @Value("${openapi.rest.header.transaction-id.buy-order}")
    private String txIdBuyOrder;
    @Value("${openapi.rest.header.transaction-id.sell-order}")
    private String txIdSellOrder;

    /**
     * 매수 주문
     */
    @Transactional
    public void startBuyOrder() {
        oAuthService.setAccessToken(TokenType.AccessToken);

        TradingService tradingService = orderTradingServiceFactory.getServiceImpl(DOMESTIC_STOCK_BUY_ORDER);
        // 주문
        List<OrderTrading> orderTradings = tradingService.order(openApiProperties, DOMESTIC_STOCK_BUY_ORDER, txIdBuyOrder);
        // 이력 저장
        tradingService.saveInfos(orderTradings);
    }

    /**
     * 매도 주문
     */
    @Transactional
    public void startSellOrder() {
        oAuthService.setAccessToken(TokenType.AccessToken);

        TradingService tradingService = orderTradingServiceFactory.getServiceImpl(DOMESTIC_STOCK_SELL_ORDER);
        // 주문
        List<OrderTrading> orderTradings = tradingService.order(openApiProperties, DOMESTIC_STOCK_SELL_ORDER, txIdSellOrder);
        // 이력 저장
        tradingService.saveInfos(orderTradings);
    }


    /**
     * 예약 매수 주문
     */
    @Transactional
    public void startReservationBuyOrder() {
        oAuthService.setAccessToken(TokenType.AccessToken);

        TradingService tradingService = orderTradingServiceFactory.getServiceImpl(DOMESTIC_STOCK_RESERVATION_BUY_ORDER);
        // 주문
        List<OrderTrading> orderTradings = tradingService.order(openApiProperties, DOMESTIC_STOCK_RESERVATION_BUY_ORDER, txIdBuyOrder);
        // 이력 저장
        tradingService.saveInfos(orderTradings);
    }
}
