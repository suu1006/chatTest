package com.example.chattest.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class KaKaoInfo {
    private Long id;
    private String nickname;
    private String email;
}
