package com.InfoSec.dynamic_password.domain.member.dto;

import com.InfoSec.dynamic_password.global.security.auth.jwt.dto.SecurityUserDto;

public record MeResponseDto(
        Long userId,
        String email,
        String role
) {

    public static MeResponseDto from(SecurityUserDto user) {
        return new MeResponseDto(
                user.getUserId(),
                user.getEmail(),
                user.getRole().toString()
        );
    }
}
