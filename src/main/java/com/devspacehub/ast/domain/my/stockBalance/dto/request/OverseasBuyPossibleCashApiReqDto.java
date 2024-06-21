/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : OverseasBuyPossibleCashApiReqDto
 creation : 2024.5.31
 author : Yoonji Moon
 */

package com.devspacehub.ast.domain.my.stockBalance.dto.request;

import com.devspacehub.ast.domain.my.dto.MyServiceRequestDto;
import org.springframework.http.HttpHeaders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.function.Consumer;

/**
 * 해외 매수 가능 금액 조회 KIS Api 요청 Dto.
 */
public class OverseasBuyPossibleCashApiReqDto {

    /**
     * OpenApi 요청 위해 파라미터 생성한다.
     * @param accntNumber 계좌번호 앞 6자리
     * @param accntProductCode 계좌번호 뒤 2자리
     * @param myServiceRequestDto 요청 Dto
     * @return 요청 파라미터
     */
    public static MultiValueMap<String, String> createParameter(String accntNumber, String accntProductCode,
                                                                MyServiceRequestDto.Overseas myServiceRequestDto) {
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.add("CANO", accntNumber);
        queryParams.add("ACNT_PRDT_CD", accntProductCode);
        queryParams.add("ITEM_CD", myServiceRequestDto.getItemCode());
        queryParams.add("OVRS_EXCG_CD", myServiceRequestDto.getExchangeCode().getLongCode());
        queryParams.add("OVRS_ORD_UNPR", myServiceRequestDto.getOrderPrice().toString());

        return queryParams;
    }

    /**
     * OpenApi 요청 위해 헤더 세팅한다.
     * @param oauth the oauth 접근 토큰
     * @param txId  the tx id 트랜잭션 ID
     * @return the headers 헤더 값
     */
    public static Consumer<HttpHeaders> setHeaders(String oauth, String txId) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + oauth);
        headers.add("tr_id", txId);
        return httpHeaders -> httpHeaders.addAll(headers);
    }
}
