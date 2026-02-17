package com.InfoSec.dynamic_password.global.exeption.custom;

public class OAuthSignupSessionNotFoundException extends RuntimeException {
    public OAuthSignupSessionNotFoundException() {
        super("OAuth signup session not found");
    }
}
