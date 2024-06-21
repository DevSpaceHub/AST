/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : StringToOpenApiType
 creation : 2024.6.1
 author : Yoonji Moon
 */

package com.devspacehub.ast.controller.converter;

import com.devspacehub.ast.common.constant.OpenApiType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * String 타입 데이터를 Enum 타입으로 변환시켜주는 Converter 클래스
 */
@Component
public class StringToOpenApiTypeConverter implements Converter<String, OpenApiType> {
    /**
     * @param source the source object to convert, which must be an instance of {@code S} (never {@code null})
     * @return @param source와 code 값이 일치하는 OpenApiType
     */
    @Override
    public OpenApiType convert(String source) {
       return OpenApiType.fromCode(source);
    }
}
