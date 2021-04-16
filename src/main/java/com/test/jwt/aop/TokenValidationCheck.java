package com.test.jwt.aop;

import com.test.jwt.entity.TokenManagerRepository;
import com.test.jwt.exception.AccessTokenExpiredException;
import com.test.jwt.exception.EmptyTokenException;
import com.test.jwt.exception.RefreshTokenExpiredException;
import com.test.jwt.lib.JwtUtil;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Aspect
@Component

public class TokenValidationCheck {
    Logger logger = LoggerFactory.getLogger(TokenValidationCheck.class);

    @Autowired
    private TokenManagerRepository repository;

    @Autowired
    private JwtUtil jwtUtil;

    @Before("execution(* com.test.jwt.controller.UserController.*(..))" +
            "&&!execution(* com.test.jwt.controller.UserController.registerUser(..))" +
            "&&!execution(* com.test.jwt.controller.UserController.generateToken(..))")

    public ResponseEntity<?> beforeLogging(JoinPoint joinPoint) throws Throwable {

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

        String token = request.getHeader("Authorization").substring(7);
        if (token == null || token.equals("") || repository.isVaildToken(token) == 0) {
            logger.info("토큰이 없습니다.");
            throw new EmptyTokenException("not found token");
        } else {
            Long result = repository.isExpiredToken(token);
            if (result == null || result == 0) {
                logger.info("엑세스 토큰의 만료기간이 지났습니다.");
                String refreshToken = repository.getRefreshTokenByAccessToken(token);
                if (refreshToken == null || refreshToken.equals("")) {
                    logger.info("리프레시 토큰의 만료기간이 지났습니다.");
                    throw new RefreshTokenExpiredException("refreshToken is expired");
//                    return ResponseEntity.notFound().build();
                } else {
                    String newAccessToken = jwtUtil.generateToken(jwtUtil.extractUsername(refreshToken));
                    repository.renewAccessToken(newAccessToken, refreshToken, LocalDateTime.ofInstant(jwtUtil.extractExpiration(newAccessToken).toInstant(), ZoneId.systemDefault()));
                    throw new AccessTokenExpiredException("액세스 토큰이 갱신되었습니다.", newAccessToken);
//                    return new ResponseEntity<>(newAccessToken, HttpStatus.OK);
                }
            } else {
                logger.info("토큰이 일치합니다." + result);
                return ResponseEntity.ok().build();
            }
        }
    }
}
