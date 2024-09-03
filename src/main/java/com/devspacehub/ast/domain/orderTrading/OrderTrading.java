/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : OrderTrading
 creation : 2024.1.3
 author : Yoonji Moon
 */

package com.devspacehub.ast.domain.orderTrading;

import com.devspacehub.ast.common.utils.NumberUtils;
import com.devspacehub.ast.domain.marketStatus.dto.StockItemDto;
import com.devspacehub.ast.domain.orderTrading.dto.StockOrderApiResDto;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

/**
 * 주문 거래 정보 Entity.
 */
@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "orders")
@Entity
public class OrderTrading extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seq;

    @Column(name = "item_code")
    private String itemCode;
    @Column(name = "item_name_korean")
    private String itemNameKor;

    @Column(name = "transaction_id")
    private String transactionId;

    @Column(name = "order_number")
    private String orderNumber;

    @Column(name = "order_division")
    private String orderDivision;

    @Column(name = "order_price")
    private BigDecimal orderPrice;

    @Column(name = "order_quantity")
    private Integer orderQuantity;

    @Column(name = "order_time")
    private String orderTime;   // HHMMSS

    @Column(name = "order_result_code")
    private String orderResultCode;

    @Column(name = "order_message_code")
    private String orderMessageCode;

    @Column(name = "order_message")
    private String orderMessage;

    /**
     * 팩토리 메서드
     * @param item 주식 정보 DTO
     * @param result 국내 주식 주문 응답 DTO
     * @param txId 트랜잭션 ID
     * @return 주문 거래 정보 Entity
     */
    public static OrderTrading from(StockItemDto item, StockOrderApiResDto result, String txId) {
        return OrderTrading.builder()
                .itemCode(item.getItemCode())
                .itemNameKor(item.getItemNameKor())
                .transactionId(txId)
                .orderDivision(item.getOrderDivision())
                .orderPrice(item.getOrderPrice())
                .orderQuantity(item.getOrderQuantity())
                .orderResultCode(result.getResultCode())
                .orderMessageCode(result.getMessageCode())
                .orderMessage(result.getMessage())
                .orderNumber(result.isSuccess() ? NumberUtils.padLeftValueWithZeros(result.getOutput().getOrderNumber(), "0", 10) : null)
                .orderTime(result.isSuccess() ? result.getOutput().getOrderTime() : null)
                .build();
    }
}
