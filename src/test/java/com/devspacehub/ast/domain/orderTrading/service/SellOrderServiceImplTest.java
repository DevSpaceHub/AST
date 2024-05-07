/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : SellOrderServiceImplTest
 creation : 2024.2.5
 author : Yoonji Moon
 */

package com.devspacehub.ast.domain.orderTrading.service;

import com.devspacehub.ast.domain.marketStatus.dto.StockItemDto;
import com.devspacehub.ast.domain.my.stockBalance.dto.response.StockBalanceExternalResDto;
import com.devspacehub.ast.domain.my.service.MyService;
import com.devspacehub.ast.domain.notification.Notificator;
import com.devspacehub.ast.domain.orderTrading.OrderTradingRepository;
import com.devspacehub.ast.util.OpenApiRequest;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static com.devspacehub.ast.common.constant.CommonConstants.OPENAPI_SUCCESS_RESULT_CODE;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class SellOrderServiceImplTest {
    SellOrderServiceImpl sellOrderService;
    @Mock
    OpenApiRequest openApiRequest;
    @Mock
    Notificator notificator;
    @Mock
    MyService myService;
    @Mock
    OrderTradingRepository orderTradingRepository;

    private final String sellOrderTxId = "VTTC0801U";

    @BeforeEach
    void setUp() {
        Float stopLossSellRatioDeadline = -5.0F;
        Float profitSellDeadline = 10.0F;
        sellOrderService = new SellOrderServiceImpl(openApiRequest, orderTradingRepository, notificator, myService);
        ReflectionTestUtils.setField(sellOrderService, "stopLossSellRatio", stopLossSellRatioDeadline);
        ReflectionTestUtils.setField(sellOrderService, "profitSellRatio", profitSellDeadline);
    }

    @Test
    @DisplayName("평가손익률이 지표 사이에 있다면 True 반환하여 매도할 수 없음을 알린다.")
    void isEvaluateProfitLossRateBetweenProfitAndStopLossPercent() {
        // True
        final String lossEvaluateProfitLossRate = "10.01";
        assertThat(sellOrderService.isEvaluateProfitLossRateBetweenProfitAndStopLossPercent(lossEvaluateProfitLossRate)).isFalse();

        final String profitEvaluateLossRate = "-5.1";
        assertThat(sellOrderService.isEvaluateProfitLossRateBetweenProfitAndStopLossPercent(profitEvaluateLossRate)).isFalse();

        // False
        final String unsoldLossEvaluateProfitLossRate = "9.99";
        assertThat(sellOrderService.isEvaluateProfitLossRateBetweenProfitAndStopLossPercent(unsoldLossEvaluateProfitLossRate)).isTrue();
        final String unsoldProfitEvaluateProfitLossRate = "-0.22";
        assertThat(sellOrderService.isEvaluateProfitLossRateBetweenProfitAndStopLossPercent(unsoldProfitEvaluateProfitLossRate)).isTrue();
    }
    @Test
    @DisplayName("주식 잔고 조회 시 보유 수량이 0이면 이미 체결된 주식이기 때문에 매도 주문할 수 없다.")
    void isStockItemSellOrderable() {
        // given
        StockBalanceExternalResDto.MyStockBalance myStockBalance =
                new StockBalanceExternalResDto.MyStockBalance("", "", "", "", "", "", "",
                        "0", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "");

        // when
        boolean result = sellOrderService.isStockItemSellOrderable(myStockBalance, sellOrderTxId);
        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("보유 수량이 1개 이상이고 주문 성공 이력이 없고 매도 지표에 부합하면 매도 주문 종목 리스트에 포함한다.")
    void pickStockItemsIncluded() {
        // given
        StockBalanceExternalResDto.MyStockBalance givenLossSellItem = new StockBalanceExternalResDto.MyStockBalance(
                "000002", "", "", "", "", "", "",
                "1", "", "","",
                "2000", "", "",
                "-5.1", "", "", "", "", "", "", "", "", "", "", "");

        StockBalanceExternalResDto.MyStockBalance givenProfitSellItem = new StockBalanceExternalResDto.MyStockBalance(
                "000003", "", "", "", "", "", "",
                "2", "", "","",
                "3000", "", "",
                "10.1", "", "", "", "", "", "", "", "", "", "", "");

        StockBalanceExternalResDto dto = StockBalanceExternalResDto.builder()
                .resultCode(OPENAPI_SUCCESS_RESULT_CODE)
                .output2(null)
                .myStockBalance(List.of(givenLossSellItem, givenProfitSellItem))
                .build();

        given(orderTradingRepository.countByItemCodeAndOrderResultCodeAndTransactionIdAndRegistrationDateTimeBetween(
                givenLossSellItem.getItemCode(), OPENAPI_SUCCESS_RESULT_CODE, sellOrderTxId,
                LocalDateTime.of(LocalDate.now(), LocalTime.of(0, 0, 0)),
                LocalDateTime.of(LocalDate.now(), LocalTime.of(23, 59, 59)))
        ).willReturn(0);
        given(orderTradingRepository.countByItemCodeAndOrderResultCodeAndTransactionIdAndRegistrationDateTimeBetween(
                givenProfitSellItem.getItemCode(), OPENAPI_SUCCESS_RESULT_CODE, sellOrderTxId,
                LocalDateTime.of(LocalDate.now(), LocalTime.of(0, 0, 0)),
                LocalDateTime.of(LocalDate.now(), LocalTime.of(23, 59, 59)))
        ).willReturn(0);

        // when
        List<StockItemDto> result = sellOrderService.pickStockItems(dto, sellOrderTxId);

        // then
        assertThat(result).hasSize(2);
        assertThat(result).extracting("itemCode").containsExactly(givenLossSellItem.getItemCode(), givenProfitSellItem.getItemCode());
    }

    @Test
    @DisplayName("이미 체결된 종목이거나 매도 주문한 이력이 있으면 매도 주문 종목 리스트에 포함하지 않는다.")
    void pickStockItemsNotIncluded() {
        // given
        StockBalanceExternalResDto.MyStockBalance alreadyOrdered = new StockBalanceExternalResDto.MyStockBalance(
                "000000", "", "", "", "", "", "",
                "1", "", "","",
                "1000", "", "",
                "10.1", "", "", "", "", "", "", "", "", "", "", "");

        StockBalanceExternalResDto.MyStockBalance alreadyConcluded = new StockBalanceExternalResDto.MyStockBalance(
                "000001", "", "", "", "", "", "",
                "0", "", "","",
                "1500", "", "",
                "10.1", "", "", "", "", "", "", "", "", "", "", "");

        StockBalanceExternalResDto dto = StockBalanceExternalResDto.builder()
                .resultCode(OPENAPI_SUCCESS_RESULT_CODE)
                .output2(null)
                .myStockBalance(List.of(alreadyOrdered, alreadyConcluded))
                .build();
        given(orderTradingRepository.countByItemCodeAndOrderResultCodeAndTransactionIdAndRegistrationDateTimeBetween(
                alreadyOrdered.getItemCode(), OPENAPI_SUCCESS_RESULT_CODE, sellOrderTxId,
                LocalDateTime.of(LocalDate.now(), LocalTime.of(0, 0, 0)),
                LocalDateTime.of(LocalDate.now(), LocalTime.of(23, 59, 59)))
        ).willReturn(1);

        // when
        List<StockItemDto> result = sellOrderService.pickStockItems(dto, sellOrderTxId);

        // then
        assertThat(result).isEmpty();
    }
}