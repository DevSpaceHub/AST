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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static com.devspacehub.ast.common.constant.CommonConstants.OPENAPI_SUCCESS_RESULT_CODE;
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
    private final float maxPBR = 100.0F;
    private final float maxPER = 5.0F;
    private final Long minMarketCapital = 300000000000L;
    private final Integer minAccumulationTradingVolume = 100000;

    @Test
    @DisplayName("매수 수량 = ((예수금 * 분할 매수 주문 위한 퍼센트) / 분할매수 갯수) / 구매단가")
    void calculateOrderQuantity() {
        // given
        ReflectionTestUtils.setField(buyOrderService, "cashBuyOrderAmountPercent",  new BigDecimal(10));
        ReflectionTestUtils.setField(buyOrderService, "splitBuyCount", new BigDecimal(3));

        final BigDecimal myDeposit = BigDecimal.valueOf(55555);
        final BigDecimal calculatedOrderPrice = BigDecimal.valueOf(980);
        // when
        int result = buyOrderService.calculateOrderQuantity(myDeposit, calculatedOrderPrice);

        // then
        assertThat(result).isEqualTo(1);
    }

    @Test
    @DisplayName("지표 PER/PBR 초과 or 지표 시가총액 미만 or 지표 누적거래양 미만이면 매수할 수 없다.")
    void checkAccordingWithIndicators_falseCase() {
        // given
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
        ReflectionTestUtils.setField(buyOrderService, "maxPBR", maxPBR);
        ReflectionTestUtils.setField(buyOrderService, "maxPER", maxPER);
        ReflectionTestUtils.setField(buyOrderService, "minMarketCapital", minMarketCapital);
        ReflectionTestUtils.setField(buyOrderService, "minAccumulationTradingVolume", minAccumulationTradingVolume);

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
    @DisplayName("금일 매수 주문한 이력이 없다면 True 반환한다.")
    void isStockItemBuyOrderable_true() {
        // given
        LocalDateTime givenMarketStart = LocalDateTime.of(LocalDate.now(), LocalTime.of(9, 0));
        LocalDateTime givenMarketEnd = LocalDateTime.of(LocalDate.now(), LocalTime.of(16, 0));
        String givenItemCode = "000155";

        given(orderTradingRepository.countByItemCodeAndOrderResultCodeAndTransactionIdAndRegistrationDateTimeBetween(
            "000155", OPENAPI_SUCCESS_RESULT_CODE, txIdBuyOrder, givenMarketStart, givenMarketEnd)
        ).willReturn(0);

        // when
        boolean result = buyOrderService.isStockItemBuyOrderable(givenItemCode, txIdBuyOrder, givenMarketStart, givenMarketEnd);

        // then
        assertThat(result).isTrue();
    }

}