package com.test.jwt.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TokenManager {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    @Column(name = "access_token")
    private String accessToken;

    @Column(name = "refresh_token", length = 500)
    private String refresh_token;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime expiredAccessDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime expiredRefreshDate;

    private Long userIdx;

    public TokenManager(String accessToken, String refresh_token, LocalDateTime expiredAccessDate, LocalDateTime expiredRefreshDate, Long userIdx) {
        this.accessToken = accessToken;
        this.refresh_token = refresh_token;
        this.expiredAccessDate = expiredAccessDate;
        this.expiredRefreshDate = expiredRefreshDate;
        this.userIdx = userIdx;
    }
}
