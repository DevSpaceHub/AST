/*
 © 2023 devspacehub, Inc. All rights reserved.

 name : MyServiceImpl
 creation : 2023.12.10
 author : Yoonji Moon
 */

package com.devspacehub.ast.domain.my.service;

import com.devspacehub.ast.util.OpenApiCall;
import lombok.RequiredArgsConstructor;

/**
 * 사용자 My 서비스 구현체.
 */
@RequiredArgsConstructor
public class MyServiceImpl implements MyService {
    private final OpenApiCall openApiCall;
    /**
     * 매수 가능 금액 조회
     *
     */
    @Override
    public void getBuyPossibleCash() {

    }
}
