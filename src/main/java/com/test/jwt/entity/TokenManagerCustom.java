package com.test.jwt.entity;

import java.time.LocalDateTime;

public interface TokenManagerCustom {
    String getUserIdByRefreshToken(String refreshToken);

    Long renewAccessToken(String newAccessToken, String refreshToken, LocalDateTime AccessTokenExpiredDate);

    Long isVaildToken(String accessToken);
}
