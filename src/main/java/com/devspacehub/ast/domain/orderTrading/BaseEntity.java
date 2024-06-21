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
import jakarta.persistence.PrePersist;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 데이터 생성 정보 Base Entity.
 */
@Getter
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@MappedSuperclass
@SuperBuilder
public class BaseEntity {
    @Column(name = "registration_datetime")
    private LocalDateTime registrationDateTime;

    @Builder.Default
    @Column(name = "registration_id", length = 100)
    private String registrationId = CommonConstants.REGISTER_ID;


    /**
     * 별도의 값 지정이 없을 경우 기본값으로 현재 시각을 세팅한다.
     */
    @PrePersist
    public void prePersist() {
        if (Objects.isNull(this.registrationDateTime)) {
            this.registrationDateTime = LocalDateTime.now();
        }
    }
}
