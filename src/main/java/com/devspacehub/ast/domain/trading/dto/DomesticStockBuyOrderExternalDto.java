/*
 © 2023 devspacehub, Inc. All rights reserved.

 name : DomesticStockOrderDto
 creation : 2023.12.11
 author : Yoonji Moon
 */

package com.devspacehub.ast.domain.trading.dto;

import com.devspacehub.ast.common.dto.WebClientRequestDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

/**
 * OpenApi 호출 - 국내 주식 주문 DTO
 */
//@NoArgsConstructor
//@AllArgsConstructor
@Getter
@Component
@Builder
@lombok.Value
public class DomesticStockBuyOrderExternalDto extends WebClientRequestDto {
    @Value("${my.comprehensive-accountnumber}")
    private String CANO;    // 종합계좌번호
    @Value("${my.accountnumber-product-code}")
    private String ACNT_PRDT_CD;         // 계좌번호 체계(8-2)의 뒤 2자리
    private String PDNO;        // 종목코드
    private String ORD_DVSN;       // 주문 구분
    private String ORD_QTY;
    private String ORD_UNPR;        // 주문 단가

//    @Builder
    /*public DomesticStockBuyOrderExternalDto(

            @Value("${my.comprehensive-accountnumber}") String accntNumber,
            String stockCode, String orderCategory, String orderQuantity, String orderPrice) {
        super();
        this.PDNO = stockCode;
        this.ORD_DVSN = orderCategory;
        this.ORD_QTY = orderQuantity;
        this.ORD_UNPR = orderPrice;
    }*/

    @Override
    public DomesticStockBuyOrderExternalDto getBody() {
        return DomesticStockBuyOrderExternalDto.builder()
                .PDNO(PDNO)
                .ORD_DVSN(ORD_DVSN)
                .ORD_QTY(ORD_QTY)
                .ORD_UNPR(ORD_UNPR)
                .build();
    }

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
        return "{\"CANO\":\""+ this.CANO + "\"," +
                "\"ACNT_PRDT_CD\":\""+ this.ACNT_PRDT_CD + "\"," +
                "\"PDNO\":\""+ this.PDNO + "\"," +
                "\"ORD_DVSN\":\""+ this.ORD_DVSN + "\"," +
                "\"ORD_QTY\":\""+ this.ORD_QTY + "\"," +
                "\"ORD_UNPR\":\"" + this.ORD_UNPR + "}";
    }
}
