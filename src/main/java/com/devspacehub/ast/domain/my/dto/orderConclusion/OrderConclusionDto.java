/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : OrderConclusionDto
 creation : 2024.4.19
 author : Yoonji Moon
 */

package com.devspacehub.ast.domain.my.dto.orderConclusion;

import com.devspacehub.ast.common.constant.OpenApiType;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 주문 체결 정보 DTO.
 */
@Builder
@Getter
public class OrderConclusionDto {
    private String itemCode;
    private String itemNameKor;
    private String orderNumber;
    private int concludedQuantity;
    private BigDecimal concludedPrice;
    private OpenApiType orderType;   // 주문 타입 (매수, 예약매수)
    private int orderQuantity;
    private BigDecimal orderPrice;
    private String orderTime;

    /**
     * 팩토리 메서드
     * @param responseBodies 주문 체결 조회 응답 DTO
     * @return
     */
    public static List<OrderConclusionDto> of (List<OrderConclusionFindExternalResDto.Output1> responseBodies) {
        List<OrderConclusionDto> orderTradingResults = new ArrayList<>();
        for (OrderConclusionFindExternalResDto.Output1 output1 : responseBodies) {
            orderTradingResults.add(OrderConclusionDto.builder()
                            .itemCode(output1.getItemCode())
                            .itemNameKor(output1.getItemNameKor())
                            .orderNumber(output1.getOrderNumber())
                            .concludedPrice(new BigDecimal(output1.getTotalConcludedPrice()))
                            .concludedQuantity(Integer.parseInt(output1.getTotalConcludedQuantity()))
                            .orderType(OpenApiType.DOMESTIC_ORDER_CONCLUSION_FIND)
                            .orderQuantity(Integer.parseInt(output1.getOrderQuantity()))
                            .orderPrice(new BigDecimal(output1.getOrderPrice()))
                            .orderTime(output1.getOrderTime())
                    .build());
        }
        return orderTradingResults;
    }
}