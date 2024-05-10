/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : ReservationBuyOrderServiceIntegrationTest
 creation : 2024.3.22
 author : Yoonji Moon
 */

package com.devspacehub.ast.domain.my.reservationOrderInfo.service;

import com.devspacehub.ast.common.config.OpenApiProperties;
import com.devspacehub.ast.common.constant.OpenApiType;
import com.devspacehub.ast.domain.marketStatus.dto.CurrentStockPriceExternalResDto;
import com.devspacehub.ast.domain.marketStatus.service.MarketStatusService;
import com.devspacehub.ast.domain.my.reservationOrderInfo.ReservationOrderInfo;
import com.devspacehub.ast.domain.my.reservationOrderInfo.ReservationOrderInfoRepository;
import com.devspacehub.ast.domain.my.service.MyServiceImpl;
import com.devspacehub.ast.domain.orderTrading.OrderTrading;
import com.devspacehub.ast.domain.orderTrading.OrderTradingRepository;
import com.devspacehub.ast.domain.orderTrading.dto.DomesticStockOrderExternalReqDto;
import com.devspacehub.ast.domain.orderTrading.dto.DomesticStockOrderExternalResDto;
import com.devspacehub.ast.domain.orderTrading.service.CurrentStockPriceInfoBuilder;
import com.devspacehub.ast.util.OpenApiRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Consumer;

import static com.devspacehub.ast.common.constant.CommonConstants.OPENAPI_SUCCESS_RESULT_CODE;
import static com.devspacehub.ast.common.constant.CommonConstants.ORDER_DIVISION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@Transactional
@SpringBootTest
@ExtendWith(MockitoExtension.class)
class ReservationBuyOrderServiceIntegrationTest {
    @Autowired
    private ReservationBuyOrderServiceImpl reservationBuyOrderService;
    @MockBean
    private MarketStatusService marketStatusService;
    @MockBean
    private MyServiceImpl myService;
    @SpyBean
    private ReservationOrderInfoRepository reservationOrderInfoRepository;
    @Autowired
    private OrderTradingRepository orderTradingRepository;
    @MockBean
    private OpenApiProperties openApiProperties;
    @MockBean
    private OpenApiRequest openApiRequest;
    final String buyTxId = "VTTC0801U";

    @DisplayName("예약 매수 종목에 대해 매수 주문을 성공하면 최근 주문 번호로 업데이트한다.")
    @Test
    void order() {
        // given
        // 예약 매수 종목 저장
        ReservationOrderInfo givenEntity = ReservationOrderInfo.builder()
                .seq(0L)
                .orderPrice(9100)
                .orderQuantity(1)
                .itemCode("000000")
                .koreanItemName("테스트 종목")
                .useYn('Y')
                .orderStartDate(LocalDate.now().minusDays(1))
                .orderEndDate(LocalDate.now().plusDays(1))
                .priority(1)
                .conclusionQuantity(0)
                .orderNumber("")
                .build();
        reservationOrderInfoRepository.save(givenEntity);
        reservationOrderInfoRepository.flush();

        // 주문
        OpenApiType openApiType = OpenApiType.DOMESTIC_STOCK_RESERVATION_BUY_ORDER;
        DomesticStockOrderExternalResDto response = new DomesticStockOrderExternalResDto();
        response.setResultCode(OPENAPI_SUCCESS_RESULT_CODE);
        DomesticStockOrderExternalResDto.Output respOutput = new DomesticStockOrderExternalResDto.Output();
        respOutput.setOrderNumber("0000000000");
        response.setOutput(respOutput);
        given(openApiRequest.httpPostRequest(any(OpenApiType.class), any(Consumer.class), any(DomesticStockOrderExternalReqDto.class))).willReturn(response);

        String givenLowerLimitPrice = "9000";
        CurrentStockPriceExternalResDto.CurrentStockPriceInfo currentStockPriceResponseOutput = CurrentStockPriceInfoBuilder.buildWithLowerLimitPrice(givenLowerLimitPrice);
        CurrentStockPriceExternalResDto currentStockPriceResponse = new CurrentStockPriceExternalResDto();
        currentStockPriceResponse.setCurrentStockPriceInfo(currentStockPriceResponseOutput);
        currentStockPriceResponse.setResultCode(OPENAPI_SUCCESS_RESULT_CODE);

        given(reservationOrderInfoRepository.findValidAll(any(LocalDate.class))).willReturn(List.of(givenEntity));
        given(myService.getBuyOrderPossibleCash(givenEntity.getItemCode(), givenEntity.getOrderPrice(), ORDER_DIVISION)).willReturn(10000);
        given(marketStatusService.getCurrentStockPrice(givenEntity.getItemCode())).willReturn(currentStockPriceResponseOutput);

        // when
        reservationBuyOrderService.order(openApiProperties, openApiType, buyTxId);

        // then
        ReservationOrderInfo result = reservationOrderInfoRepository.findById(givenEntity.getSeq()).get();
        assertThat(result.getOrderNumber()).isEqualTo(response.getOutput().getOrderNumber());
    }

    // TODO 테스트코드 메서드 자체가 Transactional 적용이 되어 있어 Dirty Checking 위한 올바른 테스트가 아니다.
    @Deprecated
    @DisplayName("매수 예약 주문한 종목 주문 성공 시 최신 주문번호로 업데이트한다.")
    @Test
    void updateLatestOrderNumber() {
        // given
        String givenOldOrderNumber = "000";
        String givenNewOrderNumber = "001";
        ReservationOrderInfo given = ReservationOrderInfo.builder()
                .orderNumber(givenOldOrderNumber)
                .build();
        ReservationOrderInfo save = reservationOrderInfoRepository.save(given);

        DomesticStockOrderExternalResDto givenApiResult = new DomesticStockOrderExternalResDto();
        DomesticStockOrderExternalResDto.Output givenOutput = new DomesticStockOrderExternalResDto.Output();
        givenApiResult.setResultCode(OPENAPI_SUCCESS_RESULT_CODE);
        givenOutput.setOrderNumber(givenNewOrderNumber);
        givenApiResult.setOutput(givenOutput);

        // when
        reservationBuyOrderService.updateLatestOrderNumber(givenApiResult, save.getSeq());
        // then
        ReservationOrderInfo result = reservationOrderInfoRepository.findById(save.getSeq()).orElseThrow();
        assertThat(result.getOrderNumber()).isEqualTo(givenNewOrderNumber);
    }

    @DisplayName("예약 매수 주문 완료한 종목들의 주문 결과를 이력 테이블에 저장한다.")
    @Test
    void saveOrderInfos() {
        // given
        OrderTrading givenData = new OrderTrading(1L, "", "", buyTxId,
                "000", ORDER_DIVISION, 9100, 1, "", OPENAPI_SUCCESS_RESULT_CODE,
                null, null);
        List<OrderTrading> given = List.of(givenData);
        // when
        reservationBuyOrderService.saveOrderInfos(given);
        orderTradingRepository.flush();

        // then
        OrderTrading result = orderTradingRepository.findById(1L).get();
        assertThat(result.getSeq()).isEqualTo(givenData.getSeq());
        assertThat(result.getOrderNumber()).isEqualTo(givenData.getOrderNumber());
    }
}