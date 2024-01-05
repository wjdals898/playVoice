package com.project.playvoice.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    private String username;
    private String password;
    private String email;
    private String nickname;
    private Long id;
}
