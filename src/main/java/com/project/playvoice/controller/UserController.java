package com.project.playvoice.controller;

import com.project.playvoice.domain.UserEntity;
import com.project.playvoice.dto.ResponseDTO;
import com.project.playvoice.dto.UserDTO;
import com.project.playvoice.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/auth")
public class UserController {

    @Autowired
    private UserService userService;

    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody UserDTO userDTO) {
        try {
            UserEntity userEntity = UserEntity.builder()
                    .username(userDTO.getUsername())
                    .password(passwordEncoder.encode(userDTO.getPassword()))
                    .email(userDTO.getEmail())
                    .nickname(userDTO.getNickname())
                    .build();

            UserEntity registerUserEntity = userService.create(userEntity);
            UserDTO responseUserDTO = UserDTO.builder()
                    .id(registerUserEntity.getId())
                    .username(registerUserEntity.getUsername())
                    .email(registerUserEntity.getEmail())
                    .nickname(registerUserEntity.getNickname())
                    .build();

            return ResponseEntity.ok().body(responseUserDTO);
        } catch (Exception e) {
            ResponseDTO responseDTO = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticate(@RequestBody UserDTO userDTO) {
        UserEntity user = userService.getByCredentials(userDTO.getUsername(), userDTO.getPassword(), passwordEncoder);

        if (user != null) {
            final UserDTO responseUserDTO = UserDTO.builder()
                    .id(user.getId())
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .nickname(user.getNickname())
                    .build();
            return ResponseEntity.ok().body(responseUserDTO);
        }
        else {
            ResponseDTO responseDTO = ResponseDTO.builder()
                    .error("fail to login")
                    .build();
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }
}
