package com.project.playvoice.auth.controller;

import com.project.playvoice.auth.service.JwtService;
import com.project.playvoice.domain.UserEntity;
import com.project.playvoice.auth.dto.LoginDTO;
import com.project.playvoice.dto.ResponseDTO;
import com.project.playvoice.auth.dto.TokenDTO;
import com.project.playvoice.user.dto.UserDTO;
import com.project.playvoice.security.TokenProvider;
import com.project.playvoice.user.repository.UserRepository;
import com.project.playvoice.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtService jwtService;
    private final TokenProvider tokenProvider;
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO loginDTO) {
        try {
            TokenDTO tokenDTO = jwtService.login(loginDTO);

            UserEntity user = userService.findByUsername(tokenDTO.getUsername());

            UserDTO responseUserDTO = UserDTO.builder()
                    .id(user.getId())
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .nickname(user.getNickname())
                    .roles(user.getRoles())
                    .build();
            HttpHeaders httpHeaders = tokenProvider.setHeaderToken(tokenDTO.getAccessToken(), tokenDTO.getRefreshToken());

            return ResponseEntity.ok().headers(httpHeaders).body(responseUserDTO);

        } catch (Exception e) {
            ResponseDTO<?> responseDTO = ResponseDTO.builder()
                    .message(e.getMessage())
                    .build();

            return ResponseEntity.badRequest().body(responseDTO);
        }
    }

    @GetMapping("/reissue")
    public ResponseEntity<?> reissueToken(HttpServletRequest request) {
        String refreshToken = tokenProvider.resolveToken(request.getHeader("Refresh"));
        String username = tokenProvider.getUsername(refreshToken);

        try {
            String newAccessToken = jwtService.reissueAccessToken(username, refreshToken);

            HttpHeaders httpHeaders = tokenProvider.setHeaderToken(newAccessToken, refreshToken);

            ResponseDTO<?> responseDTO = ResponseDTO.builder().message("success").build();

            return ResponseEntity.ok().headers(httpHeaders).body(responseDTO);
        } catch (Exception e) {
            ResponseDTO<?> responseDTO = ResponseDTO.builder()
                    .message(e.getMessage()).build();

            HttpHeaders httpHeaders = tokenProvider.setHeaderToken("", "");

            return ResponseEntity.badRequest().headers(httpHeaders).body(responseDTO);
        }

    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        try {
            String accessToken = tokenProvider.resolveToken(request.getHeader("Authorization"));
            jwtService.logout(accessToken);

            ResponseDTO<?> responseDTO = ResponseDTO.builder().message("logout success").build();

            return ResponseEntity.ok().body(responseDTO);
        } catch (Exception e) {
            ResponseDTO<?> responseDTO = ResponseDTO.builder()
                    .message(e.getMessage()).build();

            return ResponseEntity.badRequest().body(responseDTO);
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> delete(HttpServletRequest request, @RequestBody Map<String, String> map) {
        try {
            String accessToken = tokenProvider.resolveToken(request.getHeader("Authorization"));
            if (map.get("password") == null) {
                throw new RuntimeException("validation error");
            }
            jwtService.delete(accessToken, map.get("password"));

            ResponseDTO<?> responseDTO = ResponseDTO.builder().message("delete success").build();

            return ResponseEntity.ok().body(responseDTO);
        } catch (Exception e) {
            ResponseDTO<?> responseDTO = ResponseDTO.builder()
                    .message(e.getMessage()).build();

            return ResponseEntity.badRequest().body(responseDTO);
        }
    }
}
