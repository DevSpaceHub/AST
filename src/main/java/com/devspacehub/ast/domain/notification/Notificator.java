/*
 © 2023 devspacehub, Inc. All rights reserved.

 name : Notificator
 creation : 2024.1.27
 author : Yoonji Moon
 */
package com.devspacehub.ast.domain.notification;

import com.devspacehub.ast.common.constant.OpenApiType;
import com.devspacehub.ast.common.constant.ResultCode;
import com.devspacehub.ast.domain.orderTrading.OrderTrading;
import com.devspacehub.ast.exception.error.NotificationException;
import com.devspacehub.ast.exception.error.InvalidValueException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import java.util.function.Consumer;

import static com.devspacehub.ast.common.constant.CommonConstants.*;

/**
 * Discord Webhook 이용한 알림 서비스
 */
@Slf4j
@Service
public class Notificator {
    @Value("${notify.discord-webhook.url}")
    private String discordWebhookUrl;
    private static final String ORDER_NOTI_SENDER_NAME = "주문 봇";

    /**
     * 알림 요청
     * @param openApiType
     * @param activeProfile
     * @param orderTrading
     */
    public void sendMessage(OpenApiType openApiType, String activeProfile, OrderTrading orderTrading) {
        // body 생성
        String senderName = getSenderName(openApiType);
        String msg = createMessage(openApiType, getAccountStatus(activeProfile), orderTrading);
        // API 호출
        Consumer<HttpHeaders> headers = DiscordWebhookNotifyRequestDto.setHeaders();
        DiscordWebhookNotifyRequestDto requestBody = DiscordWebhookNotifyRequestDto.builder()
                .senderName(senderName)
                .message(msg)
                .build();

        try {
            WebClient.builder()
                    .baseUrl(discordWebhookUrl)
                    .build()
                    .post()
                    .headers(headers)
                    .bodyValue(requestBody)
                    .retrieve()
                    .toBodilessEntity()
                    .block();

        } catch (Exception ex) {
            if (ex instanceof WebClientResponseException && HttpStatus.NO_CONTENT.equals(((WebClientResponseException) ex).getStatusCode())) {
                return;
            }
            log.error("response error: {}", ex.getMessage());
            throw new NotificationException();
        }
    }

    private String getAccountStatus(String activeProfile) {
        return ACTIVE_PROD.equals(activeProfile) ? REAL_ACCOUNT_STATUS_KOR : TEST_ACCOUNT_STATUS_KOR;
    }

    /**
     * 알림 봇 이름 지정
     * @param openApiType
     * @return
     */
    private String getSenderName(OpenApiType openApiType) {
        switch (openApiType) {
            case DOMESTIC_STOCK_BUY_ORDER, DOMESTIC_STOCK_SELL_ORDER, DOMESTIC_STOCK_RESERVATION_BUY_ORDER -> {
                return ORDER_NOTI_SENDER_NAME;
            }
            default -> throw new InvalidValueException(ResultCode.INVALID_OPENAPI_TYPE_ERROR);
        }
    }

    /**
     * 알림 메시지 내용 작성
     * @param openApiType
     * @param accountStatusKor
     * @param orderTrading
     * @return
     */
    public String createMessage(OpenApiType openApiType, String accountStatusKor, OrderTrading orderTrading) {
        StringBuilder sb = new StringBuilder();

        sb.append("**[").append(orderTrading.getOrderTime()).append("] ").append("주문완료**");
        sb.append("\n계좌 상태 : ").append(accountStatusKor);
        sb.append("\n종목명 : ").append(orderTrading.getItemNameKor()).
                append(" (").append(orderTrading.getItemCode()).append(")");
        sb.append("\n매매구분 : ").append(openApiType.getDiscription());
        sb.append("\n주문수량 : ").append(orderTrading.getOrderQuantity()).append("주");
        sb.append("\n주문단가 : ").append(orderTrading.getOrderPrice());
        sb.append("\n주문번호 : ").append(orderTrading.getOrderNumber());

        return sb.toString();
    }

}
