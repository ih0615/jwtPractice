package com.test.jwt.service;

import com.test.jwt.entity.User;
import com.test.jwt.entity.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Slf4j
@Service
public class CustomUserDetailService implements UserDetailsService {

    @Autowired
    private UserRepository repository;

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        log.info("user Id : " + userId);
        User user = repository.findOneByUserId(userId);
        return new org.springframework.security.core.userdetails.User(user.getUserId(),user.getUserPassword(), new ArrayList<>());
    }
}
