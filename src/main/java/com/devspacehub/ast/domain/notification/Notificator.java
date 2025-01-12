/*
 © 2023 devspacehub, Inc. All rights reserved.

 name : Notificator
 creation : 2024.1.27
 author : Yoonji Moon
 */
package com.devspacehub.ast.domain.notification;

import com.devspacehub.ast.common.constant.ProfileType;
import com.devspacehub.ast.infra.kafka.config.KafkaTopicType;
import com.devspacehub.ast.infra.kafka.dto.MessageDto;
import com.devspacehub.ast.infra.kafka.producer.MessageProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 알림 클래스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class Notificator {
    private final MessageProducer messageProducer;

    /**
     * 주식 결과 알림 메세지 발송 요청한다.
     * @param content MessageDto 상속하는 메세지 Dto 클래스
     */
    public <T extends MessageDto> void sendStockResultMessage(T content) {
        messageProducer.produce(this.createTopic(KafkaTopicType.STOCK_RESULT_NOTI_TOPIC), content);
    }

    /**
     * 환경 별 토픽을 생성한다.
     * @return 토픽
     */
    private String createTopic(KafkaTopicType topicType) {
        return topicType.getValue(ProfileType.getActiveProfileType());
    }
}
