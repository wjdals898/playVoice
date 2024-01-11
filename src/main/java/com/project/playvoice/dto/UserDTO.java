package com.project.playvoice.dto;

import com.project.playvoice.domain.UserEntity;
import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    private String username;
    private String password;
    private String email;
    private String nickname;
    private List<String> roles;
    private Long id;

    public UserDTO(final UserEntity userEntity) {
        this.id = userEntity.getId();
        this.username = userEntity.getUsername();
        this.email = userEntity.getEmail();
        this.nickname = userEntity.getNickname();
        this.roles = userEntity.getRoles();
    }
}
