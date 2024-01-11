package com.project.playvoice.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EmailDTO {

    private Long id;
    private String email;
    private String username;
}
