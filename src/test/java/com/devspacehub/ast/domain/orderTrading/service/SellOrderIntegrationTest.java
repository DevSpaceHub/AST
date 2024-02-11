/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : SellOrderIntegrationTest
 creation : 2024.2.8
 author : Yoonji Moon
 */

package com.devspacehub.ast.domain.orderTrading.service;

import com.devspacehub.ast.domain.orderTrading.OrderTrading;
import com.devspacehub.ast.domain.orderTrading.OrderTradingRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;


import static com.devspacehub.ast.common.constant.CommonConstants.ORDER_DIVISION;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SellOrderIntegrationTest {
    @Autowired
    private OrderTradingRepository orderTradingRepository;
    @Autowired
    private SellOrderServiceImpl sellOrderService;

    final String sellTxId = "VTTC0801U";

    @Test
    @DisplayName("(실패)동일 종목에 대해 매도 주문은 각 1번씩만 발생한다.")
    void isNewOrder_failed() {
        // given
        final String alreadyOrderedStockCode = "000000";
        orderTradingRepository.save(OrderTrading.builder()
                .seq(0L)
                .orderDivision(ORDER_DIVISION)
                .itemCode(alreadyOrderedStockCode)
                .itemNameKor("한화생명")
                .orderQuantity(2)
                .orderPrice(4385)
                .orderNumber("0000151228")
                .orderMessage("주문 전송 완료 되었습니다.")
                .orderMessageCode("APBK0013")
                .orderResultCode("0")
                .transactionId(sellTxId)
                .orderTime("090008")
                .build());
        // when
        boolean result = sellOrderService.isNewOrder(alreadyOrderedStockCode);
        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("(성공)동일 종목에 대해 매도 주문은 각 1번씩만 발생한다.")
    void isNewOrder_success() {
        // given
        final String alreadyOrderedStockCode = "000000";
        final String notOrderedStockCode = "100000";
        orderTradingRepository.save(OrderTrading.builder()
                .seq(0L)
                .orderDivision(ORDER_DIVISION)
                .itemCode(alreadyOrderedStockCode)
                .itemNameKor("한화생명")
                .orderQuantity(2)
                .orderPrice(4385)
                .orderNumber("0000151228")
                .orderMessage("주문 전송 완료 되었습니다.")
                .orderMessageCode("APBK0013")
                .orderResultCode("0")
                .transactionId(sellTxId)
                .orderTime("090008")
                .build());
        // when
        boolean result = sellOrderService.isNewOrder(notOrderedStockCode);
        // then
        assertThat(result).isTrue();
    }
}
