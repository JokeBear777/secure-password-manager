package com.InfoSec.dynamic_password.global.config.configuration_properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "domain")
public class DomainConfig {
    private String name;
}
