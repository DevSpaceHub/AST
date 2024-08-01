/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : ReservationBuyOrderServiceImplTest
 creation : 2024.3.23
 author : Yoonji Moon
 */

package com.devspacehub.ast.domain.my.reservationOrderInfo.service;

import com.devspacehub.ast.common.constant.MarketType;
import com.devspacehub.ast.common.constant.YesNoStatus;
import com.devspacehub.ast.domain.marketStatus.dto.CurrentStockPriceExternalResDto;
import com.devspacehub.ast.domain.marketStatus.service.MarketStatusService;
import com.devspacehub.ast.domain.my.dto.MyServiceRequestDto;
import com.devspacehub.ast.domain.my.reservationOrderInfo.ReservationOrderInfo;
import com.devspacehub.ast.domain.my.reservationOrderInfo.dto.ReservationStockItem;
import com.devspacehub.ast.domain.my.service.MyServiceFactory;
import com.devspacehub.ast.domain.my.service.MyServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ReservationBuyOrderServiceImplTest {
    @Mock
    private MarketStatusService marketStatusService;
    @Mock
    private MyServiceImpl myService;
    @Mock
    private MyServiceFactory myServiceFactory;
    @InjectMocks
    private ReservationBuyOrderServiceImpl reservationBuyOrderService;

    @DisplayName("예약 매수 종목에 대해 희망 주문 가격이 하한가보다 큰 종목만 담는다.")
    @Test
    void pickStockItems_orderPriceEqualOrGreaterThanLowerLimitPrice() {
        // given
        BigDecimal giveOrderPrice = BigDecimal.valueOf(9100);
        BigDecimal givenLowerLimitPrice = BigDecimal.valueOf(9000);

        ReservationOrderInfo givenOrderedReservationOrderInfo = ReservationOrderInfo.builder()
                .itemCode("000000")
                .koreanItemName("주문될 테스트 종목")
                .orderPrice(giveOrderPrice)
                .orderQuantity(1)
                .orderStartDate(LocalDate.now().minusDays(1L))
                .orderEndDate(LocalDate.now().plusDays(1L))
                .priority(1)
                .useYn(YesNoStatus.YES.getCharCode())
                .conclusionQuantity(0)
                .build();
        CurrentStockPriceExternalResDto.CurrentStockPriceInfo givenCurrentStockPriceInfo = CurrentStockPriceExternalResDto.CurrentStockPriceInfo.builder()
                .stockLowerLimitPrice(givenLowerLimitPrice)
                .build();
        given(marketStatusService.getCurrentStockPrice(givenOrderedReservationOrderInfo.getItemCode())).willReturn(givenCurrentStockPriceInfo);
        given(myServiceFactory.resolveService(MarketType.DOMESTIC)).willReturn(myService);
        given(myService.getBuyOrderPossibleCash(any(MyServiceRequestDto.class))).willReturn(BigDecimal.valueOf(10000));

        // when
        List<ReservationStockItem> result = reservationBuyOrderService.pickStockItems(List.of(givenOrderedReservationOrderInfo));
        // then
        assertThat(result).hasSize(1)
                .extracting("itemCode", "orderPrice")
                .contains(tuple(givenOrderedReservationOrderInfo.getItemCode(), giveOrderPrice));
    }

    @DisplayName("예약 매수 종목에 대해 희망 주문 가격이 하한가보다 작으면 담지 않는다.")
    @Test
    void pickStockItems_orderPriceLowerThanLowerLimitPrice() {
        // given
        BigDecimal giveOrderPrice = BigDecimal.valueOf(8900);
        BigDecimal givenLowerLimitPrice = BigDecimal.valueOf(9100);
        ReservationOrderInfo givenReservationOrderInfo = ReservationOrderInfo.builder()
                .seq(0L)
                .itemCode("000001")
                .orderPrice(giveOrderPrice)
                .orderStartDate(LocalDate.now().minusDays(1L))
                .orderEndDate(LocalDate.now().plusDays(1L))
                .useYn(YesNoStatus.YES.getCharCode())
                .build();
        CurrentStockPriceExternalResDto.CurrentStockPriceInfo givenCurrentStockPriceInfo = CurrentStockPriceExternalResDto.CurrentStockPriceInfo.builder()
                .stockLowerLimitPrice(givenLowerLimitPrice)
                .build();
        given(marketStatusService.getCurrentStockPrice(givenReservationOrderInfo.getItemCode())).willReturn(givenCurrentStockPriceInfo);

        // when
        List<ReservationStockItem> result = reservationBuyOrderService.pickStockItems(List.of(givenReservationOrderInfo));

        // then
        assertThat(result).isEmpty();
    }
}