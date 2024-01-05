package com.project.playvoice.service;

import com.project.playvoice.domain.UserEntity;
import com.project.playvoice.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

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
        final UserEntity originalUser = userRepository.findByUsername(username);

        if (originalUser != null && encoder.matches(password, originalUser.getPassword())) { return originalUser; }
        return null;
    }
}
