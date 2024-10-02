/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : KafkaTopicType
 creation : 2024.9.18
 author : Yoonji Moon
 */

package com.devspacehub.ast.infra.kafka.config;

import com.devspacehub.ast.common.constant.ProfileType;
import lombok.RequiredArgsConstructor;

/**
 * Kafka Topic Type Enum
 */
@RequiredArgsConstructor
public enum KafkaTopicType {
    STOCK_RESULT_NOTI_TOPIC("Stock-Result-Notification", "Stock-Result-Notification_TEST");

    private final String prodValue;
    private final String testValue;

    /**
     * 환경 별 토픽 값 반환한다.
     * @param profileType 동작 중인 프로파일 타입
     * @return kafka 토픽
     */
    public String getValue(ProfileType profileType) {
        return ProfileType.PROD.equals(profileType) ? this.prodValue : this.testValue;
    }
}
