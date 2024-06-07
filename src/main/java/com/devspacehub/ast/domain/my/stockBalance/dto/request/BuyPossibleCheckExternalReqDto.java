/*
 © 2023 devspacehub, Inc. All rights reserved.

 name : BuyPossibleCheckExternalReqDto
 creation : 2023.12.20
 author : Yoonji Moon
 */

package com.devspacehub.ast.domain.my.stockBalance.dto.request;

import lombok.Builder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.math.BigDecimal;
import java.util.function.Consumer;

import static com.devspacehub.ast.common.constant.YesNoStatus.NO;

/**
 * 매수 가능 조회 요청 DTO.
 */
@Builder
public class BuyPossibleCheckExternalReqDto {

    public static MultiValueMap<String, String> createParameter(String accntNumber, String accntProductCode, String itemCode, BigDecimal orderPrice, String orderDivision) {
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.add("CANO", accntNumber);
        queryParams.add("ACNT_PRDT_CD", accntProductCode);
        queryParams.add("PDNO", itemCode);
        queryParams.add("ORD_UNPR", orderPrice.toString());
        queryParams.add("ORD_DVSN", orderDivision);
        queryParams.add("CMA_EVLU_AMT_ICLD_YN", NO.getCode());  // CMA 평가 금액 포함 여부
        queryParams.add("OVRS_ICLD_YN", NO.getCode());    // 해외 포함 여부

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