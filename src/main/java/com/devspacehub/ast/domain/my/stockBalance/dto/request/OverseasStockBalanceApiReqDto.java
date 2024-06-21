/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : OverseasStockBalanceApiReqDto
 creation : 2024.6.15
 author : Yoonji Moon
 */

package com.devspacehub.ast.domain.my.stockBalance.dto.request;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.function.Consumer;

/**
 * 해외 주식잔고 조회 API 요청 DTO
 */
public class OverseasStockBalanceApiReqDto {
    /**
     * parameter 생성하여 반환한다.
     * @param accntNumber API 요청할 계좌번호 체계 앞 8자리
     * @param accntProductCode API 요청할 계좌번호 체계 앞 2자리
     * @return MultiValueMap 타입의 파라미터
     */
    public static MultiValueMap<String, String> createParameter(String accntNumber, String accntProductCode) {
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.add("CANO", accntNumber);
        queryParams.add("ACNT_PRDT_CD", accntProductCode);
        queryParams.add("OVRS_EXCG_CD", "");
        queryParams.add("TR_CRCY_CD", "");
        queryParams.add("CTX_AREA_FK200", "");
        queryParams.add("CTX_AREA_NK200", "");

        return queryParams;
    }

    /**
     * Header 세팅하여 반환한다.
     * @param oauth OpenAPI의 RestApi 접근 토큰
     * @param txId transactionId
     * @return 세팅된 헤더
     */
    public static Consumer<HttpHeaders> setHeaders(String oauth, String txId) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + oauth);
        headers.add("tr_id", txId);
        return httpHeaders -> httpHeaders.addAll(headers);
    }
}
