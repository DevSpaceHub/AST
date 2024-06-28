/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : StockItemDtoTest
 creation : 2024.4.29
 author : Yoonji Moon
 */

package com.devspacehub.ast.domain.marketStatus.dto;

import com.devspacehub.ast.domain.my.reservationOrderInfo.ReservationOrderInfo;
import com.devspacehub.ast.domain.my.stockBalance.dto.response.overseas.OverseasStockBalanceApiResDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
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

    @Test
    void of_overseas() {
        // given
        BigDecimal givenOrderPrice = new BigDecimal("142.103900");
        OverseasStockBalanceApiResDto.MyStockBalance given = new OverseasStockBalanceApiResDto.MyStockBalance(
                "", "", "", "APPL", "애플", "", null, "", "", 10, "", "", givenOrderPrice, "", "NASD", "", "", ""
        );

        // when
        StockItemDto result = StockItemDto.Overseas.of(given);
        // then
        assertThat(result.getOrderPrice()).isEqualTo("142.1039");
        assertThat(result.getItemNameKor()).isEqualTo("애플");
        assertThat(result.getItemCode()).isEqualTo("APPL");
    }
}