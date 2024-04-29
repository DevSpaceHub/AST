/*
 © 2023 devspacehub, Inc. All rights reserved.

 name : MashupService
 creation : 2023.12.11
 author : Yoonji Moon
 */

package com.devspacehub.ast.domain.mashup.service;

import com.devspacehub.ast.common.config.OpenApiProperties;
import com.devspacehub.ast.common.constant.TokenType;
import com.devspacehub.ast.domain.my.service.MyService;
import com.devspacehub.ast.domain.notification.Notificator;
import com.devspacehub.ast.domain.notification.dto.MessageContentDto;
import com.devspacehub.ast.domain.oauth.service.OAuthService;
import com.devspacehub.ast.domain.orderTrading.OrderTrading;
import com.devspacehub.ast.domain.orderTrading.OrderTradingServiceFactory;
import com.devspacehub.ast.domain.orderTrading.dto.OrderConclusionDto;
import com.devspacehub.ast.domain.orderTrading.service.TradingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static com.devspacehub.ast.common.constant.OpenApiType.*;
import static com.devspacehub.ast.common.constant.ProfileType.*;

/**
 * The type Mashup service.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MashupService {
    private final OAuthService oAuthService;
    private final MyService myService;
    private final Notificator notificator;
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
        tradingService.saveOrderInfos(orderTradings);
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
        tradingService.saveOrderInfos(orderTradings);
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
        tradingService.saveOrderInfos(orderTradings);
    }

    /**
     * 주문 체결 결과에 대해 후처리 진행
     * 대상 : 금일 체결된 주문
     * 1. 체결 상태에 따른 예약 매수 사용 여부 업데이트
     * 2. 체결 결과 메시지 전송
     */
    @Transactional
    public void startOrderConclusionResultProcess() {
        oAuthService.setAccessToken(TokenType.AccessToken);

        // 체결 상태 확인
        List<OrderConclusionDto> concludedOrderTradingResults = myService.getConcludedStock(LocalDate.now());
        for (OrderConclusionDto orderConclusion : concludedOrderTradingResults) {
            // 예약 매수 종목 사용 여부 업데이트
            myService.updateMyReservationOrderUseYn(orderConclusion, LocalDate.now());

            // 체결된 종목들에 대해 디스코드 메시지 전달
            notificator.sendMessage(MessageContentDto.ConclusionResult.fromOne(
                    ORDER_CONCLUSION_FIND, getAccountStatus(), orderConclusion));
        }
    }
}
