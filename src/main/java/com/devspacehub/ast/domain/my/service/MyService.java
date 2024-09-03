/*
 © 2023 devspacehub, Inc. All rights reserved.

 name : MyService
 creation : 2023.12.10
 author : Yoonji Moon
 */

package com.devspacehub.ast.domain.my.service;

import com.devspacehub.ast.common.config.OpenApiProperties;
import com.devspacehub.ast.common.dto.WebClientCommonResDto;
import com.devspacehub.ast.domain.my.dto.MyServiceRequestDto;
import com.devspacehub.ast.domain.my.reservationOrderInfo.ReservationOrderInfo;
import com.devspacehub.ast.domain.my.reservationOrderInfo.ReservationOrderInfoRepository;
import com.devspacehub.ast.domain.my.dto.orderConclusion.OrderConclusionDto;
import com.devspacehub.ast.util.OpenApiRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * MyService 추상 클래스.
 */
@RequiredArgsConstructor
public abstract class MyService {
    protected static final String MORE_DATA_YN_HEADER_NAME = "tr_cont";
    protected static final List<String> MORE_DATA_HEADER_FLAGS = List.of("F", "M");
    protected static final String MORE_DATA_Y_HEADER_VALUE = "N";

    protected final ReservationOrderInfoRepository reservationOrderInfoRepository;
    protected final OpenApiRequest openApiRequest;
    protected final OpenApiProperties openApiProperties;

    /**
     * 매수 가능 금액 조회 (Get)
     * @param requestDto MyService Layer Request Dto
     * @return the buy order possible cash
     */
    public abstract <T extends MyServiceRequestDto> BigDecimal getBuyOrderPossibleCash(T requestDto);

    /**
     * 주식 잔고 조회 (Get)
     *
     * @return 나의 주식 잔고
     */
    public abstract WebClientCommonResDto getMyStockBalance();

    /**
     * 금일 체결된 종목 조회 (Get)
     */
    public abstract List<OrderConclusionDto> getConcludedStock(LocalDate today);

    /**
     * 예약 매수 중 체결된 매수 수량에 따라 예약 매수 주문 수량 업데이트한다.
     * 체결 종목이 예약 매수 종목이 아니라면 아무런 동작을 하지 않는다.
     * @param todayOrderConclusion 금일 체결 종목 조회 응답 Dto
     * @param concludedDate 체결 일자
     */
    @Transactional
    public void updateMyReservationOrderUseYn(OrderConclusionDto todayOrderConclusion, LocalDate concludedDate) {
        Optional<ReservationOrderInfo> optionalReservationItem = reservationOrderInfoRepository.findValidOneByItemCodeAndOrderNumber(
                concludedDate, todayOrderConclusion.getItemCode(), todayOrderConclusion.getOrderNumber());

        if (optionalReservationItem.isEmpty()) {
            return;
        }
        ReservationOrderInfo validReservationItem = optionalReservationItem.get();
        validReservationItem.addConcludedQuantity(todayOrderConclusion.getConcludedQuantity());

        if (validReservationItem.checkTotalConcluded()) {
            validReservationItem.disable();
        }
        validReservationItem.updateMetaData(LocalDateTime.now());
    }

    /**
     * 추가 데이터 조회 위한 API 호출에 필요한 헤더 세팅한다.
     * @param httpHeaders 요청 헤더
     * @return HttpHeaders
     */
    protected HttpHeaders prepareHeadersForSequentialApiCalls(HttpHeaders httpHeaders) {
        httpHeaders.add(MORE_DATA_YN_HEADER_NAME, MORE_DATA_Y_HEADER_VALUE);
        return httpHeaders;
    }

    /**
     * 추가 데이터 조회 위한 API 호출에 필요한 파라미터 세팅한다.
     * @param queryParams 쿼리 파라미터
     * @param ctxAreaNk 연속조회키
     * @param ctxAreaFk 연속조회검색조건
     * @return MultiValueMap 쿼리 파라미터
     */
    protected abstract MultiValueMap<String, String> prepareParamsForSequentialApiCalls(MultiValueMap<String, String> queryParams, String ctxAreaNk, String ctxAreaFk);
}
