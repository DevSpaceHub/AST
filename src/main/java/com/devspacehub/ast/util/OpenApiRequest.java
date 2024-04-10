/*
 © 2023 devspacehub, Inc. All rights reserved.

 name : OpenApiRequest
 creation : 2023.12.11
 author : Yoonji Moon
 */

package com.devspacehub.ast.util;

import com.devspacehub.ast.common.constant.OpenApiType;
import com.devspacehub.ast.common.dto.WebClientCommonReqDto;
import com.devspacehub.ast.common.dto.WebClientCommonResDto;
import com.devspacehub.ast.common.utils.LogUtils;
import com.devspacehub.ast.domain.marketStatus.dto.CurrentStockPriceExternalResDto;
import com.devspacehub.ast.domain.marketStatus.dto.DomStockTradingVolumeRankingExternalResDto;
import com.devspacehub.ast.domain.my.stockBalance.dto.response.BuyPossibleCheckExternalResDto;
import com.devspacehub.ast.domain.my.stockBalance.dto.response.StockBalanceExternalResDto;
import com.devspacehub.ast.domain.oauth.dto.AccessTokenIssueExternalReqDto;
import com.devspacehub.ast.domain.orderTrading.dto.DomesticStockOrderExternalResDto;
import com.devspacehub.ast.exception.error.OpenApiFailedResponseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * OpenApi 호출 클래스.
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class OpenApiRequest {
    private final WebClient webClient;

    @Value("${openapi.rest.domain}")
    private String openApiDomain;
    private static final long TIME_DELAY_MILLIS = 500L;

    /**
     * 접근 토큰 발급 OpenApi Api 호출 (Post)
     *
     * @param uri        the uri
     * @param requestDto the request dto
     * @return the string
     */
    public String httpOAuthRequest(String uri, AccessTokenIssueExternalReqDto requestDto) {
        timeDelay();

        String response;
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
            LogUtils.openApiRequestFailed(uri, ex.getMessage());
            throw new OpenApiFailedResponseException();
        }
        checkResponseIsNull(OpenApiType.OAUTH_ACCESS_TOKEN_ISSUE, response);
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
        timeDelay();

        Class<? extends WebClientCommonResDto> implResDtoClass = implyReturnType(openApiType);
        WebClientCommonResDto response;
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
            LogUtils.openApiRequestFailed(openApiType.getUri(), ex.getMessage());
            throw new OpenApiFailedResponseException();
        }
        checkResponseIsNull(openApiType, response);
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
        timeDelay();

        Class<? extends WebClientCommonResDto> implResDtoClass = implyReturnType(openApiType);
        WebClientCommonResDto response;
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
            LogUtils.openApiRequestFailed(openApiType.getUri(), ex.getMessage());
            throw new OpenApiFailedResponseException();
        }
        checkResponseIsNull(openApiType, response);
        return response;
    }

    private Class<? extends WebClientCommonResDto> implyReturnType(OpenApiType openApiType) {
        switch (openApiType) {
            case DOMESTIC_STOCK_BUY_ORDER, DOMESTIC_STOCK_SELL_ORDER, DOMESTIC_STOCK_RESERVATION_BUY_ORDER -> {
                return DomesticStockOrderExternalResDto.class;
            }
            case BUY_ORDER_POSSIBLE_CASH -> {
                return BuyPossibleCheckExternalResDto.class;
            }
            case STOCK_BALANCE -> {
                return StockBalanceExternalResDto.class;
            }
            case DOMSTOCK_TRADING_VOLUME_RANKING -> {
                return DomStockTradingVolumeRankingExternalResDto.class;
            }
            case CURRENT_STOCK_PRICE -> {
                return CurrentStockPriceExternalResDto.class;
            }
            default -> throw new IllegalArgumentException("적절한 응답 DTO가 없습니다.");
        }
    }

    /**
     * 응답값이 null이면 예외를 발생시킨다.
     * @param response
     */
    private void checkResponseIsNull(OpenApiType openApiType, Object response) {
        if (Objects.isNull(response)) {
            log.error("[{}] OpenApi 응답값이 Null입니다.", openApiType.getDiscription());
            throw new OpenApiFailedResponseException();
        }
    }

    /**
     * KIS Open API를 초당 2회 이상 호출하지 않기 위해 0.5초 시간 지연 수행.
     */
    private void timeDelay() {
        try {
            Thread.sleep(TIME_DELAY_MILLIS);
        } catch (InterruptedException ex) {
            log.error("시간 지연 처리 중 이슈 발생하였습니다.");
            log.error("{}", ex.getStackTrace());
        }
    }

}
