package com.InfoSec.dynamic_password.domain.member.dto;

import lombok.Getter;

public record OAuthSignupInfo(
        String email,
        String provider,
        String name,
        String mobile
) {
}
