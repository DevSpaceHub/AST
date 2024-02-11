/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : SellOrderServiceImplTest
 creation : 2024.2.5
 author : Yoonji Moon
 */

package com.devspacehub.ast.domain.orderTrading.service;

import com.devspacehub.ast.common.config.OpenApiProperties;
import com.devspacehub.ast.domain.orderTrading.OrderTradingRepository;
import com.devspacehub.ast.openApiUtil.OpenApiRequest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SellOrderServiceImplTest {
    SellOrderServiceImpl sellOrderService;

    @Mock
    OpenApiRequest openApiRequest;
    @Mock
    OpenApiProperties openApiProperties;
    @Mock
    OrderTradingRepository orderTradingRepository;

    private Float stopLossSellRatioDeadline = -5.0F;
    private Float profitSellDeadline = 10.0F;

    @BeforeAll
    void setUp() {
        sellOrderService = new SellOrderServiceImpl(openApiRequest, openApiProperties, orderTradingRepository);
        ReflectionTestUtils.setField(sellOrderService, "stopLossSellRatio", stopLossSellRatioDeadline);
        ReflectionTestUtils.setField(sellOrderService, "profitSellRatio", profitSellDeadline);
    }

    @Test
    @DisplayName("평가손익률이 지표에 부합하다면 True 반환한다. 반대는 False")
    void isSellStockItem() {
        // True
        final String lossEvaluateProfitLossRate = "10.01";
        assertThat(sellOrderService.compareEvaluateProfitLossRate(lossEvaluateProfitLossRate)).isTrue();

        final String profitEvaluateLossRate = "-5.1";
        assertThat(sellOrderService.compareEvaluateProfitLossRate(profitEvaluateLossRate)).isTrue();

        // False
        final String unsoldLossEvaluateProfitLossRate = "9.99";
        assertThat(sellOrderService.compareEvaluateProfitLossRate(unsoldLossEvaluateProfitLossRate)).isFalse();
        final String unsoldProfitEvaluateProfitLossRate = "-0.22";
        assertThat(sellOrderService.compareEvaluateProfitLossRate(unsoldProfitEvaluateProfitLossRate)).isFalse();
    }
}