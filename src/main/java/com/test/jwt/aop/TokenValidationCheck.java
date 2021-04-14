package com.test.jwt.aop;

import com.test.jwt.entity.TokenManagerRepository;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Aspect
@Component
public class TokenValidationCheck {
    Logger logger = LoggerFactory.getLogger(TokenValidationCheck.class);

    @Autowired
    private TokenManagerRepository repository;


    @Around("execution(* com.test.jwt.controller.*.*(..))")
    public Object logging(ProceedingJoinPoint pjp) throws Throwable {
        logger.info("start - " + pjp.getSignature().getDeclaringTypeName() + " / " + pjp.getSignature().getName());
        Object result = pjp.proceed();
        logger.info("finished - " + pjp.getSignature().getDeclaringTypeName() + " / " + pjp.getSignature().getName());
        return result;
    }

    @Before("execution(* com.test.jwt.controller.*.*(..))")
    public void beforeLogging(JoinPoint joinPoint) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

        String token = request.getHeader("Authorization");
        if (token == null || token.equals("")) {
            logger.info("토큰이 없습니다.");
        } else {
            Long result = repository.isVaildToken(token.substring(7));
            if (result == null || result == 0) {
                logger.info("토큰이 일치하지 않거나 만료기간이 지나갔습니다.");
            } else {
                logger.info("토큰이 일치합니다." + result);
            }
        }
    }
}
