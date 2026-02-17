package com.InfoSec.dynamic_password.global.security.auth.oauth2.handler;

import com.InfoSec.dynamic_password.domain.member.dto.OAuthSignupInfo;
import com.InfoSec.dynamic_password.global.security.auth.jwt.dto.GeneratedToken;
import com.InfoSec.dynamic_password.global.security.auth.jwt.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        String provider = oAuth2User.getAttribute("provider");
        String name = oAuth2User.getAttribute("name");
        String mobile = oAuth2User.getAttribute("mobile");

        boolean isExist = Boolean.TRUE.equals(oAuth2User.getAttribute("isExist"));

        String role = oAuth2User.getAuthorities().stream()
                .findFirst()
                .orElseThrow(IllegalAccessError::new)
                .getAuthority();

        if(isExist) {
            GeneratedToken token = jwtUtil.generateToken(email, role);

            String redirectUrl = UriComponentsBuilder
                    .fromUriString("/?token=" + token.getAccessToken())
                    .build().toUriString();

            getRedirectStrategy().sendRedirect(request, response, redirectUrl);
        }

        if(!isExist) {

            OAuthSignupInfo oAuthSignupInfo = new OAuthSignupInfo(
                    email,
                    provider,
                    name,
                    mobile
            );

            request.getSession().setAttribute("oauthSignupInfo", oAuthSignupInfo);

            response.sendRedirect("/sign-up.html");
        }

    }
}
