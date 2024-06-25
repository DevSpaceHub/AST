/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : OverseasStockConditionSearchResDto
 creation : 2024.5.26
 author : Yoonji Moon
 */

package com.devspacehub.ast.domain.marketStatus.dto;

import com.devspacehub.ast.common.constant.ExchangeCode;
import com.devspacehub.ast.common.dto.WebClientCommonResDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.micrometer.common.util.StringUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

import static com.devspacehub.ast.common.constant.CommonConstants.OPENAPI_SUCCESS_RESULT_CODE;

/**
 * 해외 주식 조건 검색 조회 OpenApi 응답 Dto.
 */
@Getter
@Setter
public class OverseasStockConditionSearchResDto extends WebClientCommonResDto {
    @JsonProperty("output1")
    private Output output;

    @JsonProperty("output2")
    private List<ResultDetail> resultDetails;

    /**
     * API 응답이 성공인지 확인하여 반환한다.<br>
     * 잘못된 요청에도 Result Code는 "0"으로 준다.
     * @return output1의 stat 값이 빈 값이 아니고 output2 갯수가 0이 아니고 resultCode가 0이면 True를 반환한다.
     */
    @JsonIgnore
    @Override
    public boolean isSuccess() {
        return !Objects.isNull(output) && StringUtils.isNotBlank(output.getStat()) && !resultDetails.isEmpty()
                && OPENAPI_SUCCESS_RESULT_CODE.equals(this.resultCode) && !output.getStat().equals("휴장");
    }

    @Setter
    @Getter
    @NoArgsConstructor
    public static class Output {
        @JsonProperty("zdiv")
        private String zdiv;

        @JsonProperty("stat")
        private String stat;

        @JsonProperty("crec")
        private String crec;

        @JsonProperty("trec")
        private String trec;

        @JsonProperty("nrec")
        private String nrec;
    }

    @Setter
    @Getter
    @NoArgsConstructor
    public static class ResultDetail {
        @JsonProperty("rsym")
        private String rsym;

        @JsonProperty("excd")
        private ExchangeCode exchangeCode;

        @JsonProperty("symb")
        private String itemCode;

        @JsonProperty("name")
        private String itemNameKor;

        @JsonProperty("last")
        private BigDecimal currentPrice;

        @JsonProperty("sign")
        private String sign;

        @JsonProperty("diff")
        private String diff;

        @JsonProperty("rate")
        private String rate;

        @JsonProperty("tvol")
        private String tvol;

        @JsonProperty("popen")
        private String popen;

        @JsonProperty("phigh")
        private String phigh;

        @JsonProperty("plow")
        private String plow;

        @JsonProperty("valx")
        private String valx;

        @JsonProperty("shar")
        private String shar;

        @JsonProperty("avol")
        private String avol;

        @JsonProperty("eps")
        private String eps;

        @JsonProperty("per")
        private String per;

        @JsonProperty("rank")
        private String rank;

        @JsonProperty("ename")
        private String ename;

        @JsonProperty("e_ordyn")
        private String eOrdyn;
    }
}
