/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : ReservationOrderInfoRepositoryTest
 creation : 2024.3.26
 author : Yoonji Moon
 */

package com.devspacehub.ast.domain.my.reservationOrderInfo;

import com.devspacehub.ast.common.config.QuerydslConfig;
import com.devspacehub.ast.common.constant.YesNoStatus;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@DataJpaTest
@Import(QuerydslConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
class ReservationOrderInfoRepositoryTest {
    @Autowired
    EntityManager em;
    @Autowired
    JPAQueryFactory queryFactory;
    @Autowired
    ReservationOrderInfoRepository reservationOrderInfoRepository;

    @Test
    @DisplayName("ORDER_START_DATE가 현재와 같거나 과거이고, ORDER_END_DATE가 현재와 같거나 미래인 종목을 조회한다.")
    void findAllByOrderStartDateBeforeAndOrderEndDateAfter() {
        // given
        LocalDate givenStart = LocalDate.parse("2021-12-20");
        LocalDate givenEnd = LocalDate.parse("2021-12-21");
        LocalDate givenNow = LocalDate.parse("2021-12-21");
        reservationOrderInfoRepository.save(ReservationOrderInfo.builder()
                .itemCode("000000")
                .orderStartDate(givenStart)
                .orderEndDate(givenEnd)
                .useYn(YesNoStatus.YES.getCharCode())
                .build());
        reservationOrderInfoRepository.flush();
        // when
        List<ReservationOrderInfo> result = reservationOrderInfoRepository
                .findValidAll(givenNow);
        // then
        assertThat(result).hasSize(1)
                .extracting("orderStartDate", "orderEndDate", "itemCode", "useYn")
                .contains(tuple(givenStart, givenEnd, "000000", YesNoStatus.YES.getCharCode()));
    }
    @DisplayName("유효한 예약 매수 종목 조회 시 USE_YN은 모두 Y이다.")
    @Test
    void findOnlyUseYnEqualTo() {
        // given
        LocalDate givenStart = LocalDate.parse("2021-12-20");
        LocalDate givenEnd = LocalDate.parse("2021-12-21");
        LocalDate givenNow = LocalDate.parse("2021-12-21");
        char givenUseYnIsY = YesNoStatus.YES.getCharCode();
        reservationOrderInfoRepository.save(ReservationOrderInfo.builder()
                .itemCode("000000")
                .orderStartDate(givenStart)
                .orderEndDate(givenEnd)
                .useYn(YesNoStatus.NO.getCharCode())
                .build());
        reservationOrderInfoRepository.save(ReservationOrderInfo.builder()
                .itemCode("000001")
                .orderStartDate(givenStart)
                .orderEndDate(givenEnd)
                .useYn(givenUseYnIsY)
                .build());

        // when
        List<ReservationOrderInfo> result = reservationOrderInfoRepository.findValidAll(givenNow);

        // then
        assertThat(result).hasSize(1)
                        .extracting("itemCode", "useYn")
                                .contains(tuple("000001", givenUseYnIsY));
    }

}