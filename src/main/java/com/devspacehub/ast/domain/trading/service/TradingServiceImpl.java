/*
 © 2023 devspacehub, Inc. All rights reserved.

 name : TradingServiceImpl
 creation : 2023.12.10
 author : Yoonji Moon
 */

package com.devspacehub.ast.domain.trading.service;

import com.devspacehub.ast.common.config.OpenApiProperties;
import com.devspacehub.ast.common.dto.WebClientResponseDto;
import com.devspacehub.ast.domain.oauth.service.OAuthService;
import com.devspacehub.ast.domain.trading.dto.DomesticStockBuyOrderExternalDto;
import com.devspacehub.ast.util.OpenApiCall;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;

import static com.devspacehub.ast.common.constant.OpenApiUri.DOMESTIC_STOCK_BUY_ORDER;

/**
 * 주식 주문 서비스 구현체.
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class TradingServiceImpl implements TradingService {
    private final OpenApiCall openApiCall;
    private final OpenApiProperties openApiProperties;
    private final OAuthService oAuthService;

    @Value("${openapi.rest.header.transaction-id.buy-order}")
    private String txIdBuyOrder;

    /**
     * 매수 주문
     * - 국내주식주문 API 호출
     * - 변환 필드 : 종목코드(6자리), 주문구분(지정가,00), 주문수량, 주문단가
    */
    @Override
    public void buyOrder() {
        // 1. 조건 체크

        // 2. oauth
        String oauth = oAuthService.issueAccessToken();

        // 3. 주식 주문
        // 호출 url
        // 종목코드(6자리), 주문구분(지정가,00), 주문수량, 주문단가 설정
        String stockCode = "000020"; // TODO 쿼리문 생성 필요
        String orderDivision = "00";
        String orderQuantity = "1";
        String orderPrice = "53000";

        Consumer<HttpHeaders> httpHeaders = DomesticStockBuyOrderExternalDto.setHeaders(oauth, txIdBuyOrder);
        DomesticStockBuyOrderExternalDto bodyDto = DomesticStockBuyOrderExternalDto.builder()
                .accntNumber(openApiProperties.getAccntNumber())
                .accntProductCode(openApiProperties.getAccntProductCode())
                .stockCode(stockCode)
                .orderDivision(orderDivision)
                .orderQuantity(orderQuantity)
                .orderPrice(orderPrice)
                .build();
        WebClientResponseDto response = openApiCall.httpPostRequest(DOMESTIC_STOCK_BUY_ORDER.getUri(), httpHeaders, bodyDto);
        /*
        // body를 String으로 반환시키고 각 상황에 맞게 dto로 변환?
        try {
            responseDto = objectMapper.readValue(
                    response.getOutput() != null ? response.getOutput() : null,
                    WebClientResponseDto.class);
        } catch (Exception e) {
            System.out.println("error");
        }*/

        log.info(response.getOutput());
        log.info(response.getRt_cd());
        log.info(response.getMsg1());
        log.info(response.getMsg_cd());
        // 4. ..
        log.info("====finish====");
    }

    /**
     * 매도 주문
     * - 국내주식주문 API 호출
     */
    @Override
    public void sellOrder() {

    }

    // 직접 dto 전환 서비스 호출과 연결된 서비스 메서드

}
