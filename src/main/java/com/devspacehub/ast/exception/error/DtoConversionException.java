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

/**
 * The type Dto translation exception.
 */
public class DtoConversionException extends BusinessException {

    /**
     * Instantiates a new Dto translation exception.
     */
    public DtoConversionException() {
        super(ErrorCode.DTO_CONVERSION_ERROR);
    }
}
