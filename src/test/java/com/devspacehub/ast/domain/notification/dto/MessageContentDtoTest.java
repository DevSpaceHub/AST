/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : MessageContentDtoTest
 creation : 2024.4.25
 author : Yoonji Moon
 */

package com.devspacehub.ast.domain.notification.dto;

import com.devspacehub.ast.common.constant.OpenApiType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * MessageContentDto 테스트 코드.
 */
class MessageContentDtoTest {

    @DisplayName("체결 결과 DTO를 이용해 디스코드 메시지 전송 DTO를 생성한다.")
    @Test
    void createMessage_conclusion() {
        // given
        MessageContentDto.ConclusionResult orderResult = MessageContentDto.ConclusionResult.builder()
                .title("체결 완료")
                .accountStatusKor("모의")
                .itemNameKor("삼성전자")
                .itemCode("005930")
                .openApiType(OpenApiType.ORDER_CONCLUSION_FIND)
                .orderQuantity(10)
                .orderPrice(80000)
                .orderNumber("0123456")
                .orderTime("090130")
                .concludedQuantity(10)
                .concludedPrice(80000)
                .build();
        // when
        String message = orderResult.createMessage(orderResult);
        // then
        assertEquals("""
                **[090130] 체결 완료**
                계좌 상태 : 모의
                종목명 : 삼성전자 (005930)
                매매구분 : 주식 일별주문 체결 조회
                주문번호 : 0123456
                주문수량 : 10주
                주문단가 : 80000
                체결수량 : 10주
                체결금액 : 80000""", message);
    }

    @DisplayName("매수 주문 결과 DTO를 이용해 디스코드 메시지 전송 DTO를 생성한다.")
    @Test
    void createMessage_orderResult() {
        // given
        MessageContentDto.OrderResult orderResult = MessageContentDto.OrderResult.builder()
                .title("주문 완료")
                .accountStatusKor("실전")
                .itemNameKor("삼성전자")
                .itemCode("005930")
                .openApiType(OpenApiType.DOMESTIC_STOCK_BUY_ORDER)
                .orderQuantity(10)
                .orderPrice(80000)
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
}