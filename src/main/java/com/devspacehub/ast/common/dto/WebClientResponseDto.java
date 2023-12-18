/*
 © 2023 devspacehub, Inc. All rights reserved.

 name : WebClientResponseDto
 creation : 2023.12.16
 author : Yoonji Moon
 */

package com.devspacehub.ast.common.dto;

import lombok.Getter;

import java.util.List;

/**
 * WebClient 의 공통 Response dto.
 */
@Getter
public class WebClientResponseDto {
    private String rt_cd;   // result code (0)
    private String msg_cd;  // message code
    private String msg1;    // message in Korea
    private List<String> output;  // concrete result body

}
