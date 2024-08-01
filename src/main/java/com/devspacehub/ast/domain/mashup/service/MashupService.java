/*
 © 2023 devspacehub, Inc. All rights reserved.

 name : MashupService
 creation : 2023.12.11
 author : Yoonji Moon
 */

package com.devspacehub.ast.domain.mashup.service;

import com.devspacehub.ast.common.config.OpenApiProperties;
import com.devspacehub.ast.common.constant.MarketType;
import com.devspacehub.ast.common.constant.OpenApiType;
import com.devspacehub.ast.common.constant.TokenType;
import com.devspacehub.ast.domain.my.service.MyService;
import com.devspacehub.ast.domain.my.service.MyServiceFactory;
import com.devspacehub.ast.domain.notification.Notificator;
import com.devspacehub.ast.domain.notification.dto.MessageContentDto;
import com.devspacehub.ast.domain.oauth.service.OAuthService;
import com.devspacehub.ast.domain.orderTrading.OrderTrading;
import com.devspacehub.ast.domain.orderTrading.OrderTradingServiceFactory;
import com.devspacehub.ast.domain.my.dto.orderConclusion.OrderConclusionDto;
import com.devspacehub.ast.domain.orderTrading.service.TradingService;
import com.devspacehub.ast.util.RequestUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

import static com.devspacehub.ast.common.constant.ProfileType.*;

/**
 * The type Mashup service.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MashupService {
    private final OAuthService oAuthService;
    private final MyServiceFactory myServiceFactory;
    private final Notificator notificator;
    private final OrderTradingServiceFactory orderTradingServiceFactory;
    private final OpenApiProperties openApiProperties;
    private static final String RESULT_CODE_DOMESTIC_PREFIX = "DM";

    /**
     * 매수/매도/예약 매수 주문
     */
    public void startOrder(OpenApiType openApiType) {
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
    public void startOrderConclusionResultProcess(OpenApiType openApiType) {
        oAuthService.setAccessToken(TokenType.AccessToken);
        MyService myServiceImpl = myServiceImpl(openApiType);

        // 체결 상태 확인
        List<OrderConclusionDto> concludedOrderTradingResults = myServiceImpl.getConcludedStock(LocalDate.now());
        for (OrderConclusionDto orderConclusion : concludedOrderTradingResults) {
            // 예약 매수 종목인 경우 처리
            myServiceImpl.updateMyReservationOrderUseYn(orderConclusion, LocalDate.now());

            // 체결된 종목들에 대해 디스코드 메시지 전달
            notificator.sendMessage(MessageContentDto.ConclusionResult.fromOne(openApiType, getAccountStatus(), orderConclusion));
            RequestUtil.timeDelay();
        }
    }

    /**
     * OpenApi 타입에 따라 MyServiceFactory를 다르게 호출하여 구현체를 획득한다.
     * @param openApiType OpenApi 타입
     * @return MyService 구현체
     */
    private MyService myServiceImpl(OpenApiType openApiType) {
        return openApiType.getCode().startsWith(RESULT_CODE_DOMESTIC_PREFIX) ? myServiceFactory.resolveService(MarketType.DOMESTIC) :
                myServiceFactory.resolveService(MarketType.OVERSEAS);
    }

}
