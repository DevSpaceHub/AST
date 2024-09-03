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

/**
 * 주식 일별 주문 체결 조회 요청 DTO.
 */
public class OrderConclusionFindExternalReqDto {

    /**
     * Sets headers.
     *
     * @param oauth the oauth
     * @param txId  the tx id
     * @return the headers
     */
    public static HttpHeaders setHeaders(String oauth, String txId) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + oauth);
        headers.add("tr_id", txId);
        return headers;
    }

    public static class Domestic {
        /**
         * 매수 주문의 체결 결과 조회 위해 파라미터 생성한다.
         * @param accntNumber 계좌번호 앞 8자리
         * @param accntProductCode 계좌번호 뒤 2자리
         * @param todayStr 조회 일자 (yyyyMMdd)
         * @return 파라미터
         */
        public static MultiValueMap<String, String> createParameter(String accntNumber, String accntProductCode, String todayStr) {
            MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
            queryParams.add("CANO", accntNumber);
            queryParams.add("ACNT_PRDT_CD", accntProductCode);
            queryParams.add("INQR_STRT_DT", todayStr);  // 조회 시작 일자
            queryParams.add("INQR_END_DT", todayStr);   // 조회 종료 일자
            queryParams.add("SLL_BUY_DVSN_CD", "02");   // 매도매수구분코드 (02:매수)
            queryParams.add("INQR_DVSN", "01");         // 조회 구분 (01:정순)
            queryParams.add("PDNO", "");                // 전 종목코드
            queryParams.add("CCLD_DVSN", "01");         // 체결 구분
            queryParams.add("ORD_GNO_BRNO", "");        // 주문채번지점번호
            queryParams.add("ODNO", "");                // 주문번호
            queryParams.add("INQR_DVSN_3", "01");       // 조회구분3 (01:현금)
            queryParams.add("INQR_DVSN_1", "");         // 조회구분1 (1:ELW, 2:프리보드)
            queryParams.add("CTX_AREA_FK100", "");      // 연속조회검색조건100
            queryParams.add("CTX_AREA_NK100", "");      // 연속조회키100

            return queryParams;
        }
    }

    public static class Overseas {
        /**
         * 매수 주문의 체결 결과 조회 위해 파라미터 생성한다.
         * @param accntNumber 계좌번호 앞 8자리
         * @param accntProductCode 계좌번호 뒤 2자리
         * @param todayStr 조회 일자 (YYYYMMDD)
         * @return 파라미터
         */
        public static MultiValueMap<String, String> createParameter(String accntNumber, String accntProductCode, String todayStr) {
            MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
            queryParams.add("CANO", accntNumber);
            queryParams.add("ACNT_PRDT_CD", accntProductCode);
            queryParams.add("PDNO", "");              // 전 종목코드
            queryParams.add("ORD_STRT_DT", todayStr);
            queryParams.add("ORD_END_DT", todayStr);
            queryParams.add("SLL_BUY_DVSN", "02");    // 매도매수구분코드 (02:매수)
            queryParams.add("CCLD_NCCS_DVSN", "01");  // 체결 구분 (01:체결)
            queryParams.add("OVRS_EXCG_CD", "%");     // 해외 거래소 코드. 모의에서는 전체조회("")만 가능. 실전에서는?
            queryParams.add("SORT_SQN", "DS");        // 정렬 순서 (DS:정순)
            queryParams.add("ORD_DT", "");
            queryParams.add("ORD_GNO_BRNO", "");
            queryParams.add("ODNO", "");              // 주문번호
            queryParams.add("CTX_AREA_FK200", "");    // 연속조회검색조건200
            queryParams.add("CTX_AREA_NK200", "");    // 연속조회키200

            return queryParams;
        }
    }
}
