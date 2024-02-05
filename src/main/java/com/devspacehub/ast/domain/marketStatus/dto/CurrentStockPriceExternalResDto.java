/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : CurrentStockPriceExternalResDto
 creation : 2024.1.23
 author : Yoonji Moon
 */

package com.devspacehub.ast.domain.marketStatus.dto;

import com.devspacehub.ast.common.dto.WebClientCommonResDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static com.devspacehub.ast.common.constant.CommonConstants.OPENAPI_SUCCESS_RESULT_CODE;
@Getter
@Setter
@NoArgsConstructor
public class CurrentStockPriceExternalResDto extends WebClientCommonResDto {
    @Override
    public boolean isSuccess() {
        return OPENAPI_SUCCESS_RESULT_CODE.equals(resultCode);
    }

    @JsonProperty("output")
    private CurrentStockPriceInfo currentStockPriceInfo;

    @JsonProperty("rt_cd")
    private String resultCode;

    @JsonProperty("msg_cd")
    private String messageCode;

    @JsonProperty("msg1")
    private String msg1;

    @Getter
    public static class CurrentStockPriceInfo {

        @JsonProperty("iscd_stat_cls_code")
        private String iscdStatClsCode;

        @JsonProperty("marg_rate")
        private String margRate;

        @JsonProperty("rprs_mrkt_kor_name")
        private String rprsMrktKorName;

        @JsonProperty("bstp_kor_isnm")
        private String bstpKorIsnm;

        @JsonProperty("temp_stop_yn")
        private String tempStopYn;

        @JsonProperty("oprc_rang_cont_yn")
        private String oprcRangContYn;

        @JsonProperty("clpr_rang_cont_yn")
        private String clprRangContYn;

        @JsonProperty("crdt_able_yn")
        private String crdtAbleYn;

        @JsonProperty("grmn_rate_cls_code")
        private String grmnRateClsCode;

        @JsonProperty("elw_pblc_yn")
        private String elwPblcYn;

        @JsonProperty("stck_prpr")
        private String currentStockPrice;   // 주식 현재가

        @JsonProperty("prdy_vrss")
        private String prdyVrss;

        @JsonProperty("prdy_vrss_sign")
        private String prdyVrssSign;

        @JsonProperty("prdy_ctrt")
        private String prdyCtrt;


        @JsonProperty("acml_tr_pbmn")
        private String acmlTrPbmn;

        @JsonProperty("acml_vol")
        private String accumulationVolume;          // 누적 거래량, Accumulation Volume

        @JsonProperty("prdy_vrss_vol_rate")
        private String prdyVrssVolRate;

        @JsonProperty("stck_oprc")
        private String stckOprc;

        @JsonProperty("stck_hgpr")
        private String stckHgpr;

        @JsonProperty("stck_lwpr")
        private String stckLwpr;

        @JsonProperty("stck_mxpr")
        private String stckMxpr;

        @JsonProperty("stck_llam")
        private String stckLlam;

        @JsonProperty("stck_sdpr")
        private String stckSdpr;

        @JsonProperty("wghn_avrg_stck_prc")
        private String wghnAvrgStckPrc;

        @JsonProperty("hts_frgn_ehrt")
        private String htsFrgnEhrt;

        @JsonProperty("frgn_ntby_qty")
        private String frgnNtbyQty;

        @JsonProperty("pgtr_ntby_qty")
        private String pgtrNtbyQty;

        @JsonProperty("pvt_scnd_dmrs_prc")
        private String pvtScndDmrsPrc;

        @JsonProperty("pvt_frst_dmrs_prc")
        private String pvtFrstDmrsPrc;

        @JsonProperty("pvt_pont_val")
        private String pvtPontVal;

        @JsonProperty("pvt_frst_dmsp_prc")
        private String pvtFrstDmspPrc;

        @JsonProperty("pvt_scnd_dmsp_prc")
        private String pvtScndDmspPrc;

        @JsonProperty("dmrs_val")
        private String dmrsVal;

        @JsonProperty("dmsp_val")
        private String dmspVal;

        @JsonProperty("cpfn")
        private String cpfn;

        @JsonProperty("rstc_wdth_prc")
        private String rstcWdthPrc;

        @JsonProperty("stck_fcam")
        private String stckFcam;

        @JsonProperty("stck_sspr")
        private String stckSspr;

        @JsonProperty("aspr_unit")
        private String asprUnit;

        @JsonProperty("hts_deal_qty_unit_val")
        private String htsDealQtyUnitVal;

        @JsonProperty("lstn_stcn")
        private String lstnStcn;

        @JsonProperty("hts_avls")
        private String htsMarketCapitalization; // HTS 시가 총액, market capitalization

        @JsonProperty("per")
        private String per;                     // 주가 수익 비율, Price Earnings Ratio

        @JsonProperty("pbr")
        private String pbr;                     // 주가 순 자산 비율, Price to Book Ratio

        @JsonProperty("stac_month")
        private String stacMonth;

        @JsonProperty("vol_tnrt")
        private String volTnrt;

        @JsonProperty("eps")
        private String eps;

        @JsonProperty("bps")
        private String bps;

        @JsonProperty("d250_hgpr")
        private String d250Hgpr;

        @JsonProperty("d250_hgpr_date")
        private String d250HgprDate;

        @JsonProperty("d250_hgpr_vrss_prpr_rate")
        private String d250HgprVrssPrprRate;

        @JsonProperty("d250_lwpr")
        private String d250Lwpr;

        @JsonProperty("d250_lwpr_date")
        private String d250LwprDate;

        @JsonProperty("d250_lwpr_vrss_prpr_rate")
        private String d250LwprVrssPrprRate;

        @JsonProperty("stck_dryy_hgpr")
        private String stckDryyHgpr;

        @JsonProperty("dryy_hgpr_vrss_prpr_rate")
        private String dryyHgprVrssPrprRate;

        @JsonProperty("dryy_hgpr_date")
        private String dryyHgprDate;

        @JsonProperty("stck_dryy_lwpr")
        private String stckDryyLwpr;

        @JsonProperty("dryy_lwpr_vrss_prpr_rate")
        private String dryyLwprVrssPrprRate;

        @JsonProperty("dryy_lwpr_date")
        private String dryyLwprDate;

        @JsonProperty("w52_hgpr")
        private String w52Hgpr;

        @JsonProperty("w52_hgpr_vrss_prpr_ctrt")
        private String w52HgprVrssPrprCtrt;

        @JsonProperty("w52_hgpr_date")
        private String w52HgprDate;

        @JsonProperty("w52_lwpr")
        private String w52Lwpr;

        @JsonProperty("w52_lwpr_vrss_prpr_ctrt")
        private String w52LwprVrssPrprCtrt;

        @JsonProperty("w52_lwpr_date")
        private String w52LwprDate;

        @JsonProperty("whol_loan_rmnd_rate")
        private String wholLoanRmndRate;

        @JsonProperty("ssts_yn")
        private String sstsYn;

        @JsonProperty("stck_shrn_iscd")
        private String stckShrnIscd;

        @JsonProperty("fcam_cnnm")
        private String fcamCnnm;

        @JsonProperty("cpfn_cnnm")
        private String cpfnCnnm;

        @JsonProperty("frgn_hldn_qty")
        private String frgnHldnQty;

        @JsonProperty("vi_cls_code")
        private String viClsCode;

        @JsonProperty("ovtm_vi_cls_code")
        private String ovtmViClsCode;

        @JsonProperty("last_ssts_cntg_qty")
        private String lastSstsCntgQty;

        @JsonProperty("invt_caful_yn")
        private String invtCarefulYn;    // 투자유의여부(N)

        @JsonProperty("mrkt_warn_cls_code")
        private String mrktWarnClsCode;

        @JsonProperty("short_over_yn")
        private String shortOverYn;     // 단기과열여부(N)

        @JsonProperty("sltr_yn")
        private String delistingYn;          // 정리매매여부(N)

    }

}