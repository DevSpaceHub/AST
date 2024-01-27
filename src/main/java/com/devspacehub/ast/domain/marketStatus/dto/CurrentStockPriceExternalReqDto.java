/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : CurrentStockPriceExternalReqDto
 creation : 2024.1.23
 author : Yoonji Moon
 */

package com.devspacehub.ast.domain.marketStatus.dto;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.function.Consumer;

/**
 * 주식 현재가 시세 조회 API 요청 DTO.
 */
public class CurrentStockPriceExternalReqDto {
    private static final String FID_CONDITION_MARKET_DIVISION_CODE = "J";   // FID 조건 시장 분류 코드

    public static Consumer<HttpHeaders> setHeaders(String oauth, String txId) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + oauth);
        headers.add("tr_id", txId);
        return httpHeaders -> httpHeaders.addAll(headers);
    }

    public static MultiValueMap<String, String> createParameter(String stockCode) {
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();

        queryParams.add("FID_COND_MRKT_DIV_CODE", FID_CONDITION_MARKET_DIVISION_CODE);
        queryParams.add("FID_INPUT_ISCD", stockCode);

        return queryParams;
    }
}