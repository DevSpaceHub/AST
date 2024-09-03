/*
 © 2023 devspacehub, Inc. All rights reserved.

 name : NotificationException
 creation : 2024.1.27
 author : Yoonji Moon
 */
package com.devspacehub.ast.exception.error;

import com.devspacehub.ast.common.constant.ResultCode;

/**
 * 알림 기능 처리 중 에러 발생 시 사용하는 Exception 클래스.
 */
public class NotificationException extends BusinessException {

    /**
     * Instantiates a notification exception.
     */
    public NotificationException(String message) {
        super(ResultCode.NOTIFICATION_ERROR, message);
    }
}
