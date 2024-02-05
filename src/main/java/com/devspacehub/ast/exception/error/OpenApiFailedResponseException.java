/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : OpenApiFailedResponseException
 creation : 2024.1.15
 author : Yoonji Moon
 */

package com.devspacehub.ast.exception.error;

import com.devspacehub.ast.common.constant.ResultCode;

/**
 * The type Open api failed exception.
 */
public class OpenApiFailedResponseException extends BusinessException {

    /**
     * Instantiates a new Dto translation exception.
     */
    public OpenApiFailedResponseException() {
        super(ResultCode.OPENAPI_SERVER_RESPONSE_ERROR);
    }
}
