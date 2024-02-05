/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : StockBalanceExternalReqDto
 creation : 2024.1.13
 author : Yoonji Moon
 */

package com.devspacehub.ast.domain.my.dto.request;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.function.Consumer;

import static com.devspacehub.ast.common.constant.YesNoStatus.NO;

/**
 * 주식 잔고 조회 요청 DTO.
 */
public class StockBalanceExternalReqDto {
    /**
     * Create parameter multi value map.
     * @param accntNumber      the accnt number
     * @param accntProductCode the accnt product code
     * @return the multi value map
     */
    public static MultiValueMap<String, String> createParameter(String accntNumber, String accntProductCode) {
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.add("CANO", accntNumber);
        queryParams.add("ACNT_PRDT_CD", accntProductCode);
        queryParams.add("AFHR_FLPR_YN", NO.getCode()); // 시간외 단일가 여부
        queryParams.add("OFL_YN", NO.getCode());     // 오프라인 여부
        queryParams.add("INQR_DVSN", "02"); // 01:대출일별, 02:종목별
        queryParams.add("UNPR_DVSN", "01");  // 단가구분
        queryParams.add("FUND_STTL_ICLD_YN", NO.getCode());  // 펀드결제분포함여부
        queryParams.add("FNCG_AMT_AUTO_RDPT_YN", NO.getCode());  // 융자금액자동상환여부
        queryParams.add("PRCS_DVSN", "00");  // 처리구분(00: 전일매매포함, 01: 전일매매미포함)
        queryParams.add("CTX_AREA_FK100", ""); // 연속조회검색조건100
        queryParams.add("CTX_AREA_NK100", ""); // 연속조회키100

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
