package com.project.playvoice.user.dto;

import lombok.*;

@Data
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class UpdateUserDTO {

    private String oldPassword;
    private String newPassword;
    private String nickname;
}
