package com.InfoSec.dynamic_password.global.security.auth.oauth2.adapter;

import java.util.Map;


public interface OAuth2AttributeAdapter {

    public String provider();

    public Map<String, Object> convertMap(
            String provider,
            String attributeKey,
            Map<String, Object> attributes);
}
