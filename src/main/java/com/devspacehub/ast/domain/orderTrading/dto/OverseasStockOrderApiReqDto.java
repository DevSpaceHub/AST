/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : OverseasStockOrderApiReqDto
 creation : 2024.6.4
 author : Yoonji Moon
 */

package com.devspacehub.ast.domain.orderTrading.dto;

import com.devspacehub.ast.common.config.OpenApiProperties;
import com.devspacehub.ast.common.dto.WebClientCommonReqDto;
import com.devspacehub.ast.domain.marketStatus.dto.StockItemDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import org.springframework.http.HttpHeaders;

import java.util.function.Consumer;

/**
 * 해외 주식 주문 OpenApi 요청 DTO.
 */
@Builder
public class OverseasStockOrderApiReqDto extends WebClientCommonReqDto {

    @JsonProperty("CANO")
    private String accntNumber;
    @JsonProperty("ACNT_PRDT_CD")
    private String accntProductCode;
    @JsonProperty("ORD_DVSN")
    private String orderDivision;
    @JsonProperty("PDNO")
    private String stockCode;
    @JsonProperty("ORD_QTY")
    private String orderQuantity;
    @JsonProperty("OVRS_ORD_UNPR")
    private String orderPrice;
    @JsonProperty("ORD_SVR_DVSN_CD")
    private String ordSvrDvsnCd;
    @JsonProperty("OVRS_EXCG_CD")
    private String exchangeCode;

    /**
     * OpenApi 호출에 필요한 헤더 세팅.
     *
     * @param oauth the oauth
     * @param txId  the tx id
     * @return the headers
     */
    public static Consumer<HttpHeaders> setHeaders(String oauth, String txId) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("content-Type", "application/json");
        headers.add("authorization", "Bearer " + oauth);
        headers.add("tr_id", txId);
        return httpHeaders -> httpHeaders.addAll(headers);
    }

    /**
     * @param openApiProperties
     * @param stockItem 주식 종목 정보
     * @return 매수/매도 주문 API의 요청 Dto
     */
    public static <T extends StockItemDto> OverseasStockOrderApiReqDto from(OpenApiProperties openApiProperties, T stockItem) {
        StockItemDto.Overseas overseasStockItem = (StockItemDto.Overseas) stockItem;
        return OverseasStockOrderApiReqDto.builder()
                .accntNumber(openApiProperties.getAccntNumber())
                .accntProductCode(openApiProperties.getAccntProductCode())
                .stockCode(overseasStockItem.getItemCode())
                .orderDivision(overseasStockItem.getOrderDivision())
                .orderQuantity(String.valueOf(overseasStockItem.getOrderQuantity()))
                .orderPrice(String.valueOf(overseasStockItem.getOrderPrice()))
                .exchangeCode(overseasStockItem.getExchangeCode().getLongCode())
                .ordSvrDvsnCd("0")  // 주문서버구분 코드 (default:0)
                .build();
    }

}
