package com.example.chattest.service;

import com.example.chattest.domain.KakaoTokenResponseDTO;
import com.example.chattest.domain.KakaoUserInfoResponseDto;
import com.example.chattest.domain.User;
import com.example.chattest.domain.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.handler.codec.http.HttpHeaderValues;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
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

    /**
     * 토큰받기
     * @param code
     * @return
     */
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

    /**
     * 로그아웃
     * @param accessToken
     */
    public void logout(String accessToken) {
        WebClient.create("https://kapi.kakao.com")
                .post()
                .uri("/v1/user/logout")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(String.class)
                .doOnSuccess(response -> log.info("Kakao logout success: {}", response))
                .doOnError(error -> log.error("Kakao logout failed", error))
                .block();
    }



    /**
     * 서비스 약관 동의 내역 확인하기
     */
    public User getUserServiceTerms(String accessToken) {
        WebClient.create("https://kapi.kakao.com")
                .get()
                .uri(uriBuilder -> uriBuilder.path("/v1/user/service/terms").build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(String.class)
                .block();
        return null;
    }


    /**
     * 사용자 정보 가져오기
     * @param accessToken
     * @return
     */
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
            log.info("Email: {}" , email);
        } else {
            log.error("Email not available or kakaoAccount is null");
        }
        return userInfo;
    }

    /**
     * 카카오 로그아웃
     * @param user
     */
//    public void kakaoLogout(String access_Token) {
//        String reqURL = "https://kapi.kakao.com/v1/user/logout";
//        try {
//            URL url = new URL(reqURL);
//            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//            conn.setRequestMethod("POST");
//            conn.setRequestProperty("Authorization", "Bearer " + access_Token);
//
//            int responseCode = conn.getResponseCode();
//            System.out.println("responseCode : " + responseCode);
//
//            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//
//            String result = "";
//            String line = "";
//
//            while ((line = br.readLine()) != null) {
//                result += line;
//            }
//            System.out.println(result);
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//    }


    public void join(User user) {
        user.setRole("ROLE_USER");
        String rawPassword = user.getPassword();
        String encPassword = new BCryptPasswordEncoder().encode(rawPassword);
        user.setPassword(encPassword);

        userRepository.save(user);
    }







}
