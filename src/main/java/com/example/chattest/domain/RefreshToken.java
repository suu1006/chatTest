package com.example.chattest.domain;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@RedisHash(value = "refresh_token")
public class RefreshToken {

    @Id
    private Long id;

    @Indexed
    private String accessToken;

    private String refreshToken;

}
