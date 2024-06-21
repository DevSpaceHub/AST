/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : OrderTradingTest
 creation : 2024.5.18
 author : Yoonji Moon
 */

package com.devspacehub.ast.domain.orderTrading;

import com.devspacehub.ast.common.constant.CommonConstants;
import com.devspacehub.ast.domain.marketStatus.dto.StockItemDto;
import com.devspacehub.ast.domain.orderTrading.dto.StockOrderApiResDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class OrderTradingTest {

    @Test
    @DisplayName("예약 매수 주문 정보, 응답 값, 트랜잭션ID 값을 받아서 주문 거래 정보 Entity 생성한다.")
    void from_Reservation() {
        // given
        StockItemDto.ReservationStockItem reservationStockItemDto = StockItemDto.ReservationStockItem.builder()
                .itemCode("000000")
                .itemNameKor("테스트주식")
                .orderPrice(BigDecimal.valueOf(10000))
                .orderQuantity(10)
                .orderDivision(CommonConstants.ORDER_DIVISION)
                .build();
        StockOrderApiResDto openApiResponseDto = new StockOrderApiResDto();
        StockOrderApiResDto.Output output = new StockOrderApiResDto.Output();
        output.setOrderNumber("1234567890");
        output.setOrderTime("090010");
        openApiResponseDto.setOutput(output);
        openApiResponseDto.setResultCode("0");
        openApiResponseDto.setMessageCode("40600000");
        openApiResponseDto.setMessage("모의투자 매수주문이 완료 되었습니다.");

        String givenTxId = "KKKKK";
        // when
        OrderTrading result = OrderTrading.from(reservationStockItemDto, openApiResponseDto, givenTxId);

        // then
        assertThat(result).extracting("itemCode", "itemNameKor", "orderPrice")
                .containsExactly(reservationStockItemDto.getItemCode(), reservationStockItemDto.getItemNameKor(), reservationStockItemDto.getOrderPrice());

        assertThat(result).extracting("orderNumber", "orderTime", "orderResultCode", "orderMessageCode", "orderMessage")
                .containsExactly(
                        openApiResponseDto.getOutput().getOrderNumber(), openApiResponseDto.getOutput().getOrderTime(), openApiResponseDto.getResultCode(),
                        openApiResponseDto.getMessageCode(), openApiResponseDto.getMessage());
        assertThat(result.getTransactionId()).isEqualTo(givenTxId);
        assertThat(result.getOrderDivision()).isEqualTo(CommonConstants.ORDER_DIVISION);
    }
}