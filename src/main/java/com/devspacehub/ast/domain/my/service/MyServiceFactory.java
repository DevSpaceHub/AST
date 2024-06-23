/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : MyServiceFactory
 creation : 2024.6.12
 author : Yoonji Moon
 */

package com.devspacehub.ast.domain.my.service;

import com.devspacehub.ast.common.constant.MarketType;
import com.devspacehub.ast.common.constant.ResultCode;
import com.devspacehub.ast.exception.error.InvalidValueException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * MyService 구현체 반환하는 팩토리 클래스
 */
@Component
@RequiredArgsConstructor
public class MyServiceFactory {
    private final MyServiceImpl myService;
    private final OverseasMyServiceImpl overseasMyService;

    /**
     * MarketType에 따른 MyService 구현체를 반환한다.
     * @param type 주식 시장 타입
     * @return MyService 구현체
     */
    public MyService resolveService(MarketType type) {
        if (Objects.isNull(type)) {
            throw new InvalidValueException(ResultCode.DATA_IS_NULL_ERROR);
        }
        return switch(type) {
            case DOMESTIC-> myService;
            case OVERSEAS-> overseasMyService;
            default -> throw new InvalidValueException(ResultCode.INVALID_MARKET_TYPE_ERROR);
        };
    }
}
