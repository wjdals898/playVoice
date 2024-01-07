package com.project.playvoice.controller;

import antlr.Token;
import com.project.playvoice.domain.UserEntity;
import com.project.playvoice.dto.LoginDTO;
import com.project.playvoice.dto.ResponseDTO;
import com.project.playvoice.dto.TokenDTO;
import com.project.playvoice.dto.UserDTO;
import com.project.playvoice.security.JwtAuthenticationFilter;
import com.project.playvoice.security.TokenProvider;
import com.project.playvoice.service.JwtService;
import com.project.playvoice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;

    @PostMapping("/signup")
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
            ResponseDTO responseDTO = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO loginDTO) {
        UserEntity user = userService.getByCredentials(loginDTO.getUsername(), loginDTO.getPassword(), passwordEncoder);

        if (user != null) {
            final String accessToken = tokenProvider.createAccessToken(user.getUsername(), user.getNickname(), user.getRoles());
            final String refreshToken = loginDTO.getIsAuthLogin() ?
                    tokenProvider.createRefreshToken(user.getUsername(), user.getNickname(), user.getRoles()) :
                    null;

            final UserDTO responseUserDTO = UserDTO.builder()
                    .id(user.getId())
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .nickname(user.getNickname())
                    .roles(user.getRoles())
                    .access_token(accessToken)
                    .refresh_token(refreshToken)
                    .build();

            final TokenDTO tokenDTO = TokenDTO.builder()
                    .userId(user.getId())
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build();
            jwtService.saveRefreshToken(tokenDTO);

            HttpHeaders httpHeaders = new HttpHeaders();
            tokenProvider.setHeaderAccessToken(httpHeaders, accessToken);
            tokenProvider.setHeaderRefreshToken(httpHeaders, refreshToken);

            return ResponseEntity.ok().headers(httpHeaders).body(responseUserDTO);
        } else {
            ResponseDTO responseDTO = ResponseDTO.builder()
                    .error("fail to login")
                    .build();
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }

}
