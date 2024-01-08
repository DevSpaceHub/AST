/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : DomesticStockOrderExternalResDto
 creation : 2024.1.8
 author : Yoonji Moon
 */

/*
 © 2023 devspacehub, Inc. All rights reserved.

 name : DomesticStockBuyOrderExternalReqDto
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

/**
 * 국내 주식 주문(매수/매도) 응답 DTO.
 */
@NoArgsConstructor
@Getter
@Setter
public class DomesticStockOrderExternalResDto extends WebClientCommonResDto {
    @JsonProperty("rt_cd")
    private String resultCode;
    @JsonProperty("msg_cd")
    private String messageCode;
    @JsonProperty("msg1")
    private String message;

    @JsonProperty("output")
    private Output output;  // TODO Array 필드라고 돼있긴 한데

    /**
     * 응답 body (실패 시 null)
     */
    @Getter
    public static class Output {
        @JsonProperty("KRX_FWDG_ORD_ORGNO")
        private String krxFwdgOrdOrgno; // 주문시 한국투자증권 시스템에서 지정된 영업점코드

        @JsonProperty("ODNO")
        private String orderNumber;

        @JsonProperty("ORD_TMD")
        private String orderDateTime;
    }

    @JsonIgnore
    @Override
    public boolean isSuccess() {
        return "0".equals(this.resultCode);
    }
}
