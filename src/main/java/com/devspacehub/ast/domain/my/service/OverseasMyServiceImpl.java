/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : OverseasMyServiceImpl
 creation : 2024.5.31
 author : Yoonji Moon
 */

package com.devspacehub.ast.domain.my.service;

import com.devspacehub.ast.common.config.OpenApiProperties;
import com.devspacehub.ast.common.constant.OpenApiType;
import com.devspacehub.ast.common.utils.LogUtils;
import com.devspacehub.ast.domain.my.dto.MyServiceRequestDto;
import com.devspacehub.ast.domain.my.dto.orderConclusion.OrderConclusionFindExternalReqDto;
import com.devspacehub.ast.domain.my.dto.orderConclusion.OverseasOrderConclusionFindExternalResDto;
import com.devspacehub.ast.domain.my.reservationOrderInfo.ReservationOrderInfoRepository;
import com.devspacehub.ast.domain.my.stockBalance.dto.request.OverseasBuyPossibleCashApiReqDto;
import com.devspacehub.ast.domain.my.stockBalance.dto.request.OverseasStockBalanceApiReqDto;
import com.devspacehub.ast.domain.my.stockBalance.dto.response.overseas.OverseasBuyPossibleCashApiResDto;
import com.devspacehub.ast.domain.my.stockBalance.dto.response.overseas.OverseasStockBalanceApiResDto;
import com.devspacehub.ast.domain.my.dto.orderConclusion.OrderConclusionDto;
import com.devspacehub.ast.exception.error.OpenApiFailedResponseException;
import com.devspacehub.ast.util.OpenApiRequest;
import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import static com.devspacehub.ast.common.constant.OpenApiType.*;

/**
 * 해외 주식 - My 기능 서비스 구현체 클래스
 * MyService 추상 클래스를 상속한다.
 */
@Slf4j
@Service
public class OverseasMyServiceImpl extends MyService {

    @Value("${openapi.rest.header.transaction-id.overseas.buy-order-possible-cash-find}")
    private String txIdBuyOrderPossibleCashFind;
    @Value("${openapi.rest.header.transaction-id.overseas.stock-balance-find}")
    private String txIdStockBalanceFind;

    @Value("${openapi.rest.header.transaction-id.overseas.order-conclusion-find}")
    private String txIdOrderConclusionFind;

    public OverseasMyServiceImpl(ReservationOrderInfoRepository reservationOrderInfoRepository, OpenApiRequest openApiRequest,
                                 OpenApiProperties openApiProperties) {
        super(reservationOrderInfoRepository, openApiRequest, openApiProperties);
    }

    /**
     * 매수 가능 금액 조회 (Get)
     * @param requestDto requestDto MyService Layer Request Dto
     * @param <T> MyserviceRequestDto를 상속하는 타입.
     * @return 해외 주문 가능 현금
     * @throws OpenApiFailedResponseException 성공 응답이 아닌 경우
     */
    @Override
    public <T extends MyServiceRequestDto> BigDecimal getBuyOrderPossibleCash(T requestDto) {
        Consumer<HttpHeaders> httpHeaders = OverseasBuyPossibleCashApiReqDto.setHeaders(openApiProperties.getOauth(), txIdBuyOrderPossibleCashFind);
        MultiValueMap<String, String> queryParams = OverseasBuyPossibleCashApiReqDto.createParameter(
                openApiProperties.getAccntNumber(), openApiProperties.getAccntProductCode(), (MyServiceRequestDto.Overseas) requestDto);

        OverseasBuyPossibleCashApiResDto responseDto = (OverseasBuyPossibleCashApiResDto) openApiRequest.httpGetRequest(OVERSEAS_BUY_ORDER_POSSIBLE_CASH, httpHeaders, queryParams);

        if (responseDto.isFailed()) {
            throw new OpenApiFailedResponseException(OVERSEAS_BUY_ORDER_POSSIBLE_CASH, responseDto.toString());
        }
        log.info("[{}] 주문 가능 현금 : {}", OVERSEAS_BUY_ORDER_POSSIBLE_CASH.getDiscription(), responseDto.getResultDetail().getOrderPossibleCash());

        return responseDto.getResultDetail().getOrderPossibleCash();
    }

    /**
     * 해외 주식 잔고 조회하기 위해 OpenApi 호출을 요청한다.
     * @return 응답 DTO
     */
    @Override
    public OverseasStockBalanceApiResDto getMyStockBalance() {
        Consumer<HttpHeaders> headers = OverseasStockBalanceApiReqDto.setHeaders(openApiProperties.getOauth(), txIdStockBalanceFind);
        MultiValueMap<String, String> queryParams = OverseasStockBalanceApiReqDto.createParameter(openApiProperties.getAccntNumber(), openApiProperties.getAccntProductCode());

        OverseasStockBalanceApiResDto responseDto = (OverseasStockBalanceApiResDto) openApiRequest.httpGetRequest(OpenApiType.OVERSEAS_STOCK_BALANCE, headers, queryParams);

        if (responseDto.isFailed()) {
            throw new OpenApiFailedResponseException(OpenApiType.OVERSEAS_STOCK_BALANCE, responseDto.getMessage());
        }

        for(OverseasStockBalanceApiResDto.MyStockBalance myStockBalance : responseDto.getMyStockBalance()) {
            log.info("[{}] 주식 종목 : {}({}) / 보유 수량 : {}주 / 현재가 : {} / 평가손익율 : {}",
                    OVERSEAS_STOCK_BALANCE.getDiscription(),
                    myStockBalance.getItemCode(), myStockBalance.getStockName(),
                    myStockBalance.getOrderPossibleQuantity(),
                    myStockBalance.getCurrentPrice(),
                    myStockBalance.getEvaluateProfitLossRate());
        }

        return responseDto;
    }

    /**
     * {today}에 체결된 해외 주식 종목 조회 API를 호출한다.
     * @param today 체결 일자
     * @return Collection 타입의 응답 DTO
     */
    @Override
    public List<OrderConclusionDto> getConcludedStock(LocalDate today) {
        HttpHeaders headers = OrderConclusionFindExternalReqDto.setHeaders(openApiProperties.getOauth(), txIdOrderConclusionFind);
        MultiValueMap<String, String> queryParams = OrderConclusionFindExternalReqDto.Overseas.createParameter(
                openApiProperties.getAccntNumber(), openApiProperties.getAccntProductCode(), today.format(DateTimeFormatter.ofPattern("YYYYMMdd")));

        return OrderConclusionDto.overseasOf(this.callOrderConclusionGetApi(OVERSEAS_ORDER_CONCLUSION_FIND, headers, queryParams));
    }

    /**
     * 체결 내역 조회 API 호출 요청한다.
     * 연쇄적으로 데이터 호출이 발생할 수 있다.
     * @param openApiType Open Api 타입
     * @param headers 요청 헤더
     * @param queryParams 요청 쿼리 파라미터
     * @return 체결 내역 조회 결과
     */
    protected List<OverseasOrderConclusionFindExternalResDto.Output> callOrderConclusionGetApi(OpenApiType openApiType, final HttpHeaders headers, MultiValueMap<String, String> queryParams) {
        return this.callApiUntilDone(new ArrayList<>(), openApiType, headers, queryParams);
    }

    /**
     * 연속 조회해야할 때까지 재귀적으로 호출 수행한다.
     * @param accumulatedResponses 최종 응답 데이터들
     * @param openApiType OpenApi 타입
     * @param headers 요청 헤더
     * @param queryParams 요청 파라미터
     * @return 최종 응답 데이터들
     */
    private List<OverseasOrderConclusionFindExternalResDto.Output> callApiUntilDone(List<OverseasOrderConclusionFindExternalResDto.Output> accumulatedResponses, OpenApiType openApiType, final HttpHeaders headers, final MultiValueMap<String, String> queryParams) {
        ResponseEntity<OverseasOrderConclusionFindExternalResDto> response =
                (ResponseEntity<OverseasOrderConclusionFindExternalResDto>) openApiRequest.httpGetRequestWithExecute(openApiType, headers, queryParams);

        if (!Objects.isNull(response.getBody()) && response.getBody().isSuccess()) {
            log.info("[{}] 일부 데이터 갯수 : {} 개", openApiType.getDiscription(), response.getBody().getOutput().size());
            accumulatedResponses.addAll(response.getBody().getOutput());
        } else {
            LogUtils.openApiFailedResponseMessage(openApiType, response.getBody().getMessage(), response.getBody().getMessageCode());
        }

        if (StringUtils.isNotBlank(response.getHeaders().getFirst(MORE_DATA_YN_HEADER_NAME)) && MORE_DATA_HEADER_FLAGS.contains(response.getHeaders().getFirst(MORE_DATA_YN_HEADER_NAME))) {
            return callApiUntilDone(accumulatedResponses, openApiType, this.prepareHeadersForSequentialApiCalls(headers),
                    this.prepareParamsForSequentialApiCalls(queryParams, response.getBody().getCtxAreaNk200(), response.getBody().getCtxAreaFk200()));
        } else {
            return accumulatedResponses;
        }
    }

    /**
     * 연속 데이터 조회위해 추가 API 호출 위한 요청 파라미터 추가 세팅한다.
     * @param queryParams 기존 쿼리 파라미터
     * @param ctxAreaNk 연속조회키200
     * @param ctxAreaFk 연속조회검색조건200
     * @return 추가 세팅된 쿼리 파라미터
     */
    @Override
    protected MultiValueMap<String, String> prepareParamsForSequentialApiCalls(MultiValueMap<String, String> queryParams, String ctxAreaNk, String ctxAreaFk) {
        queryParams.set("CTX_AREA_NK200", ctxAreaNk);
        queryParams.set("CTX_AREA_FK200", ctxAreaFk);
        return queryParams;
    }
}
