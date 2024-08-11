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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static com.devspacehub.ast.common.constant.CommonConstants.ORDER_DIVISION;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class SellOrderIntegrationTest {
    @Autowired
    private OrderTradingRepository orderTradingRepository;
    @Autowired
    private SellOrderServiceImpl sellOrderService;
    @Value("${openapi.rest.transaction-id.domestic.sell-order}")
    private String sellTxId;
    private static final LocalTime MARKET_OPEN_TIME = LocalTime.of(9, 0, 0);
    private static final LocalTime MARKET_CLOSE_TIME = LocalTime.of(16, 0, 0);

    @Test
    @DisplayName("전달한 종목으로 전달한 기간 사이에 이미 성공한 매도 주문 이력이 있다면 False를 반환한다.")
    void isNewOrder_failed() {
        // given
        final String alreadyOrderedItemCode = "000000";
        LocalDate today = LocalDate.now();
        LocalDateTime givenDateTime = LocalDateTime.of(today, LocalTime.of(9, 30, 0));

        orderTradingRepository.save(OrderTrading.builder()
                .seq(0L)
                .orderDivision(ORDER_DIVISION)
                .itemCode(alreadyOrderedItemCode)
                .itemNameKor("한화생명")
                .orderQuantity(2)
                .orderPrice(BigDecimal.valueOf(4385))
                .orderNumber("0000151228")
                .orderMessage("주문 전송 완료 되었습니다.")
                .orderMessageCode("APBK0013")
                .orderResultCode("0")
                .transactionId(sellTxId)
                .orderTime("093000")
                .registrationDateTime(givenDateTime)
                .build());
        orderTradingRepository.flush();

        // when
        boolean result = sellOrderService.isNewOrder(alreadyOrderedItemCode, sellTxId,
                LocalDateTime.of(today, MARKET_OPEN_TIME), LocalDateTime.of(today, MARKET_CLOSE_TIME)
        );
        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("전달한 종목으로 전달한 기간 사이에 매도 주문이 1개도 없다면 True를 반환한다.")
    void isNewOrder_success() {
        // given
        final String alreadyOrderedItemCode = "000000";
        final String notOrderedItemCode = "100000";
        LocalDate today = LocalDate.now();
        LocalDateTime givenOrderDateTime = LocalDateTime.of(today, LocalTime.of(9, 30, 0));

        orderTradingRepository.save(OrderTrading.builder()
                .seq(0L)
                .orderDivision(ORDER_DIVISION)
                .itemCode(alreadyOrderedItemCode)
                .itemNameKor("한화생명")
                .orderQuantity(2)
                .orderPrice(BigDecimal.valueOf(4385))
                .orderNumber("0000151228")
                .orderMessage("주문 전송 완료 되었습니다.")
                .orderMessageCode("APBK0013")
                .orderResultCode("0")
                .transactionId(sellTxId)
                .orderTime("090008")
                .registrationDateTime(givenOrderDateTime)
                .build());
        orderTradingRepository.flush();

        // when
        boolean result = sellOrderService.isNewOrder(notOrderedItemCode, sellTxId,
                LocalDateTime.of(today, MARKET_OPEN_TIME), LocalDateTime.of(today, MARKET_CLOSE_TIME));

        // then
        assertThat(result).isTrue();
    }
}
