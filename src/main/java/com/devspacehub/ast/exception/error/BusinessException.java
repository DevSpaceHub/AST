/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : BusinessException
 creation : 2024.1.8
 author : Yoonji Moon
 */

package com.devspacehub.ast.exception.error;

import com.devspacehub.ast.common.constant.ResultCode;
import lombok.Getter;

/**
 * The type Business exception.
 */
@Getter
public class BusinessException extends RuntimeException {
    private final ResultCode resultCode;

    /**
     * BusinessException 하위 클래스 혹은 비즈니스 로직에서 호출하는 생성자
     * @param resultCode 결과 코드
     */
    public BusinessException(ResultCode resultCode) {
        super(String.format("Code: %s (%s)", resultCode.name(), resultCode.getMessage()));
        this.resultCode = resultCode;
    }

    /**
     * BusinessException 하위 클래스 혹은 비즈니스 로직에서 호출하는 생성자
     * @param resultCode 결과 코드
     * @param message 에러 메세지
     */
    public BusinessException(ResultCode resultCode, String message) {
        super(String.format("Code: %s (%s)%nException Message: %s", resultCode.name(), resultCode.getMessage(), message));
        this.resultCode = resultCode;
    }
}
