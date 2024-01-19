/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : ApiResult
 creation : 2024.1.12
 author : Yoonji Moon
 */
package com.devspacehub.ast.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public class ApiResult {
    private Boolean success;
    private String code;
    private String message;
}
