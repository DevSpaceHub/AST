/*
 © 2023 devspacehub, Inc. All rights reserved.

 name : OpenApiCall
 creation : 2023.12.11
 author : Yoonji Moon
 */

package com.devspacehub.ast.util;

import com.devspacehub.ast.common.dto.WebClientRequestDto;
import com.devspacehub.ast.common.dto.WebClientResponseDto;
import com.devspacehub.ast.domain.oauth.service.dto.AccessTokenIssueExternalReqDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.function.Consumer;

/**
 * OpenApi 호출 클래스.
 */
@RequiredArgsConstructor
@Component
public class OpenApiCall {
    private final WebClient webClient;

    /**
     * OpenApi 호출 (Post)
     * @param <T>        the type parameter
     * @param uri        the uri
     * @param headers    the headers
     * @param requestDto the request dto
     * @return the web client response dto
     */
    public <T extends WebClientRequestDto> WebClientResponseDto httpPostRequest(String uri, Consumer<HttpHeaders> headers, T requestDto) {
        return webClient
                .mutate()
                .build()
                .post()
                .uri(uri)
                .headers(headers)
                .bodyValue(requestDto)
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                        clientResponse ->  clientResponse.bodyToMono(String.class)
                                .map(body -> new RuntimeException(body)))
                .bodyToMono(WebClientResponseDto.class)
                .block();
    }

    /**
     * OpenApi 접근 위한 OAuth 호출 <Post 메서드>
     *
     * @param uri        the uri
     * @param requestDto the request dto
     * @return the string
     */
    public String httpOAuthRequest(String uri, AccessTokenIssueExternalReqDto requestDto) {
        return webClient
                .mutate()
                .build()
                .post()
                .uri(uri)
                .bodyValue(requestDto)
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                        clientResponse ->  clientResponse.bodyToMono(String.class)
                                .map(body -> new RuntimeException(body)))
                .bodyToMono(String.class)
                .block();
    }
}
