/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : OrderConclusionDtoTest
 creation : 2024.5.18
 author : Yoonji Moon
 */

package com.devspacehub.ast.domain.orderTrading.dto;

import com.devspacehub.ast.common.constant.OpenApiType;
import com.devspacehub.ast.domain.my.orderConclusion.dto.OrderConclusionFindExternalResDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;


class OrderConclusionDtoTest {
    @DisplayName("일자별 주문 체결 조회 응답 Body로부터 주문 체결 정보 DTO를 생성한다.")
    @Test
    void of() {
        // given
        OpenApiType givenOrderType = OpenApiType.ORDER_CONCLUSION_FIND;
        OrderConclusionFindExternalResDto.Output1 firstGivenOutput1 = OrderConclusionFindExternalResDto.Output1.builder()
                .itemCode("00000")
                .itemNameKor("테스트1")
                .orderNumber("00000001")
                .totalConcludedPrice("1000")
                .totalConcludedQuantity("1")
                .orderQuantity("2")
                .orderPrice("1000")
                .build();
        OrderConclusionFindExternalResDto.Output1 secondGivenOutput1 = OrderConclusionFindExternalResDto.Output1.builder()
                .itemCode("00001")
                .itemNameKor("테스트2")
                .orderNumber("00000002")
                .totalConcludedPrice("2000")
                .totalConcludedQuantity("2")
                .orderQuantity("3")
                .orderPrice("2000")
                .build();
        List<OrderConclusionFindExternalResDto.Output1> givenOutput1s = List.of(firstGivenOutput1, secondGivenOutput1);
        // when
        List<OrderConclusionDto> results = OrderConclusionDto.of(givenOutput1s);
        // then
        assertThat(results).hasSize(2)
                .extracting(
                        "orderType", "itemCode", "itemNameKor", "orderNumber",
                        "concludedPrice", "concludedQuantity",
                        "orderQuantity", "orderPrice")
                .containsExactly(
                        tuple(givenOrderType, firstGivenOutput1.getItemCode(), firstGivenOutput1.getItemNameKor(), firstGivenOutput1.getOrderNumber(),
                                new BigDecimal(firstGivenOutput1.getTotalConcludedPrice()), Integer.parseInt(firstGivenOutput1.getTotalConcludedQuantity()),
                                Integer.parseInt(firstGivenOutput1.getOrderQuantity()), new BigDecimal(firstGivenOutput1.getOrderPrice())),
                        tuple(givenOrderType, secondGivenOutput1.getItemCode(), secondGivenOutput1.getItemNameKor(), secondGivenOutput1.getOrderNumber(),
                                new BigDecimal(secondGivenOutput1.getTotalConcludedPrice()), Integer.parseInt(secondGivenOutput1.getTotalConcludedQuantity()),
                                Integer.parseInt(secondGivenOutput1.getOrderQuantity()), new BigDecimal(secondGivenOutput1.getOrderPrice())
                        ));
    }

}