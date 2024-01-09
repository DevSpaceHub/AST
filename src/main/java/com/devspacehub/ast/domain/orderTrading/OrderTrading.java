/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : OrderTrading
 creation : 2024.1.3
 author : Yoonji Moon
 */

package com.devspacehub.ast.domain.orderTrading;

import jakarta.persistence.*;
import lombok.*;

/**
 * 주문 거래 정보 Entity.
 */
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "orders")
@Entity
public class OrderTrading extends OrderTradingBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seq;

    @Column(name = "item_code")
    private String itemCode;

    @Column(name = "transaction_id")
    private String transactionId;

    @Column(name = "order_number")
    private String orderNumber;

    @Column(name = "order_division")
    private String orderDivision;

    @Column(name = "order_price")
    private Integer orderPrice;

    @Column(name = "order_quantity")
    private Integer orderQuantity;

    @Column(name = "order_datetime")
    private String orderDateTime;   // HHMMSS

    @Column(name = "order_result_code")
    private String orderResultCode;

    @Column(name = "order_message_code")
    private String orderMessageCode;

    @Column(name = "order_message")
    private String orderMessage;

}
