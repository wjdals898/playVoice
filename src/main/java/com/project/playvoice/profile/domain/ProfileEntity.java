package com.project.playvoice.profile.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.project.playvoice.domain.BaseTimeEntity;
import com.project.playvoice.profile.dto.PhotoDTO;
import com.project.playvoice.user.domain.UserEntity;
import lombok.*;

import javax.persistence.*;

@Getter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "profile_entity")
public class ProfileEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToOne(targetEntity = UserEntity.class)
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private UserEntity user;

    @Column
    private String originProfileName;

    @Column
    private String profileFilePath;

    @Column
    private String originBackgroundName;

    @Column
    private String backgroundFilePath;

    @Column
    private String profileContent;

    public void updateProfile(PhotoDTO photoDTO, String content) {
        this.originProfileName = photoDTO.getOriginProfileName();
        this.profileFilePath = photoDTO.getProfileFilePath();
        this.originBackgroundName = photoDTO.getOriginBackgroundName();
        this.backgroundFilePath = photoDTO.getBackgroundFilePath();
        this.profileContent = content;
    }
}
