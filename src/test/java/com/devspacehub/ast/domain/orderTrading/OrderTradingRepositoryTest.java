/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : OrderTradingRepositoryTest
 creation : 2024.6.7
 author : Yoonji Moon
 */
package com.devspacehub.ast.domain.orderTrading;

import com.devspacehub.ast.common.config.QuerydslConfig;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * OrderTradingRepository 단위 테스트 코드
 */
@DataJpaTest
@Import(QuerydslConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class OrderTradingRepositoryTest {
    @Autowired
    EntityManager em;
    @Autowired
    JPAQueryFactory queryFactory;
    @Autowired
    OrderTradingRepository orderTradingRepository;

    @DisplayName("특정 시간대 사이에 특정 종목으로 주문에 성공한 데이터 갯수를 조회한다.")
    @Test
    void countByItemCodeAndOrderResultCodeAndTransactionIdAndRegistrationDateTimeBetween() {
        // given
        LocalDateTime givenOrderedDateTimeStart = LocalDateTime.now().minusMinutes(1L);
        LocalDateTime givenOrderedDateTimeEnd = LocalDateTime.now().plusMinutes(1L);
        OrderTrading entity1 = OrderTrading.builder()
                .itemCode("000000")
                .orderNumber("1111")
                .orderResultCode("0")
                .transactionId("TEST")
                .build();
        OrderTrading entity2 = OrderTrading.builder()
                .itemCode("000000")
                .orderNumber("1112")
                .orderResultCode("0")
                .transactionId("TEST")
                .build();
        orderTradingRepository.saveAll(List.of(entity1, entity2));
        orderTradingRepository.flush();

        // when
        int result = orderTradingRepository.countByItemCodeAndOrderResultCodeAndTransactionIdAndRegistrationDateTimeBetween(
                "000000", "0", "TEST",
                givenOrderedDateTimeStart,
                givenOrderedDateTimeEnd);
        // then
        assertThat(result).isEqualTo(2);
    }

    @DisplayName("BigDecimal 타입의 orderPrice 칼럼이 정상 저장 및 반환된다.")
    @Test
    void selectBigDecimalType() {
        // given
        BigDecimal givenPriceBigDecimalType = BigDecimal.valueOf(95000);
        OrderTrading savedEntity = orderTradingRepository.save(OrderTrading.builder()
                .itemCode("000000")
                .orderPrice(givenPriceBigDecimalType)
                .build());
        orderTradingRepository.flush();
        // when
        OrderTrading result = orderTradingRepository.findById(savedEntity.getSeq())
                .orElseThrow(EntityNotFoundException::new);
        // then
        assertThat(result.getOrderPrice()).isEqualTo(givenPriceBigDecimalType);
        assertThat(result.getOrderPrice().intValue()).isEqualTo(95000);
    }
}