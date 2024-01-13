/*
 © 2023 devspacehub, Inc. All rights reserved.

 name : StockBalanceExternalResDto
 creation : 2023.12.19
 author : Yoonji Moon
 */

package com.devspacehub.ast.domain.my.dto.response;

import com.devspacehub.ast.common.dto.WebClientCommonResDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 주식 잔고 조회 응답 DTO.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class StockBalanceExternalResDto extends WebClientCommonResDto {
    @JsonProperty("rt_cd")
    private String resultCode;
    @JsonProperty("msg_cd")
    private String messageCode;
    @JsonProperty("msg1")
    private String message;


    @JsonProperty("ctx_area_fk100")
    private String ctxAreaFk100;

    @JsonProperty("ctx_area_nk100")
    private String ctxAreaNk100;

    @JsonProperty("output1")
    private List<Output1> output1;

    @JsonProperty("output2")
    private List<Output2> output2;

    @Getter
    @NoArgsConstructor
    public static class Output1 {
        @JsonProperty("pdno")
        private String stockCode;   // 종목번호

        @JsonProperty("prdt_name")
        private String stockName;    // 종목명

        @JsonProperty("trad_dvsn_name")
        private String tradDvsnName;

        @JsonProperty("bfdy_buy_qty")
        private String bfdyBuyQty;

        @JsonProperty("bfdy_sll_qty")
        private String bfdySllQty;

        @JsonProperty("thdt_buyqty")
        private String thdtBuyqty;

        @JsonProperty("thdt_sll_qty")
        private String thdtSllQty;

        @JsonProperty("hldg_qty")
        private String hldgQty;

        @JsonProperty("ord_psbl_qty")
        private String ordPsblQty;

        @JsonProperty("pchs_avg_pric")
        private String pchsAvgPric;

        @JsonProperty("pchs_amt")
        private String pchsAmt;

        @JsonProperty("prpr")
        private String prpr;

        @JsonProperty("evlu_amt")
        private String evluAmt;

        @JsonProperty("evlu_pfls_amt")
        private String evluPflsAmt;

        @JsonProperty("evlu_pfls_rt")
        private String evluPflsRt;

        @JsonProperty("evlu_erng_rt")
        private String evluErngRt;

        @JsonProperty("loan_dt")
        private String loanDt;

        @JsonProperty("loan_amt")
        private String loanAmt;

        @JsonProperty("stln_slng_chgs")
        private String stlnSllngChgs;

        @JsonProperty("expd_dt")
        private String expdDt;

        @JsonProperty("fltt_rt")
        private String flttRt;

        @JsonProperty("bfdy_cprs_icdc")
        private String bfdyCprsIcdc;

        @JsonProperty("item_mgna_rt_name")
        private String itemMgnaRtName;

        @JsonProperty("grta_rt_name")
        private String grtaRtName;

        @JsonProperty("sbst_pric")
        private String sbstPric;

        @JsonProperty("stck_loan_unpr")
        private String stckLoanUnpr;

    }

    @Getter
    @NoArgsConstructor
    public static class Output2 {
        @JsonProperty("dnca_tot_amt")
        private String dncaTotAmt;

        @JsonProperty("nxdy_excc_amt")
        private String nxdyExccAmt;

        @JsonProperty("prvs_rcdl_excc_amt")
        private String prvsRcdlExccAmt;

        @JsonProperty("cma_evlu_amt")
        private String cmaEvluAmt;

        @JsonProperty("bfdy_buy_amt")
        private String bfdyBuyAmt;

        @JsonProperty("thdt_buy_amt")
        private String thdtBuyAmt;

        @JsonProperty("nxdy_auto_rdpt_amt")
        private String nxdyAutoRdptAmt;

        @JsonProperty("bfdy_sll_amt")
        private String bfdySllAmt;

        @JsonProperty("thdt_sll_amt")
        private String thdtSllAmt;

        @JsonProperty("d2_auto_rdpt_amt")
        private String d2AutoRdptAmt;

        @JsonProperty("bfdy_tlex_amt")
        private String bfdyTlexAmt;

        @JsonProperty("thdt_tlex_amt")
        private String thdtTlexAmt;

        @JsonProperty("tot_loan_amt")
        private String totLoanAmt;

        @JsonProperty("scts_evlu_amt")
        private String sctsEvluAmt;

        @JsonProperty("tot_evlu_amt")
        private String totEvluAmt;

        @JsonProperty("nass_amt")
        private String nassAmt;

        @JsonProperty("fncg_gld_auto_rdpt_yn")
        private String fncgGldAutoRdptYn;

        @JsonProperty("pchs_amt_smtl_amt")
        private String pchsAmtSmtlAmt;

        @JsonProperty("evlu_amt_smtl_amt")
        private String evluAmtSmtlAmt;

        @JsonProperty("evlu_pfls_smtl_amt")
        private String evluPflsSmtlAmt;

        @JsonProperty("tot_stln_slng_chgs")
        private String totStlnSllngChgs;

        @JsonProperty("bfdy_tot_asst_evlu_amt")
        private String bfdyTotAsstEvluAmt;

        @JsonProperty("asst_icdc_amt")
        private String asstIcdcAmt;

        @JsonProperty("asst_icdc_erng_rt")
        private String asstIcdcErngRt;
    }

    @JsonIgnore
    @Override
    public boolean isSuccess() {
        return "0".equals(this.resultCode);
    }
}
