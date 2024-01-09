/*
 © 2023 devspacehub, Inc. All rights reserved.

 name : OpenApiCall
 creation : 2023.12.11
 author : Yoonji Moon
 */

package com.devspacehub.ast.util;

import com.devspacehub.ast.common.constant.OpenApiType;
import com.devspacehub.ast.common.dto.WebClientCommonReqDto;
import com.devspacehub.ast.common.dto.WebClientCommonResDto;
import com.devspacehub.ast.domain.my.dto.response.BuyPossibleCheckExternalResDto;
import com.devspacehub.ast.domain.my.dto.response.StockBalanceExternalResDto;
import com.devspacehub.ast.domain.oauth.dto.AccessTokenIssueExternalReqDto;
import com.devspacehub.ast.domain.orderTrading.dto.DomesticStockOrderExternalResDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.function.Consumer;

/**
 * OpenApi 호출 클래스.
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class OpenApiCall {
    private final WebClient webClient;

    @Value("${openapi.rest.domain}")
    private String openApiDomain;

    /**
     * 접근 토큰 발급 OpenApi Api 호출 (Post)
     *
     * @param uri        the uri
     * @param requestDto the request dto
     * @return the string
     */
    public String httpOAuthRequest(String uri, AccessTokenIssueExternalReqDto requestDto) {
        String response = null;
        try {
            response = WebClient.builder()
                    .baseUrl(openApiDomain)
                    .build()
                    .post()
                    .uri(uri)
                    .bodyValue(requestDto)
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                            clientResponse -> clientResponse.bodyToMono(String.class)
                                    .map(RuntimeException::new))
                    .bodyToMono(String.class)
                    .block();
        } catch (Exception ex) {
            log.error("요청 실패하였습니다.(요청 uri : {})", uri);
        }
        return response;
    }

    /**
     * web client 통해 OpenApi 호출 (Get)
     *
     * @param openApiType the open api type
     * @param headers     the headers
     * @param queryParams the query params
     * @return the web client common res dto
     */
    public WebClientCommonResDto httpGetRequest(OpenApiType openApiType, Consumer<HttpHeaders> headers, MultiValueMap<String, String> queryParams) {
        Class<? extends WebClientCommonResDto> implResDtoClass = implyReturnType(openApiType);
        WebClientCommonResDto response =  null;
        try {
            response = webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(openApiType.getUri())
                        .queryParams(queryParams).build()
                )
                .headers(headers)
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                        clientResponse -> clientResponse.bodyToMono(String.class)
                                .map(RuntimeException::new))
                .bodyToMono(implResDtoClass)
                .block();
        } catch (Exception ex) {
            log.error("요청 실패하였습니다.(요청 uri : {})", openApiType.getUri());
        }
        return response;
}

    /**
     * OpenApi 호출 (Post)
     *
     * @param <T>         the type parameter
     * @param openApiType the open api type
     * @param headers     the headers
     * @param requestDto  the request dto
     * @return the web client response dto
     */
    public <T extends WebClientCommonReqDto> WebClientCommonResDto httpPostRequest(OpenApiType openApiType, Consumer<HttpHeaders> headers, T requestDto) {

        Class<? extends WebClientCommonResDto> implResDtoClass = implyReturnType(openApiType);
        WebClientCommonResDto response =  null;
        try {
            response = webClient
                    .mutate()
                    .build()
                    .post()
                    .uri(openApiType.getUri())
                    .headers(headers)
                    .bodyValue(requestDto)
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                            clientResponse ->  clientResponse.bodyToMono(String.class)
                                    .map(RuntimeException::new))
                    .bodyToMono(implResDtoClass)
                    .block();
        } catch (Exception ex) {
            log.error("요청 실패하였습니다.(요청 uri : {})", openApiType.getUri());
        }
        return response;
    }

    private Class<? extends WebClientCommonResDto> implyReturnType(OpenApiType openApiType) {
        switch (openApiType) {
            case DOMESTIC_STOCK_BUY_ORDER -> {
                return DomesticStockOrderExternalResDto.class;
            }
            case BUY_ORDER_POSSIBLE_CASH -> {
                return BuyPossibleCheckExternalResDto.class;
            }
            case STOCK_BALANCE -> {
                return StockBalanceExternalResDto.class;
            }
            default -> throw new IllegalArgumentException("적절한 응답 DTO가 없습니다.");
        }
    }
}
