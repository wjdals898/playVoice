package com.project.playvoice.repository;

import com.project.playvoice.domain.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByUsername(String username); // username으로 사용자 정보 가져옴
    Boolean existsByUsername(String username);
    Boolean existsByNickname(String nickname);
    Boolean existsByEmail(String email);
    UserEntity findByUsernameAndPassword(String username, String password);
}
