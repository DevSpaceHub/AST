/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : ReservationOrderInfoRepositoryTest
 creation : 2024.3.26
 author : Yoonji Moon
 */

package com.devspacehub.ast.domain.my.reservationOrderInfo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
class ReservationOrderInfoRepositoryTest {
    @Autowired
    ReservationOrderInfoRepository reservationOrderInfoRepository;

    @Test
    @DisplayName("ORDER_START_DATE가 현재와 같거나 과거이고, ORDER_END_DATE가 현재와 같거나 미래인 종목을 조회한다.")
    void findAllByOrderStartDateBeforeAndOrderEndDateAfter() {
        // given
        LocalDate givenStart = LocalDate.now().minusDays(1);
        LocalDate givenEnd = LocalDate.now();
        reservationOrderInfoRepository.save(ReservationOrderInfo.builder()
                        .itemCode("000000")
                        .orderStartDate(givenStart)
                        .orderEndDate(givenEnd)
                        .orderPrice(1000)
                        .orderQuantity(1)
                        .useYn('Y')
                        .priority(1)
                        .koreanItemName("테스트 종목")
                .build());
        // when
        List<ReservationOrderInfo> result = reservationOrderInfoRepository
                .findAllByOrderStartDateBeforeOrOrderStartDateEqualsAndOrderEndDateAfterOrOrderEndDateEqualsOrderByPriority(
                        LocalDate.now(), LocalDate.now(), LocalDate.now(), LocalDate.now());
        // then
        assertThat(result).hasSize(1)
                .extracting("orderStartDate", "orderEndDate", "itemCode")
                .contains(tuple(givenStart, givenEnd, "000000"));
    }

    @DisplayName("USE_YN=Y인 데이터만 조회한다.")
    @Test
    void findOnlyUseYnEqualTo() {
        // given
        char givenUseYn = 'N';
        reservationOrderInfoRepository.save(ReservationOrderInfo.builder()
                .itemCode("000000")
                .orderStartDate(LocalDate.now())
                .orderEndDate(LocalDate.now())
                .orderPrice(1000)
                .orderQuantity(1)
                .useYn(givenUseYn)
                .priority(1)
                .koreanItemName("테스트 종목")
                .build());
        // when
        List<ReservationOrderInfo> result = reservationOrderInfoRepository.findAll();

        // then
        assertThat(result).isEmpty();
    }

}