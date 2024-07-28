/*
 © 2023 devspacehub, Inc. All rights reserved.

 name : MyServiceImpl
 creation : 2023.12.10
 author : Yoonji Moon
 */

package com.devspacehub.ast.domain.my.service;

import com.devspacehub.ast.common.config.OpenApiProperties;
import com.devspacehub.ast.common.constant.OpenApiType;
import com.devspacehub.ast.domain.my.dto.MyServiceRequestDto;
import com.devspacehub.ast.domain.my.dto.orderConclusion.OrderConclusionFindExternalReqDto;
import com.devspacehub.ast.domain.my.dto.orderConclusion.OrderConclusionFindExternalResDto;
import com.devspacehub.ast.domain.my.reservationOrderInfo.ReservationOrderInfoRepository;
import com.devspacehub.ast.domain.my.stockBalance.dto.request.StockBalanceApiReqDto;
import com.devspacehub.ast.domain.my.stockBalance.dto.response.BuyPossibleCashApiResDto;
import com.devspacehub.ast.domain.my.stockBalance.dto.response.StockBalanceApiResDto;
import com.devspacehub.ast.domain.my.stockBalance.dto.request.BuyPossibleCashApiReqDto;
import com.devspacehub.ast.domain.my.dto.orderConclusion.OrderConclusionDto;
import com.devspacehub.ast.exception.error.OpenApiFailedResponseException;
import com.devspacehub.ast.util.OpenApiRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
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
@Service
public class MyServiceImpl extends MyService {
    private final OpenApiRequest openApiRequest;
    private final OpenApiProperties openApiProperties;

    @Value("${openapi.rest.header.transaction-id.domestic.buy-order-possible-cash-find}")
    private String txIdBuyPossibleCashFind;

    @Value("${openapi.rest.header.transaction-id.domestic.stock-balance-find}")
    private String txIdStockBalanceFind;
    @Value("${openapi.rest.header.transaction-id.domestic.order-conclusion-find}")
    private String txIdOrderConclusionFind;

    /**
     * 생성자
     * @param reservationOrderInfoRepository 상위 생성자를 통해 주입되는 예약주문 정보 Repository
     * @param openApiRequest OpenApi 요청 클래스
     * @param openApiProperties OpenApi 요청 관련 프로퍼티
     */
    public MyServiceImpl(ReservationOrderInfoRepository reservationOrderInfoRepository, OpenApiRequest openApiRequest,
                         OpenApiProperties openApiProperties) {
        super(reservationOrderInfoRepository);
        this.openApiRequest = openApiRequest;
        this.openApiProperties = openApiProperties;
    }

    /**
     * 매수 가능 금액 조회 (Get)
     * @param requestDto MyService Layer Request Dto
     * @param <T> MyserviceRequestDto를 상속하는 타입.
     * @return 매수 주문 가능한 현금
     */
    @Override
    public <T extends MyServiceRequestDto> BigDecimal getBuyOrderPossibleCash(T requestDto) {
        MyServiceRequestDto.Domestic embodiedRequestDto = (MyServiceRequestDto.Domestic) requestDto;
        // 헤더 & 파라미터 값 생성
        Consumer<HttpHeaders> httpHeaders = BuyPossibleCashApiReqDto.setHeaders(openApiProperties.getOauth(), txIdBuyPossibleCashFind);
        MultiValueMap<String, String> queryParams = BuyPossibleCashApiReqDto.createParameter(
                openApiProperties.getAccntNumber(), openApiProperties.getAccntProductCode(), embodiedRequestDto.getItemCode(), embodiedRequestDto.getOrderPrice(), embodiedRequestDto.getOrderDivision());

        BuyPossibleCashApiResDto responseDto = (BuyPossibleCashApiResDto) openApiRequest.httpGetRequest(DOMESTIC_BUY_ORDER_POSSIBLE_CASH, httpHeaders, queryParams);

        if (responseDto.isFailed()) {
            throw new OpenApiFailedResponseException(OpenApiType.DOMESTIC_BUY_ORDER_POSSIBLE_CASH, responseDto.getMessage());
        }
        log.info("[{}] 주문 가능 현금 : {}", OpenApiType.DOMESTIC_BUY_ORDER_POSSIBLE_CASH.getDiscription(), responseDto.getOutput().getOrderPossibleCash());
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
            throw new OpenApiFailedResponseException(OpenApiType.DOMESTIC_STOCK_BALANCE, responseDto.getMessage());
        }

        log.info("[{}] {}", OpenApiType.DOMESTIC_STOCK_BALANCE.getDiscription(), responseDto.getMessage());
        for(StockBalanceApiResDto.MyStockBalance myStockBalance : responseDto.getMyStockBalance()) {
            log.info("[{}] 주식 종목 : {}({}) / 보유 수량 : {}주 / 현재가 : {} / 평가손익율 : {}",
                    OpenApiType.DOMESTIC_STOCK_BALANCE.getDiscription(),
                    myStockBalance.getItemCode(), myStockBalance.getStockName(),
                    myStockBalance.getHoldingQuantity(), myStockBalance.getCurrentPrice(), myStockBalance.getEvaluateProfitLossRate());
        }

        return responseDto;
    }

    /**
     * 금일 거래가 체결된 종목을 조회한다.
     * @param today 금일 일자
     * @return 금일 일자로 조회되는 리스트 타입의 체결 종목 정보
     */
    @Override
    public List<OrderConclusionDto> getConcludedStock(LocalDate today) {
        // 헤더 & 파라미터 값 생성
        Consumer<HttpHeaders> httpHeaders = OrderConclusionFindExternalReqDto.setHeaders(openApiProperties.getOauth(), txIdOrderConclusionFind);
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        MultiValueMap<String, String> queryParams = OrderConclusionFindExternalReqDto.createParameter(
                openApiProperties.getAccntNumber(), openApiProperties.getAccntProductCode(), today.format(dateTimeFormatter));

        OrderConclusionFindExternalResDto responseDto = (OrderConclusionFindExternalResDto) openApiRequest.httpGetRequest(DOMESTIC_ORDER_CONCLUSION_FIND, httpHeaders, queryParams);

        if (responseDto.isFailed()) {
            throw new OpenApiFailedResponseException(DOMESTIC_ORDER_CONCLUSION_FIND, responseDto.getMessage());
        }

        return OrderConclusionDto.of(responseDto.getOutput1());
    }

}
