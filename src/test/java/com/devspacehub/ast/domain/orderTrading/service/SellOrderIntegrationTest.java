/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : SellOrderIntegrationTest
 creation : 2024.2.8
 author : Yoonji Moon
 */

package com.devspacehub.ast.domain.orderTrading.service;

import com.devspacehub.ast.common.constant.CommonConstants;
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
    private static final int MARKET_START_HOUR = 9;
    private static final int MARKET_END_HOUR = 16;

    // TODO 테스트코드 성공 위해 .registerDateTime() 을 세팅할 수 있도록 해야 한다.
    @Test
    @DisplayName("전달한 종목으로 전달한 기간 사이에 이미 성공한 매도 주문 이력이 있다면 False를 반환한다.")
    void isNewOrder_failed() {
        // given
        final String alreadyOrderedItemCode = "000000";
        LocalDate today = LocalDate.now();
        LocalDateTime givenDateTime = LocalDateTime.of(today, LocalTime.of(MARKET_START_HOUR, 30, 0));

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
                LocalDateTime.of(today, LocalTime.of(MARKET_START_HOUR, 0, 0)),
                LocalDateTime.of(today, LocalTime.of(MARKET_END_HOUR, 0, 0))
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
        LocalDateTime givenOrderDateTime = LocalDateTime.of(today, LocalTime.of(MARKET_START_HOUR, 30, 0));

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
                CommonConstants.DOMESTIC_MARKET_START_DATETIME_KST, CommonConstants.DOMESTIC_MARKET_END_DATETIME_KST);
        // then
        assertThat(result).isTrue();
    }
}
