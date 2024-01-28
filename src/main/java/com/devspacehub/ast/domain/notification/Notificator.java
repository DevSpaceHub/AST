/*
 © 2023 devspacehub, Inc. All rights reserved.

 name : Notificator
 creation : 2024.1.27
 author : Yoonji Moon
 */
package com.devspacehub.ast.domain.notification;

import com.devspacehub.ast.exception.error.NotificationException;
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

    /**
     * 알림 요청
     * @param senderName
     * @param msg
     */
    public void sendMessage(String senderName, String msg) {
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
                    .bodyToMono(Void.class);

        } catch (Exception ex) {
            if (ex instanceof WebClientResponseException && HttpStatus.NO_CONTENT.equals(((WebClientResponseException) ex).getStatusCode())) {
                return;
            }
            log.error("response error: {}", ex.getMessage());
            throw new NotificationException();
        }
    }

}
