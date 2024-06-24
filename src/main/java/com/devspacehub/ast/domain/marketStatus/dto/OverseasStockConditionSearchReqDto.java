/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : OverseasStockConditionSearchReqDto
 creation : 2024.5.26
 author : Yoonji Moon
 */

package com.devspacehub.ast.domain.marketStatus.dto;

import com.devspacehub.ast.common.constant.ExchangeCode;
import com.devspacehub.ast.common.constant.YesNoStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.function.Consumer;

/**
 * 해외 주식 조건검색 조회 API Request Dto.
 */
@AllArgsConstructor
public class OverseasStockConditionSearchReqDto {
    private ExchangeCode exchangeCode;
    private String minTradingVolume;
    private String maxTradingVolume;
    private String minMarketCapital;
    private String maxMarketCapital;
    private String minPER;
    private String maxPER;

    @Builder
    private OverseasStockConditionSearchReqDto(ExchangeCode exchangeCode, String minTradingVolume, String minMarketCapital,
                                               String minPER, String maxPER) {
        this.exchangeCode = exchangeCode;
        this.minTradingVolume = minTradingVolume;
        this.maxTradingVolume = "9999999999";
        this.minMarketCapital = minMarketCapital;
        this.maxMarketCapital = "99999999999999999";
        this.minPER = minPER;
        this.maxPER = maxPER;
    }

    /**
     * OpenApi 요청의 헤더 세팅
     * @param oauth OpenApi 접근 토큰
     * @param transactionId 해외 주식 조건검색 Api 요청의 트랜잭션 ID
     * @return Consuemr 타입의 세팅된 헤더
     */
    public static Consumer<HttpHeaders> setHeaders(String oauth, String transactionId) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + oauth);
        headers.add("tr_id", transactionId);

        return httpHeaders -> httpHeaders.addAll(headers);
    }

    /**
     * OpenApi 요청의 쿼리 파라미터 생성
     * @return MultiValueMap 타입의 쿼리 파라미터
     */
    public MultiValueMap<String, String> createParameter() {
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.add("AUTH", "");
        queryParams.add("EXCD", exchangeCode.getShortCode());
        queryParams.add("CO_YN_PRICECUR", String.valueOf(YesNoStatus.YES.getNumber()));
        queryParams.add("CO_ST_PRICECUR", "1");
        queryParams.add("CO_EN_PRICECUR", "9999999999999");

        queryParams.add("CO_YN_VOLUME", String.valueOf(YesNoStatus.YES.getNumber()));
        queryParams.add("CO_ST_VOLUME", this.minTradingVolume);
        queryParams.add("CO_EN_VOLUME", this.maxTradingVolume);

        queryParams.add("CO_YN_VALX", String.valueOf(YesNoStatus.YES.getNumber()));
        queryParams.add("CO_ST_VALX", this.minMarketCapital);
        queryParams.add("CO_EN_VALX", this.maxMarketCapital);

        queryParams.add("CO_YN_PER", String.valueOf(YesNoStatus.YES.getNumber()));
        queryParams.add("CO_ST_PER", this.minPER);
        queryParams.add("CO_EN_PER", this.maxPER);

        return queryParams;
    }
}