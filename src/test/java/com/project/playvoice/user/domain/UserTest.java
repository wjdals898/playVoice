package com.project.playvoice.user.domain;

import com.project.playvoice.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@TestPropertySource(locations = "classpath:application-test.yml")
public class UserTest {

    @Autowired
    UserRepository userRepository;

    @Test
    @DisplayName("Auditing 테스트")
    public void auditingTest() {
        UserEntity newUser = UserEntity.builder()
                .username("test")
                .password("1234")
                .email("test@naver.com")
                .nickname("테스트")
                .roles(Collections.singletonList("ROLE_USER"))
                .build();

        userRepository.save(newUser);

        UserEntity user = userRepository.findByUsername(newUser.getUsername())
                .orElseThrow(EntityNotFoundException::new);

        System.out.println("username : " + user.getUsername());
        System.out.println("created time : " + user.getCreatedDate());
        System.out.println("modified time : " + user.getModifiedDate());

    }

}
