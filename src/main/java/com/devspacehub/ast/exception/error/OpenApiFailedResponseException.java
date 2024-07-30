/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : OpenApiFailedResponseException
 creation : 2024.1.15
 author : Yoonji Moon
 */

package com.devspacehub.ast.exception.error;

import com.devspacehub.ast.common.constant.OpenApiType;
import com.devspacehub.ast.common.constant.ResultCode;

/**
 * The type Open api failed exception.
 */
public class OpenApiFailedResponseException extends BusinessException {

    /**
     * Instantiates a new Dto translation exception.
     */
    public OpenApiFailedResponseException(String message) {
        super(ResultCode.OPENAPI_SERVER_RESPONSE_ERROR, message);
    }
    public OpenApiFailedResponseException(OpenApiType openApiType, String exMessage) {
        super(ResultCode.OPENAPI_SERVER_RESPONSE_ERROR, String.format("요청 실패하였습니다.(API : %s, Open Api Server Response Message: %s)", openApiType.getDiscription(), exMessage));
    }
}
