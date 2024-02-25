/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : UnexpectedValueException
 creation : 2024.1.30
 author : Yoonji Moon
 */

package com.devspacehub.ast.exception.error;

import com.devspacehub.ast.common.constant.ResultCode;

/**
 * 예상치 못한 타입/값인 경우 발생하는 Exception.
 */
public class InvalidValueException extends BusinessException {
    public InvalidValueException(ResultCode resultCode) {
        super(resultCode);
    }
}
