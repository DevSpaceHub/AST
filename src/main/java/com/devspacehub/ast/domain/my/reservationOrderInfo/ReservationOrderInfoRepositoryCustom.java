/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : ReservationOrderInfoRepositoryCustom
 creation : 2024.3.31
 author : Yoonji Moon
 */

package com.devspacehub.ast.domain.my.reservationOrderInfo;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 예약 매수 종목 테이블 - Querydsl
 */
public interface ReservationOrderInfoRepositoryCustom {

    List<ReservationOrderInfo> findValidAll(LocalDate now);

    Optional<ReservationOrderInfo> findValidOneByItemCodeAndOrderNumber(LocalDate now, String itemCode, String orderNumber);
}
