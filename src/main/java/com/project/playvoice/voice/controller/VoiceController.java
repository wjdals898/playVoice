package com.project.playvoice.voice.controller;

import com.project.playvoice.dto.ResponseDTO;
import com.project.playvoice.security.TokenProvider;
import com.project.playvoice.user.domain.UserEntity;
import com.project.playvoice.voice.dto.RequestVoiceDTO;
import com.project.playvoice.voice.dto.VoiceDTO;
import com.project.playvoice.voice.service.VoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/voice")
public class VoiceController {

    private final VoiceService voiceService;
    private final TokenProvider tokenProvider;

    @PostMapping()
    public ResponseEntity<?> post(@RequestPart MultipartFile thumbnail,
                                  @RequestPart MultipartFile video,
                                  @RequestPart RequestVoiceDTO requestDTO,
                                  HttpServletRequest request) {
        String accessToken = tokenProvider.resolveToken(request.getHeader("Authorization"));
        String username = tokenProvider.getUsername(accessToken);

        try {
            if (requestDTO.getMusicTitle() == null | requestDTO.getMusicTitle().equals("")) {
                throw new RuntimeException("곡명을 입력해주세요.");
            } else if (requestDTO.getOrigSinger() == null | requestDTO.getOrigSinger().equals("")) {
                throw new RuntimeException("가수명을 입력해주세요.");
            } else if (requestDTO.getContent() == null | requestDTO.getContent().equals("")) {
                throw new RuntimeException("제목을 입력해주세요.");
            } else if (video.getBytes().length == 0) {
                throw new RuntimeException("첨부된 영상이 없습니다.");
            }

            VoiceDTO savedVoice = voiceService.post(thumbnail, video, requestDTO, username);
            ResponseDTO<VoiceDTO> responseDTO = ResponseDTO.<VoiceDTO>builder()
                    .message("보이스 추가 성공!")
                    .data(savedVoice)
                    .build();

            return ResponseEntity.ok().body(responseDTO);

        } catch (Exception e) {
            ResponseDTO<?> responseDTO = ResponseDTO.builder().message(e.getMessage()).build();

            return ResponseEntity.badRequest().body(responseDTO);
        }
    }

    @GetMapping
    public ResponseEntity<?> retrieveAll() {
        try {
            List<VoiceDTO> dtos = voiceService.getAllVoices();
            ResponseDTO<VoiceDTO> responseDTO = ResponseDTO.<VoiceDTO>builder().message("전체 보이스 조회 성공!").dataList(dtos).build();

            return ResponseEntity.ok().body(responseDTO);

        } catch (Exception e) {
            ResponseDTO<?> responseDTO = ResponseDTO.builder().message(e.getMessage()).build();

            return ResponseEntity.badRequest().body(responseDTO);
        }
    }

    @GetMapping("/{voiceId}")
    public ResponseEntity<?> retrieveById(@PathVariable Long voiceId) {
        try {
            VoiceDTO voice = voiceService.getVoiceById(voiceId);
            ResponseDTO<VoiceDTO> responseDTO = ResponseDTO.<VoiceDTO>builder().message("보이스 조회 성공!").data(voice).build();

            return ResponseEntity.ok().body(responseDTO);

        } catch (Exception e) {
            ResponseDTO<?> responseDTO = ResponseDTO.builder().message(e.getMessage()).build();

            return ResponseEntity.badRequest().body(responseDTO);
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> retrieveByUserId(@PathVariable Long userId) {
        try {
            List<VoiceDTO> voices = voiceService.getVoiceByUserId(userId);
            ResponseDTO<VoiceDTO> responseDTO = ResponseDTO.<VoiceDTO>builder().message("보이스 조회 성공!").dataList(voices).build();

            return ResponseEntity.ok().body(responseDTO);

        } catch (Exception e) {
            ResponseDTO<?> responseDTO = ResponseDTO.builder().message(e.getMessage()).build();

            return ResponseEntity.badRequest().body(responseDTO);
        }
    }

    @DeleteMapping("/{voiceId}")
    public ResponseEntity<?> delete(@PathVariable Long voiceId, HttpServletRequest request) {
        try {
            String accessToken = tokenProvider.resolveToken(request.getHeader("Authorization"));
            String username = tokenProvider.getUsername(accessToken);
            voiceService.delete(voiceId, username);
            ResponseDTO<?> responseDTO = ResponseDTO.builder().message("보이스 삭제 성공!").build();

            return ResponseEntity.ok().body(responseDTO);

        } catch (Exception e) {
            ResponseDTO<?> responseDTO = ResponseDTO.builder().message(e.getMessage()).build();

            return ResponseEntity.badRequest().body(responseDTO);
        }
    }
}
