/*
 © 2023 devspacehub, Inc. All rights reserved.

 name : MashupService
 creation : 2023.12.11
 author : Yoonji Moon
 */

package com.devspacehub.ast.domain.mashup.service;

import com.devspacehub.ast.common.config.OpenApiProperties;
import com.devspacehub.ast.common.constant.OpenApiType;
import com.devspacehub.ast.common.constant.TokenType;
import com.devspacehub.ast.domain.my.service.MyService;
import com.devspacehub.ast.domain.notification.Notificator;
import com.devspacehub.ast.domain.notification.dto.MessageContentDto;
import com.devspacehub.ast.domain.oauth.service.OAuthService;
import com.devspacehub.ast.domain.orderTrading.OrderTrading;
import com.devspacehub.ast.domain.orderTrading.OrderTradingServiceFactory;
import com.devspacehub.ast.domain.orderTrading.dto.OrderConclusionDto;
import com.devspacehub.ast.domain.orderTrading.service.TradingService;
import com.devspacehub.ast.util.RequestUtil;
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

    /**
     * 매수 주문
     */
    @Transactional
    public void startBuyOrder(OpenApiType openApiType) {
        oAuthService.setAccessToken(TokenType.AccessToken);

        TradingService tradingService = orderTradingServiceFactory.getServiceImpl(openApiType);
        // 주문
        List<OrderTrading> orderTradings = tradingService.order(openApiProperties, openApiType);
        // 이력 저장
        tradingService.saveOrderInfos(orderTradings);
    }

    /**
     * 매도 주문
     */
    public void startSellOrder(OpenApiType openApiType) {
        oAuthService.setAccessToken(TokenType.AccessToken);

        TradingService tradingService = orderTradingServiceFactory.getServiceImpl(openApiType);
        // 주문
        List<OrderTrading> orderTradings = tradingService.order(openApiProperties, openApiType);
        // 이력 저장
        tradingService.saveOrderInfos(orderTradings);
    }


    /**
     * 예약 매수 주문
     */
    public void startReservationBuyOrder(OpenApiType openApiType) {
        oAuthService.setAccessToken(TokenType.AccessToken);

        TradingService tradingService = orderTradingServiceFactory.getServiceImpl(openApiType);
        // 주문
        List<OrderTrading> orderTradings = tradingService.order(openApiProperties, openApiType);
        // 이력 저장
        tradingService.saveOrderInfos(orderTradings);
    }

    /**
     * 주문 체결 결과에 대해 후처리 진행
     * 대상 : 금일 체결된 주문
     * 1. 예약 매수 종목인 경우 일부 데이터 업데이트 진행
     * 2. 체결 결과 메시지 전송
     */
    @Transactional
    public void startOrderConclusionResultProcess(OpenApiType openApiType) {
        oAuthService.setAccessToken(TokenType.AccessToken);

        // 체결 상태 확인
        List<OrderConclusionDto> concludedOrderTradingResults = myService.getConcludedStock(LocalDate.now());
        for (OrderConclusionDto orderConclusion : concludedOrderTradingResults) {
            // 예약 매수 종목인 경우 처리
            myService.updateMyReservationOrderUseYn(orderConclusion, LocalDate.now());

            // 체결된 종목들에 대해 디스코드 메시지 전달
            notificator.sendMessage(MessageContentDto.ConclusionResult.fromOne(openApiType, getAccountStatus(), orderConclusion));
            RequestUtil.timeDelay();
        }
    }
}
