/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : OverseasOrderConclusionFindExternalResDto
 creation : 2024.8.15
 author : Yoonji Moon
 */

package com.devspacehub.ast.domain.my.dto.orderConclusion;

import com.devspacehub.ast.common.dto.WebClientCommonResDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.micrometer.common.util.StringUtils;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Objects;

import static com.devspacehub.ast.common.constant.CommonConstants.OPENAPI_SUCCESS_RESULT_CODE;

/**
 * 해외 주식 주문체결내역 조회 응답 DTO.
 */
@Setter
@Getter
public class OverseasOrderConclusionFindExternalResDto extends WebClientCommonResDto {

    @JsonProperty("ctx_area_nk200")
    private String ctxAreaNk200;

    @JsonProperty("ctx_area_fk200")
    private String ctxAreaFk200;

    @JsonProperty("output")
    private List<Output> output;


    /**
     * API 응답이 성공인지 확인한다.
     * @return 성공 응답이면 true, 실패 응답이면 false
     */
    @Override
    public boolean isSuccess() {
        return !Objects.isNull(output) && OPENAPI_SUCCESS_RESULT_CODE.equals(this.resultCode);
    }

    /**
     * 응답 필드 ctxAreaFk200 Getter
     * @return 값의 양옆 공백 제거된 숫자
     */
    public String getCtxAreaFk200() {
        return StringUtils.isBlank(this.ctxAreaFk200) ? "" : this.ctxAreaFk200.trim();
    }

    /**
     * 응답 필드 ctxAreaNk200 Getter
     * @return 값의 양옆 공백 제거된 숫자
     */
    public String getCtxAreaNk200() {
        return StringUtils.isBlank(this.ctxAreaNk200) ? "" : this.ctxAreaNk200.trim();
    }

    @Getter
    public static class Output {

        @JsonProperty("ord_dt")
        private String ordDt;

        @JsonProperty("ord_gno_brno")
        private String ordGnoBrno;

        @JsonProperty("odno")
        private String orderNumber;

        @JsonProperty("orgn_odno")
        private String orgnOdno;

        @JsonProperty("sll_buy_dvsn_cd")
        private String sllBuyDvsnCd;

        @JsonProperty("sll_buy_dvsn_cd_name")
        private String sllBuyDvsnCdName;

        @JsonProperty("rvse_cncl_dvsn")
        private String rvseCnclDvsn;

        @JsonProperty("rvse_cncl_dvsn_name")
        private String rvseCnclDvsnName;

        @JsonProperty("pdno")
        private String itemCode;    // 종목 코드

        @JsonProperty("prdt_name")
        private String itemNameKor;    // 국문 종목명

        @JsonProperty("ft_ord_qty")
        private String orderQuantity;        // 주문 수량

        @JsonProperty("ft_ord_unpr3")
        private String orderPrice;          // 주문 가격

        @JsonProperty("ft_ccld_qty")
        private String totalConcludedQuantity;  // 체결 수량

        @JsonProperty("ft_ccld_unpr3")
        private String ftCcldUnpr3;      // 단가

        @JsonProperty("ft_ccld_amt3")
        private String totalConcludedPrice;       // 체결된 총 금액 (단가 * 체결 수량)

        @JsonProperty("nccs_qty")
        private String nccsQty;

        @JsonProperty("prcs_stat_name")
        private String prcsStatName;

        @JsonProperty("rjct_rson")
        private String rjctRson;

        @JsonProperty("rjct_rson_name")
        private String rjctRsonName;

        @JsonProperty("ord_tmd")
        private String orderTime;

        @JsonProperty("tr_mket_name")
        private String trMketName;

        @JsonProperty("tr_natn")
        private String trNatn;

        @JsonProperty("tr_natn_name")
        private String trNatnName;

        @JsonProperty("ovrs_excg_cd")
        private String ovrsExcgCd;

        @JsonProperty("tr_crcy_cd")
        private String trCrcyCd;

        @JsonProperty("dmst_ord_dt")
        private String dmstOrdDt;

        @JsonProperty("thco_ord_tmd")
        private String thcoOrdTmd;

        @JsonProperty("loan_type_cd")
        private String loanTypeCd;

        @JsonProperty("loan_dt")
        private String loanDt;

        @JsonProperty("mdia_dvsn_name")
        private String mdiaDvsnName;
    }
}
