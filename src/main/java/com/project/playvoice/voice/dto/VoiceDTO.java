package com.project.playvoice.voice.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VoiceDTO {

    private Long id;
    private String username;
    private String content;
    private String musicTitle;
    private String origSinger;
    private String thumbnailName;
    private String videoName;
}
