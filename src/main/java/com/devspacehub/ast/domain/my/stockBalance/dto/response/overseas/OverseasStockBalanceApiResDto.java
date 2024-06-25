/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : OverseasStockBalanceApiResDto
 creation : 2024.6.12
 author : Yoonji Moon
 */

package com.devspacehub.ast.domain.my.stockBalance.dto.response.overseas;

import com.devspacehub.ast.common.dto.WebClientCommonResDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

import static com.devspacehub.ast.common.constant.CommonConstants.OPENAPI_SUCCESS_RESULT_CODE;

/**
 * 해외 주식 잔고 조회 Api 응답 DTO.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class OverseasStockBalanceApiResDto extends WebClientCommonResDto {

    @JsonProperty("ctx_area_fk200")
    private String ctxAreaFk200;

    @JsonProperty("ctx_area_nk200")
    private String ctxAreaNk200;

    @JsonProperty("output1")
    private List<MyStockBalance> myStockBalance;

    @JsonProperty("output2")
    private Output2 output2;

    /**
     * @return output1, 2가 빈 값이 아니고 결과 코드가 0인 경우 True를 반환한다.
     */
    @Override
    public boolean isSuccess() {
        return !Objects.isNull(output2) && !Objects.isNull(myStockBalance) && OPENAPI_SUCCESS_RESULT_CODE.equals(this.resultCode);
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class MyStockBalance {

        @JsonProperty("cano")
        private String cano;

        @JsonProperty("acnt_prdt_cd")
        private String acntPrdtCd;

        @JsonProperty("prdt_type_cd")
        private String prdtTypeCd;

        @JsonProperty("ovrs_pdno")
        private String itemCode;

        @JsonProperty("ovrs_item_name")
        private String stockName;

        @JsonProperty("frcr_evlu_pfls_amt")
        private String frcrEvluPflsAmt;

        @JsonProperty("evlu_pfls_rt")
        private Float evaluateProfitLossRate;   // 평가손익을 기준으로 한 수익률

        @JsonProperty("pchs_avg_pric")
        private String pchsAvgPric;

        @JsonProperty("ovrs_cblc_qty")
        private String ovrsCblcQty;

        @JsonProperty("ord_psbl_qty")
        private int orderPossibleQuantity;   // 매도 가능한 주문 수량

        @JsonProperty("frcr_pchs_amt1")
        private String frcrPchsAmt1;

        @JsonProperty("ovrs_stck_evlu_amt")
        private String ovrsStckEvluAmt;

        @JsonProperty("now_pric2")
        private BigDecimal currentPrice;

        @JsonProperty("tr_crcy_cd")
        private String trCrcyCd;

        @JsonProperty("ovrs_excg_cd")
        private String exchangeCode;

        @JsonProperty("loan_type_cd")
        private String loanTypeCd;

        @JsonProperty("loan_dt")
        private String loanDt;

        @JsonProperty("expd_dt")
        private String expdDt;
    }

    public static class Output2 {

        @JsonProperty("frcr_pchs_amt1")
        private String frcrPchsAmt1;

        @JsonProperty("ovrs_rlzt_pfls_amt")
        private String ovrsRlztPflsAmt;

        @JsonProperty("ovrs_tot_pfls")
        private String ovrsTotPfls;

        @JsonProperty("rlzt_erng_rt")
        private String rlztErngRt;

        @JsonProperty("tot_evlu_pfls_amt")
        private String totEvluPflsAmt;

        @JsonProperty("tot_pftrt")
        private String totPftrt;

        @JsonProperty("frcr_buy_amt_smtl1")
        private String frcrBuyAmtSmtl1;

        @JsonProperty("ovrs_rlzt_pfls_amt2")
        private String ovrsRlztPflsAmt2;

        @JsonProperty("frcr_buy_amt_smtl2")
        private String frcrBuyAmtSmtl2;
    }
}
