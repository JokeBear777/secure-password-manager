package com.InfoSec.dynamic_password.domain.member.controller;
import com.InfoSec.dynamic_password.domain.member.dto.OAuthSignupInfo;
import com.InfoSec.dynamic_password.domain.member.dto.SignUpRequestDto;
import com.InfoSec.dynamic_password.domain.member.service.MemberService;
import com.InfoSec.dynamic_password.global.exeption.custom.OAuthSignupSessionNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Tag(name = "Member API", description = "회원 가입 관련 API")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class SignUpController {

    private final MemberService memberService;

    @Operation(summary = "회원가입", description = "OAuth2를 통해 인증된 사용자 정보를 기반으로 회원가입을 처리합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "회원가입 성공"),
            @ApiResponse(responseCode = "400", description = "요청 데이터가 유효하지 않음"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자")
    })
    @PostMapping("/sign-up")
    public ResponseEntity<?> signUp (
            @Valid @RequestBody SignUpRequestDto signUpRequestDto,
            HttpServletRequest request
    ) {
        HttpSession session = request.getSession(false);

        OAuthSignupInfo info = Optional.ofNullable(session)
                .map(s -> (OAuthSignupInfo) s.getAttribute("oauthSignupInfo"))
                .orElseThrow(OAuthSignupSessionNotFoundException::new);

        memberService.signUpOAuth(info, signUpRequestDto);

        session.removeAttribute("oauthSignupInfo");

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }


    @GetMapping("/oauth-info")
    public ResponseEntity<?> getOAuthInfo(HttpServletRequest request) {

        HttpSession session = request.getSession(false);

        if (session == null) {
            throw new RuntimeException("OAuth session not found");
        }

        OAuthSignupInfo info =
                (OAuthSignupInfo) session.getAttribute("oauthSignupInfo");

        if (info == null) {
            throw new RuntimeException("OAuth info not found");
        }

        return ResponseEntity.ok(info);
    }


}
