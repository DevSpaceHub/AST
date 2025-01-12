/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : MessageDto
 creation : 2024.8.30
 author : Yoonji Moon
 */

package com.devspacehub.ast.infra.kafka.dto;

import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * Kafka Producer를 통해 발행하는 공통 Dto 클래스
 */
@Getter
@ToString
@SuperBuilder
public abstract class MessageDto {
    protected String title;
}
