/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : BuyOrderServiceImplTest
 creation : 2024.2.1
 author : Yoonji Moon
 */

package com.devspacehub.ast.domain.orderTrading.service;

import com.devspacehub.ast.common.config.OpenApiProperties;
import com.devspacehub.ast.domain.itemInfo.ItemInfoRepository;
import com.devspacehub.ast.domain.marketStatus.service.MarketStatusService;
import com.devspacehub.ast.domain.my.service.MyService;
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

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BuyOrderServiceImplTest {
    BuyOrderServiceImpl buyOrderService;

    @Mock
    OpenApiRequest openApiRequest;
    @Mock
    OpenApiProperties openApiProperties;
    @Mock
    OrderTradingRepository orderTradingRepository;
    @Mock
    MyService myService;
    @Mock
    MarketStatusService marketStatusService;
    @Mock
    ItemInfoRepository itemInfoRepository;
    private final String txIdBuyOrder = "TTTC0802U";
    private final float limitPBR = 100.0F;
    private final float limitPER = 5.0F;
    private final Long limitHtsMarketCapital = 300000000000L;
    private final Integer limitAccumulationVolume = 100000;
    @BeforeAll
    void setUp() {
        buyOrderService = new BuyOrderServiceImpl(openApiRequest, openApiProperties, orderTradingRepository, myService, marketStatusService, itemInfoRepository);
        ReflectionTestUtils.setField(buyOrderService, "txIdBuyOrder", txIdBuyOrder);
        ReflectionTestUtils.setField(buyOrderService, "limitPBR", limitPBR);
        ReflectionTestUtils.setField(buyOrderService, "limitPER", limitPER);
        ReflectionTestUtils.setField(buyOrderService, "limitHtsMarketCapital", limitHtsMarketCapital);
        ReflectionTestUtils.setField(buyOrderService, "limitAccumulationVolume", limitAccumulationVolume);
    }

    @Test
    @DisplayName("예수금을 현재가로 나눈 결과값을 정수로 정상 반환한다.")
    void calculateOrderQuantity_zero() {
        // given
        final int myDeposit = 100000;
        final int currentStockPrice = 11500;
        // when
        int result = buyOrderService.calculateOrderQuantity(myDeposit, currentStockPrice);

        // then
        assertThat(result).isZero();
    }

    @Test
    @DisplayName("매수 수량이 0이면 True 반환한다. 아니면 False")
    void isZero_success() {
        // given
        final int orderQuantity = 0;
        // when
        boolean result = buyOrderService.isZero(orderQuantity);
        // then
        assertThat(result).isTrue();
    }
}