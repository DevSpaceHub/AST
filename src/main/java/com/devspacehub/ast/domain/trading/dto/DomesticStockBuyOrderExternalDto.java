/*
 © 2023 devspacehub, Inc. All rights reserved.

 name : DomesticStockOrderDto
 creation : 2023.12.11
 author : Yoonji Moon
 */

package com.devspacehub.ast.domain.trading.dto;

import com.devspacehub.ast.common.dto.WebClientRequestDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.http.HttpHeaders;

import java.util.function.Consumer;

/**
 * OpenApi 호출 - 국내 주식 주문 DTO
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class DomesticStockBuyOrderExternalDto extends WebClientRequestDto {
    private String txId;

    @JsonProperty("CANO")
    private String accntNumber;    // 종합계좌번호
    @JsonProperty("ACNT_PRDT_CD")
    private String accntProductCode;         // 계좌번호 체계(8-2)의 뒤 2자리
    @JsonProperty("PDNO")
    private String stockCode;        // 종목코드
    @JsonProperty("ORD_DVSN")
    private String orderDivision;       // 주문 구분
    @JsonProperty("ORD_QTY")
    private String orderQuantity;
    @JsonProperty("ORD_UNPR")
    private String orderPrice;        // 주문 단가

    /**
     * OpenApi 호출에 필요한 헤더 세팅.
     *
     * @param oauth the oauth
     * @param txId  the tx id
     * @return the headers
     */
    @JsonIgnore
    public static Consumer<HttpHeaders> setHeaders(String oauth, String txId) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("content-Type", "application/json");
        headers.add("authorization", "Bearer " + oauth);
        headers.add("tr_id", txId);
        return httpHeaders -> {
            httpHeaders.addAll(headers);
        };
    }
    @Override
    public String toString() {
        return "{\"CANO\":\""+ this.accntNumber + "\"," +
                "\"ACNT_PRDT_CD\":\""+ this.accntProductCode + "\"," +
                "\"PDNO\":\""+ this.stockCode + "\"," +
                "\"ORD_DVSN\":\""+ this.orderDivision + "\"," +
                "\"ORD_QTY\":\""+ this.orderQuantity + "\"," +
                "\"ORD_UNPR\":\"" + this.orderPrice + "}";
    }
}
