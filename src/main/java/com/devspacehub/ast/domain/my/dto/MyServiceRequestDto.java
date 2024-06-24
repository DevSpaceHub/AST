/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : MyServiceRequestDto
 creation : 2024.5.31
 author : Yoonji Moon
 */

package com.devspacehub.ast.domain.my.dto;

import com.devspacehub.ast.common.constant.ExchangeCode;
import com.devspacehub.ast.common.utils.BigDecimalUtil;
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
         * @param itemCode 종목 코드
         * @param orderPrice 주문 단가
         * @param orderDivision 주문 구분 (지정가)
         * @return 국내 Myservice 구현체로 전달하는 Dto
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
         * 해외 종목 요청하기 위해 DTO 생성한다.
         * @param itemCode 종목 코드
         * @param orderPrice 소수점 아래 세자리 수의 주문단가
         * @param exchangeCode 거래소 코드
         * @return MyServiceRequestDto 해외 주식에 대해 사용자 정보 관한 Serivce layer Dto
         */
        public static MyServiceRequestDto.Overseas from(String itemCode, BigDecimal orderPrice, ExchangeCode exchangeCode) {
            return MyServiceRequestDto.Overseas.builder()
                    .itemCode(itemCode)
                    .orderPrice(BigDecimalUtil.setScale(orderPrice, 3))
                    .exchangeCode(exchangeCode)
                    .build();
        }
    }
}
