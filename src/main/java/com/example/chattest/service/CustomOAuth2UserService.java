package com.example.chattest.service;

import com.example.chattest.domain.User;
import com.example.chattest.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * OAuth 로그인 후 사용자 정보를 가져오고 처리하는 서비스
 */
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = new DefaultOAuth2UserService().loadUser(userRequest);

        // 소셜 서비스에서 가져온 사용자 정보
        String email = oAuth2User.getAttribute("email");

        // 사용자 정보 저장 / 갱신
        User user = userRepository.findByEmail(email).orElseGet(()-> userRepository.save(User.builder()
                .email(email)
                .username(oAuth2User.getAttribute("name"))
                .role("USER")
                .build()));

        return new DefaultOAuth2User((Collections.singleton(new SimpleGrantedAuthority(user.getRole()))), oAuth2User.getAttributes(), "email");
    }
}
