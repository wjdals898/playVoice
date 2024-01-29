package com.project.playvoice.profile.dto;

import com.project.playvoice.user.domain.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileDTO {

    private long id;
    private String username;
    private String originProfileName;
    private String profilePath;
    private String originBackgroundName;
    private String backgroundPath;
    private String profileContent;
}
