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
import lombok.Builder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 데이터 생성 정보 Base Entity.
 */
@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
public class BaseEntity {
    @CreatedDate
    @Column(name = "registration_datetime")
    private LocalDateTime registrationDateTime;

    @Builder.Default
    @Column(name = "registration_id", length = 100)
    private String registrationId = CommonConstants.REGISTER_ID;
}
