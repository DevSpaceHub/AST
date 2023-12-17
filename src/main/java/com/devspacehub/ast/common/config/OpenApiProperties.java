/*
 © 2023 devspacehub, Inc. All rights reserved.

 name : OpenApiProperties
 creation : 2023.12.17
 author : Yoonji Moon
 */

package com.devspacehub.ast.common.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
@Getter
@Component
public class OpenApiProperties {
    @Value("${my.comprehensive-accountnumber}")
    private String accntNumber;
    @Value("${my.accountnumber-product-code}")
    private String accntProductCode;

}
