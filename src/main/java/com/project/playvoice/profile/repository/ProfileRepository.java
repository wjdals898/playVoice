package com.project.playvoice.profile.repository;

import com.project.playvoice.profile.domain.ProfileEntity;
import com.project.playvoice.user.domain.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProfileRepository extends JpaRepository<ProfileEntity, Long> {

    Optional<ProfileEntity> findByUser(UserEntity user);

    List<ProfileEntity> findAll();

    Boolean existsByUser(UserEntity user);
}
