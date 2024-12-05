package com.example.chattest.controller;

import com.example.chattest.common.auth.JwtTokenProvider;
import com.example.chattest.domain.AuthResponse;
import com.example.chattest.domain.KakaoUserInfoResponseDto;
import com.example.chattest.domain.User;
import com.example.chattest.service.OAuthService;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import jakarta. servlet. http. Cookie;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@Slf4j
@Controller
@RequiredArgsConstructor
public class OAuthController {

    private final OAuthService oauthService;
    private final JwtTokenProvider jwtTokenProvider;

    @Value("${kakao.client.id}")
    String clientId;

    @Value("${kakao.redirect.uri}")
    String redirectUri;

    @Value("${kakao.client.secret}")
    String clientSecret;

    private static final String KAKAO_AUTH_URI = "https://kauth.kakao.com";
    private static final String KAKAO_API_URI = "https://kapi.kakao.com";

    @PostConstruct
    public void init() {
        System.out.println("======= OAuthController init =======");
        System.out.println("clientId: " + clientId);
        System.out.println("clientSecret: " + clientSecret);
        System.out.println("redirectUri: " + redirectUri);
        System.out.println("====================================");
    }

    /**
     * 카카오 로그인 페이지
     * @param model
     * @return 302 Redirect -> 인가코드 발급
     */
    @GetMapping("/login/page")
    public String loginPage(Model model) {
        String location = KAKAO_AUTH_URI + "/oauth/authorize?response_type=code&client_id=" + clientId + "&redirect_uri=" + redirectUri;
        model.addAttribute("location", location);
        return "login";
    }

    /**
     * Redirect 후 유저정보 조회
     * @param code
     * @return
     */
    @GetMapping("/callback")
    public String callback(@RequestParam("code") String code, HttpServletResponse response) throws IOException {
        // 1. access token 발급
        String accessToken = oauthService.getAccessToken(code);
        log.info("accessToken: {}", accessToken);

        // 2. 사용자 정보 가져오기
        KakaoUserInfoResponseDto userInfo = oauthService.getUserInfo(accessToken);
        String userId = String.valueOf(userInfo.getId());
        log.info("userId: {}", userId);

        // 3. jwt 토큰 발급
        String jwtToken = jwtTokenProvider.createToken(userId, "USER");
        log.info("jwtToken: {}", jwtToken);

        return "/home";
    }

    @PostMapping("/join")
    public String join(User user) {
        log.info("user: {}", user);
        oauthService.join(user);
        return "redirect:/loginForm";
    }

    @GetMapping("/home")
    public String homePage() {
        return "home";
    }
}
