package com.project.playvoice.user.service;

import com.project.playvoice.user.domain.UserEntity;
import com.project.playvoice.user.dto.FollowDTO;
import com.project.playvoice.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FollowService {

    private final UserRepository userRepository;

    public FollowDTO follow(UserEntity fromUser, UserEntity toUser) {
        if (fromUser == toUser) {
            throw new RuntimeException("본인은 팔로우할 수 없습니다.");
        } else if (isFollowerPresent(toUser, fromUser.getId())) {
            throw new RuntimeException("이미 팔로우한 사용자입니다.");
        } else {
            fromUser.getFollowings().add(toUser.getId());
            toUser.getFollowers().add(fromUser.getId());
            userRepository.save(fromUser);
            userRepository.save(toUser);

            return FollowDTO.builder()
                    .username(toUser.getUsername())
                    .followers(toUser.getFollowers().size())
                    .followings(toUser.getFollowings().size())
                    .build();
        }
    }

    public boolean isFollowerPresent(UserEntity user, long requestId) {
        List<Long> followers = user.getFollowers();
        for (long id: followers) {
            if (id == requestId) {
                return true;
            }
        }
        return false;
    }

    public boolean isFollowingPresent(UserEntity user, long selectId) {
        List<Long> followings = user.getFollowings();
        for (long id : followings) {
            if (id == selectId) {
                return true;
            }
        }
        return false;
    }

    public List<String> getFollowerList(UserEntity user) {
        List<Long> list = user.getFollowers();
        List<String> followerList = new ArrayList<>();
        try {
            for (long id : list) {
                if (userRepository.findById(id).isPresent()) {
                    followerList.add(userRepository.findById(id).get().getUsername());
                }
            }

            return followerList;
        } catch (Exception e) {
            throw new RuntimeException("팔로워 정보를 불러올 수 없습니다.");
        }
    }

    public List<String> getFollowingList(UserEntity user) {
        List<Long> list = user.getFollowings();
        List<String> followingList = new ArrayList<>();
        try {
            for (long id : list) {
                if (userRepository.findById(id).isPresent()) {
                    followingList.add(userRepository.findById(id).get().getUsername());
                }
            }

            return followingList;
        } catch (Exception e) {
            throw new RuntimeException("팔로잉 정보를 불러올 수 없습니다.");
        }
    }

    public FollowDTO deleteFollow(UserEntity requestUser, UserEntity selectedUser) {
        if (requestUser == selectedUser) {
            throw new RuntimeException("본인은 팔로우 취소할 수 없습니다.");
        } else if (!isFollowerPresent(selectedUser, requestUser.getId())
                || !isFollowingPresent(requestUser, selectedUser.getId())) {
            throw new RuntimeException("팔로우하지 않은 사용자입니다.");
        } else {
            requestUser.getFollowings().remove(selectedUser.getId());
            selectedUser.getFollowers().remove(requestUser.getId());
            userRepository.save(requestUser);
            userRepository.save(selectedUser);

            return FollowDTO.builder()
                    .username(requestUser.getUsername())
                    .followers(requestUser.getFollowers().size())
                    .followings(requestUser.getFollowings().size())
                    .build();
        }
    }
}
