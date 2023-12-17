/*
 © 2023 devspacehub, Inc. All rights reserved.

 name : OpenApiUri
 creation : 2023.12.17
 author : Yoonji Moon
 */

package com.devspacehub.ast.common.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * The enum Open api uri.
 */
@Getter
@RequiredArgsConstructor
public enum OpenApiUri {
    DOMESTIC_STOCK_BUY_ORDER("/uapi/domestic-stock/v1/trading/order-cash", "국내 주식 매수(현금)"),
    TEST("", "")
    ;
    private final String uri;
    private final String discription;

}
