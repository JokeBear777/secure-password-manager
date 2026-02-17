package com.InfoSec.dynamic_password.domain.member.service;

import com.InfoSec.dynamic_password.domain.member.dto.OAuthSignupInfo;
import com.InfoSec.dynamic_password.domain.member.entity.Member;
import com.InfoSec.dynamic_password.domain.member.dto.SignUpRequestDto;
import com.InfoSec.dynamic_password.domain.member.repository.MemberRepository;
import com.InfoSec.dynamic_password.domain.member.type.MemberRole;
import com.InfoSec.dynamic_password.global.redis.service.RedisTemplateService;
import com.InfoSec.dynamic_password.global.security.auth.jwt.dto.SecurityUserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.webjars.NotFoundException;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final RedisTemplateService redisTemplateService;

    public Optional<Member> findByEmail(String email) {
        String redisKey = "member:" + email;

        Member redisMember = redisTemplateService.getData(redisKey, Member.class);
        if(redisMember != null) {
            log.info("[Test] redisKey : {} value is present ", redisKey);
            return Optional.of(redisMember);
        }

        Optional<Member> memberOptional = memberRepository.findByEmail(email);

        memberOptional.ifPresent(member->
                redisTemplateService.saveDataWithTTL(redisKey, member, 60, TimeUnit.MINUTES)
        );

        return memberOptional;
    }

    /*
    public void signUp(
                       SignUpRequestDto signUpRequestDto
    ) {
        if (signUpRequestDto.getEmail().isEmpty()) {
            throw new NotFoundException("Email is empty");
        }

        if (signUpRequestDto.getName().isEmpty()) {
            throw new NotFoundException("Name is empty");
        }

        if(signUpRequestDto.getMobile().isEmpty()) {
            throw new NotFoundException("Mobile number is empty");
        }

        if (!isValidEmail(signUpRequestDto.getEmail())) {
            throw new IllegalArgumentException("Invalid email format");
        }

        if (findByEmail(signUpRequestDto.getEmail()).isPresent()) {
            throw new RuntimeException("Email is already in use");
        }

        Member member = Member.builder()
                .email(signUpRequestDto.getEmail())
                .name(signUpRequestDto.getName())
                .mobile(signUpRequestDto.getMobile())
                .memberRole(MemberRole.USER)
                .build();

        memberRepository.save(member);
    }
     */

    @Transactional
    public void signUpOAuth(OAuthSignupInfo oAuthSignupInfo,
                            SignUpRequestDto dto
    ) {
        if (oAuthSignupInfo.email().isEmpty()) {
            throw new NotFoundException("Email is empty");
        }

        if (dto.getName().isEmpty()) {
            throw new NotFoundException("Name is empty");
        }

        if(dto.getMobile().isEmpty()) {
            throw new NotFoundException("Mobile number is empty");
        }

        if (!isValidEmail(oAuthSignupInfo.email())) {
            throw new IllegalArgumentException("Invalid email format");
        }

        if (findByEmail(oAuthSignupInfo.email()).isPresent()) {
            throw new RuntimeException("Email is already in use");
        }
        Member member = Member.builder()
                .email(oAuthSignupInfo.email())
                .name(dto.getName())
                .mobile(dto.getMobile())
                .memberRole(MemberRole.USER)
                .build();

        memberRepository.save(member);
    }

    @Transactional
    public void deActiveAccount(SecurityUserDto securityUserDto) {
        Member member = memberRepository.findByEmail(securityUserDto.getEmail())
                .orElseThrow(() -> new NotFoundException("Member not found"));

        // 멤버가 클럽에 가입되어잇는지 확인, 추후 추가한다
        //
        //

        member.setMemberRole(MemberRole.INACTIVE);
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return email.matches(emailRegex);
    }
}
