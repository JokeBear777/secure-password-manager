package com.InfoSec.dynamic_password.domain.member.controller;

import com.InfoSec.dynamic_password.domain.member.dto.MeResponseDto;
import com.InfoSec.dynamic_password.domain.member.service.MemberService;
import com.InfoSec.dynamic_password.global.utils.dto.RedirectResponseData;
import com.InfoSec.dynamic_password.global.utils.dto.StatusResponseDto;
import com.InfoSec.dynamic_password.global.security.auth.jwt.dto.SecurityUserDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Tag(name = "Member API", description = "멤버 관련 API")
@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @Operation(summary = "계정 비활성화",
            description = "OAuth2를 통해 인증된 사용자 정보를 기반으로 계정 비활성화, 일주일 후 완전 삭제")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "계정 비활성화 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RedirectResponseData.class))),
            @ApiResponse(responseCode = "400", description = "요청 데이터가 유효하지 않음"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자")
    })
    @DeleteMapping("/me")
    public ResponseEntity<?> deActiveAccount(
            @AuthenticationPrincipal SecurityUserDto securityUserDto
    ) {
        memberService.deActiveAccount(securityUserDto);
        RedirectResponseData redirectResponseData = new RedirectResponseData(
                "Account deactivated. Redirect to login page.",
                "/"
        );
        return ResponseEntity.ok(StatusResponseDto.success(redirectResponseData));
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(
            @AuthenticationPrincipal SecurityUserDto user
    ) {
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("인증되지 않은 사용자");
        }

        return ResponseEntity.ok(
                MeResponseDto.from(user)
        );
    }

}
