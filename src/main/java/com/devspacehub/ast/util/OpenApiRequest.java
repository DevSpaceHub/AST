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
import com.devspacehub.ast.domain.marketStatus.dto.CurrentStockPriceExternalResDto;
import com.devspacehub.ast.domain.marketStatus.dto.DomStockTradingVolumeRankingExternalResDto;
import com.devspacehub.ast.domain.marketStatus.dto.OverseasStockConditionSearchResDto;
import com.devspacehub.ast.domain.my.dto.orderConclusion.OrderConclusionFindExternalResDto;
import com.devspacehub.ast.domain.my.stockBalance.dto.response.BuyPossibleCashApiResDto;
import com.devspacehub.ast.domain.my.stockBalance.dto.response.StockBalanceApiResDto;
import com.devspacehub.ast.domain.my.stockBalance.dto.response.overseas.OverseasBuyPossibleCashApiResDto;
import com.devspacehub.ast.domain.my.stockBalance.dto.response.overseas.OverseasStockBalanceApiResDto;
import com.devspacehub.ast.domain.oauth.dto.AccessTokenIssueExternalReqDto;
import com.devspacehub.ast.domain.orderTrading.dto.StockOrderApiResDto;
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

    /**
     * 접근 토큰 발급 OpenApi Api 호출 (Post)
     * @param openApiType OpenApi 타입
     * @param requestDto 요청 Dto
     * @return 신규 발급된 접근 토큰
     */
    public String httpOAuthRequest(OpenApiType openApiType, AccessTokenIssueExternalReqDto requestDto) {
        RequestUtil.timeDelay();

        String response;
        try {
            response = WebClient.builder()
                    .baseUrl(openApiDomain)
                    .build()
                    .post()
                    .uri(openApiType.getUri())
                    .bodyValue(requestDto)
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                            clientResponse -> clientResponse.bodyToMono(String.class)
                                    .map(RuntimeException::new))
                    .bodyToMono(String.class)
                    .block();
        } catch (Exception ex) {
            throw new OpenApiFailedResponseException(openApiType, ex.getMessage());
        }
        checkResponseIsNull(OpenApiType.OAUTH_ACCESS_TOKEN_ISSUE, response);
        return response;
    }


    /**
     * web client 통해 OpenApi 호출 (Get)
     * @param openApiType the open api type
     * @param headers     the headers
     * @param queryParams the query params
     * @return the web client common res dto
     */
    public WebClientCommonResDto httpGetRequest(OpenApiType openApiType, Consumer<HttpHeaders> headers, MultiValueMap<String, String> queryParams) {
        RequestUtil.timeDelay();

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
            throw new OpenApiFailedResponseException(openApiType, ex.getMessage());
        }
        checkResponseIsNull(openApiType, response);
        return response;
    }

    /**
     * OpenApi 호출 (Post)
     * @param <T>         the type parameter
     * @param openApiType the open api type
     * @param headers     the headers
     * @param requestDto  the request dto
     * @return the web client response dto
     */
    public <T extends WebClientCommonReqDto> WebClientCommonResDto httpPostRequest(OpenApiType openApiType, Consumer<HttpHeaders> headers, T requestDto) {
        RequestUtil.timeDelay();

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
            throw new OpenApiFailedResponseException(openApiType, ex.getMessage());
        }
        checkResponseIsNull(openApiType, response);
        return response;
    }

    /**
     * 형변환을 위해 WebClientCommonResDto를 상속하는 구현 클래스 타입 반환한다.
     * @param openApiType OpenApi 요청하고자 하는 타입
     * @return OpenApiType에 맞는 응답 Dto Class
     */
    public Class<? extends WebClientCommonResDto> implyReturnType(OpenApiType openApiType) {
        switch (openApiType) {
            case DOMESTIC_STOCK_BUY_ORDER, DOMESTIC_STOCK_SELL_ORDER, DOMESTIC_STOCK_RESERVATION_BUY_ORDER,
                    OVERSEAS_STOCK_BUY_ORDER, OVERSEAS_STOCK_SELL_ORDER, OVERSEAS_STOCK_RESERVATION_BUY_ORDER -> {
                return StockOrderApiResDto.class;
            }
            case DOMESTIC_BUY_ORDER_POSSIBLE_CASH -> {
                return BuyPossibleCashApiResDto.class;
            }
            case OVERSEAS_BUY_ORDER_POSSIBLE_CASH -> {
                return OverseasBuyPossibleCashApiResDto.class;
            }
            case DOMESTIC_STOCK_BALANCE -> {
                return StockBalanceApiResDto.class;
            }
            case OVERSEAS_STOCK_BALANCE -> {
                return OverseasStockBalanceApiResDto.class;
            }
            case DOMESTIC_TRADING_VOLUME_RANKING -> {
                return DomStockTradingVolumeRankingExternalResDto.class;
            }
            case OVERSEAS_STOCK_CONDITION_SEARCH -> {
                return OverseasStockConditionSearchResDto.class;
            }
            case CURRENT_STOCK_PRICE -> {
                return CurrentStockPriceExternalResDto.class;
            }
            case DOMESTIC_ORDER_CONCLUSION_FIND -> {
                return OrderConclusionFindExternalResDto.class;
            }
            default -> throw new IllegalArgumentException("적절한 응답 DTO가 없습니다.");
        }
    }

    /**
     * 응답값이 null이면 예외를 발생시킨다.
     * @param openApiType OpenApi 타입
     * @param response 응답 Dto
     */
    private void checkResponseIsNull(OpenApiType openApiType, Object response) {
        if (Objects.isNull(response)) {
            throw new OpenApiFailedResponseException(String.format("[%s] OpenApi 응답값이 Null입니다.", openApiType.getDiscription()));
        }
    }

}
