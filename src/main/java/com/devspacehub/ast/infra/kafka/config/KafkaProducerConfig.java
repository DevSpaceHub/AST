/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : KafkaProducerConfig
 creation : 2024.9.15
 author : Yoonji Moon
 */

package com.devspacehub.ast.infra.kafka.config;

import com.devspacehub.ast.infra.kafka.dto.MessageDto;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

/**
 * Kafka Producer Configuration
 */
@Configuration
public class KafkaProducerConfig {

    @Value("${kafka.producer.bootstrap-server.host}")
    private String bootstrapServerHost;
    @Value("${kafka.producer.bootstrap-server.port}")
    private int bootstrapServerPort;

    /**
     * Kafka Producer 설정 프로퍼티 세팅하여 반환한다.
     * @return ProducerFactory
     */
    @Bean
    public ProducerFactory<String, MessageDto> kafkaProducerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, String.format("%s:%d", bootstrapServerHost, bootstrapServerPort));

        return new DefaultKafkaProducerFactory<>(props);
    }

    /**
     * Kafka Producer Bean
     * @return KafkaTemplate
     */
    @Bean
    public KafkaTemplate<String, MessageDto> kafkaTemplate(){
        return new KafkaTemplate<>(kafkaProducerFactory());
    }
}
