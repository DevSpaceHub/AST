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
import com.devspacehub.ast.domain.my.stockBalance.dto.request.StockBalanceExternalReqDto;
import com.devspacehub.ast.domain.my.stockBalance.dto.response.BuyPossibleCheckExternalResDto;
import com.devspacehub.ast.domain.my.stockBalance.dto.response.StockBalanceExternalResDto;
import com.devspacehub.ast.domain.my.stockBalance.dto.request.BuyPossibleCheckExternalReqDto;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import static com.devspacehub.ast.common.constant.OpenApiType.BUY_ORDER_POSSIBLE_CASH;
import static com.devspacehub.ast.common.constant.OpenApiType.ORDER_CONCLUSION_FIND;

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

    @Value("${openapi.rest.header.transaction-id.buy-possible-cash-find}")
    private String txIdBuyPossibleCashFind;

    @Value("${openapi.rest.header.transaction-id.stock-balance-find}")
    private String txIdStockBalanceFind;
    @Value("${openapi.rest.header.transaction-id.order-conclusion-find}")
    private String txIdOrderConclusionFind;

    /**
     * 매수 가능 금액 조회
     */
    @Override
    public int getBuyOrderPossibleCash(String stockCode, Integer orderPrice, String orderDivision) {
        // 헤더 & 파라미터 값 생성
        Consumer<HttpHeaders> httpHeaders = BuyPossibleCheckExternalReqDto.setHeaders(openApiProperties.getOauth(), txIdBuyPossibleCashFind);
        MultiValueMap<String, String> queryParams = BuyPossibleCheckExternalReqDto.createParameter(
                openApiProperties.getAccntNumber(), openApiProperties.getAccntProductCode(), stockCode, orderPrice, orderDivision);

        BuyPossibleCheckExternalResDto responseDto = (BuyPossibleCheckExternalResDto) openApiRequest.httpGetRequest(BUY_ORDER_POSSIBLE_CASH, httpHeaders, queryParams);

        if (responseDto.isFailed()) {
            throw new OpenApiFailedResponseException();
        }
        log.info("[매수가능금액 조회] API 응답 메시지 : {}", responseDto.getMessage());
        log.info("[매수가능금액 조회] 주문 가능 현금 : {}", responseDto.getOutput().getOrderPossibleCash());
        log.info("[매수가능금액 조회] 최대 구매 가능 금액 : {}", responseDto.getOutput().getMaxBuyAmount());
        log.info("[매수가능금액 조회] 최대 구매 가능 수량 : {}", responseDto.getOutput().getMaxBuyQuantity());
        return Integer.parseInt(responseDto.getOutput().getOrderPossibleCash());
    }


    /**
     * 주식 잔고 조회
     * @return StockBalanceExternalResDto
     */
    @Override
    public StockBalanceExternalResDto getMyStockBalance() {
        Consumer<HttpHeaders> headers = StockBalanceExternalReqDto.setHeaders(openApiProperties.getOauth(), txIdStockBalanceFind);
        MultiValueMap<String, String> queryParams = StockBalanceExternalReqDto.createParameter(openApiProperties.getAccntNumber(), openApiProperties.getAccntProductCode());

        StockBalanceExternalResDto responseDto = (StockBalanceExternalResDto) openApiRequest.httpGetRequest(OpenApiType.STOCK_BALANCE, headers, queryParams);

        if (responseDto.isFailed()) {
            throw new OpenApiFailedResponseException();
        }

        log.info("[매도 주문] 주식잔고조회 : {}", responseDto.getMessage());
        for(StockBalanceExternalResDto.MyStockBalance myStockBalance : responseDto.getMyStockBalance()) {
            log.info("[매도 주문] 1. 주식 종목 : {}({})", myStockBalance.getItemCode(), myStockBalance.getStockName());
            log.info("[매도 주문] 2. 보유 수량 : {}", myStockBalance.getHoldingQuantity());
            log.info("[매도 주문] 3. 현재가 : {}", myStockBalance.getCurrentPrice());
            log.info("[매도 주문] 4. 평가손익율 : {}\n", myStockBalance.getEvaluateProfitLossRate());
        }

        return responseDto;
    }

    @Override
    public boolean isMyDepositLowerThanOrderPrice(int myDeposit, int orderPrice) {
        return myDeposit < orderPrice;
    }

    @Override
    public List<OrderConclusionDto> getConcludedStock(LocalDate today) {
        // 헤더 & 파라미터 값 생성
        Consumer<HttpHeaders> httpHeaders = OrderConclusionFindExternalReqDto.setHeaders(openApiProperties.getOauth(), txIdOrderConclusionFind);
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        MultiValueMap<String, String> queryParams = OrderConclusionFindExternalReqDto.createParameter(
                openApiProperties.getAccntNumber(), openApiProperties.getAccntProductCode(), today.format(dateTimeFormatter));

        OrderConclusionFindExternalResDto responseDto = (OrderConclusionFindExternalResDto) openApiRequest.httpGetRequest(ORDER_CONCLUSION_FIND, httpHeaders, queryParams);

        if (responseDto.isFailed()) {
            throw new OpenApiFailedResponseException();
        }

        return OrderConclusionDto.of(responseDto.getOutput1());
    }

    /**
     * 예약 매수 중 체결된 매수 수량에 따라 예약 매수 주문 수량 업데이트한다.
     *
     * @param orderConclusion
     */
    @Override
    @Transactional
    public void updateMyReservationOrderUseYn(OrderConclusionDto orderConclusion, LocalDate concludedDate) {
        Optional<ReservationOrderInfo> optionalValidReservationItem = reservationOrderInfoRepository.findValidOneByItemCodeAndOrderNumber(
                concludedDate, orderConclusion.getItemCode(), orderConclusion.getOrderNumber());
        
        if (optionalValidReservationItem.isEmpty()) {
            return;
        }

        ReservationOrderInfo validReservationItem = optionalValidReservationItem.get();
        validReservationItem.setConclusionQuantity(orderConclusion.getConcludedQuantity());

        if (validReservationItem.checkTotalConcluded(orderConclusion.getConcludedQuantity())) {
            validReservationItem.disable();
        } else {
            validReservationItem.updateOrderQuantity(orderConclusion.getConcludedQuantity());
        }
        validReservationItem.updateMetaData(LocalDateTime.now());
    }
}
