/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : RequestUtil
 creation : 2024.5.14
 author : Yoonji Moon
 */

package com.devspacehub.ast.util;

import lombok.extern.slf4j.Slf4j;

/**
 * 외부 요청 관련 유틸성 클래스.
 */
@Slf4j
public class RequestUtil {
    private static final long TIME_DELAY_MILLIS = 700L;

    /**
     * KIS Open API 및 디스코드 메세지 전송 API 호출 간 0.7초 시간 지연 수행한다.
     * - KIS Open API : 1초당 2회 이내
     */
    public static void timeDelay() {
        try {
            Thread.sleep(TIME_DELAY_MILLIS);
        } catch (InterruptedException ex) {
            log.error("시간 지연 처리 중 이슈 발생하였습니다.");
            log.error("{}", ex.getMessage());
        }
    }
}
