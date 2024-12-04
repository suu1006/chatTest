package com.example.chattest.common.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * OAuth2 인증 성공 시, jwt 를 생성하여 클라이언트에 전달
 */
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        DefaultOAuth2User oAuth2User = (DefaultOAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        String role = "ROLE_USER";

        String token = jwtTokenProvider.createToken(email, role);

        Map<String, String> responseBody = Map.of("token", token);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        new ObjectMapper().writeValue(response.getWriter(), responseBody);
    }
}
