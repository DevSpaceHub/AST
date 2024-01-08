/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : TradingRepository
 creation : 2024.1.6
 author : Yoonji Moon
 */

package com.devspacehub.ast.domain.orderTrading;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 주문 거래 정보 repository.
 */
public interface OrderTradingRepository extends JpaRepository<OrderTrading, Long> {
}
