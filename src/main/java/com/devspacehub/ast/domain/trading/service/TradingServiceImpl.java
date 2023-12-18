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

    @Value("${openapi.rest.header.transaction-id.buy-order}")
    private String txIdBuyOrder;

    /**
     * 매수 주문
     * - 국내주식주문 API 호출
     *
     * @param stockCode     종목코드(6자리)
     * @param orderDivision 주문구분(지정가,00)
     * @param orderQuantity 주문수량
     * @param orderPrice    주문단가
     */
    @Override
    public void buyOrder(String stockCode, String orderDivision, String orderQuantity, String orderPrice) {
        // 1. 조건 체크


        // 3. 주식 주문
        // 호출 url
        // 종목코드(6자리), 주문구분(지정가,00), 주문수량, 주문단가 설정

        Consumer<HttpHeaders> httpHeaders = DomesticStockBuyOrderExternalDto.setHeaders(openApiProperties.getOauth(), txIdBuyOrder);
        DomesticStockBuyOrderExternalDto bodyDto = DomesticStockBuyOrderExternalDto.builder()
                .accntNumber(openApiProperties.getAccntNumber())
                .accntProductCode(openApiProperties.getAccntProductCode())
                .stockCode(stockCode)
                .orderDivision(orderDivision)
                .orderQuantity(orderQuantity)
                .orderPrice(orderPrice)
                .build();
        WebClientResponseDto response = openApiCall.httpPostRequest(DOMESTIC_STOCK_BUY_ORDER.getUri(), httpHeaders, bodyDto);

        if (isStockMarketClosed(response)) {

        }
        /*
        // body를 String으로 반환시키고 각 상황에 맞게 dto로 변환?
        try {
            responseDto = objectMapper.readValue(
                    response.getOutput() != null ? response.getOutput() : null,
                    WebClientResponseDto.class);
        } catch (Exception e) {
            System.out.println("error");
        }*/

        for (String output : response.getOutput()) {
            log.info(output);   // body : 성공 시 null
        }
        log.info(response.getRt_cd());  // rt_cd : 1 (실패), 0 (성공)
        log.info(response.getMsg1());   // msg
        log.info(response.getMsg_cd()); // 실패 : 40100000,  성공 : APBK0013
        // 4. ..
        log.info("====finish====");
    }

    private boolean isStockMarketClosed(WebClientResponseDto response) {
        return "40100000".equals(response.getMsg_cd());
    }

    /**
     * 매도 주문
     * - 국내주식주문 API 호출
     */
    @Override
    public void sellOrder() {
        // 1. 조건 체크

        //
        String stockCode = "000020"; // TODO 쿼리문 생성 필요
        String orderDivision = "00";
        String orderQuantity = "1";
        String orderPrice = "53000";

        Consumer<HttpHeaders> httpHeaders = DomesticStockBuyOrderExternalDto.setHeaders(openApiProperties.getOauth(), txIdBuyOrder);
        DomesticStockBuyOrderExternalDto bodyDto = DomesticStockBuyOrderExternalDto.builder()
                .accntNumber(openApiProperties.getAccntNumber())
                .accntProductCode(openApiProperties.getAccntProductCode())
                .stockCode(stockCode)
                .orderDivision(orderDivision)
                .orderQuantity(orderQuantity)
                .orderPrice(orderPrice)
                .build();
        WebClientResponseDto response = openApiCall.httpPostRequest(DOMESTIC_STOCK_BUY_ORDER.getUri(), httpHeaders, bodyDto);

    }

}
