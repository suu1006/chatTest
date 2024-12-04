package com.example.chattest.service;

import com.example.chattest.domain.KakaoTokenResponseDTO;
import com.example.chattest.domain.KakaoUserInfoResponseDto;
import com.example.chattest.domain.User;
import com.example.chattest.domain.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.handler.codec.http.HttpHeaderValues;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class OAuthService {

    private String clientId;
    private final String KAUTH_TOKEN_URL_HOST;
    private final String KAUTH_USER_URL_HOST;

    private final UserRepository userRepository;

    @Autowired
    public OAuthService(@Value("${kakao.client.id}") String clientId, UserRepository userRepository) {
        this.clientId = clientId;
        this.userRepository = userRepository;
        KAUTH_TOKEN_URL_HOST ="https://kauth.kakao.com";
        KAUTH_USER_URL_HOST = "https://kapi.kakao.com";
    }

    public String getAccessToken(String code){
        KakaoTokenResponseDTO kakaoTokenResponseDto = WebClient.create(KAUTH_TOKEN_URL_HOST).post()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .path("/oauth/token")
                        .queryParam("grant_type", "authorization_code")
                        .queryParam("client_id", clientId)
                        .queryParam("code", code)
                        .build(true))
                .header(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_X_WWW_FORM_URLENCODED.toString())
                .retrieve()
                .bodyToMono(KakaoTokenResponseDTO.class)
                .block();

        log.info(" [Kakao Service] Access Token ------> {}", kakaoTokenResponseDto.getAccessToken());
        log.info(" [Kakao Service] Refresh Token ------> {}", kakaoTokenResponseDto.getRefreshToken());

        return kakaoTokenResponseDto.getAccessToken();

    }

    public KakaoUserInfoResponseDto getUserInfo(String accessToken) {
        KakaoUserInfoResponseDto userInfo = WebClient.create(KAUTH_USER_URL_HOST)
                .get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .path("/v2/user/me")
                        .build(true))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken) // access token 인가
                .header(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_X_WWW_FORM_URLENCODED.toString())
                .retrieve()
                .bodyToMono(KakaoUserInfoResponseDto.class)
                .block();

        if (userInfo != null && userInfo.getKakaoAccount() != null) {
            String email = userInfo.getKakaoAccount().getEmail();
            System.out.println("Email: " + email);
        } else {
            System.out.println("Email not available or kakaoAccount is null");
        }

        return userInfo;
    }

    public void join(User user) {
        user.setRole("ROLE_USER");
        String rawPassword = user.getPassword();
        String encPassword = new BCryptPasswordEncoder().encode(rawPassword);
        user.setPassword(encPassword);

        userRepository.save(user);
    }







}
