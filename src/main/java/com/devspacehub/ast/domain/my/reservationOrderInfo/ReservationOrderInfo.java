/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : ReservationOrderInfo
 creation : 2024.3.18
 author : Yoonji Moon
 */

package com.devspacehub.ast.domain.my.reservationOrderInfo;

import com.devspacehub.ast.domain.orderTrading.OrderTradingBaseEntity;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table
public class ReservationOrderInfo extends OrderTradingBaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seq;

    private String itemCode;

    private String koreanItemName;

    private int orderPrice;

    private int orderQuantity;
    private LocalDateTime orderStateDatetime;
    private LocalDateTime orderEndDatetime;
    private int priority;
    private char useYn;

    private LocalDateTime updateDateTime;
    private String updateId;

}
