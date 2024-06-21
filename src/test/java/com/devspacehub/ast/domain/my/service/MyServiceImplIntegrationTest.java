/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : MyServiceImplIntegrationTest
 creation : 2024.4.27
 author : Yoonji Moon
 */

package com.devspacehub.ast.domain.my.service;

import com.devspacehub.ast.common.constant.CommonConstants;
import com.devspacehub.ast.common.constant.YesNoStatus;
import com.devspacehub.ast.domain.my.reservationOrderInfo.QReservationOrderInfo;
import com.devspacehub.ast.domain.my.reservationOrderInfo.ReservationOrderInfo;
import com.devspacehub.ast.domain.my.reservationOrderInfo.ReservationOrderInfoRepository;
import com.devspacehub.ast.domain.my.stockBalance.dto.response.StockBalanceApiResDto;
import com.devspacehub.ast.domain.orderTrading.dto.OrderConclusionDto;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Service - Repository 통합 테스트를 수행한다.
 */
@Transactional
@SpringBootTest
class MyServiceImplIntegrationTest {
    @Autowired
    MyServiceImpl myServiceImpl;
    @Autowired
    ReservationOrderInfoRepository reservationOrderInfoRepository;
    @Autowired
    JPAQueryFactory queryFactory;

    @DisplayName("체결 결과 정보에 해당하는 나의 예약 매수 종목을 조회하여 모두 체결 완료됐다면 비사용 처리하고 체결 수량을 업데이트한다.")
    @Test
    void updateMyReservationOrderUseYn() {
        // given
        String givenOrderNumber = "11111";
        String givenItemCode = "000000";
        BigDecimal givenPrice = BigDecimal.valueOf(1000);
        int givenQuantity = 1;
        LocalDate givenDate = LocalDate.of(2024, 4, 27);
        reservationOrderInfoRepository.save(ReservationOrderInfo.builder()
                .seq(0L)
                .itemCode(givenItemCode)
                .useYn(YesNoStatus.YES.getCharCode())
                .orderStartDate(givenDate)
                .orderEndDate(givenDate)
                .orderPrice(givenPrice)
                .orderQuantity(givenQuantity)
                .orderNumber(givenOrderNumber)
                .build());
        reservationOrderInfoRepository.flush();

        OrderConclusionDto givenOrderConclusionDto = OrderConclusionDto.builder()
                .itemCode(givenItemCode)
                .orderNumber(givenOrderNumber)
                .orderQuantity(givenQuantity)
                .orderPrice(givenPrice)
                .concludedPrice(givenPrice)
                .concludedQuantity(givenQuantity)
                .build();

        // when
        myServiceImpl.updateMyReservationOrderUseYn(givenOrderConclusionDto, givenDate);

        // then
        ReservationOrderInfo result = queryFactory.selectFrom(QReservationOrderInfo.reservationOrderInfo)
                .where(QReservationOrderInfo.reservationOrderInfo.itemCode.eq(givenItemCode))
                .fetchOne();
        assertThat(result.getUseYn()).isEqualTo(YesNoStatus.NO.getCharCode());
        assertThat(result.getConclusionQuantity()).isEqualTo(givenQuantity);
        assertThat(result.getUpdateId()).isEqualTo(CommonConstants.REGISTER_ID);
        assertThat(result.getUpdateDatetime().toLocalDate()).isEqualTo(LocalDate.now());
    }

}