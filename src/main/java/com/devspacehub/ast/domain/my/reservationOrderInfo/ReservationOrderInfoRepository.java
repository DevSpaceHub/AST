/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : ReservationOrderInfoRepository
 creation : 2024.3.22
 author : Yoonji Moon
 */

package com.devspacehub.ast.domain.my.reservationOrderInfo;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 예약 매수 종목 테이블 - JPA Repository
 */
public interface ReservationOrderInfoRepository extends JpaRepository<ReservationOrderInfo, Long>, ReservationOrderInfoRepositoryCustom {

}
