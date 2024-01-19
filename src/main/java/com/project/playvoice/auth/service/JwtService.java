package com.project.playvoice.auth.service;

import com.project.playvoice.user.domain.UserEntity;
import com.project.playvoice.auth.dto.LoginDTO;
import com.project.playvoice.auth.dto.TokenDTO;
import com.project.playvoice.security.TokenProvider;
import com.project.playvoice.user.repository.UserRepository;
import com.project.playvoice.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtService {

    private final UserService userService;
    private final UserRepository userRepository;
    private final TokenProvider tokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final RedisTemplate<String, String> redisTemplate;

    @Value("${jwt.refresh-validity-time}")
    private long refreshExpired;

    public TokenDTO login(final LoginDTO loginDTO) {
        try {
            UserEntity userEntity = userService.getByCredentials(loginDTO.getUsername(), loginDTO.getPassword(), passwordEncoder);
            if (userEntity == null) {
                throw new RuntimeException("invalid password");
            }
            Authentication authentication = tokenProvider.getAuthenticationByUsername(userEntity.getUsername());

            String accessToken = tokenProvider.createAccessToken(authentication);
            String refreshToken = null;
            if (loginDTO.getIsAuthLogin()) {
                refreshToken = tokenProvider.createRefreshToken(authentication);
            }
            return TokenDTO.builder()
                    .username(loginDTO.getUsername())
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build();

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public String reissueAccessToken(final String username, final String refreshToken) {
        try {
            Authentication authentication = tokenProvider.getAuthenticationByUsername(username);
            String redisRefreshToken = redisTemplate.opsForValue().get(authentication.getName());
            if (!tokenProvider.validateToken(refreshToken) || !refreshToken.equals(redisRefreshToken)) {
                throw new RuntimeException("expired token");
            }

            String newAccessToken = tokenProvider.createAccessToken(authentication);

            if (newAccessToken == null) {
                throw new RuntimeException("fail to access");
            }

            return newAccessToken;
        } catch (Exception e) {
            throw new RuntimeException("expired refresh token");
        }
    }

    public void logout(final String accessToken) {
        try {
            if (tokenProvider.validateToken(accessToken)) {
                long expiration = tokenProvider.getExpired(accessToken);
                log.info("atk expiration : " + expiration);
                String username = tokenProvider.getAuthenticationByAccessToken(accessToken).getName();
                if (redisTemplate.opsForValue().get(username) != null) {
                    redisTemplate.delete(username);
                }

                redisTemplate.opsForValue().set(
                        accessToken,
                        "logout",
                        expiration,
                        TimeUnit.MILLISECONDS
                );
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public void delete(String accessToken, String password) {
        try {
            String username = tokenProvider.getAuthenticationByAccessToken(accessToken).getName();
            UserEntity user = userService.getByCredentials(username, password, passwordEncoder);
            Long expiration = tokenProvider.getExpired(accessToken);
            if (user == null) {
                throw new RuntimeException("invalid password");
            }
            userRepository.delete(user);
            if (redisTemplate.opsForValue().get(username) != null) {
                redisTemplate.delete(username);
            }

            redisTemplate.opsForValue().set(
                    accessToken,
                    "withdrawal",
                    expiration,
                    TimeUnit.MILLISECONDS
            );

        } catch (Exception e) {
            throw new RuntimeException("fail to delete user");
        }
    }
}
