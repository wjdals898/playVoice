package com.project.playvoice.profile.service;

import com.project.playvoice.profile.dto.PhotoDTO;
import com.project.playvoice.profile.dto.ProfileDTO;
import com.project.playvoice.profile.domain.ProfileEntity;
import com.project.playvoice.profile.repository.ProfileRepository;
import com.project.playvoice.user.domain.UserEntity;
import com.project.playvoice.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;

    @Value("${com.project.playVoice.path.profile}")
    private String profilePath;

    @Value("${com.project.playVoice.path.background}")
    private String backgroundPath;

    public ProfileDTO uploadProfile(String content, MultipartFile profileImg, MultipartFile backgroundImg, String username) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("user not found"));
        log.info("user = "+user.getUsername());
        try {
            String originProfileName = profileImg.getOriginalFilename();
            String originBackgroundName = backgroundImg.getOriginalFilename();
            String newProfilePath = profilePath + File.separator + generateUniqueFileName(originProfileName, "profile");
            String newBackgroundPath = backgroundPath + File.separator +  generateUniqueFileName(originBackgroundName, "background");
            log.info("profilePath = "+newProfilePath);
            log.info("backgroundPath = "+newBackgroundPath);

            File savedProfileFile = new File(newProfilePath);
            File savedBackgroundFile = new File(newBackgroundPath);

            profileImg.transferTo(savedProfileFile);
            backgroundImg.transferTo(savedBackgroundFile);

            log.info("서버 저장 성공");

            if (!profileRepository.existsByUser(user)) { // user에 해당하는 프로필이 존재하지 않을 경우 (처음 프로필 생성)
                profileRepository.save(ProfileEntity.builder()
                        .user(user)
                        .originProfileName(originProfileName)
                        .profileFilePath(newProfilePath)
                        .originBackgroundName(originBackgroundName)
                        .backgroundFilePath(newBackgroundPath)
                        .profileContent(content)
                        .build());
            }
            else {  // user에 해당하는 프로필이 존재할 경우 (프로필 수정)
                ProfileEntity profile = profileRepository.findByUser(user)
                        .orElseThrow();
                PhotoDTO photoDTO = PhotoDTO.builder()
                                .originProfileName(originProfileName)
                                .profileFilePath(newProfilePath)
                                .backgroundFilePath(originBackgroundName)
                                .backgroundFilePath(newBackgroundPath)
                                .build();
                profile.updateProfile(photoDTO, content);
                profileRepository.save(profile);
            }

            ProfileEntity savedProfile = profileRepository.findByUser(user)
                    .orElseThrow(() -> new RuntimeException("fail to upload profile"));

            return ProfileDTO.builder()
                    .id(savedProfile.getId())
                    .username(savedProfile.getUser().getUsername())
                    .originProfileName(savedProfile.getOriginProfileName())
                    .profilePath(savedProfile.getProfileFilePath())
                    .originBackgroundName(savedProfile.getOriginBackgroundName())
                    .backgroundPath(savedProfile.getBackgroundFilePath())
                    .profileContent(savedProfile.getProfileContent())
                    .build();

        } catch (Exception e) {
            throw new RuntimeException("fail to upload profile");
        }
    }

    public String generateUniqueFileName(String originFileName, String type) {   // 중복 방지를 위한 고유한 파일 이름 생성
        String newName = "";
        String timestamp = Long.toString(System.currentTimeMillis());
        if (type.equals("profile")) {
            newName = "profile_" + timestamp + "_" + originFileName;
        } else if (type.equals("background")) {
            newName = "background_" + timestamp + "_" + originFileName;
        }

        return newName;
    }

    public List<ProfileDTO> retrieveAllProfiles() {
        try {
            List<ProfileEntity> profiles = profileRepository.findAll();
            List<ProfileDTO> dtos = profiles.stream().map(profileEntity ->
                    ProfileDTO.builder()
                            .id(profileEntity.getId())
                            .username(profileEntity.getUser().getUsername())
                            .originProfileName(profileEntity.getOriginProfileName())
                            .profilePath(profileEntity.getProfileFilePath())
                            .originBackgroundName(profileEntity.getOriginBackgroundName())
                            .backgroundPath(profileEntity.getBackgroundFilePath())
                            .profileContent(profileEntity.getProfileContent())
                            .build()
            ).collect(Collectors.toList());

            return dtos;
        } catch (Exception e) {
            throw new RuntimeException("fail to fetch profiles");
        }
    }

    public ProfileDTO retrieveProfile(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("user not found"));
        ProfileEntity profile = profileRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("user profile not found"));

        return ProfileDTO.builder()
                .id(profile.getId())
                .username(profile.getUser().getUsername())
                .originProfileName(profile.getOriginProfileName())
                .profilePath(profile.getProfileFilePath())
                .originBackgroundName(profile.getOriginBackgroundName())
                .backgroundPath(profile.getBackgroundFilePath())
                .profileContent(profile.getProfileContent())
                .build();
    }
}
