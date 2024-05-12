/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : ReservationOrderInfoRepositoryImpl
 creation : 2024.3.31
 author : Yoonji Moon
 */

package com.devspacehub.ast.domain.my.reservationOrderInfo;

import com.devspacehub.ast.common.constant.YesNoStatus;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.devspacehub.ast.domain.my.reservationOrderInfo.QReservationOrderInfo.*;

/**
 * 예약 매수 종목 테이블 - Querydsl 구현체
 */
@Repository
@RequiredArgsConstructor
public class ReservationOrderInfoRepositoryImpl implements ReservationOrderInfoRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    /**
     * 구매 가능한 예약 종목 전체 조회
     * @param now
     * @return
     */
    @Override
    public List<ReservationOrderInfo> findValidAll(LocalDate now) {
        return queryFactory.selectFrom(reservationOrderInfo)
                .where(isEqualOrBetweenStartDateAndEndDate(now), isUsable())
                .orderBy(reservationOrderInfo.priority.asc())
                .fetch();
    }

    /**
     * 특정 종목 중 구매 가능한 예약 종목 단일 조회
     * @param now
     * @param itemCode
     * @return
     */
    @Override
    public Optional<ReservationOrderInfo> findValidOneByItemCodeAndOrderNumber(LocalDate now, String itemCode, String orderNumber) {
        ReservationOrderInfo findOne = queryFactory.selectFrom(reservationOrderInfo)
                .where(isEqualOrBetweenStartDateAndEndDate(now),
                        reservationOrderInfo.itemCode.eq(itemCode),
                        reservationOrderInfo.orderNumber.eq(orderNumber),
                        isUsable())
                .fetchOne();
        return Optional.ofNullable(findOne);
    }

    /**
     * 사용 가능 여부 확인한다.
     * @return
     */
    private BooleanExpression isUsable() {
        return reservationOrderInfo.useYn.eq(YesNoStatus.YES.getCharCode());
    }

    /**
     * 현재 일자가 startDate와 endDate 사이에 있는지 확인한다.
     * @param now 현 시점
     * @return BooleanExpression
     */
    private BooleanExpression isEqualOrBetweenStartDateAndEndDate(LocalDate now) {
        if (Objects.isNull(now)) {
            return null;
        }
        return (reservationOrderInfo.orderStartDate.eq(now).or(reservationOrderInfo.orderStartDate.before(now)))
                .and((reservationOrderInfo.orderEndDate.eq(now).or(reservationOrderInfo.orderEndDate.after(now))));
    }
}
