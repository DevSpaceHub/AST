/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : ReservationOrderInfoTest
 creation : 2024.3.26
 author : Yoonji Moon
 */

package com.devspacehub.ast.domain.my.reservationOrderInfo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class ReservationOrderInfoTest {

    @DisplayName("주문가를 업데이트한다.")
    @Test
    void updateToAdjustedPrice() {
        // given
        BigDecimal givenBeforeUpdate = new BigDecimal(3900);
        BigDecimal givenAfterUpdate = new BigDecimal(4000);
        ReservationOrderInfo reservationOrderInfo = ReservationOrderInfo.builder()
                .orderPrice(givenBeforeUpdate)
                .build();
        // when
        reservationOrderInfo.updateOrderPrice(givenAfterUpdate);
        // then
        assertThat(reservationOrderInfo.getOrderPrice()).isEqualTo(givenAfterUpdate);
    }

    @DisplayName("체결된 수량을 주문 수량에서 뺀다.")
    @Test
    void subtractConcludedQuantity() {
        // given
        int givenOrderQuantity = 10;
        int givenConcludedQuantity = 3;
        ReservationOrderInfo reservationOrderInfo = ReservationOrderInfo.builder()
                .orderQuantity(givenOrderQuantity)
                .conclusionQuantity(givenConcludedQuantity)
                .build();
        // when
        reservationOrderInfo.subtractConcludedQuantity(givenConcludedQuantity);

        // then
        assertThat(reservationOrderInfo.getOrderQuantity()).isEqualTo(7);
    }

    @Test
    @DisplayName("주문 희망 수량이 체결 수량과 동일한지 확인한다.")
    void return_true_when_orderQuantity_is_equal_to_conclusionQuantity() {
        ReservationOrderInfo given = ReservationOrderInfo.builder().orderQuantity(5).conclusionQuantity(5).build();

        boolean result = given.checkTotalConcluded();

        assertThat(result).isTrue();
    }

    @DisplayName("예약 매수 종목을 비활성화 처리한다.")
    @Test
    void disable_entity_by_setting_useYn_as_N() {
        ReservationOrderInfo given = ReservationOrderInfo.builder().useYn('Y').build();

        given.disable();

        assertThat(given.getUseYn()).isEqualTo('N');
    }
}