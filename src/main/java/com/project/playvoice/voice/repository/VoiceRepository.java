package com.project.playvoice.voice.repository;

import com.project.playvoice.user.domain.UserEntity;
import com.project.playvoice.voice.domain.VoiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface VoiceRepository extends JpaRepository<VoiceEntity, Long> {

    List<VoiceEntity> findAllByUser(UserEntity user);
}
