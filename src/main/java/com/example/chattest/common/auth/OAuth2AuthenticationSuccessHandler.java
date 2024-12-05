package com.example.chattest.common.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;
import jakarta. servlet. http. Cookie;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 *  OAuth2 인증이 성공하면 호출됨.(Spring Security가 자동 호출함.) - 후처리에 이용(리디렉션, 세션/쿠키에 저장, 사용자 프로필 업데이트, 외부 API 호출)
 *  Spring Security에서 인증 성공 후, JWT 발급을 처리하고 싶은 경우 사용
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        System.out.println("진입------------");
        DefaultOAuth2User oAuth2User = (DefaultOAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        String role = "ROLE_USER";  // 역할을 기본 값으로 설정, 필요에 따라 동적으로 결정 가능

        // 로그로 이메일과 역할 확인
        log.info("OAuth2 로그인 성공, 이메일: " + email + ", 역할: " + role);

        // JWT 생성
        String jwtToken = jwtTokenProvider.createToken(email, role);

        // JWT를 쿠키로 설정하여 클라이언트에 전달
        Cookie cookie = new Cookie("JWT", jwtToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(false); // HTTPS에서만 전송
        cookie.setPath("/");
        response.addCookie(cookie);

        // 리디렉션 URL 설정
        String redirectUrl = "/home";  // 홈 페이지로 리디렉션
        response.sendRedirect(redirectUrl);
    }
}
