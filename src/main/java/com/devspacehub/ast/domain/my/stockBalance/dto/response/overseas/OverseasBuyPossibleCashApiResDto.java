/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : OverseasBuyPossibleCashApiResDto
 creation : 2024.5.31
 author : Yoonji Moon
 */

package com.devspacehub.ast.domain.my.stockBalance.dto.response.overseas;

import com.devspacehub.ast.common.dto.WebClientCommonResDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Objects;

import static com.devspacehub.ast.common.constant.CommonConstants.OPENAPI_SUCCESS_RESULT_CODE;

/**
 * 해외 주식 매수가능 금액 조회 KIS Api 응답 Dto.
 */
@NoArgsConstructor
@Getter
public class OverseasBuyPossibleCashApiResDto extends WebClientCommonResDto {
    @JsonProperty("output")
    private ResultDetail resultDetail;

    /**
     * @return 응답이 성공인지 체크한다.
     */
    @Override
    public boolean isSuccess() {
        return !Objects.isNull(resultDetail) && OPENAPI_SUCCESS_RESULT_CODE.equals(this.resultCode);
    }

    @JsonIgnore
    @Override
    public String toString() {
        return String.format("detail(%s), status(%s)", Objects.isNull(resultDetail) ? "null" : resultDetail.toString(), super.toString());
    }

    @Getter
    public static class ResultDetail {

        @JsonProperty("echm_af_ord_psbl_amt")
        private String echmAfOrdPsblAmt;

        @JsonProperty("echm_af_ord_psbl_qty")
        private String echmAfOrdPsblQty;

        @JsonProperty("exrt")
        private String exrt;

        @JsonProperty("frcr_ord_psbl_amt1")
        private String frcrOrdPsblAmt1;

        @JsonProperty("max_ord_psbl_qty")
        private String maxOrdPsblQty;

        @JsonProperty("ord_psbl_frcr_amt")
        private String ordPsblFrcrAmt;

        @JsonProperty("ord_psbl_qty")
        private String ordPsblQty;

        @JsonProperty("ovrs_max_ord_psbl_qty")
        private String ovrsMaxOrdPsblQty;

        @JsonProperty("ovrs_ord_psbl_amt")
        private BigDecimal orderPossibleCash;

        @JsonProperty("sll_ruse_psbl_amt")
        private String sllRusePsblAmt;

        @JsonProperty("tr_crcy_cd")
        private String trCrcyCd;


        @JsonIgnore
        @Override
        public String toString() {
            return String.format("orderPossibleCash = %s", orderPossibleCash);
        }
    }
}