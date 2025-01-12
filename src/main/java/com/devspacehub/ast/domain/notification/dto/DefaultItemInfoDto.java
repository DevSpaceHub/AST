/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : DefaultItemInfoDto
 creation : 2024.9.18
 author : Yoonji Moon
 */

package com.devspacehub.ast.domain.notification.dto;

import com.devspacehub.ast.common.constant.OpenApiType;
import com.devspacehub.ast.domain.orderTrading.OrderTrading;
import com.devspacehub.ast.infra.kafka.dto.MessageDto;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

/**
 * 메세지 발송 시 생성되는 주식 관련 정보 Dto
 */
@Getter
@SuperBuilder
public abstract class DefaultItemInfoDto extends MessageDto {
    private String accountStatusKor;
    private String itemNameKor;
    private String itemCode;
    private OpenApiType openApiType;
    private int orderQuantity;
    private BigDecimal orderPrice;
    private String orderNumber;
    private String orderTime;

    /**
     * 주문 결과 Dto.
     * DefaultItemInfoDto를 상속한다.
     */
    @ToString
    @SuperBuilder
    public static class ItemOrderResultDto extends DefaultItemInfoDto {

        /**
         * 전달받는 인자로 주문 결과 Dto 인스턴스를 반환한다.
         * @param openApiType OpenApi Type
         * @param accountStatus 계좌 상태
         * @param orderTrading 주문 결과 Dto
         * @return ItemOrderResultDto
         */
        public static ItemOrderResultDto from(OpenApiType openApiType, String accountStatus, OrderTrading orderTrading) {
            return ItemOrderResultDto.builder()
                    .title("주문 완료")
                    .accountStatusKor(accountStatus)
                    .itemNameKor(orderTrading.getItemNameKor())
                    .itemCode(orderTrading.getItemCode())
                    .openApiType(openApiType)
                    .orderQuantity(orderTrading.getOrderQuantity())
                    .orderPrice(orderTrading.getOrderPrice())
                    .orderNumber(orderTrading.getOrderNumber())
                    .orderTime(orderTrading.getOrderTime())
                    .build();
        }
    }

}
