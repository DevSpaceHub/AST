/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : MyServiceRequestDto
 creation : 2024.5.31
 author : Yoonji Moon
 */

package com.devspacehub.ast.domain.my.dto;

import com.devspacehub.ast.common.constant.ExchangeCode;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

/**
 * MyService 에 호출 시 사용하는 Service Layer DTO.
 */
@Getter
@SuperBuilder
public class MyServiceRequestDto {
    // 공통
    private String itemCode;
    private BigDecimal orderPrice;

    /**
     * MyServiceRequestDto를 상속하는 국내 용 DTO.
     */
    @Getter
    @SuperBuilder
    public static class Domestic extends MyServiceRequestDto {
        private String orderDivision;

        /**
         * 국내 종목 요청 시 사용한다.
         * @param itemCode
         * @param orderPrice
         * @param orderDivision
         * @return
         */
    public static MyServiceRequestDto.Domestic from(String itemCode, BigDecimal orderPrice, String orderDivision) {
        return MyServiceRequestDto.Domestic.builder()
                .itemCode(itemCode)
                .orderPrice(orderPrice)
                .orderDivision(orderDivision)
                .build();

        }
    }

    /**
     * MyServiceRequestDto를 상속하는 해외 용 DTO.
     */
    @Getter
    @SuperBuilder
    public static class Overseas extends MyServiceRequestDto {
        private ExchangeCode exchangeCode;

        /**
         * 해외 종목 요청 시 사용한다.
         * @param itemCode
         * @param orderPrice
         * @param exchangeCode
         * @return
         */
        public static MyServiceRequestDto.Overseas from(String itemCode, BigDecimal orderPrice, ExchangeCode exchangeCode) {
            return MyServiceRequestDto.Overseas.builder()
                    .itemCode(itemCode)
                    .orderPrice(orderPrice)
                    .exchangeCode(exchangeCode)
                    .build();
        }
    }
}
