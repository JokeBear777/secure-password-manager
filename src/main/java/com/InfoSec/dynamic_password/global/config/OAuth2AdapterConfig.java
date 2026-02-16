package com.InfoSec.dynamic_password.global.config;

import com.InfoSec.dynamic_password.global.annotation.OAuth2Provider;
import com.InfoSec.dynamic_password.global.security.auth.oauth2.adapter.OAuth2AttributeAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/*
@Configuration
public class OAuth2AdapterConfig {

    @Bean
    public Map<String, OAuth2AttributeAdapter> attributeAdapterMap(List<OAuth2AttributeAdapter> adapters) {
        return adapters.stream()
                .collect(Collectors.toMap(
                        adapter -> adapter.getClass().getAnnotation(OAuth2Provider.class).value(),
                        Function.identity()
                ));
    }
}
*/

