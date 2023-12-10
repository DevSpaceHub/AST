/*
 © 2023 devspacehub, Inc. All rights reserved.

 name : AstApplicationTests
 creation : 2023.12.9
 author : Yoonji Moon
 */

package com.devspacehub.ast;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class AstApplication {

	public static void main(String[] args) {
		SpringApplication.run(AstApplication.class, args);
	}

}
