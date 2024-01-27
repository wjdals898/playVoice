package com.project.playvoice.profile.controller;

import com.project.playvoice.dto.ResponseDTO;
import com.project.playvoice.profile.dto.ProfileDTO;
import com.project.playvoice.profile.service.ProfileService;
import com.project.playvoice.security.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final TokenProvider tokenProvider;
    private final ProfileService profileService;

    @PostMapping(value = "", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> uploadProfile(
            @RequestPart("content") String content,
            @RequestPart("profile") MultipartFile profileImg,
            @RequestPart("background") MultipartFile backgroundImg,
            HttpServletRequest request) {
        String accessToken = tokenProvider.resolveToken(request.getHeader("Authorization"));
        String username = tokenProvider.getUsername(accessToken);

        try {
            ProfileDTO savedProfile = profileService.uploadProfile(content, profileImg, backgroundImg, username);

            ResponseDTO<ProfileDTO> responseDTO = ResponseDTO.<ProfileDTO>builder().message("success").data(savedProfile).build();

            return ResponseEntity.ok().body(responseDTO);
        } catch (Exception e) {
            ResponseDTO<?> responseDTO = ResponseDTO.builder().message(e.getMessage()).build();

            return ResponseEntity.badRequest().body(responseDTO);
        }
    }

    @GetMapping("")
    public ResponseEntity<?> findAllProfiles() {
        try {
            List<ProfileDTO> profiles = profileService.retrieveAllProfiles();

            ResponseDTO<ProfileDTO> responseDTO = ResponseDTO.<ProfileDTO>builder().message("success").dataList(profiles).build();

            return ResponseEntity.ok().body(responseDTO);
        } catch (Exception e) {
            ResponseDTO<?> responseDTO = ResponseDTO.builder().message(e.getMessage()).build();

            return ResponseEntity.badRequest().body(responseDTO);
        }
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> findProfileByUserId(@PathVariable Long userId) {
        try {
            ProfileDTO profile = profileService.retrieveProfile(userId);

            ResponseDTO<ProfileDTO> responseDTO = ResponseDTO.<ProfileDTO>builder().message("success").data(profile).build();

            return ResponseEntity.ok().body(responseDTO);
        } catch (Exception e) {
            ResponseDTO<?> responseDTO = ResponseDTO.builder().message(e.getMessage()).build();

            return ResponseEntity.badRequest().body(responseDTO);
        }
    }
}
