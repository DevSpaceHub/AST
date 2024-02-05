/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : NotEnoughCashException
 creation : 2024.1.8
 author : Yoonji Moon
 */
package com.devspacehub.ast.exception.error;

import com.devspacehub.ast.common.constant.ResultCode;

public class NotEnoughCashException extends BusinessException {

    public NotEnoughCashException() {
        super(ResultCode.NOT_ENOUGH_CASH_ERROR);
    }
}
