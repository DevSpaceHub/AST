/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : QuerydslConfig
 creation : 2024.3.31
 author : Yoonji Moon
 */

package com.devspacehub.ast.common.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Querydsl 실행에 필요한 설정
 * - JPAQueryFactory Bean으로 등록.
 */
@Configuration
public class QuerydslConfig {
    @PersistenceContext
    private EntityManager em;
    @Bean
    public JPAQueryFactory jpaQueryFactory() {
        return new JPAQueryFactory(em);
    }
}
