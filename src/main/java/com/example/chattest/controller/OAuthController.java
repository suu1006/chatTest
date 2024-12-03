package com.example.chattest.controller;

import com.example.chattest.domain.KakaoUserInfoResponseDto;
import com.example.chattest.service.OAuthService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Controller
@RequiredArgsConstructor
public class OAuthController {

    private final OAuthService oauthService;

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
     * @return
     */
    @GetMapping("/login/page")
    public String loginPage(Model model) {
        String location = KAKAO_AUTH_URI + "/oauth/authorize?response_type=code&client_id=" + clientId + "&redirect_uri=" + redirectUri;
        model.addAttribute("location", location);
        return "login";
    }

    @GetMapping("/callback")
    public ResponseEntity<?> callback(@RequestParam("code") String code) {
        String accessToken = oauthService.getAccessToken(code);
        System.out.println("accessToken: " + accessToken);
        KakaoUserInfoResponseDto userInfo = oauthService.getUserInfo(accessToken);
        System.out.println("userInfo: " + userInfo);
        return new ResponseEntity<>(code, HttpStatus.OK);
    }


}
