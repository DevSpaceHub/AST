/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : OrderTradingBaseEntity
 creation : 2024.1.6
 author : Yoonji Moon
 */

package com.devspacehub.ast.domain.orderTrading;

import com.devspacehub.ast.common.constant.CommonConstants;
import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 데이터 생성 정보 Base Entity.
 */
@Getter
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@MappedSuperclass
@SuperBuilder
public class BaseEntity {
    @Builder.Default
    @Column(name = "registration_datetime")
    private LocalDateTime registrationDateTime = LocalDateTime.now();

    @Builder.Default
    @Column(name = "registration_id", length = 100)
    private String registrationId = CommonConstants.REGISTER_ID;

}
