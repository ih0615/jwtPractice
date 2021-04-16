package com.test.jwt.entity;

import com.querydsl.core.types.Projections;
import com.test.jwt.request.AuthRequest;
import com.test.jwt.response.TokenResponseDto;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;

import static com.test.jwt.entity.QTokenManager.tokenManager;
import static com.test.jwt.entity.QUser.user;

@Transactional
@Repository
public class TokenManagerRepositoryImpl extends QuerydslRepositorySupport implements TokenManagerCustom {

    public TokenManagerRepositoryImpl() {
        super(TokenManager.class);
    }

    @Override
    public String getUserIdByRefreshToken(String refreshToken) {
        return from(tokenManager)
                .select(Projections.fields(User.class, user.userId))
                .join(user)
                .on(tokenManager.idx.eq(user.idx).and(tokenManager.refresh_token.eq(refreshToken)))
                .fetchJoin()
                .fetchOne()
                .getUserId();
    }

    @Override
    public Long renewAccessToken(String newAccessToken, String refreshToken, LocalDateTime AccessTokenExpiredDate) {
        return update(tokenManager)
                .where(tokenManager.refresh_token.eq(refreshToken))
                .set(tokenManager.accessToken, newAccessToken)
                .set(tokenManager.expiredAccessDate, AccessTokenExpiredDate)
                .execute();
    }

    @Override
    public Long isVaildToken(String accessToken) {
        return from(tokenManager).select(tokenManager.count())
                .where(tokenManager.accessToken.eq(accessToken))
                .fetchOne();
    }

    @Override
    public Long isExpiredToken(String token) {
        return from(tokenManager).select(tokenManager.count())
                .where(tokenManager.accessToken.eq(token).and(tokenManager.expiredAccessDate.after(LocalDateTime.now(ZoneId.systemDefault()))))
                .fetchOne();
    }

    @Override
    public String getRefreshTokenByAccessToken(String token) {
        return from(tokenManager).select(tokenManager.refresh_token)
                .where(tokenManager.accessToken.eq(token).and(tokenManager.expiredRefreshDate.after(LocalDateTime.now(ZoneId.systemDefault()))))
                .fetchOne();
    }

    @Override
    public TokenResponseDto.TokenCommonDto findToken(AuthRequest authRequest) {
        return from(tokenManager)
                .select(
                        Projections.constructor(
                                TokenResponseDto.TokenCommonDto.class,
                                tokenManager.accessToken,
                                tokenManager.expiredAccessDate,
                                tokenManager.refresh_token,
                                tokenManager.expiredRefreshDate,
                                tokenManager.userIdx))
                .where(tokenManager.userIdx
                        .eq(from(user)
                                .select(user.idx)
                                .where(user.userId.eq(authRequest.getUserId())
                                        )))
                .fetchOne();
    }

    @Override
    public Long updateAllToken(TokenManager tokenManager1) {
        return update(tokenManager)
                .where(tokenManager.userIdx.eq(tokenManager1.getUserIdx()))
                .set(tokenManager.accessToken, tokenManager1.getAccessToken())
                .set(tokenManager.refresh_token, tokenManager1.getRefresh_token())
                .set(tokenManager.expiredAccessDate, tokenManager1.getExpiredAccessDate())
                .set(tokenManager.expiredRefreshDate, tokenManager1.getExpiredRefreshDate())
                .execute();
    }

    @Override
    public Long updateAccessToken(String accessToken, LocalDateTime expiredAccessDate, Long userIdx) {
        return update(tokenManager)
                .where(tokenManager.userIdx.eq(userIdx))
                .set(tokenManager.accessToken, accessToken)
                .set(tokenManager.expiredAccessDate, expiredAccessDate)
                .execute();
    }
}
