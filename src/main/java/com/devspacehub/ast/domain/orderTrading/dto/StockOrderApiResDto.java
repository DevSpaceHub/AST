/*
 © 2023 devspacehub, Inc. All rights reserved.

 name : StockOrderExternalResDto
 creation : 2023.12.21
 author : Yoonji Moon
 */

package com.devspacehub.ast.domain.orderTrading.dto;

import com.devspacehub.ast.common.dto.WebClientCommonResDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

import static com.devspacehub.ast.common.constant.CommonConstants.OPENAPI_SUCCESS_RESULT_CODE;

/**
 * 해외/국내 주식 주문(매수/매도) 응답 DTO.
 */
@NoArgsConstructor
@Getter
@Setter
public class StockOrderApiResDto extends WebClientCommonResDto {
    @JsonProperty("output")
    private Output output;

    /**
     * 응답 body (실패 시 null)
     */
    @Getter
    @Setter
    @NoArgsConstructor
    public static class Output {
        @JsonProperty("KRX_FWDG_ORD_ORGNO")
        private String krxFwdgOrdOrgno; // 주문시 한국투자증권 시스템에서 지정된 영업점코드

        @JsonProperty("ODNO")
        private String orderNumber;

        @JsonProperty("ORD_TMD")
        private String orderTime;
    }

    @JsonIgnore
    @Override
    public boolean isSuccess() {
        return !Objects.isNull(output) && OPENAPI_SUCCESS_RESULT_CODE.equals(this.resultCode);
    }
}
