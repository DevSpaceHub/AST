/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : ReservationOrderInfoRepositoryImpl
 creation : 2024.3.31
 author : Yoonji Moon
 */

package com.devspacehub.ast.domain.my.reservationOrderInfo;

import com.devspacehub.ast.common.constant.YesNoStatus;
import com.devspacehub.ast.domain.my.reservationOrderInfo.dto.QReservationStockItem;
import com.devspacehub.ast.domain.my.reservationOrderInfo.dto.ReservationStockItem;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.devspacehub.ast.domain.itemInfo.QItemInfo.itemInfo;
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
     * @param date 예약 날짜 유효성 여부 기준 Date
     * @return 유효한 예약종목 데이터
     */
    @Override
    public List<ReservationOrderInfo> findValidAll(LocalDate date) {
        return queryFactory.selectFrom(reservationOrderInfo)
                .where(isEqualOrBetweenStartDateAndEndDate(date), isUsable())
                .orderBy(reservationOrderInfo.priority.asc())
                .fetch();
    }

    /**
     * 구매 가능한 예약 종목 전체 조회
     * @param date          예약 날짜 유효성 여부 기준 Date
     * @param exchangeCodes 거래소 코드 필터링
     * @return 유효한 예약종목 데이터
     */
    @Override
    public List<ReservationStockItem> findValidAllByExchangeCodes(LocalDate date, List<String> exchangeCodes) {
        return queryFactory.select(new QReservationStockItem(
                        reservationOrderInfo.seq, reservationOrderInfo.itemCode, reservationOrderInfo.koreanItemName,
                        reservationOrderInfo.orderQuantity, reservationOrderInfo.orderPrice, itemInfo.marketCategory
                ))
                .from(reservationOrderInfo)
                .join(itemInfo)
                .on(eqItemCode(itemInfo.itemCode))
                .where(isEqualOrBetweenStartDateAndEndDate(date), isUsable(), inExchangeCode(exchangeCodes))
                .orderBy(reservationOrderInfo.priority.asc())
                .fetch();
    }

    /**
     * 인자로 전달하는 거래소 코드들에 해당하는 지 확인한다.
     * @param exchangeCodes 거래소 코드들
     * @return 인자로 받은 거래소 코드들에 포함되면 True
     */
    private BooleanExpression inExchangeCode(List<String> exchangeCodes) {
        return itemInfo.marketCategory.in(exchangeCodes);
    }

    /**
     * 특정 종목 중 구매 가능한 예약 종목 단일 조회
     * @param now 유효 기준 일자
     * @param itemCode 종목 코드
     * @return 유효한 예약 매수 종목
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
     * @return 'Y'이면 True
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

    /**
     * 두 문자열이 동일한지 확인한다.
     * @param itemCode 비교 대상 종목 코드
     * @return 두 문자열이 동일한지 여부
     */
    protected BooleanExpression eqItemCode(StringPath itemCode) {
        return itemCode.isNotEmpty().and(reservationOrderInfo.itemCode.isNotEmpty()).and(reservationOrderInfo.itemCode.eq(itemCode));
    }
}
