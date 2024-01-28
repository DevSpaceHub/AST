/*
 © 2023 devspacehub, Inc. All rights reserved.

 name : DiscordWebhookNotifyRequestDto
 creation : 2024.1.27
 author : Yoonji Moon
 */
package com.devspacehub.ast.domain.notification;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.function.Consumer;

/**
 * Discord Webhook 이용한 알림 요청 DTO.
 */
@Builder
@Getter
@Setter
public class DiscordWebhookNotifyRequestDto {
    @JsonProperty(value = "username")
    private String senderName;
    @JsonProperty(value = "content")
    private String message;

    public static Consumer<HttpHeaders> setHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        return httpHeaders -> httpHeaders.addAll(headers);
    }
}
