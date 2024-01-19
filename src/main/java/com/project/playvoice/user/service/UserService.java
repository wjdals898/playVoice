package com.project.playvoice.user.service;

import com.project.playvoice.user.domain.UserEntity;
import com.project.playvoice.security.TokenProvider;
import com.project.playvoice.user.dto.UpdateUserDTO;
import com.project.playvoice.user.dto.UserDTO;
import com.project.playvoice.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;

    public UserEntity create(final UserEntity userEntity) {
        if (userEntity == null ||
                userEntity.getUsername().equals("") ||
                userEntity.getPassword().equals("") ||
                userEntity.getNickname().equals("")) {
            throw new RuntimeException("validation error");
        }
        final String username = userEntity.getUsername();
        final String nickname = userEntity.getNickname();
        final String email = userEntity.getEmail();
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("username already exists");
        }
        else if (userRepository.existsByNickname(nickname)) {
            throw new RuntimeException("nickname already exists");
        }
        else if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("email already exists");
        }

        return userRepository.save(userEntity);
    }

    public UserEntity getByCredentials(final String username, final String password, final PasswordEncoder encoder) {
        final UserEntity originalUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("user not found"));

        if (originalUser != null && encoder.matches(password, originalUser.getPassword())) {
            return originalUser;
        }
        return null;
    }

    public List<UserEntity> findAllUsers() {
        return userRepository.findAll();
    }

    public UserEntity findByNickname(final String nickname) {
        return userRepository.findByNickname(nickname)
                .orElseThrow(() -> new RuntimeException("user not found"));
    }

    public UserEntity findByEmail(final String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("user not found"));

    }

    public UserEntity findByUsername(final String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("user not found"));
    }

    public UserDTO update(UpdateUserDTO updateUserDTO, String accessToken) {
        String username = tokenProvider.getUsername(accessToken);
        UserEntity user = getByCredentials(username, updateUserDTO.getOldPassword(), passwordEncoder);
        if (user == null) {
            throw new RuntimeException("invalid password");
        }

        if (updateUserDTO.getNewPassword() != null) {   // 비밀번호 변경
            user.updatePassword(passwordEncoder.encode(updateUserDTO.getNewPassword()));
        }
        if (updateUserDTO.getNickname() != null) { // 닉네임 변경
            if (userRepository.existsByNickname(updateUserDTO.getNickname())) {    // 닉네임 중복 확인
                throw new RuntimeException("duplicated nickname");
            }
            user.updateNickname(updateUserDTO.getNickname());
        }
        userRepository.save(user);

        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .roles(user.getRoles())
                .build();
    }
}
