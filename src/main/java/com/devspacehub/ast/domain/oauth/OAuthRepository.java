/*
 © 2023 devspacehub, Inc. All rights reserved.

 name : OAuthRepository
 creation : 2023.12.21
 author : Yoonji Moon
 */

package com.devspacehub.ast.domain.oauth;

import com.devspacehub.ast.common.constant.TokenType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
@Repository
public interface OAuthRepository extends JpaRepository<OAuthTokens, Long> {
    Optional<OAuthTokens> findTopByTokenTypeIsAndOauthTokenExpiredGreaterThanOrderByRegistrationDatetimeDesc(TokenType tokenType, LocalDateTime now);
}
