/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : StockItemDto
 creation : 2024.1.4
 author : Yoonji Moon
 */
package com.devspacehub.ast.domain.marketStatus.dto;

import lombok.*;

import static com.devspacehub.ast.common.constant.CommonConstants.ORDER_DIVISION;

/**
 * 주식 정보 Dto.
 */
@Getter
@Setter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class StockItemDto {

    private String stockCode;

    private String orderDivision;

    private Integer orderQuantity;

    private Integer currentStockPrice; // 현재가

    @Builder
    private static StockItemDto StockItemDto(String stockCode, String orderQuantity, String currentStockPrice) {
        return new StockItemDto(stockCode, ORDER_DIVISION, Integer.valueOf(orderQuantity), Integer.valueOf(currentStockPrice));
    }
}
