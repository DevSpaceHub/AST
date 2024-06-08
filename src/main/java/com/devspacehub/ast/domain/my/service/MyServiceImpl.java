/*
 © 2023 devspacehub, Inc. All rights reserved.

 name : MyServiceImpl
 creation : 2023.12.10
 author : Yoonji Moon
 */

package com.devspacehub.ast.domain.my.service;

import com.devspacehub.ast.common.config.OpenApiProperties;
import com.devspacehub.ast.common.constant.OpenApiType;
import com.devspacehub.ast.domain.my.orderConclusion.dto.OrderConclusionFindExternalReqDto;
import com.devspacehub.ast.domain.my.orderConclusion.dto.OrderConclusionFindExternalResDto;
import com.devspacehub.ast.domain.my.reservationOrderInfo.ReservationOrderInfo;
import com.devspacehub.ast.domain.my.reservationOrderInfo.ReservationOrderInfoRepository;
import com.devspacehub.ast.domain.my.stockBalance.dto.request.StockBalanceApiReqDto;
import com.devspacehub.ast.domain.my.stockBalance.dto.response.BuyPossibleCashApiResDto;
import com.devspacehub.ast.domain.my.stockBalance.dto.response.StockBalanceApiResDto;
import com.devspacehub.ast.domain.my.stockBalance.dto.request.BuyPossibleCashApiReqDto;
import com.devspacehub.ast.domain.orderTrading.dto.OrderConclusionDto;
import com.devspacehub.ast.exception.error.OpenApiFailedResponseException;
import com.devspacehub.ast.util.OpenApiRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import static com.devspacehub.ast.common.constant.OpenApiType.DOMESTIC_BUY_ORDER_POSSIBLE_CASH;
import static com.devspacehub.ast.common.constant.OpenApiType.DOMESTIC_ORDER_CONCLUSION_FIND;

/**
 * 사용자 개인 서비스 구현체.
 * - 매수 가능 금액 조회 (외부 API)
 * - 주식 잔고 조회 (외부 API)
 * - 예약 주문 정보 조회 (Table)
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class MyServiceImpl implements MyService {
    private final OpenApiRequest openApiRequest;
    private final OpenApiProperties openApiProperties;
    private final ReservationOrderInfoRepository reservationOrderInfoRepository;

    @Value("${openapi.rest.header.transaction-id.domestic.buy-possible-cash-find}")
    private String txIdBuyPossibleCashFind;

    @Value("${openapi.rest.header.transaction-id.domestic.stock-balance-find}")
    private String txIdStockBalanceFind;
    @Value("${openapi.rest.header.transaction-id.domestic.order-conclusion-find}")
    private String txIdOrderConclusionFind;

    /**
     * 매수 가능 금액 조회
     */
    @Override
    public BigDecimal getBuyOrderPossibleCash(String stockCode, BigDecimal orderPrice, String orderDivision) {
        // 헤더 & 파라미터 값 생성
        Consumer<HttpHeaders> httpHeaders = BuyPossibleCashApiReqDto.setHeaders(openApiProperties.getOauth(), txIdBuyPossibleCashFind);
        MultiValueMap<String, String> queryParams = BuyPossibleCashApiReqDto.createParameter(
                openApiProperties.getAccntNumber(), openApiProperties.getAccntProductCode(), stockCode, orderPrice, orderDivision);

        BuyPossibleCashApiResDto responseDto = (BuyPossibleCashApiResDto) openApiRequest.httpGetRequest(DOMESTIC_BUY_ORDER_POSSIBLE_CASH, httpHeaders, queryParams);

        if (responseDto.isFailed()) {
            throw new OpenApiFailedResponseException();
        }
        log.info("[매수가능금액 조회] API 응답 메시지 : {}", responseDto.getMessage());
        log.info("[매수가능금액 조회] 주문 가능 현금 : {}", responseDto.getOutput().getOrderPossibleCash());
        log.info("[매수가능금액 조회] 최대 구매 가능 금액 : {}", responseDto.getOutput().getMaxBuyAmount());
        log.info("[매수가능금액 조회] 최대 구매 가능 수량 : {}", responseDto.getOutput().getMaxBuyQuantity());
        return new BigDecimal(responseDto.getOutput().getOrderPossibleCash());
    }


    /**
     * 주식 잔고 조회
     * @return StockBalanceExternalResDto
     */
    @Override
    public StockBalanceApiResDto getMyStockBalance() {
        Consumer<HttpHeaders> headers = StockBalanceApiReqDto.setHeaders(openApiProperties.getOauth(), txIdStockBalanceFind);
        MultiValueMap<String, String> queryParams = StockBalanceApiReqDto.createParameter(openApiProperties.getAccntNumber(), openApiProperties.getAccntProductCode());

        StockBalanceApiResDto responseDto = (StockBalanceApiResDto) openApiRequest.httpGetRequest(OpenApiType.DOMESTIC_STOCK_BALANCE, headers, queryParams);

        if (responseDto.isFailed()) {
            throw new OpenApiFailedResponseException();
        }

        log.info("[매도 주문] 주식잔고조회 : {}", responseDto.getMessage());
        for(StockBalanceApiResDto.MyStockBalance myStockBalance : responseDto.getMyStockBalance()) {
            log.info("[매도 주문] 1. 주식 종목 : {}({})", myStockBalance.getItemCode(), myStockBalance.getStockName());
            log.info("[매도 주문] 2. 보유 수량 : {}", myStockBalance.getHoldingQuantity());
            log.info("[매도 주문] 3. 현재가 : {}", myStockBalance.getCurrentPrice());
            log.info("[매도 주문] 4. 평가손익율 : {}\n", myStockBalance.getEvaluateProfitLossRate());
        }

        return responseDto;
    }

    @Override
    public List<OrderConclusionDto> getConcludedStock(LocalDate today) {
        // 헤더 & 파라미터 값 생성
        Consumer<HttpHeaders> httpHeaders = OrderConclusionFindExternalReqDto.setHeaders(openApiProperties.getOauth(), txIdOrderConclusionFind);
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        MultiValueMap<String, String> queryParams = OrderConclusionFindExternalReqDto.createParameter(
                openApiProperties.getAccntNumber(), openApiProperties.getAccntProductCode(), today.format(dateTimeFormatter));

        OrderConclusionFindExternalResDto responseDto = (OrderConclusionFindExternalResDto) openApiRequest.httpGetRequest(DOMESTIC_ORDER_CONCLUSION_FIND, httpHeaders, queryParams);

        if (responseDto.isFailed()) {
            throw new OpenApiFailedResponseException();
        }

        return OrderConclusionDto.of(responseDto.getOutput1());
    }

    /**
     * 예약 매수 중 체결된 매수 수량에 따라 예약 매수 주문 수량 업데이트한다.
     * 체결 종목이 예약 매수 종목이 아니라면 아무런 동작을 하지 않는다.
     * @param todayOrderConclusion 금일 체결 종목 조회 응답 Dto
     * @param concludedDate 체결 일자
     */
    @Override
    @Transactional
    public void updateMyReservationOrderUseYn(OrderConclusionDto todayOrderConclusion, LocalDate concludedDate) {
        Optional<ReservationOrderInfo> optionalReservationItem = reservationOrderInfoRepository.findValidOneByItemCodeAndOrderNumber(
                concludedDate, todayOrderConclusion.getItemCode(), todayOrderConclusion.getOrderNumber());

        if (optionalReservationItem.isEmpty()) {
            return;
        }
        ReservationOrderInfo validReservationItem = optionalReservationItem.get();
        validReservationItem.addConcludedQuantity(todayOrderConclusion.getConcludedQuantity());

        if (validReservationItem.checkTotalConcluded(todayOrderConclusion.getConcludedQuantity())) {
            validReservationItem.disable();
        }
        validReservationItem.updateMetaData(LocalDateTime.now());
    }
}
