/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : BusinessException
 creation : 2024.1.8
 author : Yoonji Moon
 */

package com.devspacehub.ast.exception.error;

/**
 * The type Business exception.
 */
public abstract class BusinessException extends RuntimeException {
    private final ErrorCode errorCode;

    /**
     * Instantiates a new Business exception.
     *
     * @param errorCode the error code
     */
    BusinessException(ErrorCode errorCode) {
        super(errorCode.name() + " : " + errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
