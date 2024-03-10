/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : BuyOrderIntegrationTest
 creation : 2024.3.9
 author : Yoonji Moon
 */

package com.devspacehub.ast.domain.orderTrading.service;

import com.devspacehub.ast.common.config.OpenApiProperties;
import com.devspacehub.ast.domain.itemInfo.ItemInfoRepository;
import com.devspacehub.ast.domain.marketStatus.dto.DomStockTradingVolumeRankingExternalResDto;
import com.devspacehub.ast.domain.marketStatus.service.MarketStatusService;
import com.devspacehub.ast.domain.my.service.MyService;
import com.devspacehub.ast.domain.orderTrading.OrderTrading;
import com.devspacehub.ast.domain.orderTrading.OrderTradingRepository;
import com.devspacehub.ast.util.OpenApiRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import static com.devspacehub.ast.common.constant.CommonConstants.OPENAPI_SUCCESS_RESULT_CODE;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class BuyOrderIntegrationTest {
    @Autowired
    private OpenApiRequest openApiRequest;
    @Autowired
    private OpenApiProperties openApiProperties;
    @Autowired
    private OrderTradingRepository orderTradingRepository;
    @Autowired
    private MyService myService;
    @Autowired
    private MarketStatusService marketStatusService;
    @Autowired
    private ItemInfoRepository itemInfoRepository;
    @Autowired
    private BuyOrderServiceImpl buyOrderService;

    private final String txIdBuyOrder = "TTTC0802U";
    private final String stockCode = "000000";

    @Test
    @DisplayName("ITEM_INFO 테이블에 없는 종목은 유효하지 않은 종목이다.")
    void isStockItemBuyOrderable_notInItemInfoTable() {
        // given
        DomStockTradingVolumeRankingExternalResDto.StockInfo stockInfo = new DomStockTradingVolumeRankingExternalResDto
                .StockInfo("",
                stockCode, "", "", "", "", "", "", "","", "", "",
                "", "", "", "", "", "", "");

        // when
        boolean result = buyOrderService.isStockItemBuyOrderable(stockInfo);
        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("이미 금일 매수 주문한 이력이 있다면 매수 불가능하다.")
    void isNewOrder_false() {
        // given
        ReflectionTestUtils.setField(buyOrderService, "txIdBuyOrder", txIdBuyOrder);

        OrderTrading saved = orderTradingRepository.save(OrderTrading.builder()
                .itemCode(stockCode)
                .itemNameKor("TEST 종목명")
                .orderDivision("00")
                .orderPrice(1000)
                .orderQuantity(1)
                .orderNumber("0000000000")
                .orderTime("123800")
                .orderMessage("주문 전송 완료 되었습니다.")
                .orderResultCode(OPENAPI_SUCCESS_RESULT_CODE)
                .transactionId(txIdBuyOrder)
                .build());
        // when
        boolean result = buyOrderService.isNewOrder(stockCode);
        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("금일 매수 주문한 이력이 없다면 매수 가능하다.")
    void isNewOrder_true() {
        // given
        ReflectionTestUtils.setField(buyOrderService, "txIdBuyOrder", txIdBuyOrder);
        // when
        boolean result = buyOrderService.isNewOrder(stockCode);
        // then
        assertThat(result).isTrue();
    }


}
