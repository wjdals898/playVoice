package com.project.playvoice.user.controller;

import com.project.playvoice.dto.ResponseDTO;
import com.project.playvoice.user.domain.UserEntity;
import com.project.playvoice.user.dto.FollowDTO;
import com.project.playvoice.user.service.FollowService;
import com.project.playvoice.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class FollowController {
    private final FollowService followService;
    private final UserService userService;

    @PostMapping("/follow/{username}")
    public ResponseEntity<?> follow(Authentication authentication, @PathVariable String username) {
        try {
            UserEntity fromUser = userService.findByUsername(authentication.getName());
            UserEntity toUser = userService.findByUsername(username);
            FollowDTO followDTO = followService.follow(fromUser, toUser);

            ResponseDTO<FollowDTO> responseDTO = ResponseDTO.<FollowDTO>builder()
                    .message("success")
                    .data(followDTO)
                    .build();

            return ResponseEntity.ok().body(responseDTO);
        } catch (Exception e) {
            ResponseDTO<?> responseDTO = ResponseDTO.builder().message(e.getMessage()).build();

            return ResponseEntity.badRequest().body(responseDTO);
        }
    }

    @GetMapping("/{username}/follower")
    public ResponseEntity<?> getFollowerList(@PathVariable String username) {
        try {
            UserEntity user = userService.findByUsername(username);
            List<String> followerList = followService.getFollowerList(user);

            ResponseDTO<String> responseDTO = ResponseDTO.<String>builder()
                    .message("success")
                    .dataList(followerList)
                    .build();

            return ResponseEntity.ok().body(responseDTO);
        } catch (Exception e) {
            ResponseDTO<?> responseDTO = ResponseDTO.builder().message(e.getMessage()).build();

            return ResponseEntity.badRequest().body(responseDTO);
        }
    }

    @GetMapping("/{username}/following")
    public ResponseEntity<?> getFollowingList(@PathVariable String username) {
        try {
            UserEntity user = userService.findByUsername(username);
            List<String> followingList = followService.getFollowingList(user);

            ResponseDTO<String> responseDTO = ResponseDTO.<String>builder()
                    .message("success")
                    .dataList(followingList)
                    .build();

            return ResponseEntity.ok().body(responseDTO);
        } catch (Exception e) {
            ResponseDTO<?> responseDTO = ResponseDTO.builder().message(e.getMessage()).build();

            return ResponseEntity.badRequest().body(responseDTO);
        }
    }

    @DeleteMapping("/follow/{username}")
    public ResponseEntity<?> deleteFollow(Authentication authentication, @PathVariable String username) {
        try {
            UserEntity requestUser = userService.findByUsername(authentication.getName());
            UserEntity selectedUser = userService.findByUsername(username);

            FollowDTO followDTO =  followService.deleteFollow(requestUser, selectedUser);

            ResponseDTO<FollowDTO> responseDTO = ResponseDTO.<FollowDTO>builder()
                    .message("success")
                    .data(followDTO)
                    .build();

            return ResponseEntity.ok().body(responseDTO);
        } catch (Exception e) {
            ResponseDTO<?> responseDTO = ResponseDTO.builder().message(e.getMessage()).build();

            return ResponseEntity.badRequest().body(responseDTO);
        }
    }
}
