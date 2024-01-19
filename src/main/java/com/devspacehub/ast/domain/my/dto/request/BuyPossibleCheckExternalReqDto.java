/*
 © 2023 devspacehub, Inc. All rights reserved.

 name : BuyPossibleCheckExternalReqDto
 creation : 2023.12.20
 author : Yoonji Moon
 */

package com.devspacehub.ast.domain.my.dto.request;

import com.devspacehub.ast.common.dto.WebClientCommonReqDto;
import lombok.Builder;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static com.devspacehub.ast.common.constant.YesNoStatus.NO;

/**
 * 매수 가능 조회 요청 DTO.
 */
@Builder
public class BuyPossibleCheckExternalReqDto extends WebClientCommonReqDto {

    public static MultiValueMap<String, String> createParameter(String accntNumber, String accntProductCode, String stockCode, Integer orderPrice, String orderDivision) {
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.add("CANO", accntNumber);
        queryParams.add("ACNT_PRDT_CD", accntProductCode);
        queryParams.add("PDNO", stockCode);
        queryParams.add("ORD_UNPR", String.valueOf(orderPrice));
        queryParams.add("ORD_DVSN", orderDivision);
        queryParams.add("CMA_EVLU_AMT_ICLD_YN", NO.getCode());  // CMA 평가 금액 포함 여부
        queryParams.add("OVRS_ICLD_YN", NO.getCode());    // 해외 포함 여부

        return queryParams;
    }
}