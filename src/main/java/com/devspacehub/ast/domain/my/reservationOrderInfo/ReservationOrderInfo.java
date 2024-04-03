/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : ReservationOrderInfo
 creation : 2024.3.18
 author : Yoonji Moon
 */

package com.devspacehub.ast.domain.my.reservationOrderInfo;

import com.devspacehub.ast.domain.orderTrading.OrderTradingBaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 예약 매수 Entity.
 */
@Getter
@SQLRestriction("use_yn = 'Y'")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "reservation_order_info")
@Entity
public class ReservationOrderInfo extends OrderTradingBaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seq;

    @Column(name = "item_code", length = 6)
    private String itemCode;

    @Column(name = "korean_item_name")
    private String koreanItemName;
    @Column(name = "order_price")
    private int orderPrice;

    @Column(name = "order_quantity")
    private int orderQuantity;
    @Column(name = "order_start_date")
    private LocalDate orderStartDate;
    @Column(name = "order_end_date")
    private LocalDate orderEndDate;
    private int priority;
    @Column(name = "use_yn")
    private char useYn;

    @Column(name = "update_datetime")
    private LocalDateTime updateDatetime;
    @Column(name = "update_id")
    private String updateId;
    @Builder
    private ReservationOrderInfo(String itemCode, String koreanItemName, int orderPrice, int orderQuantity, LocalDate orderStartDate, LocalDate orderEndDate, int priority, char useYn) {
        this.itemCode = itemCode;
        this.koreanItemName = koreanItemName;
        this.orderPrice = orderPrice;
        this.orderQuantity = orderQuantity;
        this.orderStartDate = orderStartDate;
        this.orderEndDate = orderEndDate;
        this.priority = priority;
        this.useYn = useYn;
    }

    public boolean isOrderPriceGreaterOrEqualThan(int lowerLimitPrice) {
        return orderPrice >= lowerLimitPrice;
    }

    public void updateToAdjustedPrice(int orderPrice) {
        this.orderPrice = orderPrice;
    }

}
