package com.test.jwt.entity;

import com.test.jwt.response.UserResponseDto;

import java.util.List;

public interface UserCustom {
    //전체 찾기
    List<UserResponseDto.UserCommonDto> findAllUser();
    //자신 찾기
    UserResponseDto.UserCommonDto findOneUser(String userId);
    //로그인시 자신 정보 불러오기
    User findOneByUserId(String userId);
    //로그인 시 자신의 idx 정보 가지고 오기
    Long findIdxByUserId(String userId);
}
