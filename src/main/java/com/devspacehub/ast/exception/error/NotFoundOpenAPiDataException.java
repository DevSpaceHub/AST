/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : NotFoundOpenAPiDataException
 creation : 2024.1.28
 author : Yoonji Moon
 */

package com.devspacehub.ast.exception.error;

import com.devspacehub.ast.common.constant.ResultCode;

public class NotFoundOpenAPiDataException extends BusinessException{

    public NotFoundOpenAPiDataException(ResultCode errorCode) {
        super(errorCode);
    }
}
