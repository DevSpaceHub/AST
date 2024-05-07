/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : StockItemDtoTest
 creation : 2024.4.29
 author : Yoonji Moon
 */

package com.devspacehub.ast.domain.marketStatus.dto;

import com.devspacehub.ast.domain.my.reservationOrderInfo.ReservationOrderInfo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * StockItemDto 테스트
*/
class StockItemDtoTest {
    @DisplayName("ReservationOrderInfo 엔티티를 받아서 ReservationStockItem으로 변환한다.")
    @Test
    void of_ReservationStockItem() {
        // given
        ReservationOrderInfo givenReservationStockItem = ReservationOrderInfo.builder()
                .seq(0L)
                .itemCode("00000")
                .build();
        // when
        StockItemDto.ReservationStockItem result = StockItemDto.ReservationStockItem.of(givenReservationStockItem);
        // then
        assertThat(result.getReservationSeq()).isEqualTo(givenReservationStockItem.getSeq());
        assertThat(result.getItemCode()).isEqualTo(givenReservationStockItem.getItemCode());
    }
}