/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : OrderTradingRepository
 creation : 2024.1.6
 author : Yoonji Moon
 */

package com.devspacehub.ast.domain.orderTrading;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

/**
 * 주문 거래 정보 repository.
 */
public interface OrderTradingRepository extends JpaRepository<OrderTrading, Long> {
    int countByItemCodeAndOrderResultCodeAndTransactionIdAndRegistrationDateTimeBetween(String itemCode, String OrderResultCode, String txId,
                                                                                        LocalDateTime start, LocalDateTime end);
}
