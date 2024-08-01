/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : InsufficientMoneyException
 creation : 2024.7.27
 author : Yoonji Moon
 */

package com.devspacehub.ast.exception.error;

import com.devspacehub.ast.common.constant.ResultCode;

/**
 * 금액이 충분하지 않은 경우 발생하는 Exception.
 */
public class InsufficientMoneyException extends BusinessException {
    public InsufficientMoneyException(String statusMessage) {
        super(ResultCode.INSUFFICIENT_MONEY_ERROR, statusMessage);
    }
}
