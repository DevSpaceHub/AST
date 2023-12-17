/*
 © 2023 devspacehub, Inc. All rights reserved.

 name : StockOrderType
 creation : 2023.12.11
 author : Yoonji Moon
 */

package com.devspacehub.ast.common.constant;

import lombok.RequiredArgsConstructor;

/**
 * The enum Stock order type.
 */
@RequiredArgsConstructor
public enum StockOrderType {
    BUY("VTTC0802U"),
    SELL("VTTC0801U")
    ;

    private final String transactionId;


}
