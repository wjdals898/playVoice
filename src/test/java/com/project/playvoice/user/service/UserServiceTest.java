package com.project.playvoice.user.service;

import com.project.playvoice.user.dto.UserDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.yml")
public class UserServiceTest {

    @Autowired
    UserService userService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    MockMvc mockMvc;

    public UserDTO createUser() {

        return UserDTO.builder()
                .username("test")
                .password("test1234")
                .email("test@naver.com")
                .nickname("테스트")
                .roles(Collections.singletonList("ROLE_USER"))
                .build();
    }

    @Test
    @DisplayName("회원가입 테스트")
    public void signupTest() {
        UserDTO user = createUser();

        UserDTO registerUser = userService.create(user);

        assertEquals(user.getUsername(), registerUser.getUsername());
        assertEquals(user.getEmail(), registerUser.getEmail());
        assertEquals(user.getNickname(), registerUser.getNickname());
        assertEquals(user.getRoles(), registerUser.getRoles());
    }

    @Test
    @DisplayName("중복 회원가입 테스트")
    public void duplicatedSignupTest() {
        UserDTO user1 = createUser();
        UserDTO user2 = createUser();

        userService.create(user1);

        Throwable e = assertThrows(RuntimeException.class, () -> {
            userService.create(user2);
        });

        assertEquals("username already exists", e.getMessage());
    }

    @Test
    @DisplayName("회원정보 수정 테스트")
    @WithMockUser(username = "test", password = "test1234", roles = "ROLE_USER")
    public void updateTest() {
        UserDTO user = createUser();

        // mockMvc.perform();
    }
}
