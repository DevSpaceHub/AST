/*
 © 2023 devspacehub, Inc. All rights reserved.

 name : WebClientCommonResDto
 creation : 2023.12.16
 author : Yoonji Moon
 */

package com.devspacehub.ast.common.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * WebClient 의 공통 Response dto.
 */
@Getter
@Setter
public abstract class WebClientCommonResDto {

    @JsonProperty("msg1")
    protected String message;

    @JsonProperty("msg_cd")
    protected String messageCode;

    @JsonProperty("rt_cd")
    protected String resultCode;

    public abstract boolean isSuccess();

    public boolean isFailed() {
        return !isSuccess();
    }


    @JsonIgnore
    @Override
    public String toString() {
        return String.format("message = %s, messageCode = %s, resultCode = %s", message, messageCode, resultCode);
    }
}
