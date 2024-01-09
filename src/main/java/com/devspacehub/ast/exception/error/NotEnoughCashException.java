/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : NotEnoughCashException
 creation : 2024.1.8
 author : Yoonji Moon
 */
package com.devspacehub.ast.exception.error;

public class NotEnoughCashException extends BusinessException {

    public NotEnoughCashException() {
        super(ErrorCode.NOT_ENOUGH_CASH_ERROR);
    }
}
