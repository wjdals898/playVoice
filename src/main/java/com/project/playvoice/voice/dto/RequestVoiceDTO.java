package com.project.playvoice.voice.dto;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RequestVoiceDTO {

    private String content;
    private String musicTitle;
    private String origSinger;
}
