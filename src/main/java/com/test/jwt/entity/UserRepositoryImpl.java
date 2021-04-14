package com.test.jwt.entity;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.test.jwt.lib.UserStatus;
import com.test.jwt.response.UserResponseDto;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.test.jwt.entity.QUser.user;


@Repository
public class UserRepositoryImpl extends QuerydslRepositorySupport implements UserCustom {

    private final JPAQueryFactory queryFactory;

    public UserRepositoryImpl(JPAQueryFactory queryFactory) {
        super(User.class);
        this.queryFactory = queryFactory;
    }

    @Override
    public List<UserResponseDto.UserCommonDto> findAllUser() {
        return from(user)
                .select(Projections.constructor(UserResponseDto.UserCommonDto.class, user.idx, user.userId, user.userPassword, user.email, user.userStatus))
                .where(user.userStatus.ne(UserStatus.deleted))
                .fetch();
    }

    @Override
    public UserResponseDto.UserCommonDto findOneUser(String userId) {
        return from(user)
                .select(Projections.constructor(UserResponseDto.UserCommonDto.class, user.idx, user.userId, user.userPassword, user.email, user.userStatus))
                .where(user.userStatus.ne(UserStatus.deleted).and(user.userId.eq(userId)))
                .fetchOne();
    }

    @Override
    public User findOneByUserId(String userId) {
        return from(user)
                .where(user.userId.eq(userId).and(user.userStatus.ne(UserStatus.deleted)))
                .fetchOne();
    }

    @Override
    public Long findIdxByUserId(String userId) {
        return from(user)
                .where(user.userId.eq(userId))
                .fetch().get(0).getIdx();
    }
}
