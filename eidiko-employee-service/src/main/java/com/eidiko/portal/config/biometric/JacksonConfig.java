package com.eidiko.portal.config.biometric;

import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.TimeZone;

@Configuration
public class JacksonConfig {
	@Bean
	Jackson2ObjectMapperBuilderCustomizer jacksonObjectMapperCustomization() {
		return builder -> {
			builder.timeZone(TimeZone.getTimeZone("IST"));
		};
	}

	
}
