/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : OverseasMyServiceImpl
 creation : 2024.5.31
 author : Yoonji Moon
 */

package com.devspacehub.ast.domain.my.service;

import com.devspacehub.ast.common.config.OpenApiProperties;
import com.devspacehub.ast.common.constant.OpenApiType;
import com.devspacehub.ast.domain.my.dto.MyServiceRequestDto;
import com.devspacehub.ast.domain.my.reservationOrderInfo.ReservationOrderInfoRepository;
import com.devspacehub.ast.domain.my.stockBalance.dto.request.OverseasBuyPossibleCashApiReqDto;
import com.devspacehub.ast.domain.my.stockBalance.dto.request.OverseasStockBalanceApiReqDto;
import com.devspacehub.ast.domain.my.stockBalance.dto.response.overseas.OverseasBuyPossibleCashApiResDto;
import com.devspacehub.ast.domain.my.stockBalance.dto.response.overseas.OverseasStockBalanceApiResDto;
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
import java.util.List;
import java.util.function.Consumer;

import static com.devspacehub.ast.common.constant.OpenApiType.OVERSEAS_BUY_ORDER_POSSIBLE_CASH;
import static com.devspacehub.ast.common.constant.OpenApiType.OVERSEAS_STOCK_BALANCE;

/**
 * 해외 주식 - My 기능 서비스 구현체 클래스
 * MyService 추상 클래스를 상속한다.
 */
@Slf4j
@Service
public class OverseasMyServiceImpl extends MyService {
    private final OpenApiRequest openApiRequest;
    private final OpenApiProperties openApiProperties;

    @Value("${openapi.rest.header.transaction-id.overseas.buy-order-possible-cash-find}")
    private String txIdBuyOrderPossibleCashFind;
    @Value("${openapi.rest.header.transaction-id.overseas.stock-balance-find}")
    private String txIdStockBalanceFind;

    public OverseasMyServiceImpl(ReservationOrderInfoRepository reservationOrderInfoRepository, OpenApiRequest openApiRequest,
                                 OpenApiProperties openApiProperties) {
        super(reservationOrderInfoRepository);
        this.openApiRequest = openApiRequest;
        this.openApiProperties = openApiProperties;
    }

    /**
     * 매수 가능 금액 조회 (Get)
     * @param requestDto requestDto MyService Layer Request Dto
     * @param <T> MyserviceRequestDto를 상속하는 타입.
     * @throws OpenApiFailedResponseException 성공 응답이 아닌 경우
     * @return 해외 주문 가능 현금
     */
    @Override
    public <T extends MyServiceRequestDto> BigDecimal getBuyOrderPossibleCash(T requestDto) {
        Consumer<HttpHeaders> httpHeaders = OverseasBuyPossibleCashApiReqDto.setHeaders(openApiProperties.getOauth(), txIdBuyOrderPossibleCashFind);
        MultiValueMap<String, String> queryParams = OverseasBuyPossibleCashApiReqDto.createParameter(
                openApiProperties.getAccntNumber(), openApiProperties.getAccntProductCode(), (MyServiceRequestDto.Overseas)requestDto);

        OverseasBuyPossibleCashApiResDto responseDto = (OverseasBuyPossibleCashApiResDto) openApiRequest.httpGetRequest(OVERSEAS_BUY_ORDER_POSSIBLE_CASH, httpHeaders, queryParams);

        if (responseDto.isFailed()) {
            throw new OpenApiFailedResponseException(OVERSEAS_BUY_ORDER_POSSIBLE_CASH, responseDto.getMessage());
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
        // TODO 추후 개발
        return null;
    }

    /**
     * 해외 예약 매수 수량 만큼 모두 체결되면 사용 여부를 비활성화 한다.
     * @param orderConclusion 체결 종목 조회 응답 Dto
     * @param concludedDate 체결 일자
     */
    @Override
    public void updateMyReservationOrderUseYn(OrderConclusionDto orderConclusion, LocalDate concludedDate) {
        // TODO 추후 해외 예약 매수 개발 후 개발
    }
}
