package com.project.playvoice.service;

import com.project.playvoice.domain.UserEntity;
import com.project.playvoice.dto.TokenDTO;
import com.project.playvoice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

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
                .orElseThrow(() -> new RuntimeException("username not found"));

        if (originalUser != null && encoder.matches(password, originalUser.getPassword())) { return originalUser; }
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
}
