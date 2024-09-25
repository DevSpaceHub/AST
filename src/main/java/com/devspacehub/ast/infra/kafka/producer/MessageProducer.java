/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : MessageProducer
 creation : 2024.8.30
 author : Yoonji Moon
 */

package com.devspacehub.ast.infra.kafka.producer;

import com.devspacehub.ast.infra.kafka.dto.MessageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * 주식 결과 (주문 결과, 체결 결과 등) 메시지 발행하는 Producer
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MessageProducer {
    private final KafkaTemplate<String, MessageDto> template;

    /**
     * 메세지를 발행한다.
     * @param topic 데이터의 토픽
     * @param message MessageDto 상속하는 메세지 Dto 클래스
     */
    public <T extends MessageDto> void produce(String topic, T message) {
        try {
            template.send(this.createRecord(topic, message));
        } catch (Exception e) {
            log.error(String.format("cause: %s, result: failed to produce '%s' message.", e.getMessage(), message.getTitle()), e);
        } finally {
            template.flush();
        }

    }

    /**
     * 메세지 발행 위해 ProducerRecord 생성한다.
     * @param topic 데이터의 토픽
     * @param message MessageDto 상속하는 메세지 Dto 클래스
     * @return ProducerRecord
     */
    private <T extends MessageDto> ProducerRecord<String, MessageDto> createRecord(String topic, T message) {
        log.info("produce message={}, topic={}", message, topic);

        return new ProducerRecord<>(topic, message);
    }
}