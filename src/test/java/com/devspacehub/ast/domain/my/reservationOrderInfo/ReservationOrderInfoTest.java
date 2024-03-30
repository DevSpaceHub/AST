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
    @DisplayName("주문가가 인자로 전달받는 하한가보다 크거나 같은지 비교한다.")
    @Test
    void isOrderPriceGreaterOrEqualThan() {
        // given
        int givenOrderPrice = 3900;
        ReservationOrderInfo reservationOrderInfo = ReservationOrderInfo.builder()
                .orderPrice(givenOrderPrice)
                .build();
        // when
        boolean resultSuccess = reservationOrderInfo.isOrderPriceGreaterOrEqualThan(3900);
        boolean resultFailed = reservationOrderInfo.isOrderPriceGreaterOrEqualThan(4000);
        // then
        assertThat(resultSuccess).isTrue();
        assertThat(resultFailed).isFalse();
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
        reservationOrderInfo.updateToAdjustedPrice(givenAfterUpdate);
        // then
        assertThat(reservationOrderInfo.getOrderPrice()).isEqualTo(givenAfterUpdate);
    }
}