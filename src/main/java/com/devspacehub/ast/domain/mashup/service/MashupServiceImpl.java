/*
 © 2023 devspacehub, Inc. All rights reserved.

 name : mashup
 creation : 2023.12.11
 author : Yoonji Moon
 */

package com.devspacehub.ast.domain.mashup.service;

import com.devspacehub.ast.common.config.OpenApiProperties;
import com.devspacehub.ast.common.dto.WebClientResponseDto;
import com.devspacehub.ast.domain.my.dto.BuyPossibleCheckReqDto;
import com.devspacehub.ast.domain.oauth.service.OAuthService;
import com.devspacehub.ast.domain.trading.service.TradingService;
import com.devspacehub.ast.util.OpenApiCall;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;

import static com.devspacehub.ast.common.constant.OpenApiUri.DOMESTIC_STOCK_BUY_ORDER;

/**
 * The type Mashup service.
 */
@Service
@RequiredArgsConstructor
public class MashupServiceImpl {
    private final OAuthService oAuthService;
    private final TradingService tradingService;
    private final OpenApiCall openApiCall;
    private final OpenApiProperties openApiProperties;

    /**
     * 거래 시작 메서드
     */
    public void startTrading() {
        // 1. oauth
        String oauth = oAuthService.issueAccessToken();
        openApiProperties.setOauth(oauth);
        // 2. 예수금 조회 (MyService)

        // 3. 주식 주문 가능 여부 체크 (매수가능조회 api)
        if (isBuyPossible()) {

        }
        // 4. 주식 주문 (TradingService)
        tradingService.buyOrder("000020", "00", "1", "53000");
    }

    private boolean isBuyPossible() {
        Consumer<HttpHeaders> httpHeaders = BuyPossibleCheckReqDto.setHeaders(openApiProperties.getOauth());
        BuyPossibleCheckReqDto bodyDto = BuyPossibleCheckReqDto.builder()
                .build();
        WebClientResponseDto response = openApiCall.httpPostRequest(DOMESTIC_STOCK_BUY_ORDER.getUri(), httpHeaders, bodyDto);

        return true;
    }
}
