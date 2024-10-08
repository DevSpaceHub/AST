/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : MessageContentDtoTest
 creation : 2024.4.25
 author : Yoonji Moon
 */

package com.devspacehub.ast.domain.notification.dto;

import com.devspacehub.ast.common.constant.OpenApiType;
import com.devspacehub.ast.common.constant.ProfileType;
import com.devspacehub.ast.domain.orderTrading.OrderTrading;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * MessageContentDto 테스트 코드.
 */
class MessageContentDtoTest {

    @DisplayName("체결 결과 DTO를 이용해 디스코드 메시지 전송 DTO를 생성한다.")
    @Test
    void createMessageTest_conclusion() {
        // given
        MessageContentDto.ConclusionResult orderResult = MessageContentDto.ConclusionResult.builder()
                .title("체결 완료")
                .accountStatusKor("모의")
                .itemNameKor("삼성전자")
                .itemCode("005930")
                .openApiType(OpenApiType.DOMESTIC_ORDER_CONCLUSION_FIND)
                .orderQuantity(10)
                .orderPrice(BigDecimal.valueOf(80000))
                .orderNumber("0123456")
                .orderTime("090130")
                .concludedQuantity(10)
                .concludedPrice(BigDecimal.valueOf(80000))
                .build();
        // when
        String message = orderResult.createMessage(orderResult);
        // then
        assertEquals("""
                **[090130] 체결 완료**
                계좌 상태 : 모의
                종목명 : 삼성전자 (005930)
                매매구분 : 국내 주식 일별주문 체결 조회
                주문번호 : 0123456
                주문수량 : 10주
                주문단가 : 80000
                체결수량 : 10주
                체결금액 : 80000""", message);
    }

    @DisplayName("매수 주문 결과 DTO를 이용해 디스코드 메시지 전송 DTO를 생성한다.")
    @Test
    void createMessageTest_orderResult() {
        // given
        MessageContentDto.OrderResult orderResult = MessageContentDto.OrderResult.builder()
                .title("주문 완료")
                .accountStatusKor("실전")
                .itemNameKor("삼성전자")
                .itemCode("005930")
                .openApiType(OpenApiType.DOMESTIC_STOCK_BUY_ORDER)
                .orderQuantity(10)
                .orderPrice(BigDecimal.valueOf(80000))
                .orderNumber("0123456")
                .orderTime("090130")
                .build();
        // when
        String message = orderResult.createMessage(orderResult);
        // then
        assertEquals("""
                **[090130] 주문 완료**
                계좌 상태 : 실전
                종목명 : 삼성전자 (005930)
                매매구분 : 국내 주식(현금)-매수
                주문번호 : 0123456
                주문수량 : 10주
                주문단가 : 80000""", message);
    }
    @DisplayName("OpenApiType, 운영 상태 값, 주문 결과를 전달하여 디스코드 메세지 > 주문 결과 Dto를 생성한다.")
    @Test
    void orderResultTest_fromOne() {
        // given
        System.setProperty("spring.profiles.active", "test");
        OpenApiType givenOpenApiType = OpenApiType.OVERSEAS_STOCK_SELL_ORDER;
        OrderTrading givenOrderTrading = OrderTrading.builder()
                .itemCode("AAPL")
                .itemNameKor("애플")
                .orderPrice(new BigDecimal("134.02"))
                .orderNumber("111111")
                .orderQuantity(1)
                .orderTime("093030")
                .build();
        // when
        MessageContentDto.OrderResult result = MessageContentDto.OrderResult.fromOne(
                givenOpenApiType, ProfileType.getAccountStatus(), givenOrderTrading);
        // then
        assertThat(result.getTitle()).isEqualTo("주문 완료");
        assertThat(result.getItemNameKor()).isEqualTo("애플");
        assertThat(result.getOrderPrice()).isEqualTo(new BigDecimal("134.02"));
        assertThat(result.getAccountStatusKor()).isEqualTo("모의");
        assertThat(result.getOpenApiType()).isEqualTo(givenOpenApiType);
    }
}