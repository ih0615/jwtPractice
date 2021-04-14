package com.test.jwt.entity;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

import static com.test.jwt.entity.QTokenManager.tokenManager;
import static com.test.jwt.entity.QUser.user;

@Repository
public class TokenManagerRepositoryImpl extends QuerydslRepositorySupport implements TokenManagerCustom {
    private final JPAQueryFactory queryFactory;

    public TokenManagerRepositoryImpl(JPAQueryFactory queryFactory) {
        super(TokenManager.class);
        this.queryFactory = queryFactory;
    }

    @Override
    public String getUserIdByRefreshToken(String refreshToken) {
        return queryFactory
                .select(Projections.fields(User.class, user.userId))
                .from(tokenManager)
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
                .where(tokenManager.accessToken.eq(accessToken).and(tokenManager.expiredAccessDate.after(LocalDateTime.now())))
                .fetchOne();
    }
}
