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
import com.devspacehub.ast.exception.error.InsufficientMoneyException;
import com.devspacehub.ast.exception.error.InvalidValueException;
import com.devspacehub.ast.util.OpenApiRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import static com.devspacehub.ast.common.constant.CommonConstants.OPENAPI_SUCCESS_RESULT_CODE;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

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
        given(reservationOrderInfoRepository.findValidAllByExchangeCodes(any(LocalDate.class), anyList())).willReturn(Collections.emptyList());

        List<OrderTrading> results = overseasReservationBuyOrderService.order(openApiProperties, OpenApiType.OVERSEAS_STOCK_RESERVATION_BUY_ORDER);

        assertThat(results).isEmpty();
    }

    @DisplayName("원하는 종목과 그 종목의 주문, 수량 데이터를 포함하여 매수 주문을 요청한 뒤 응답받은 DTO를 반환한다.")
    @Test
    void return_response_after_buy_order_openApi_request() {
        ReservationStockItem reservationStockItem = ReservationStockItem.builder()
                .stockItem(StockItemDto.Overseas.builder()
                        .itemCode("AAPL")
                        .orderDivision("00")
                        .orderQuantity(1)
                        .orderPrice(new BigDecimal("2.928"))
                        .exchangeCode(ExchangeCode.NASDAQ)
                        .build())
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
        ReservationStockItem givenReservationDto = ReservationStockItem.builder()
                .stockItem(StockItemDto.Overseas.builder()
                        .itemCode("AAPL")
                        .orderQuantity(1)
                        .orderPrice(new BigDecimal("121.1011"))
                        .build())
                .reservationSeq(1L)
                .build();
        given(reservationOrderInfoRepository.findValidAllByExchangeCodes(LocalDate.now(),
                List.of(ExchangeCode.NASDAQ.getLongCode(), ExchangeCode.NEWYORK.getLongCode()))).willReturn(List.of(givenReservationDto));

        List<ReservationStockItem> result = overseasReservationBuyOrderService.getValidReservationsForToday();

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

    @DisplayName("예수금이 예약 금액 * 예약 수량보다 높거나 같으면 통과한다.")
    @CsvSource({
            "12.023, 3",
            "12.024, 3"
    })
    @ParameterizedTest
    void passed_when_myDeposit_is_greaterThanOrEqualTo_totalOrderPrice(BigDecimal givenOrderPrice, int orderQuantity) {
        BigDecimal myDeposit = new BigDecimal("12.024").multiply(new BigDecimal("3.0"));
        ReservationStockItem givenReservation = ReservationStockItem.builder()
                .stockItem(StockItemDto.Overseas.builder()
                        .orderPrice(givenOrderPrice)
                        .orderQuantity(orderQuantity)
                        .build())
                .build();

        overseasReservationBuyOrderService.sufficientDepositCheck(givenReservation, myDeposit);
    }

    @DisplayName("예약 금액 * 예약 수량이 예수금보다 높으면 InsufficientMoneyException이 발생한다.")
    @CsvSource({
            "12.0241, 3",
            "12.0242, 3"
    })
    @ParameterizedTest
    void throw_InsufficientMoneyException_when_myDeposit_is_lessThan_totalOrderPrice(BigDecimal givenOrderPrice, int orderQuantity) {
        BigDecimal myDeposit = new BigDecimal("12.0240").multiply(new BigDecimal("3"));
        ReservationStockItem givenReservation = ReservationStockItem.builder()
                .stockItem(StockItemDto.Overseas.builder()
                        .orderPrice(givenOrderPrice)
                        .orderQuantity(orderQuantity)
                        .build())
                .build();

        assertThatThrownBy(() -> overseasReservationBuyOrderService.sufficientDepositCheck(givenReservation, myDeposit))
                .isInstanceOf(InsufficientMoneyException.class)
                .hasMessage(String.format("Code: INSUFFICIENT_MONEY_ERROR (금액이 충분하지 않습니다.)%nException Message: totalAmount: %s, myDeposit: 36.0720",
                                givenOrderPrice.multiply(BigDecimal.valueOf(orderQuantity))));
    }

    @DisplayName("알림 발송 객체에게 알림 발송 요청한다.")
    @Test
    void call_notificator_when_orderResult_is_success() {
        OrderTrading orderTrading = OrderTrading.builder().itemCode("AAPL").itemNameKor("애플").orderQuantity(1).orderPrice(new BigDecimal("29.000"))
                .orderNumber("193284932").orderTime("090018").build();
        willDoNothing().given(notificator).sendMessage(any(MessageContentDto.OrderResult.class));

        overseasReservationBuyOrderService.orderApiResultProcess(orderTrading);

        verify(notificator).sendMessage(any(MessageContentDto.OrderResult.class));
    }

    @DisplayName("주문가가 0원일 때 InvalidValueException이 발생한다.")
    @Test
    void throw_InvalidValueException_when_orderPrice_is_zero() {
        BigDecimal given = new BigDecimal("0.0");

        assertThatThrownBy(() -> overseasReservationBuyOrderService.prepareOrderPrice(given))
                .isInstanceOf(InvalidValueException.class)
                .hasMessage(String.format("Code: INVALID_VALUE (유효하지 않은 값 입니다.)%nException Message: 주문가 : 0.0"));
    }
    @DisplayName("주어진 ReservationStockItem의 orderPrice를 호가에 맞게 조정하고 거래소 코드가 해외 것인지 확인한 뒤 StockItemDto를 생성한다.")
    @Test
    void prepare_StockItemDto_from_ReservationStockItem() {
        ReservationStockItem given = ReservationStockItem.builder()
                .stockItem(StockItemDto.Overseas.builder()
                        .itemCode("AAPL")
                        .orderPrice(new BigDecimal("0.09876"))
                        .orderQuantity(1)
                        .exchangeCode(ExchangeCode.NASDAQ).build())
                .build();

        StockItemDto.Overseas result = overseasReservationBuyOrderService.prepareOrderRequestInfo(given);

        assertThat(result.getOrderPrice()).isEqualTo(new BigDecimal("0.0987"));
        assertThat(result.getExchangeCode()).isEqualTo(ExchangeCode.NASDAQ);
    }

    @DisplayName("ReservationStockItem 필드들이 정상적이면 통과한다.")
    @Test
    void passed_when_all_fields_are_ok() {
        ReservationStockItem given = ReservationStockItem.builder()
                .stockItem(StockItemDto.Overseas.builder()
                        .itemCode("AAPL")
                        .orderPrice(new BigDecimal("2.00"))
                        .orderQuantity(1)
                        .exchangeCode(ExchangeCode.NASDAQ).build()).build();

        overseasReservationBuyOrderService.validateValue(given);
    }
    @DisplayName("itemCode가 비었으면 InvalidValueException이 발생한다.")
    @Test
    void throw_InvalidValueException_when_itemCode_is_blank() {
        String given = " ";
        ReservationStockItem reservationStockItem = ReservationStockItem.builder()
                .stockItem(StockItemDto.Overseas.builder()
                        .itemCode(given)
                        .orderPrice(new BigDecimal("2.00"))
                        .orderQuantity(1)
                        .exchangeCode(ExchangeCode.NASDAQ).build()).build();

        assertThatThrownBy(() -> overseasReservationBuyOrderService.validateValue(reservationStockItem))
                .isInstanceOf(InvalidValueException.class)
                .hasMessage("Code: DATA_IS_BLANK_ERROR (값이 비었습니다.)");
    }

    @DisplayName("주문가가 0보다 작거나 같으면 InvalidValueException이 발생한다.")
    @Test
    void throw_InvalidValueException_when_orderPrice_is_lessThanOrEqualTo_zero() {
        BigDecimal given = new BigDecimal("0.00");
        ReservationStockItem reservationStockItem = ReservationStockItem.builder()
                .stockItem(StockItemDto.Overseas.builder()
                        .itemCode("AAPL")
                        .orderPrice(given)
                        .orderQuantity(1)
                        .exchangeCode(ExchangeCode.NASDAQ).build()).build();

        assertThatThrownBy(() -> overseasReservationBuyOrderService.validateValue(reservationStockItem))
                .isInstanceOf(InvalidValueException.class)
                .hasMessage(String.format("Code: INVALID_VALUE (유효하지 않은 값 입니다.)%nException Message: 주문가: 0.00"));
    }

    @DisplayName("주문 수량이 0보다 작거나 같으면 InvalidValueException이 발생한다.")
    @Test
    void throw_InvalidValueException_when_orderQuantity_is_lessThanOrEqualTo_zero() {
        int given = 0;
        ReservationStockItem reservationStockItem = ReservationStockItem.builder()
                .stockItem(StockItemDto.Overseas.builder()
                        .itemCode("AAPL")
                        .orderPrice(new BigDecimal("1.00"))
                        .orderQuantity(given)
                        .exchangeCode(ExchangeCode.NASDAQ).build()).build();

        assertThatThrownBy(() -> overseasReservationBuyOrderService.validateValue(reservationStockItem))
                .isInstanceOf(InvalidValueException.class)
                .hasMessage(String.format("Code: INVALID_VALUE (유효하지 않은 값 입니다.)%nException Message: 주문 수량: 0"));
    }
    @ValueSource(strings = {"KOSPI", "KOSDAQ", "KONEX"})
    @DisplayName("거래소 코드가 해외 거래소 코드가 아니면 InvalidValueException이 발생한다.")
    @ParameterizedTest
    void throw_InvalidValueException_when_exchangeCode_is_not_overseas(ExchangeCode given) {
        ReservationStockItem reservationStockItem = ReservationStockItem.builder()
                .stockItem(StockItemDto.Overseas.builder()
                        .itemCode("AAPL")
                        .orderPrice(new BigDecimal("1.00"))
                        .orderQuantity(1)
                        .exchangeCode(given).build()).build();

        assertThatThrownBy(() -> overseasReservationBuyOrderService.validateValue(reservationStockItem))
                .isInstanceOf(InvalidValueException.class)
                .hasMessage(String.format("Code: INVALID_VALUE (유효하지 않은 값 입니다.)%nException Message: 거래소 코드: %s", given.name()));
    }
}