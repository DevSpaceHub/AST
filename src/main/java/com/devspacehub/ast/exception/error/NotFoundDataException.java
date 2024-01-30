/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : NotFoundDataException
 creation : 2024.1.30
 author : Yoonji Moon
 */

package com.devspacehub.ast.exception.error;

import com.devspacehub.ast.common.constant.ResultCode;

/**
 * 데이터가 존재하지 않을 시 발생하는 Exception.
 */
public class NotFoundDataException extends BusinessException {

    /**
     * Instantiates a NotFoundOpenApiData exception.
     */
    public NotFoundDataException(ResultCode resultCode) {
        super(resultCode);
    }
}
