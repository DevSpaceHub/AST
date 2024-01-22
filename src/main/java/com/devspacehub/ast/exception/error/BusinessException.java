/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : BusinessException
 creation : 2024.1.8
 author : Yoonji Moon
 */

package com.devspacehub.ast.exception.error;

import com.devspacehub.ast.common.constant.ResultCode;
import lombok.Getter;

/**
 * The type Business exception.
 */
@Getter
public class BusinessException extends RuntimeException {
    private final ResultCode resultCode;

    /**
     * Instantiates a new Business exception.
     *
     * @param resultCode the error code
     */
    public BusinessException(ResultCode resultCode) {
        super(resultCode.name() + " : " + resultCode.getMessage());
        this.resultCode = resultCode;
    }
}
