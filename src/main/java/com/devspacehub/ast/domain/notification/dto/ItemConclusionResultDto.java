/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : ItemConclusionResultDto
 creation : 2024.9.18
 author : Yoonji Moon
 */

package com.devspacehub.ast.domain.notification.dto;

import com.devspacehub.ast.common.constant.OpenApiType;
import com.devspacehub.ast.domain.my.dto.orderConclusion.OrderConclusionDto;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

/**
 * 주식 체결 결과 Dto
 * DefaultItemInfoDto 상속
 */
@ToString
@SuperBuilder
public class ItemConclusionResultDto extends DefaultItemInfoDto {
    private int concludedQuantity;
    private BigDecimal concludedPrice;


    /**
     * 전달받는 인자로 주식 체결 결과 Dto 인스턴스 생성하여 반환한다.
     * @param orderApiType OpenApi Type
     * @param accountStatus 계좌 상태
     * @param orderConclusion 주문 결과 정보
     * @return ItemConclusionResultDto
     */
    public static ItemConclusionResultDto from(OpenApiType orderApiType, String accountStatus, OrderConclusionDto orderConclusion) {
        return ItemConclusionResultDto.builder()
                .title("체결 완료")
                .accountStatusKor(accountStatus)
                .itemNameKor(orderConclusion.getItemNameKor())
                .itemCode(orderConclusion.getItemCode())
                .openApiType(orderApiType)
                .orderQuantity(orderConclusion.getOrderQuantity())
                .orderPrice(orderConclusion.getOrderPrice())
                .orderNumber(orderConclusion.getOrderNumber())
                .concludedQuantity(orderConclusion.getConcludedQuantity())
                .concludedPrice(orderConclusion.getConcludedPrice())
                .orderTime(orderConclusion.getOrderTime())
                .build();
    }
}
