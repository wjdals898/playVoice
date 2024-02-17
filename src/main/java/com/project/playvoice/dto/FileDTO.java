package com.project.playvoice.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileDTO {

    private Long id;
    private String origFileName;
    private String fileName;
    private String filePath;
}
