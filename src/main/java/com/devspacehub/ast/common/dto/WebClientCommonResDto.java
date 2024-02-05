/*
 © 2023 devspacehub, Inc. All rights reserved.

 name : WebClientCommonResDto
 creation : 2023.12.16
 author : Yoonji Moon
 */

package com.devspacehub.ast.common.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * WebClient 의 공통 Response dto.
 */
@Getter
@Setter
public abstract class WebClientCommonResDto {
    public abstract boolean isSuccess();

}
