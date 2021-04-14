package com.test.jwt.response;

import com.test.jwt.lib.UserStatus;
import com.test.jwt.entity.User;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class UserResponseDto {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserCommonDto{
        @ApiModelProperty(value = "사용자 번호", dataType = "Long", example = "1")
        private Long idx;
        @ApiModelProperty(value = "사용자 아이디", dataType = "String", example = "aaa")
        private String userId;
        @ApiModelProperty(value = "사용자 비밀번호", dataType = "String", example = "aaa")
        private String userPassword;
        @ApiModelProperty(value = "사용자 이메일", dataType = "String", example = "a@yahoo.co.kr")
        private String email;
        @ApiModelProperty(value = "사용자 상태", dataType = "UserStatus.class", example = "alive")
        private UserStatus userStatus;

        public UserCommonDto(User entity) {
            this.idx = entity.getIdx();
            this.userId = entity.getUserId();
            this.userPassword = entity.getUserPassword();
            this.email = entity.getEmail();
            this.userStatus = entity.getUserStatus();
        }
    }
    @Data
    @NoArgsConstructor
    public static class UserIdxDto{
        private Long idx;

        public UserIdxDto(Long idx) {
            this.idx = idx;
        }
    }
}
