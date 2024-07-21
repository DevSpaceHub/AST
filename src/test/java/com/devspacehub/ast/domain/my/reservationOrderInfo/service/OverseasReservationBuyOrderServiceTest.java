/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : OverseasReservationBuyOrderServiceTest
 creation : 2024.6.29
 author : Yoonji Moon
 */

package com.devspacehub.ast.domain.my.reservationOrderInfo.service;

import com.devspacehub.ast.common.config.OpenApiProperties;
import com.devspacehub.ast.common.constant.ExchangeCode;
import com.devspacehub.ast.common.constant.OpenApiType;
import com.devspacehub.ast.common.dto.WebClientCommonResDto;
import com.devspacehub.ast.domain.marketStatus.dto.StockItemDto;
import com.devspacehub.ast.domain.my.reservationOrderInfo.ReservationOrderInfoRepository;
import com.devspacehub.ast.domain.my.reservationOrderInfo.dto.ReservationStockItem;
import com.devspacehub.ast.domain.notification.Notificator;
import com.devspacehub.ast.domain.notification.dto.MessageContentDto;
import com.devspacehub.ast.domain.orderTrading.OrderTrading;
import com.devspacehub.ast.domain.orderTrading.OrderTradingRepository;
import com.devspacehub.ast.domain.orderTrading.dto.OverseasStockOrderApiReqDto;
import com.devspacehub.ast.domain.orderTrading.dto.StockOrderApiResDto;
import com.devspacehub.ast.util.OpenApiRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import static com.devspacehub.ast.common.constant.CommonConstants.OPENAPI_SUCCESS_RESULT_CODE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OverseasReservationBuyOrderServiceTest {
    @InjectMocks
    OverseasReservationBuyOrderService overseasReservationBuyOrderService;
    @Mock
    OpenApiProperties openApiProperties;
    @Mock
    OpenApiRequest openApiRequest;
    @Mock
    ReservationOrderInfoRepository reservationOrderInfoRepository;
    @Mock
    OrderTradingRepository orderTradingRepository;
    @Mock
    Notificator notificator;


    @DisplayName("DB에 유효한 예약 종목이 없으면 빈 값을 반환한다.")
    @Test
    void return_empty_list_when_validate_reservationItems_are_empty() {
        given(reservationOrderInfoRepository.findValidAll(LocalDate.now())).willReturn(Collections.emptyList());

        List<OrderTrading> results = overseasReservationBuyOrderService.order(openApiProperties, OpenApiType.OVERSEAS_STOCK_RESERVATION_BUY_ORDER);

        assertThat(results).isEmpty();
    }


    @DisplayName("원하는 종목과 그 종목의 주문, 수량 데이터를 포함하여 매수 주문을 요청한 뒤 응답받은 DTO를 반환한다.")
    @Test
    void return_response_after_buy_order_openApi_request() {
        ReservationStockItem.Overseas reservationStockItem = ReservationStockItem.Overseas.builder()
                .exchangeCode(ExchangeCode.NASDAQ)
                .build();

        given(openApiProperties.getOauth()).willReturn("oauth");
        given(openApiProperties.getAccntNumber()).willReturn("123456");
        given(openApiProperties.getAccntProductCode()).willReturn("01");

        StockOrderApiResDto given = new StockOrderApiResDto();
        given.setOutput(new StockOrderApiResDto.Output());
        given.setResultCode(OPENAPI_SUCCESS_RESULT_CODE);
        given(openApiRequest.httpPostRequest(any(OpenApiType.class), any(Consumer.class), any(OverseasStockOrderApiReqDto.class))).willReturn(given);

        WebClientCommonResDto result = overseasReservationBuyOrderService.callOrderApi(openApiProperties, reservationStockItem.getStockItem(), OpenApiType.OVERSEAS_STOCK_RESERVATION_BUY_ORDER, "txId");

        assertThat(result)
                .isNotNull()
                .isInstanceOf(StockOrderApiResDto.class);
        assertThat(result.isSuccess()).isTrue();
        verify(openApiRequest).httpPostRequest(any(OpenApiType.class), any(Consumer.class), any(OverseasStockOrderApiReqDto.class));
    }

    @DisplayName("오늘 기준으로 유효한 예약 종목 데이터들을 조회하여 반환한다.")
    @Test
    void return_valid_reservations_for_today() {
        ReservationStockItem.Overseas givenReservationDto = ReservationStockItem.Overseas.builder()
                .stockItem(StockItemDto.builder()
                        .itemCode("AAPL")
                        .orderQuantity(1)
                        .orderPrice(new BigDecimal("121.1011"))
                        .build())
                .reservationSeq(1L)
                .build();
        given(reservationOrderInfoRepository.findValidAllByExchangeCodes(LocalDate.now(),
                List.of(ExchangeCode.NASDAQ.getLongCode(), ExchangeCode.NEWYORK.getLongCode()))).willReturn(List.of(givenReservationDto));

        List<ReservationStockItem.Overseas> result = overseasReservationBuyOrderService.getValidReservationsForToday();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStockItem().getItemCode()).isEqualTo("AAPL");
        assertThat(result.get(0).getStockItem().getOrderQuantity()).isEqualTo(1);
        assertThat(result.get(0).getStockItem().getOrderPrice()).isEqualTo(new BigDecimal("121.1011"));
    }

    @DisplayName("주문 거래 응답에 대해 Orders 테이블에 모두 저장한 후 반환한다.")
    @Test
    void saveOrderInfos() {
        OrderTrading givenOrderTrading = OrderTrading.builder()
                .orderTime("221010")
                .itemCode("AAPL")
                .orderResultCode("0")
                .build();
        given(orderTradingRepository.saveAll(List.of(givenOrderTrading))).willReturn(List.of(givenOrderTrading));

        List<OrderTrading> result = overseasReservationBuyOrderService.saveOrderInfos(List.of(givenOrderTrading));

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getOrderTime()).isEqualTo("221010");
        assertThat(result.get(0).getItemCode()).isEqualTo("AAPL");
        assertThat(result.get(0).getOrderResultCode()).isEqualTo("0");

        verify(orderTradingRepository).saveAll(any(List.class));
    }

    @DisplayName("주문 거래 응답이 없으면 빈 리스트를 반환한다.")
    @Test
    void return_empty_list_when_parameter_is_empty() {

        List<OrderTrading> result = overseasReservationBuyOrderService.saveOrderInfos(Collections.emptyList());

        assertThat(result).isEmpty();
        verify(orderTradingRepository, never()).saveAll(any(List.class));
    }

    @DisplayName("예약 금액 * 예약 수량이 예수금보다 낮거나 같으면 True를 반환한다.")
    @CsvSource({
            "12.023, 3, true",
            "12.024, 3, true",
            "12.025, 3, false"
    })
    @ParameterizedTest
    void return_true_when_myDeposit_is_greaterThan_totalOrderPrice(BigDecimal givenOrderPrice, int orderQuantity, boolean expected) {
        BigDecimal myDeposit = new BigDecimal("12.024").multiply(new BigDecimal("3.0"));
        ReservationStockItem.Overseas givenReservation = ReservationStockItem.Overseas.builder()
                .stockItem(StockItemDto.builder()
                        .orderPrice(givenOrderPrice)
                        .orderQuantity(orderQuantity)
                        .build())
                .build();

        boolean result = overseasReservationBuyOrderService.hasSufficientDeposit(givenReservation, myDeposit);

        assertThat(result).isEqualTo(expected);
    }

    @DisplayName("응답 데이터가 성공일 때 알림 발송 객체에게 알림 발송 요청한다.")
    @Test
    void call_notificator_when_orderResult_is_success() {
        StockOrderApiResDto response = new StockOrderApiResDto();
        response.setResultCode(OPENAPI_SUCCESS_RESULT_CODE);
        response.setOutput(new StockOrderApiResDto.Output());
        OrderTrading orderTrading = OrderTrading.builder().itemCode("AAPL").itemNameKor("애플").orderQuantity(1).orderPrice(new BigDecimal("29.000"))
                .orderNumber("193284932").orderTime("090018").build();
        willDoNothing().given(notificator).sendMessage(any(MessageContentDto.OrderResult.class));

        overseasReservationBuyOrderService.orderApiResultProcess(response, orderTrading);

        verify(notificator).sendMessage(any(MessageContentDto.OrderResult.class));
    }

    @DisplayName("응답 데이터가 실패일 때 알림 발송 객체에게 알림 발송 요청하지 않는다.")
    @Test
    void not_call_notificator_when_orderResult_is_failed() {
        StockOrderApiResDto response = new StockOrderApiResDto();
        response.setResultCode("1");
        OrderTrading orderTrading = OrderTrading.builder().itemCode("AAPL").itemNameKor("애플").orderQuantity(1).orderPrice(new BigDecimal("29.000"))
                .orderNumber("193284932").orderTime("090018").build();

        overseasReservationBuyOrderService.orderApiResultProcess(response, orderTrading);

        verify(notificator, never()).sendMessage(any(MessageContentDto.OrderResult.class));
    }
}