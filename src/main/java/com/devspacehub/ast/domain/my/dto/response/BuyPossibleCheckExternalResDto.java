/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : BuyPossibleCheckExternalResDto
 creation : 2024.1.8
 author : Yoonji Moon
 */

/*
 © 2023 devspacehub, Inc. All rights reserved.

 name : BuyPossibleCheckExternalResDto
 creation : 2023.12.20
 author : Yoonji Moon
 */

package com.devspacehub.ast.domain.my.dto.response;

import com.devspacehub.ast.common.dto.WebClientCommonResDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 매수 가능 조회 응답 dto.
 */
@NoArgsConstructor
@Getter
public class BuyPossibleCheckExternalResDto extends WebClientCommonResDto {
    @JsonProperty("rt_cd")
    private String resultCode;
    @JsonProperty("msg_cd")
    private String messageCode;
    @JsonProperty("msg1")
    private String message;

    @JsonProperty("output")
    private Output output;

    /**
     * 응답 body (실패 시 null)
     */
    @Getter
    public static class Output {
        @JsonProperty("ord_psbl_cash")
        private String orderPossibleCash;

        @JsonProperty("ord_psbl_sbst")
        private String ordPsblSbst;

        @JsonProperty("ruse_psbl_amt")
        private String rusePsblAmt;

        @JsonProperty("fund_rpch_chgs")
        private String fundRpchChgs;

        @JsonProperty("psbl_qty_calc_unpr")
        private String psblQtyCalcUnpr;

        @JsonProperty("nrcvb_buy_amt")
        private String nrcvbBuyAmount;

        @JsonProperty("nrcvb_buy_qty")
        private String nrcvbBuyQuantity;

        @JsonProperty("max_buy_amt")
        private String maxBuyAmount;

        @JsonProperty("max_buy_qty")
        private String maxBuyQuantity;

        @JsonProperty("cma_evlu_amt")
        private String cmaEvluAmt;

        @JsonProperty("ovrs_re_use_amt_wcrc")
        private String ovrsReUseAmtWcrc;

        @JsonProperty("ord_psbl_frcr_amt_wcrc")
        private String ordPsblFrcrAmtWcrc;
    }

    @JsonIgnore
    @Override
    public boolean isSuccess() {
        return "0".equals(this.resultCode);
    }

}
