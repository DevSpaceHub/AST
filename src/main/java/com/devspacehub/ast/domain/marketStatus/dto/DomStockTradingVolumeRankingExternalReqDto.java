/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : DomStockTradingVolumeRankingExternalReqDto
 creation : 2024.1.15
 author : Yoonji Moon
 */

package com.devspacehub.ast.domain.marketStatus.dto;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.function.Consumer;

/**
 * 국내 주식 거래량 순위 조회 요청 DTO.
 */
public class DomStockTradingVolumeRankingExternalReqDto {
    private static final String PERSONAL_CUST_TYPE = "P";

    public static Consumer<HttpHeaders> setHeaders(String oauth, String txId) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + oauth);
        headers.add("tr_id", txId);
        headers.add("custtype", PERSONAL_CUST_TYPE);
        return httpHeaders -> httpHeaders.addAll(headers);
    }

    public static MultiValueMap<String, String> createParameter() {
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();

        queryParams.add("FID_COND_MRKT_DIV_CODE", "J");          // 조건 시장 분류 코드 (고정값)
        queryParams.add("FID_COND_SCR_DIV_CODE", "20171");       // 조건 화면 분류 코드 (고정값)
        queryParams.add("FID_INPUT_ISCD", "0000");               // 입력 종목 코드 (전체:0000)
        queryParams.add("FID_DIV_CLS_CODE", "0");                // 0(전체) 1(보통주) 2(우선주)
        queryParams.add("FID_BLNG_CLS_CODE", "0");               // 0 : 평균거래량 1:거래증가율 2:평균거래회전율 3:거래금액순 4:평균거래금액회전율
        queryParams.add("FID_TRGT_CLS_CODE", "111111111");       // 9자리 모두 1. 1 or 0 9자리 (차례대로 증거금 30% 40% 50% 60% 100% 신용보증금 30% 40% 50% 60%)
        queryParams.add("FID_TRGT_EXLS_CLS_CODE", "111111");     // 6자리 모두 1. 1 or 0 6자리 (차례대로 투자위험/경고/주의 관리종목 정리매매 불성실공시 우선주 거래정지)   ex) "000000"
        queryParams.add("FID_INPUT_PRICE_1", "");                // 전체 가격 대상 조회 시 FID_INPUT_PRICE_1, FID_INPUT_PRICE_2 모두 ""(공란) 입력
        queryParams.add("FID_INPUT_PRICE_2", "");
        queryParams.add("FID_VOL_CNT", "");                      //전체 거래량 대상 조회 시 FID_VOL_CNT ""(공란) 입력
        queryParams.add("FID_INPUT_DATE_1", "");

        return queryParams;
    }
}
