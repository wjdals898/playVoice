package com.project.playvoice.user.controller;

import com.project.playvoice.domain.UserEntity;
import com.project.playvoice.dto.*;
import com.project.playvoice.security.TokenProvider;
import com.project.playvoice.auth.service.JwtService;
import com.project.playvoice.user.dto.EmailDTO;
import com.project.playvoice.user.dto.UpdateUserDTO;
import com.project.playvoice.user.dto.UserDTO;
import com.project.playvoice.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;

    @PostMapping("")
    public ResponseEntity<?> registerUser(@RequestBody UserDTO userDTO) {
        try {
            UserEntity userEntity = UserEntity.builder()
                    .username(userDTO.getUsername())
                    .password(passwordEncoder.encode(userDTO.getPassword()))
                    .email(userDTO.getEmail())
                    .nickname(userDTO.getNickname())
                    .roles(Collections.singletonList("ROLE_USER"))
                    .build();

            UserEntity registerUserEntity = userService.create(userEntity);
            UserDTO responseUserDTO = UserDTO.builder()
                    .id(registerUserEntity.getId())
                    .username(registerUserEntity.getUsername())
                    .email(registerUserEntity.getEmail())
                    .nickname(registerUserEntity.getNickname())
                    .roles(registerUserEntity.getRoles())
                    .build();

            return ResponseEntity.ok().body(responseUserDTO);
        } catch (Exception e) {
            ResponseDTO<UserDTO> responseDTO = ResponseDTO.<UserDTO>builder().message(e.getMessage()).build();
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }

    @GetMapping
    public ResponseEntity<?> findAllUsers() {
        try {
            List<UserEntity> entities = userService.findAllUsers();

            List<UserDTO> dtos = entities.stream().map(UserDTO::new).collect(Collectors.toList());

            ResponseDTO<UserDTO> responseDTO = ResponseDTO.<UserDTO>builder().dataList(dtos).build();

            return ResponseEntity.ok().body(responseDTO);
        } catch (Exception e) {
            ResponseDTO<UserDTO> responseDTO = ResponseDTO.<UserDTO>builder()
                    .message("fail to fetch users")
                    .build();
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }

    @GetMapping("/nickname/{nickname}")
    public ResponseEntity<?> findByNickname(@PathVariable String nickname) {
        try {
            UserEntity userEntity = userService.findByNickname(nickname);
            UserDTO userDTO = UserDTO.builder()
                    .id(userEntity.getId())
                    .username(userEntity.getUsername())
                    .email(userEntity.getEmail())
                    .nickname(userEntity.getNickname())
                    .roles(userEntity.getRoles())
                    .build();

            return ResponseEntity.ok().body(userDTO);
        } catch (Exception e) {
            ResponseDTO<UserDTO> responseDTO = ResponseDTO.<UserDTO>builder()
                    .message(e.getMessage())
                    .build();

            return ResponseEntity.badRequest().body(responseDTO);
        }
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<?> findUsernameByEmail(@PathVariable String email) {
        try {
            UserEntity userEntity = userService.findByEmail(email);
            EmailDTO emailDTO = EmailDTO.builder()
                    .id(userEntity.getId())
                    .email(userEntity.getEmail())
                    .username(userEntity.getUsername())
                    .build();

            return ResponseEntity.ok().body(emailDTO);
        } catch (Exception e) {
            ResponseDTO<EmailDTO> responseDTO = ResponseDTO.<EmailDTO>builder()
                    .message(e.getMessage())
                    .build();

            return ResponseEntity.badRequest().body(responseDTO);
        }
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<?> findPasswordByUsername(@PathVariable String username) {
        try {
            UserEntity userEntity = userService.findByUsername(username);

            EmailDTO emailDTO = EmailDTO.builder()
                    .id(userEntity.getId())
                    .email(userEntity.getEmail())
                    .username(userEntity.getUsername())
                    .build();

            ResponseDTO<EmailDTO> responseDTO = ResponseDTO.<EmailDTO>builder()
                    .message("success to find user")
                    .data(emailDTO)
                    .build();

            return ResponseEntity.ok().body(responseDTO);
        } catch (Exception e) {
            ResponseDTO<EmailDTO> responseDTO = ResponseDTO.<EmailDTO>builder()
                    .message(e.getMessage())
                    .build();

            return ResponseEntity.badRequest().body(responseDTO);
        }
    }

    @PutMapping("")
    public ResponseEntity<?> updateUser(@RequestBody UpdateUserDTO updateUserDTO, HttpServletRequest request) {
        try {
            if (updateUserDTO.getOldPassword() == null && updateUserDTO.getNewPassword() == null && updateUserDTO.getNickname() == null) {
                throw new RuntimeException("validation error");
            }
            String accessToken = tokenProvider.resolveToken(request.getHeader("Authorization"));
            UserDTO responseUser = userService.update(updateUserDTO, accessToken);

            ResponseDTO<UserDTO> responseDTO = ResponseDTO.<UserDTO>builder()
                    .message("success update")
                    .data(responseUser)
                    .build();

            return ResponseEntity.ok().body(responseDTO);
        } catch (Exception e) {
            ResponseDTO<?> responseDTO = ResponseDTO.builder()
                    .message(e.getMessage())
                    .build();

            return ResponseEntity.badRequest().body(responseDTO);
        }
    }
}
