/*
 © 2023 devspacehub, Inc. All rights reserved.

 name : MyService
 creation : 2023.12.10
 author : Yoonji Moon
 */

package com.devspacehub.ast.domain.my.service;

import com.devspacehub.ast.common.dto.WebClientCommonResDto;
import com.devspacehub.ast.domain.my.dto.MyServiceRequestDto;
import com.devspacehub.ast.domain.my.reservationOrderInfo.ReservationOrderInfo;
import com.devspacehub.ast.domain.my.reservationOrderInfo.ReservationOrderInfoRepository;
import com.devspacehub.ast.domain.orderTrading.dto.OrderConclusionDto;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

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
     protected final ReservationOrderInfoRepository reservationOrderInfoRepository;

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

        if (validReservationItem.checkTotalConcluded(todayOrderConclusion.getConcludedQuantity())) {
            validReservationItem.disable();
        }
        validReservationItem.updateMetaData(LocalDateTime.now());
    }
}
