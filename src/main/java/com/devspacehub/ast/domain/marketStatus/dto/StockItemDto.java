/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : OrderItemDto
 creation : 2024.1.4
 author : Yoonji Moon
 */
package com.devspacehub.ast.domain.marketStatus.dto;

import lombok.*;

/**
 * 주식 정보 Dto.
 */
@Getter
@Setter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class StockItemDto {

    private String transactionId;

    private String stockCode;

    private String orderDivision;

    private Integer orderQuantity;

    private Integer orderPrice;

    @Builder
    private static StockItemDto StockItemDto(String transactionId, String stockCode, String orderDivision,
                                             String orderQuantity, String orderPrice) {
        return new StockItemDto(
                transactionId, stockCode, orderDivision, Integer.valueOf(orderQuantity), Integer.valueOf(orderPrice));
    }

}
