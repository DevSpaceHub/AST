/*
 © 2023 devspacehub, Inc. All rights reserved.

 name : Notificator
 creation : 2024.1.27
 author : Yoonji Moon
 */
package com.devspacehub.ast.domain.notification;

import com.devspacehub.ast.common.constant.OpenApiType;
import com.devspacehub.ast.common.constant.ResultCode;
import com.devspacehub.ast.domain.notification.dto.DiscordWebhookNotifyRequestDto;
import com.devspacehub.ast.domain.notification.dto.MessageContentDto;
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


/**
 * Discord Webhook 이용한 알림 서비스
 */
@Slf4j
@Service
public class Notificator {
    @Value("${notify.discord-webhook.url}")
    private String discordWebhookUrl;
    private static final String DOMESTIC_ORDER_NOTI_SENDER_NAME = "국내 주문 봇";
    private static final String DOMESTIC_CONCLUSION_NOTI_SENDER_NAME = "국내 체결 봇";

    private static final String OVERSEAS_ORDER_NOTI_SENDER_NAME = "해외 주문 봇";
    private static final String OVERSEAS_CONCLUSION_NOTI_SENDER_NAME = "해외 체결 봇";

    /**
     * 단일 메시지 발송 요청
     * @param content 메세지 내용 DTO
     */
    public <T extends MessageContentDto> void sendMessage(T content) {
        String senderName = getSenderName(content.getOpenApiType());
        String msg = content.createMessage(content);
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
            if (ex instanceof WebClientResponseException webClientResponseException && HttpStatus.NO_CONTENT.equals(webClientResponseException.getStatusCode())) {
                return;
            }
            throw new NotificationException(ex.getMessage());
        }
    }
    /**
     * 알림 봇 이름 지정
     * @param openApiType OpenApiType
     * @return 알림 봇 이름
     */
    private String getSenderName(OpenApiType openApiType) {
        return switch (openApiType) {
            case DOMESTIC_STOCK_BUY_ORDER, DOMESTIC_STOCK_SELL_ORDER, DOMESTIC_STOCK_RESERVATION_BUY_ORDER -> DOMESTIC_ORDER_NOTI_SENDER_NAME;
            case DOMESTIC_ORDER_CONCLUSION_FIND -> DOMESTIC_CONCLUSION_NOTI_SENDER_NAME;
            case OVERSEAS_STOCK_BUY_ORDER, OVERSEAS_STOCK_SELL_ORDER, OVERSEAS_STOCK_RESERVATION_BUY_ORDER -> OVERSEAS_ORDER_NOTI_SENDER_NAME;
            case OVERSEAS_ORDER_CONCLUSION_FIND -> OVERSEAS_CONCLUSION_NOTI_SENDER_NAME;
            default -> throw new InvalidValueException(ResultCode.INVALID_OPENAPI_TYPE_ERROR);
        };
    }

}
