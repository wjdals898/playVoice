package com.project.playvoice.dto;

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
    private String access_token;
    private String refresh_token;
    private Long id;
}
