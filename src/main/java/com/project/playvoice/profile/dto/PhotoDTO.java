package com.project.playvoice.profile.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PhotoDTO {

    private String originProfileName;
    private String profileFilePath;
    private String originBackgroundName;
    private String backgroundFilePath;
}
