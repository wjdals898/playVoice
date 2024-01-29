package com.project.playvoice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class PlayVoiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(PlayVoiceApplication.class, args);
    }

}
