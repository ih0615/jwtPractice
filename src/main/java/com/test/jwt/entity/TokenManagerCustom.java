package com.test.jwt.entity;

import com.test.jwt.request.AuthRequest;
import com.test.jwt.response.TokenResponseDto;

import java.time.LocalDateTime;

public interface TokenManagerCustom {
    String getUserIdByRefreshToken(String refreshToken);

    Long renewAccessToken(String newAccessToken, String refreshToken, LocalDateTime AccessTokenExpiredDate);

    Long isVaildToken(String accessToken);

    Long isExpiredToken(String token);

    String getRefreshTokenByAccessToken(String token);

    TokenResponseDto.TokenCommonDto findToken(AuthRequest authRequest);

    Long updateAllToken(TokenManager tokenManager);

    Long updateAccessToken(String accessToken, LocalDateTime expiredAccessDate, Long userIdx);
}
