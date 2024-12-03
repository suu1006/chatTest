package com.example.chattest.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true) // 매핑되지 않은 필드 무시
public class KakaoTokenResponseDTO {

    @JsonProperty("token_type") // 역직렬화할 때 사용할 변수명
    public String tokenType;

    @JsonProperty("access_token")
    public String accessToken;

    @JsonProperty("id_token")
    public String idToken;

    @JsonProperty("expires_in")
    public Integer expiresIn;

    @JsonProperty("refresh_token")
    public String refreshToken;

    @JsonProperty("refresh_token_expires_in")
    public Integer refreshTokenExpiresIn;

    @JsonProperty("scope")
    public String scope;


}
