package com.test.jwt.filter;

import com.test.jwt.entity.TokenManagerRepository;
import com.test.jwt.exception.AccessTokenExpiredException;
import com.test.jwt.exception.RefreshTokenExpiredException;
import com.test.jwt.lib.JwtUtil;
import com.test.jwt.service.CustomUserDetailService;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CustomUserDetailService svc;

    @Autowired
    private TokenManagerRepository repository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        //Authorization이라는 Header값 추출
        String authorizationHeader = request.getHeader("Authorization");

        String token = null;
        String userName = null;

        //authorizationHeader에 값이 있고 시작을 Bearer로 할때
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            token = authorizationHeader.substring(7);
            try {
                //header안의 userName 추출
                userName = jwtUtil.extractUsername(token);
            } catch (ExpiredJwtException e) {
                String refreshToken = repository.getRefreshTokenByAccessToken(token);
                if (refreshToken == null || refreshToken.equals("")) {
                    logger.info("리프레시 토큰의 만료기간이 지났습니다.");
                    throw new RefreshTokenExpiredException("리프레시 토큰이 만료되었습니다.");
                } else {
                    String newAccessToken = jwtUtil.generateToken(jwtUtil.extractUsername(refreshToken));
                    repository.renewAccessToken(newAccessToken, refreshToken, LocalDateTime.ofInstant(jwtUtil.extractExpiration(newAccessToken).toInstant(), ZoneId.systemDefault()));
                    throw new AccessTokenExpiredException(newAccessToken,"액세스 토큰이 갱신되었습니다.");
                }
            }

        }

        //추출한 userName 유효성 확인
        //SecurityContextHolder의 getAuthentication이 비어있다면 최초인증임으로 셋팅 해줌
        if (userName != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = svc.loadUserByUsername(userName);
            if (jwtUtil.validateToken(token, userDetails)) {
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                usernamePasswordAuthenticationToken
                        .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}
