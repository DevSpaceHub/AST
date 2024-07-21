/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : ReservationStockItemTest
 creation : 2024.7.4
 author : Yoonji Moon
 */

package com.devspacehub.ast.domain.my.reservationOrderInfo.dto;

import com.devspacehub.ast.domain.my.reservationOrderInfo.ReservationOrderInfo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ReservationStockItemTest {
    @DisplayName("ReservationOrderInfo 엔티티를 받아서 ReservationStockItem으로 변환한다.")
    @Test
    void of_ReservationStockItem() {
        ReservationOrderInfo givenReservationStockItem = ReservationOrderInfo.builder()
                .seq(0L)
                .itemCode("00000")
                .build();

        ReservationStockItem.Domestic result = ReservationStockItem.Domestic.of(givenReservationStockItem);

        assertThat(result.getReservationSeq()).isEqualTo(givenReservationStockItem.getSeq());
        assertThat(result.getStockItem().getItemCode()).isEqualTo(givenReservationStockItem.getItemCode());
    }
}