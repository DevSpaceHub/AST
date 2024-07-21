/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : ReservationOrderInfoRepositoryTest
 creation : 2024.3.26
 author : Yoonji Moon
 */

package com.devspacehub.ast.domain.my.reservationOrderInfo;

import com.devspacehub.ast.common.config.QuerydslConfig;
import com.devspacehub.ast.common.constant.CommonConstants;
import com.devspacehub.ast.common.constant.ExchangeCode;
import com.devspacehub.ast.common.constant.YesNoStatus;
import com.devspacehub.ast.domain.itemInfo.ItemInfo;
import com.devspacehub.ast.domain.itemInfo.ItemInfoRepository;
import com.devspacehub.ast.domain.my.reservationOrderInfo.dto.ReservationStockItem;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@DataJpaTest
@Import(QuerydslConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ReservationOrderInfoRepositoryTest {
    @Autowired
    EntityManager em;
    @Autowired
    JPAQueryFactory queryFactory;
    @Autowired
    ReservationOrderInfoRepository reservationOrderInfoRepository;
    @Autowired
    ItemInfoRepository itemInfoRepository;

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
        reservationOrderInfoRepository.flush();

        // when
        List<ReservationOrderInfo> result = reservationOrderInfoRepository.findValidAll(givenNow);

        // then
        assertThat(result).hasSize(1)
                        .extracting("itemCode", "useYn")
                                .contains(tuple("000001", givenUseYnIsY));
    }

    @Test
    @DisplayName("ORDER_START_DATE가 현재와 같거나 과거이고, ORDER_END_DATE가 현재와 같거나 미래인 종목을 조회한다.")
    void findOrderableAll_DateCheck() {
        // given
        LocalDate givenStart = LocalDate.of(2021, 12, 20);
        LocalDate givenEnd = LocalDate.of(2021, 12, 21);
        LocalDate givenNow = LocalDate.of(2021, 12, 21);
        reservationOrderInfoRepository.save(ReservationOrderInfo.builder()
                .seq(0L)
                .itemCode("000000")
                .orderStartDate(givenStart)
                .orderEndDate(givenEnd)
                .useYn(YesNoStatus.YES.getCharCode())
                .priority(1)
                .build());
        reservationOrderInfoRepository.flush();

        // when
        List<ReservationOrderInfo> result = reservationOrderInfoRepository.findValidAll(givenNow);
        // then
        assertThat(result).hasSize(1)
                .extracting("orderStartDate", "orderEndDate", "itemCode")
                .contains(tuple(givenStart, givenEnd, "000000"));
    }

    @Test
    @DisplayName("priority 순으로 오름차순 조회한다.")
    void findOrderableAll_PriorityCheck() {
        // given
        LocalDate givenNow = LocalDate.of(2021, 12, 21);
        reservationOrderInfoRepository.save(ReservationOrderInfo.builder()
                .itemCode("000000")
                .orderStartDate(LocalDate.of(2021, 12, 20))
                .orderEndDate(LocalDate.of(2021, 12, 21))
                .useYn(YesNoStatus.YES.getCharCode())
                .priority(1)
                .build());
        reservationOrderInfoRepository.save(ReservationOrderInfo.builder()
                .itemCode("000001")
                .orderStartDate(LocalDate.of(2021, 12, 20))
                .orderEndDate(LocalDate.of(2021, 12, 21))
                .useYn(YesNoStatus.YES.getCharCode())
                .priority(2)
                .build());
        reservationOrderInfoRepository.flush();

        // when
        List<ReservationOrderInfo> result = reservationOrderInfoRepository.findValidAll(givenNow);
        // then
        assertThat(result).hasSize(2)
                .extracting("priority", "itemCode")
                .containsExactly(tuple(1, "000000"),
                        tuple(2, "000001")
                );
    }

    @DisplayName("데이터 등록 시 BaseEntity 클래스의 필드들도 값 세팅이 정상적으로 이루어진다.")
    @Test
    void findValidAllTest() {
        // given
        LocalDate givenDate = LocalDate.of(2021, 12, 21);
        LocalDate givenNow = LocalDate.now();
        reservationOrderInfoRepository.saveAndFlush(ReservationOrderInfo.builder()
                .itemCode("000000")
                .orderStartDate(LocalDate.of(2021, 12, 20))
                .orderEndDate(LocalDate.of(2021, 12, 21))
                .useYn(YesNoStatus.YES.getCharCode())
                .priority(1)
                .build());
        // when
        List<ReservationOrderInfo> result = reservationOrderInfoRepository.findValidAll(givenDate);

        // then
        assertThat(result).hasSize(1)
                .extracting("registrationId", "itemCode")
                .containsExactly(tuple(CommonConstants.REGISTER_ID, "000000"));
        assertThat(result.get(0).getRegistrationDateTime().toLocalDate()).isEqualTo(givenNow);
    }


    @DisplayName("인자로 전달하는 일자가 ORDER_START_DATE와 ORDER_END_DATE 사이에 있고, USE_YN이 Y이면 정상 조회된다.")
    @Test
    void findValidAllByExchangeCodesTest() {
        // given
        LocalDate givenNow = LocalDate.of(2021, 12, 21);

        reservationOrderInfoRepository.saveAndFlush(ReservationOrderInfo.builder()
                .itemCode("AAPL1")
                .orderStartDate(LocalDate.of(2021, 12, 20))
                .orderEndDate(LocalDate.of(2021, 12, 21))
                .useYn(YesNoStatus.YES.getCharCode())
                .priority(1)
                .orderQuantity(2)
                .orderPrice(new BigDecimal("2.9200"))
                .koreanItemName("애플")
                .build());
        itemInfoRepository.saveAndFlush(new ItemInfo("AAPL1", null, "애플", "Apple Inc. Common Stock", ExchangeCode.NASDAQ.getLongCode(), "", "", LocalDateTime.now(), "application", null, null));
        // when
        List<ReservationStockItem.Overseas> result = reservationOrderInfoRepository.findValidAllByExchangeCodes(givenNow,
                List.of(ExchangeCode.NEWYORK.getLongCode(), ExchangeCode.NASDAQ.getLongCode()));

        // then
        assertThat(result).hasSize(1)
                .extracting("exchangeCode", "itemCode", "orderQuantity", "itemNameKor", "orderPrice")
                .containsExactly(tuple(ExchangeCode.NASDAQ, "AAPL1", 2, "애플", new BigDecimal("2.9200")));
    }
}