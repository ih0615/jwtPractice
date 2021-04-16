package com.test.jwt.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class TokenResponseDto {

    @Data
    @NoArgsConstructor
    public static class TokenCommonDto {
        private String accessToken;
        private LocalDateTime expiredAccessDate;
        private String refreshToken;
        private LocalDateTime expiredRefreshDate;
        private Long userIdx;

        public TokenCommonDto(String accessToken, LocalDateTime expiredAccessDate, String refreshToken, LocalDateTime expiredRefreshDate, Long userIdx) {
            this.accessToken = accessToken;
            this.expiredAccessDate = expiredAccessDate;
            this.refreshToken = refreshToken;
            this.expiredRefreshDate = expiredRefreshDate;
            this.userIdx = userIdx;
        }
    }
}
