/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : ReservationOrderInfoRepository
 creation : 2024.3.22
 author : Yoonji Moon
 */

package com.devspacehub.ast.domain.my.reservationOrderInfo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
@Repository
public interface ReservationOrderInfoRepository extends JpaRepository<ReservationOrderInfo, Long> {
    // TODO QueryDsl 추가하여 더 간결하게 리팩토링 필요.
    List<ReservationOrderInfo> findAllByOrderStartDateBeforeOrOrderStartDateEqualsAndOrderEndDateAfterOrOrderEndDateEquals(LocalDate orderStartDate, LocalDate equalStartDate, LocalDate orderEndDate, LocalDate equalEndDate);
}
