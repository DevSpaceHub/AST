/*
 © 2023 devspacehub, Inc. All rights reserved.

 name : OAuth
 creation : 2023.12.21
 author : Yoonji Moon
 */

package com.devspacehub.ast.domain.oauth;

import com.devspacehub.ast.common.constant.TokenType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "oauth_tokens")
@Entity
public class OAuthTokens {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seq;
    @Column(name = "oauth_token", length = 400)
    private String oauthToken;

    @Enumerated(EnumType.STRING)
    @Column(name = "token_type", length = 20)
    private TokenType tokenType;

    @Column(name = "oauth_token_expired")
    private LocalDateTime oauthTokenExpired;

    @Column(name = "registration_datetime")
    private LocalDateTime registrationDatetime;

    @Column(name = "registration_id")
    private String registrationId;
}
