/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : DtoConversionException
 creation : 2024.1.9
 author : Yoonji Moon
 */

/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : DtoTranslationException
 creation : 2024.1.9
 author : Yoonji Moon
 */

package com.devspacehub.ast.exception.error;

import com.devspacehub.ast.common.constant.ResultCode;

/**
 * The type Dto translation exception.
 */
public class DtoConversionException extends InternalServerErrorException {

    /**
     * Instantiates a new Dto translation exception.
     */
    public DtoConversionException() {
        super(ResultCode.DTO_CONVERSION_ERROR);
    }
}
