package com.example.chattest.common.auth;

import com.example.chattest.service.CustomOAuth2UserService;
import com.example.chattest.service.OAuthService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig{

    private final CustomOAuth2UserService customOAuth2UserService;
    private final JwtTokenProvider jwtTokenProvider;


    @Bean
    public BCryptPasswordEncoder encodePwd() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // CSRF 비활성화
        http.csrf(csrf -> csrf.disable());

        // 인증 없이 접근할 URL 설정
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/kkoLogin/page", "/callback").permitAll()  // 인증 없이 접근 허용
                .requestMatchers("/api/admin/**").hasRole("ADMIN")  // ADMIN 권한 필요
                .anyRequest().authenticated()  // 나머지 모든 요청은 인증 필요
        );

        // JWT 인증 필터 추가
        http.addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationEntryPoint customAuthenticationEntryPoint() {
        return new AuthenticationEntryPoint() {
            @Override
            public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws ServletException {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                response.setCharacterEncoding(StandardCharsets.UTF_8.name());

                try (PrintWriter writer = response.getWriter()) {
                    writer.write("{\"error\": \"Unauthorized\", \"message\": \"" + authException.getMessage() + "\"}");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }
}
