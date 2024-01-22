/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : ItemInfo
 creation : 2024.1.20
 author : Yoonji Moon
 */

package com.devspacehub.ast.domain.itemInfo;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 종목정보 테이블 Entity.
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "oauth_tokens")
@Entity
public class ItemInfo {
    @Id
    @Column(name = "item_code", length = 6)
    private String itemCode; // 종목코드

    @Column(name = "standard_code", length = 12)
    private String standardCode; // 표준코드

    @Column(name = "korean_item_name")
    private String korItemName; // 한글종목명

    @Column(name = "english_item_name")
    private String engItemName;

    @Column(name = "market_category", length = 100, nullable = false)
    private String marketCategory;  // 시장구분

    @Column(name = "stock_category", length = 100, nullable = false)
    private String stockCategory;   // 주식구분

    @Column(name = "stock_type", length = 100, nullable = false)
    private String stockType;   // 주식종류

    @Column(name = "registration_datetime")
    private LocalDateTime registrationDatetime;

    @Column(name = "registration_id", length = 100)
    private String registrationId;

    @Column(name = "update_datetime")
    private LocalDateTime updateDatetime;

    @Column(name = "update_id", length = 100)
    private String updateId;

}
