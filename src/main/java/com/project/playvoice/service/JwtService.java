package com.project.playvoice.service;

import com.project.playvoice.domain.TokenEntity;
import com.project.playvoice.domain.UserEntity;
import com.project.playvoice.dto.TokenDTO;
import com.project.playvoice.repository.RefreshRepository;
import com.project.playvoice.repository.UserRepository;
import com.project.playvoice.security.TokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtService {

    private final UserRepository userRepository;
    private final RefreshRepository refreshRepository;
    private final ApplicationContext context;

    public void saveRefreshToken(TokenDTO tokenDTO) {
        refreshRepository.findById(tokenDTO.getUserId())
                .ifPresentOrElse(
                        r -> {
                            r.setRefreshToken(tokenDTO.getRefreshToken());
                        },
                        () -> {
                            TokenEntity token = TokenEntity.builder().id(tokenDTO.getUserId())
                                    .refreshToken(tokenDTO.getRefreshToken()).build();
                            refreshRepository.save(token);
                        }
                );
    }

    public TokenDTO refresh(String token) {
        TokenProvider tokenProvider = context.getBean(TokenProvider.class);

        String refreshToken = tokenProvider.resolveToken(token);
        if (!tokenProvider.validateToken(refreshToken)) {
            throw new RuntimeException("unauthorized access");
        }

        TokenEntity findRefreshToken = refreshRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new UsernameNotFoundException("refresh token not found"));

        // refresh token 을 활용하여 username 정보 획득
        UserEntity user = userRepository.findById(findRefreshToken.getId())
                .orElseThrow(() -> new RuntimeException("user not found"));

        // access token 과 refresh token 모두를 재발급
        // Authentication authentication = tokenProvider.getAuthenticationByUsername(user.getUsername());
        String newAccessToken = tokenProvider.createAccessToken(user.getUsername(), user.getNickname(), user.getRoles());
        String newRefreshToken = tokenProvider.createRefreshToken(user.getUsername(), user.getNickname(), user.getRoles());

        TokenDTO tokenDto = TokenDTO.builder().userId(findRefreshToken.getId()).accessToken(newAccessToken)
                .refreshToken(newRefreshToken).build();

        this.saveRefreshToken(tokenDto);

        return tokenDto;
    }
}
