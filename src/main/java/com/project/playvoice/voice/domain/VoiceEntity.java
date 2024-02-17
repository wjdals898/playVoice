package com.project.playvoice.voice.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.project.playvoice.domain.BaseTimeEntity;
import com.project.playvoice.user.domain.UserEntity;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name="voice_entity")
public class VoiceEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(targetEntity = UserEntity.class)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private String musicTitle;

    @Column(nullable = false)
    private String origSinger;

    @Column
    private Long thumbnailId;

    @Column(nullable = false)
    private Long videoId;

}
