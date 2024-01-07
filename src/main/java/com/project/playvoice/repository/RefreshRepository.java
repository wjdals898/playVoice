package com.project.playvoice.repository;

import com.project.playvoice.domain.TokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshRepository extends JpaRepository<TokenEntity, Long> {
    public Optional<TokenEntity> findById(Long id);
    public Optional<TokenEntity> findByRefreshToken(String token);
}
