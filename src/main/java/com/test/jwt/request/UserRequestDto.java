package com.test.jwt.request;

import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

public class UserRequestDto {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserCommonDto {

        @ApiParam(name = "userId", type = "String", value = "사용자 아이디", example = "aaa")
        @NotBlank(message = "{parameter.null}")
        @Pattern(regexp = "^[0-9a-zA-Z]{3,15}$", message = "{userId.regexp}")
        private String userId;
        @ApiParam(type = "String", value = "사용자 비밀번호", example = "aaaaaa")
        @NotBlank(message = "{userPassword.null}")
        @Pattern(regexp = "^[0-9a-zA-Z!@#$%^]{6,15}$", message = "{userPassword.regexp}")
        private String userPassword;
        @ApiParam(type = "String", value = "사용자 이메일", example = "a@yahoo.co.kr")
        @Email(message = "{email.regexp}")
        private String email;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserUpdateDto {
        private String userPassword;
        private String email;
    }
}
