/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : BuyOrderServiceImplTest
 creation : 2024.2.1
 author : Yoonji Moon
 */

package com.devspacehub.ast.domain.orderTrading.service;

import com.devspacehub.ast.common.config.OpenApiProperties;
import com.devspacehub.ast.domain.itemInfo.ItemInfoRepository;
import com.devspacehub.ast.domain.marketStatus.dto.CurrentStockPriceExternalResDto;
import com.devspacehub.ast.domain.marketStatus.dto.DomStockTradingVolumeRankingExternalResDto;
import com.devspacehub.ast.domain.marketStatus.service.MarketStatusService;
import com.devspacehub.ast.domain.my.service.MyService;
import com.devspacehub.ast.domain.orderTrading.OrderTradingRepository;
import com.devspacehub.ast.util.OpenApiRequest;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static com.devspacehub.ast.common.constant.CommonConstants.OPENAPI_SUCCESS_RESULT_CODE;
import static com.devspacehub.ast.common.constant.StockPriceUnit.*;
import static com.devspacehub.ast.common.constant.YesNoStatus.NO;
import static com.devspacehub.ast.common.constant.YesNoStatus.YES;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class BuyOrderServiceImplTest {
    @InjectMocks
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
    @BeforeEach
    void setUp() {
//        buyOrderService = new BuyOrderServiceImpl(openApiRequest, openApiProperties, orderTradingRepository, myService, marketStatusService, itemInfoRepository);
        System.out.println("===========buyOrderService: " + buyOrderService.getClass());
        System.out.println("===========orderTradingRepository: " + orderTradingRepository);     // 왜 Null이지?
    }

    @Test
    @DisplayName("매수 수량 = ((예수금 * 분할 매수 주문 위한 퍼센트) / 분할매수 갯수) / 구매단가")
    void calculateOrderQuantity() {
        // given
        ReflectionTestUtils.setField(buyOrderService, "cashBuyOrderAmountPercent", 10);
        ReflectionTestUtils.setField(buyOrderService, "splitBuyCount", 3);

        final int myDeposit = 55555;
        final int calculatedOrderPrice = 980;
        // when
        int result = buyOrderService.calculateOrderQuantity(myDeposit, calculatedOrderPrice);

        // then
        assertThat(result).isEqualTo(1);
    }

    @Test
    @DisplayName("매수 수량이 0이면 True 반환하여 예수금이 부족함을 알 수 있다.")
    void isZero_success() {
        // given
        final int orderQuantity = 0;
        // when
        boolean result = buyOrderService.isZero(orderQuantity);
        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("지표 PER/PBR 초과 or 지표 시가총액 미만 or 지표 누적거래양 미만이면 매수할 수 없다.")
    void checkAccordingWithIndicators_falseCase() {
        // given
        ReflectionTestUtils.setField(buyOrderService, "txIdBuyOrder", txIdBuyOrder);
        ReflectionTestUtils.setField(buyOrderService, "limitPBR", limitPBR);
        ReflectionTestUtils.setField(buyOrderService, "limitPER", limitPER);
        ReflectionTestUtils.setField(buyOrderService, "limitHtsMarketCapital", limitHtsMarketCapital);
        ReflectionTestUtils.setField(buyOrderService, "limitAccumulationVolume", limitAccumulationVolume);

        final String perUnderLimit = "4.9";
        final String perOverLimit = "5.1";
        final String pbrUnderLimit = "99.0";
        final String pbrOverLimit = "101.0";
        final String marketCapitalizationUnderLimit = "299999999999";
        final String marketCapitalizationOverLimit = "300000000000";    // 3000억

        final String accumulationVolumeUnderLimit = "99999";
        final String accumulationVolumeOverLimit = "100000";

        CurrentStockPriceExternalResDto.CurrentStockPriceInfo falseByInvtCarefulYn = CurrentStockPriceInfoBuilder.buildWith(perUnderLimit, pbrUnderLimit, marketCapitalizationOverLimit, accumulationVolumeOverLimit, YES, NO, NO);

        CurrentStockPriceExternalResDto.CurrentStockPriceInfo falseByShortOverYn = CurrentStockPriceInfoBuilder.buildWith(perUnderLimit, pbrUnderLimit, marketCapitalizationOverLimit, accumulationVolumeOverLimit, NO, YES, NO);

        CurrentStockPriceExternalResDto.CurrentStockPriceInfo falseByDelistingYn = CurrentStockPriceInfoBuilder.buildWith(perUnderLimit, pbrUnderLimit, marketCapitalizationOverLimit, accumulationVolumeOverLimit, NO, NO, YES);

        CurrentStockPriceExternalResDto.CurrentStockPriceInfo falseByPER = CurrentStockPriceInfoBuilder.buildWith(perOverLimit, pbrUnderLimit, marketCapitalizationOverLimit, accumulationVolumeOverLimit, NO, NO, NO);

        CurrentStockPriceExternalResDto.CurrentStockPriceInfo falseByPBR = CurrentStockPriceInfoBuilder.buildWith(perUnderLimit, pbrOverLimit, marketCapitalizationOverLimit, accumulationVolumeOverLimit, NO, NO, NO);

        CurrentStockPriceExternalResDto.CurrentStockPriceInfo falseByHtsMarketCapitalization = CurrentStockPriceInfoBuilder.buildWith(perUnderLimit, pbrUnderLimit, marketCapitalizationUnderLimit, accumulationVolumeOverLimit, NO, NO, NO);

        CurrentStockPriceExternalResDto.CurrentStockPriceInfo falseByAccumulationVolume = CurrentStockPriceInfoBuilder.buildWith(perUnderLimit, pbrUnderLimit, marketCapitalizationOverLimit, accumulationVolumeUnderLimit, NO, NO, NO);

        // when & then
        assertThat(buyOrderService.checkAccordingWithIndicators(falseByInvtCarefulYn)).isFalse();
        assertThat(buyOrderService.checkAccordingWithIndicators(falseByShortOverYn)).isFalse();
        assertThat(buyOrderService.checkAccordingWithIndicators(falseByDelistingYn)).isFalse();
        assertThat(buyOrderService.checkAccordingWithIndicators(falseByPER)).isFalse();
        assertThat(buyOrderService.checkAccordingWithIndicators(falseByPBR)).isFalse();
        assertThat(buyOrderService.checkAccordingWithIndicators(falseByHtsMarketCapitalization)).isFalse();
        assertThat(buyOrderService.checkAccordingWithIndicators(falseByAccumulationVolume)).isFalse();
    }
    @Test
    @DisplayName("지표 PER/PBR 이하 + 지표 시가총액 이상 + 지표 누적거래양 이상일 때 매수할 수 있다.")
    void checkAccordingWithIndicators_trueCase() {
        // given
        ReflectionTestUtils.setField(buyOrderService, "txIdBuyOrder", txIdBuyOrder);
        ReflectionTestUtils.setField(buyOrderService, "limitPBR", limitPBR);
        ReflectionTestUtils.setField(buyOrderService, "limitPER", limitPER);
        ReflectionTestUtils.setField(buyOrderService, "limitHtsMarketCapital", limitHtsMarketCapital);
        ReflectionTestUtils.setField(buyOrderService, "limitAccumulationVolume", limitAccumulationVolume);

        final String perUnderLimit = "4.9";
        final String pbrUnderLimit = "99.9";
        final String marketCapitalizationOverLimit = "300000000000";
        final String accumulationVolumeOverLimit = "100000";

        CurrentStockPriceExternalResDto.CurrentStockPriceInfo falseByInvtCarefulYn = CurrentStockPriceInfoBuilder.buildWith(perUnderLimit, pbrUnderLimit, marketCapitalizationOverLimit, accumulationVolumeOverLimit, NO, NO, NO);

        // when
        boolean result = buyOrderService.checkAccordingWithIndicators(falseByInvtCarefulYn);
        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("호가 단위에 맞춰서 현재가의 자릿수를 세팅한다.")
    void orderPriceCuttingByPriceUnit() {
        final int currentPriceUnder2000 = 1999;
        final int currentPriceUnder5000 = 4999;
        final int currentPriceUnder20000 = 19999;
        final int currentPriceUnder50000 = 49999;
        final int currentPriceUnder200000 = 199999;
        final int currentPriceUnder500000 = 499999;
        final int currentPriceOver500000 = 500000;

        assertThat(buyOrderService.orderPriceCuttingByPriceUnit(currentPriceUnder2000, ONE.getCode())).isEqualTo(1999);
        assertThat(buyOrderService.orderPriceCuttingByPriceUnit(currentPriceUnder5000, FIVE.getCode())).isEqualTo(4995);
        assertThat(buyOrderService.orderPriceCuttingByPriceUnit(currentPriceUnder20000, TEN.getCode())).isEqualTo(19990);
        assertThat(buyOrderService.orderPriceCuttingByPriceUnit(currentPriceUnder50000, FIFTY.getCode())).isEqualTo(49950);
        assertThat(buyOrderService.orderPriceCuttingByPriceUnit(currentPriceUnder200000, HUNDRED.getCode())).isEqualTo(199900);
        assertThat(buyOrderService.orderPriceCuttingByPriceUnit(currentPriceUnder500000, FIVE_HUNDRED.getCode())).isEqualTo(499500);
        assertThat(buyOrderService.orderPriceCuttingByPriceUnit(currentPriceOver500000, THOUSAND.getCode())).isEqualTo(500000);
    }

    @Test
    @DisplayName("ITEM_INFO 테이블에 있고 금일 매수 주문한 이력이 없다면 True 반환한다.")
    void isStockItemBuyOrderable_true() {
        // given
        ReflectionTestUtils.setField(buyOrderService, "txIdBuyOrder", txIdBuyOrder);

        DomStockTradingVolumeRankingExternalResDto.StockInfo stockInfo = new DomStockTradingVolumeRankingExternalResDto.StockInfo("",
                "000155", "", "", "", "", "", "", "","", "", "",
                "", "", "", "", "", "", "");

        given(itemInfoRepository.countByItemCode("000155")).willReturn(1);
        given(orderTradingRepository.countByItemCodeAndOrderResultCodeAndTransactionIdAndRegistrationDateTimeBetween(
            "000155", OPENAPI_SUCCESS_RESULT_CODE, txIdBuyOrder,
                LocalDateTime.of(LocalDate.now(), LocalTime.of(0, 0,0)),
                LocalDateTime.of(LocalDate.now(), LocalTime.of(23,59, 59)))
        ).willReturn(0);

        // when
        boolean result = buyOrderService.isStockItemBuyOrderable(stockInfo);

        // then
        assertThat(result).isTrue();
    }
}