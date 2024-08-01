/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : MyServiceRequestDtoTest
 creation : 2024.6.24
 author : Yoonji Moon
 */

package com.devspacehub.ast.domain.my.dto;

import com.devspacehub.ast.common.constant.ExchangeCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 사용자의 정보에 대한 CRUD 요청 시 사용되는 클래스 요청 Dto
 * (매수가능금액 Read / 주식 잔고 Read / 체결 잔고 Read / 예약매수 데이터 Update)
 */
class MyServiceRequestDtoTest {

    @DisplayName("해외>매수 가능 금액 조회할 때 주문 단가는 소수점 아래 세 자릿수이어야 한다.")
    @Test
    void overseas_from() {
        // given
        BigDecimal givenOrderPrice = new BigDecimal("142.2345");
        // when
        MyServiceRequestDto.Overseas result = MyServiceRequestDto.Overseas.from("AAPL", givenOrderPrice, ExchangeCode.NASDAQ);
        // then
        assertThat(result.getOrderPrice()).isEqualTo(new BigDecimal("142.234"));
    }

}