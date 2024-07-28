/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : OrderConclusionFindExternalResDto
 creation : 2024.4.19
 author : Yoonji Moon
 */

package com.devspacehub.ast.domain.my.dto.orderConclusion;

import com.devspacehub.ast.common.dto.WebClientCommonResDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Objects;

import static com.devspacehub.ast.common.constant.CommonConstants.OPENAPI_SUCCESS_RESULT_CODE;

/**
 * 주식 일별 주문 체결 조회 응답 DTO.
 */
@Setter
public class OrderConclusionFindExternalResDto extends WebClientCommonResDto {

    @JsonProperty("ctx_area_fk100")
    private String ctxAreaFk100;

    @JsonProperty("ctx_area_nk100")
    private String ctxAreaNk100;
    @Getter
    @JsonProperty("output1")
    private List<Output1> output1;

    @JsonProperty("output2")
    private Output2 output2;

    @Builder
    @Getter
    public static class Output1 {
        @JsonProperty("ord_dt")
        private String ordDt;

        @JsonProperty("ord_gno_brno")
        private String ordGnoBrno;

        @JsonProperty("odno")
        private String orderNumber;

        @JsonProperty("orgn_odno")
        private String orgnOdno;

        @JsonProperty("ord_dvsn_name")
        private String ordDvsnName;

        @JsonProperty("sll_buy_dvsn_cd")
        private String sllBuyDvsnCd;

        @JsonProperty("sll_buy_dvsn_cd_name")
        private String sllBuyDvsnCdName;

        @JsonProperty("pdno")
        private String itemCode;

        @JsonProperty("prdt_name")
        private String itemNameKor;

        @JsonProperty("ord_qty")
        private String orderQuantity;

        @JsonProperty("ord_unpr")
        private String orderPrice;

        @JsonProperty("ord_tmd")
        private String orderTime;

        @JsonProperty("tot_ccld_qty")
        private String totalConcludedQuantity;

        @JsonProperty("avg_prvs")
        private String avgPrvs;

        @JsonProperty("cncl_yn")
        private String cnclYn;

        @JsonProperty("tot_ccld_amt")
        private String totalConcludedPrice;

        @JsonProperty("loan_dt")
        private String loanDt;

        @JsonProperty("ordr_empno")
        private String ordrEmpno;

        @JsonProperty("ord_dvsn_cd")
        private String ordDvsnCd;

        @JsonProperty("cncl_cfrm_qty")
        private String cnclCfrmQty;

        @JsonProperty("rmn_qty")
        private String rmnQty;

        @JsonProperty("rjct_qty")
        private String rjctQty;

        @JsonProperty("ccld_cndt_name")
        private String ccldCndtName;

        @JsonProperty("inqr_ip_addr")
        private String inqrIpAddr;

        @JsonProperty("cpbc_ordp_ord_rcit_dvsn_cd")
        private String cpbcOrdpOrdRcitDvsnCd;

        @JsonProperty("cpbc_ordp_infm_mthd_dvsn_cd")
        private String cpbcOrdpInfmMthdDvsnCd;

        @JsonProperty("infm_tmd")
        private String infmTmd;

        @JsonProperty("ctac_tlno")
        private String ctacTlno;

        @JsonProperty("prdt_type_cd")
        private String prdtTypeCd;

        @JsonProperty("excg_dvsn_cd")
        private String excgDvsnCd;

        @JsonProperty("cpbc_ordp_mtrl_dvsn_cd")
        private String cpbcOrdpMtrlDvsnCd;

        @JsonProperty("ord_orgno")
        private String ordOrgno;

        @JsonProperty("rsvn_ord_end_dt")
        private String rsvnOrdEndDt;
    }
    @NoArgsConstructor
    public static class Output2 {
        @JsonProperty("tot_ord_qty")
        private String totOrdQty;

        @JsonProperty("tot_ccld_qty")
        private String totCcldQty;

        @JsonProperty("tot_ccld_amt")
        private String totCcldAmt;

        @JsonProperty("prsm_tlex_smtl")
        private String prsmTlexSmtl;

        @JsonProperty("pchs_avg_pric")
        private String pchsAvgPric;
    }

    @JsonIgnore
    @Override
    public boolean isSuccess() {
        return !Objects.isNull(output1) && !Objects.isNull(output2) && OPENAPI_SUCCESS_RESULT_CODE.equals(this.resultCode);
    }
}
