package com.test.jwt.entity;

import com.test.jwt.lib.UserRole;
import com.test.jwt.lib.UserStatus;
import com.test.jwt.request.UserRequestDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.Constraint;

@Entity
@Table(name = "user_tbl")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;
    @Column(unique = true)
    private String userId;
    private String userPassword;
    private String email;
    @Enumerated(EnumType.STRING)
    private UserRole userRole;
    @Enumerated(EnumType.STRING)
    private UserStatus userStatus;

    public User(UserRequestDto.UserCommonDto commonDto) {
        this.userId = commonDto.getUserId();
        this.userPassword = "{noop}" + commonDto.getUserPassword();
        this.email = commonDto.getEmail();
        this.userRole = commonDto.getUserRole();
    }
}
