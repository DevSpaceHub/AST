/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : DomStockInfoTradingVolumeRankingExternalResDto
 creation : 2024.1.15
 author : Yoonji Moon
 */

package com.devspacehub.ast.domain.marketStatus.dto;
import com.devspacehub.ast.common.dto.WebClientCommonResDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;
import java.util.Objects;

import static com.devspacehub.ast.common.constant.CommonConstants.OPENAPI_SUCCESS_RESULT_CODE;

/**
 * 국내 주식 거래량 순위 조회 응답 DTO.
 */
@Getter
@Setter
public class DomStockTradingVolumeRankingExternalResDto extends WebClientCommonResDto {
    @JsonProperty("output")
    private List<StockInfo> stockInfos;

    @JsonIgnore
    @Override
    public boolean isSuccess() {
        return !Objects.isNull(stockInfos) && OPENAPI_SUCCESS_RESULT_CODE.equals(this.resultCode);
    }

    @Setter
    @Getter
    @NoArgsConstructor
    public static class StockInfo {
        @JsonProperty("hts_kor_isnm")
        private String htsStockNameKor;      // 한글 종목명

        @JsonProperty("mksc_shrn_iscd")
        private String itemCode;      // 유가증권 단축 종목코드

        @JsonProperty("data_rank")
        private String dataRank;

        @JsonProperty("stck_prpr")
        private String stckPrpr;

        @JsonProperty("prdy_vrss_sign")
        private String prdyVrssSign;

        @JsonProperty("prdy_vrss")
        private String prdyVrss;

        @JsonProperty("prdy_ctrt")
        private String prdyCtrt;

        @JsonProperty("acml_vol")
        private String acmlVol;

        @JsonProperty("prdy_vol")
        private String prdyVol;

        @JsonProperty("lstn_stcn")
        private String lstnStcn;

        @JsonProperty("avrg_vol")
        private String avrgVol;

        @JsonProperty("n_befr_clpr_vrss_prpr_rate")
        private String nBefrClprVrssPrprRate;

        @JsonProperty("vol_inrt")
        private String volInrt;

        @JsonProperty("vol_tnrt")
        private String volTnrt;

        @JsonProperty("nday_vol_tnrt")
        private String ndayVolTnrt;

        @JsonProperty("avrg_tr_pbmn")
        private String avrgTrPbmn;

        @JsonProperty("tr_pbmn_tnrt")
        private String trPbmnTnrt;

        @JsonProperty("nday_tr_pbmn_tnrt")
        private String ndayTrPbmnTnrt;

        @JsonProperty("acml_tr_pbmn")
        private String acmlTrPbmn;

    }
}
