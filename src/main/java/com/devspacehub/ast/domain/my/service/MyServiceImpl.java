/*
 © 2023 devspacehub, Inc. All rights reserved.

 name : MyServiceImpl
 creation : 2023.12.10
 author : Yoonji Moon
 */

package com.devspacehub.ast.domain.my.service;

import com.devspacehub.ast.common.config.OpenApiProperties;
import com.devspacehub.ast.common.constant.OpenApiType;
import com.devspacehub.ast.common.dto.WebClientCommonReqDto;
import com.devspacehub.ast.domain.my.dto.request.BuyPossibleCheckExternalReqDto;
import com.devspacehub.ast.domain.my.dto.request.StockBalanceExternalReqDto;
import com.devspacehub.ast.domain.my.dto.response.BuyPossibleCheckExternalResDto;
import com.devspacehub.ast.domain.my.dto.response.StockBalanceExternalResDto;
import com.devspacehub.ast.exception.error.OpenApiFailedResponseException;
import com.devspacehub.ast.openApiUtil.OpenApiRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import java.util.function.Consumer;

import static com.devspacehub.ast.common.constant.OpenApiType.BUY_ORDER_POSSIBLE_CASH;

/**
 * 사용자 My 서비스 구현체.
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class MyServiceImpl implements MyService {
    private final OpenApiRequest openApiRequest;
    private final OpenApiProperties openApiProperties;
    @Value("${openapi.rest.header.transaction-id.buy-possible-cash-find}")
    private String txIdBuyPossibleCashFind;

    @Value("${openapi.rest.header.transaction-id.stock-balance-find}")
    private String txIdStockBalanceFind;

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

        log.info("응답 : {}", responseDto.getMessage());
        log.info("주문 가능 현금 : {}", responseDto.getOutput().getOrderPossibleCash());
        log.info("최대 구매 가능 금액 : {}", responseDto.getOutput().getMaxBuyAmount());
        log.info("최대 구매 가능 수량 : {}", responseDto.getOutput().getMaxBuyQuantity());
        return Integer.valueOf(responseDto.getOutput().getOrderPossibleCash());
    }

    /**
     * 매수 가능한 종목인지 체크
     */
    @Override
    public boolean buyOrderPossibleCheck(String stockCode, String orderDivision, Integer orderPrice) {
        int myCash = getBuyOrderPossibleCash(stockCode, orderPrice, orderDivision);
        if (orderPrice <= myCash) {
            return true;
        }
        log.info("매수 주문 금액이 부족합니다. (매수 가능 금액: {})", myCash);
        return false;
    }

    /**
     * 주식 잔고 조회
     * @return
     */
    @Override
    public StockBalanceExternalResDto getMyStockBalance() {
        Consumer<HttpHeaders> headers = WebClientCommonReqDto.setHeaders(openApiProperties.getOauth(), txIdStockBalanceFind);
        MultiValueMap<String, String> queryParams = StockBalanceExternalReqDto.createParameter(openApiProperties.getAccntNumber(), openApiProperties.getAccntProductCode());

        StockBalanceExternalResDto responseDto = (StockBalanceExternalResDto) openApiRequest.httpGetRequest(OpenApiType.STOCK_BALANCE, headers, queryParams);

        if (!responseDto.isSuccess()) {
            throw new OpenApiFailedResponseException();
        }
        // log (TODO 삭제 예정)
        log.info("응답 : {}", responseDto.getMessage());
        for(StockBalanceExternalResDto.Output1 output1 : responseDto.getOutput1()) {
            log.info("주식 종목 : {}({})", output1.getStockCode(), output1.getStockName());
            log.info("보유 수량 : {}", output1.getHldgQty());
            log.info("주문 가능 수량 : {}", output1.getOrderPossibleQuantity());
            log.info("매입금액 : {}", output1.getPurchaseAmount());
            log.info("평가손익율 : {}", output1.getEvaluateProfitLossRate());
            log.info("평가수익율 : {}", output1.getEvaluateEarningRate());
        }

        return responseDto;
    }
}
