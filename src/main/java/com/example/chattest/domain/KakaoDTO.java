package com.example.chattest.domain;

import lombok.Getter;
import org.springframework.web.bind.annotation.GetMapping;

public class KakaoDTO {
    @Getter
    public static class OAuthToken {
        private String access_token;
        private String token_type;
        private String refresh_token;
        private int expires_in;
        private String scope;
        private int refresh_token_expires_in;
    }
}
