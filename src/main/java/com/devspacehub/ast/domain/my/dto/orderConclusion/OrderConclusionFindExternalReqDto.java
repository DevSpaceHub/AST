/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : OrderConclusionExternalReqDto
 creation : 2024.4.19
 author : Yoonji Moon
 */

package com.devspacehub.ast.domain.my.dto.orderConclusion;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.function.Consumer;

/**
 * 주식 일별 주문 체결 조회 요청 DTO.
 */
public class OrderConclusionFindExternalReqDto {

    public static MultiValueMap<String, String> createParameter(String accntNumber, String accntProductCode, String todayStr) {
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.add("CANO", accntNumber);
        queryParams.add("ACNT_PRDT_CD", accntProductCode);
        queryParams.add("INQR_STRT_DT", todayStr);  // 조회 시작 일자
        queryParams.add("INQR_END_DT", todayStr);   // 조회 종료 일자
        queryParams.add("SLL_BUY_DVSN_CD", "02");   // 매도매수구분코드 (02:매수)
        queryParams.add("INQR_DVSN", "01");     // 조회 구분 (01:정순)
        queryParams.add("PDNO", "");            // 종콕코드
        queryParams.add("CCLD_DVSN", "01");     // 체결 구분
        queryParams.add("ORD_GNO_BRNO", "");    // 주문채번지점번호
        queryParams.add("ODNO", "");            // 주문번호
        queryParams.add("INQR_DVSN_3", "01");   // 조회구분3 (01:현금)
        queryParams.add("INQR_DVSN_1", "");     // 조회구분1 (1:ELW, 2:프리보드)
        queryParams.add("CTX_AREA_FK100", "");   // 연속조회검색조건100
        queryParams.add("CTX_AREA_NK100", "");   // 연속조회키100

        return queryParams;
    }

    /**
     * Sets headers.
     *
     * @param oauth the oauth
     * @param txId  the tx id
     * @return the headers
     */
    public static Consumer<HttpHeaders> setHeaders(String oauth, String txId) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + oauth);
        headers.add("tr_id", txId);
        return httpHeaders -> httpHeaders.addAll(headers);
    }
}
