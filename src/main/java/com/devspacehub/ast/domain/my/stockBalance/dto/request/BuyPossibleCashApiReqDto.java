/*
 © 2023 devspacehub, Inc. All rights reserved.

 name : BuyPossibleCashApiReqDto
 creation : 2023.12.20
 author : Yoonji Moon
 */

package com.devspacehub.ast.domain.my.stockBalance.dto.request;

import lombok.Builder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.math.BigDecimal;
import java.util.function.Consumer;

import static com.devspacehub.ast.common.constant.YesNoStatus.NO;

/**
 * 매수 가능 조회 요청 DTO.
 */
@Builder
public class BuyPossibleCashApiReqDto {

    /**
     * 요청 파라미터 생성한다.
     * @param accntNumber 계좌 번호의 앞 6자리
     * @param accntProductCode 계좌 번호의 뒤 2자리
     * @param itemCode 주식 종목 코드
     * @param orderPrice 주문가
     * @param orderDivision 주문 구분 (지정가)
     * @return MultiValuemap 타입의 요청 파라미터
     */
    public static MultiValueMap<String, String> createParameter(String accntNumber, String accntProductCode, String itemCode, BigDecimal orderPrice, String orderDivision) {
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.add("CANO", accntNumber);
        queryParams.add("ACNT_PRDT_CD", accntProductCode);
        queryParams.add("PDNO", itemCode);
        queryParams.add("ORD_UNPR", orderPrice.toString());
        queryParams.add("ORD_DVSN", orderDivision);
        queryParams.add("CMA_EVLU_AMT_ICLD_YN", NO.getCode());  // CMA 평가 금액 포함 여부
        queryParams.add("OVRS_ICLD_YN", NO.getCode());    // 해외 포함 여부

        return queryParams;
    }

    /**
     * 요청 헤더를 세팅한다.
     *
     * @param oauth OpenApi의 접근 토큰
     * @param txId  매수 가능 조회 OpenApi의 트랜잭션 ID
     * @return Consumer 타입의 세팅된 헤더
     */
    public static Consumer<HttpHeaders> setHeaders(String oauth, String txId) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + oauth);
        headers.add("tr_id", txId);
        return httpHeaders -> httpHeaders.addAll(headers);
    }
}