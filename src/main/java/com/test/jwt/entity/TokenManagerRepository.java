package com.test.jwt.entity;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenManagerRepository extends JpaRepository<TokenManager, Long>, TokenManagerCustom{
}
