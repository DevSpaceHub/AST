/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : InternalServerErrorExcetion
 creation : 2024.6.15
 author : Yoonji Moon
 */

package com.devspacehub.ast.exception.error;

import com.devspacehub.ast.common.constant.ResultCode;
import lombok.Getter;

/**
 * Server Error 발생 Exception
 * 상태코드는 500으로 전달된다.
 */
@Getter
public class InternalServerErrorException extends RuntimeException {
    private final ResultCode resultCode;

    /**
     * 하위 예외 혹은 비즈니스 로직에서 예외 발생 시 호출하는 생성자
     * @param resultCode 결과 코드
     * @param message 에러 메세지
     */
    public InternalServerErrorException(ResultCode resultCode, String message) {
        super(String.format("Code: %s (%s), Exception Message: %s", resultCode.name(), resultCode.getMessage(), message));
        this.resultCode = resultCode;
    }

    /**
     * 하위 예외 혹은 비즈니스 로직에서 예외 발생 시 호출하는 생성자
     * - 에러 메세지로는 결과 코드의 메세지를 담는다.
     * @param resultCode 결과 코드
     */
    public InternalServerErrorException(ResultCode resultCode) {
        super(String.format("Code: %s (%s)", resultCode.name(), resultCode.getMessage()));
        this.resultCode = resultCode;
    }
}
