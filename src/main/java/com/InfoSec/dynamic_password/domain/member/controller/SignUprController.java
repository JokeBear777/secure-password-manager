package com.InfoSec.dynamic_password.domain.member.controller;
import com.InfoSec.dynamic_password.domain.member.dto.SignUpRequestDto;
import com.InfoSec.dynamic_password.domain.member.service.MemberService;
import com.InfoSec.dynamic_password.global.security.auth.jwt.dto.SecurityUserDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Member API", description = "회원 가입 관련 API")
@RestController
@RequestMapping("/api/sign-up")
@RequiredArgsConstructor
public class SignUprController {

    private final MemberService memberService;

    @Operation(summary = "회원가입", description = "OAuth2를 통해 인증된 사용자 정보를 기반으로 회원가입을 처리합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "회원가입 성공"),
            @ApiResponse(responseCode = "400", description = "요청 데이터가 유효하지 않음"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자")
    })
    @PostMapping()
    public ResponseEntity<?> signUp (
            @Valid @RequestBody SignUpRequestDto signUpRequestDto
    ) {
        memberService.signUp(signUpRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }




}
