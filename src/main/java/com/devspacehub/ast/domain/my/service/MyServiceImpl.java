/*
 © 2023 devspacehub, Inc. All rights reserved.

 name : MyServiceImpl
 creation : 2023.12.10
 author : Yoonji Moon
 */

package com.devspacehub.ast.domain.my.service;

import com.devspacehub.ast.common.config.OpenApiProperties;
import com.devspacehub.ast.domain.my.dto.request.BuyPossibleCheckExternalReqDto;
import com.devspacehub.ast.domain.my.dto.response.BuyPossibleCheckExternalResDto;
import com.devspacehub.ast.openApiUtil.OpenApiRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.function.Consumer;

import static com.devspacehub.ast.common.constant.OpenApiType.BUY_ORDER_POSSIBLE_CASH;

/**
 * 사용자 My 서비스 구현체.
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class MyServiceImpl implements MyService {
    private final OpenApiRequest openApiRequest;
    private final OpenApiProperties openApiProperties;
    private final ObjectMapper objectMapper;
    @Value("${openapi.rest.header.transaction-id.buy-possible-cash-find}")
    private String txIdBuyPossibleCashFind;

    /**
     * 매수 가능 금액 조회
     */
    @Override
    public int getBuyOrderPossibleCash(String stockCode, Integer orderPrice, String orderDivision) {
        // 헤더 & 파라미터 값 생성
        Consumer<HttpHeaders> httpHeaders = BuyPossibleCheckExternalReqDto.setHeaders(openApiProperties.getOauth(), txIdBuyPossibleCashFind);
        MultiValueMap<String, String> queryParams = createRequestParameter(stockCode, orderPrice, orderDivision);

        BuyPossibleCheckExternalResDto responseDto = (BuyPossibleCheckExternalResDto) openApiRequest.httpGetRequest(BUY_ORDER_POSSIBLE_CASH, httpHeaders, queryParams);

        log.info("응답 : {}", responseDto.getMessage());
        log.info("주문 가능 현금 : {}", responseDto.getOutput().getOrderPossibleCash());
        log.info("최대 구매 가능 금액 : {}", responseDto.getOutput().getMaxBuyAmount());
        log.info("최대 구매 가능 수량 : {}", responseDto.getOutput().getMaxBuyQuantity());
        return Integer.valueOf(responseDto.getOutput().getOrderPossibleCash());
    }

    private MultiValueMap<String, String> createRequestParameter(String stockCode, Integer orderPrice, String orderDivision) {
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.add("CANO", openApiProperties.getAccntNumber());
        queryParams.add("ACNT_PRDT_CD", openApiProperties.getAccntProductCode());
        queryParams.add("PDNO", stockCode);
        queryParams.add("ORD_UNPR", String.valueOf(orderPrice));
        queryParams.add("ORD_DVSN", orderDivision);
        queryParams.add("CMA_EVLU_AMT_ICLD_YN", "N");  // CMA 평가 금액 포함 여부
        queryParams.add("OVRS_ICLD_YN", "N");    // 해외 포함 여부

        return queryParams;
    }

    /**
     * 매수 가능 금액 조회
     */
    @Override
    public boolean BuyOrderPossibleCheck(String stockCode, String orderDivision, Integer orderPrice) {
        int myCash = getBuyOrderPossibleCash(stockCode, orderPrice, orderDivision);
        if (orderPrice <= myCash) {
            return true;
        }
        log.info("매수 가능 금액({})이 매수가({})보다 낮습니다.", myCash, orderPrice);
        return false;
    }
}
