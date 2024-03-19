/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : MyServiceImpl
 creation : 2024.3.19
 author : Yoonji Moon
 */

/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : MyServiceImpl
 creation : 2024.3.18
 author : Yoonji Moon
 */

/*
 © 2023 devspacehub, Inc. All rights reserved.

 name : MyServiceImpl
 creation : 2023.12.10
 author : Yoonji Moon
 */

package com.devspacehub.ast.domain.my.stockBalance.service;

import com.devspacehub.ast.common.config.OpenApiProperties;
import com.devspacehub.ast.common.constant.OpenApiType;
import com.devspacehub.ast.domain.my.stockBalance.dto.request.StockBalanceExternalReqDto;
import com.devspacehub.ast.domain.my.stockBalance.dto.response.BuyPossibleCheckExternalResDto;
import com.devspacehub.ast.domain.my.stockBalance.dto.response.StockBalanceExternalResDto;
import com.devspacehub.ast.domain.my.stockBalance.dto.request.BuyPossibleCheckExternalReqDto;
import com.devspacehub.ast.exception.error.OpenApiFailedResponseException;
import com.devspacehub.ast.util.OpenApiRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import java.util.function.Consumer;

import static com.devspacehub.ast.common.constant.OpenApiType.BUY_ORDER_POSSIBLE_CASH;

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

        if (!responseDto.isSuccess()) {
            throw new OpenApiFailedResponseException();
        }
        log.info("[buy] 매수가능금액조회 : {}", responseDto.getMessage());
        log.info("[buy] 주문 가능 현금 : {}", responseDto.getOutput().getOrderPossibleCash());
        log.info("[buy] 최대 구매 가능 금액 : {}", responseDto.getOutput().getMaxBuyAmount());
        log.info("[buy] 최대 구매 가능 수량 : {}", responseDto.getOutput().getMaxBuyQuantity());
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

        if (!responseDto.isSuccess()) {
            throw new OpenApiFailedResponseException();
        }

        log.info("[sell] 주식잔고조회 : {}", responseDto.getMessage());
        for(StockBalanceExternalResDto.MyStockBalance myStockBalance : responseDto.getMyStockBalance()) {
            log.info("[sell] 1. 주식 종목 : {}({})", myStockBalance.getStockCode(), myStockBalance.getStockName());
            log.info("[sell] 2. 보유 수량 : {}", myStockBalance.getHoldingQuantity());
            log.info("[sell] 3. 현재가 : {}", myStockBalance.getCurrentPrice());
            log.info("[sell] 4. 평가손익율 : {}\n", myStockBalance.getEvaluateProfitLossRate());
        }

        return responseDto;
    }
}