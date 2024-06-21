/*
 © 2023 devspacehub, Inc. All rights reserved.

 name : BuyPossibleCashApiResDto
 creation : 2023.12.20
 author : Yoonji Moon
 */

package com.devspacehub.ast.domain.my.stockBalance.dto.response;

import com.devspacehub.ast.common.dto.WebClientCommonResDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

import static com.devspacehub.ast.common.constant.CommonConstants.OPENAPI_SUCCESS_RESULT_CODE;

/**
 * 매수 가능 조회 응답 dto.
 */
@NoArgsConstructor
@Getter
public class BuyPossibleCashApiResDto extends WebClientCommonResDto {
    @JsonProperty("output")
    private Output output;

    /**
     * 응답 body (실패 시 null)
     */
    @Getter
    public static class Output {
        @JsonProperty("ord_psbl_cash")
        private String orderPossibleCash;   // 주문가능현금

        @JsonProperty("ord_psbl_sbst")
        private String ordPsblSbst; // 대용가격(substitute price of securities)

        @JsonProperty("ruse_psbl_amt")
        private String rusePsblAmount;  // 재사용가능금액

        @JsonProperty("fund_rpch_chgs")
        private String fundRpchChgs;    // 펀드환매대금

        @JsonProperty("psbl_qty_calc_unpr")
        private String psblQtyCalcUnpr; // 가능수량계산단가

        @JsonProperty("nrcvb_buy_amt")
        private String nrcvbBuyAmount;  // 미수없는매수금액

        @JsonProperty("nrcvb_buy_qty")
        private String nrcvbBuyQuantity;  // 미수없는매수수량

        @JsonProperty("max_buy_amt")
        private String maxBuyAmount;    // 최대매수금액

        @JsonProperty("max_buy_qty")
        private String maxBuyQuantity;  // 최대매수수량

        @JsonProperty("cma_evlu_amt")
        private String cmaEvluAmt;  // CMA평가금액

        @JsonProperty("ovrs_re_use_amt_wcrc")
        private String ovrsReUseAmtWcrc;    // 해외재사용금액원화

        @JsonProperty("ord_psbl_frcr_amt_wcrc")
        private String ordPsblFrcrAmtWcrc;  // 주문가능외화금액원화
    }

    @JsonIgnore
    @Override
    public boolean isSuccess() {
        return !Objects.isNull(output) && OPENAPI_SUCCESS_RESULT_CODE.equals(this.resultCode);
    }

}
