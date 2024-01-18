/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : YesNoStatus
 creation : 2024.1.17
 author : Yoonji Moon
 */

package com.devspacehub.ast.common.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum YesNoStatus {

    YES("Y"),
    NO("N"),
    ;

    private final String code;
}
