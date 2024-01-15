/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : OpenApiFailedException
 creation : 2024.1.15
 author : Yoonji Moon
 */

package com.devspacehub.ast.exception.error;

/**
 * The type Open api failed exception.
 */
public class OpenApiFailedResponseException extends BusinessException {

    /**
     * Instantiates a new Dto translation exception.
     */
    public OpenApiFailedResponseException() {
        super(ErrorCode.OPENAPI_FAILED_RESPONSE);
    }
}
