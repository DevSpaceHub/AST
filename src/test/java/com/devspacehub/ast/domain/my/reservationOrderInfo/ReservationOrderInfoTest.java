/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : ReservationOrderInfoTest
 creation : 2024.3.26
 author : Yoonji Moon
 */

package com.devspacehub.ast.domain.my.reservationOrderInfo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ReservationOrderInfoTest {
    @DisplayName("주문가가 인자로 전달받는 하한가보다 크거나 동일하면 False 반환한다.")
    @Test
    void isOrderPriceGreaterOrEqualThan() {
        // given
        int givenOrderPrice = 3900;
        ReservationOrderInfo reservationOrderInfo = ReservationOrderInfo.builder()
                .orderPrice(givenOrderPrice)
                .build();
        // when
        boolean result1 = reservationOrderInfo.isOrderPriceLowerThan(3900);
        boolean result2 = reservationOrderInfo.isOrderPriceLowerThan(3800);
        // then
        assertThat(result1).isFalse();
        assertThat(result2).isFalse();
    }

    @DisplayName("주문가를 업데이트한다.")
    @Test
    void updateToAdjustedPrice() {
        // given
        int givenBeforeUpdate = 3900;
        int givenAfterUpdate = 4000;
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
}