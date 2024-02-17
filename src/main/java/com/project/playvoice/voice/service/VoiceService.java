package com.project.playvoice.voice.service;

import com.project.playvoice.FileRepository;
import com.project.playvoice.domain.FileEntity;
import com.project.playvoice.dto.FileDTO;
import com.project.playvoice.user.domain.UserEntity;
import com.project.playvoice.user.repository.UserRepository;
import com.project.playvoice.user.service.UserService;
import com.project.playvoice.voice.domain.VoiceEntity;
import com.project.playvoice.voice.dto.RequestVoiceDTO;
import com.project.playvoice.voice.dto.VoiceDTO;
import com.project.playvoice.voice.repository.VoiceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@RequiredArgsConstructor
@Service
public class VoiceService {

    private final FileRepository fileRepository;
    private final VoiceRepository voiceRepository;
    private final UserRepository userRepository;

    @Value("${com.project.playVoice.path.thumbnail}")
    private String thumbnailPath;

    @Value("${com.project.playVoice.path.video}")
    private String videoPath;

    public VoiceDTO post(MultipartFile thumbnail, MultipartFile video, RequestVoiceDTO requestDTO, String username) throws IOException {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("user not found"));

        log.info("user = "+user.getUsername());

        String origThumbnailName = thumbnail.getOriginalFilename();
        String origVideoName = video.getOriginalFilename();
        String newThumbnailName = generateUniqueFilename(origThumbnailName, "thumbnail");
        String newVideoName = generateUniqueFilename(origVideoName, "video");

        File savedThumbnail = new File(thumbnailPath + File.separator + newThumbnailName);
        File savedVideo = new File(videoPath + File.separator + newVideoName);

        thumbnail.transferTo(savedThumbnail);
        video.transferTo(savedVideo);

        // DB에 파일 정보 저장
        FileEntity thumbnailEntity = saveFile(origThumbnailName, newThumbnailName, savedThumbnail.getPath());
        log.info(thumbnailEntity.getFileName()+" DB 저장 완료!");
        FileEntity videoEntity = saveFile(origVideoName, newVideoName, savedVideo.getPath());
        log.info(videoEntity.getFileName()+" DB 저장 완료!");

        // DB에 Voice 저장
        VoiceEntity savedVoice = voiceRepository.save(VoiceEntity.builder()
                .user(user)
                .content(requestDTO.getContent())
                .musicTitle(requestDTO.getMusicTitle())
                .origSinger(requestDTO.getOrigSinger())
                .thumbnailId(thumbnailEntity.getId())
                .videoId(videoEntity.getId()).build());
        log.info(savedVoice.getUser().getUsername() + "의 새 보이스" + savedVoice.getId() + " 저장 완료!");

        if (savedVoice.getId() == null) {
            throw new RuntimeException("fail to create voice");
        }

        return VoiceDTO.builder()
                .id(savedVoice.getId())
                .username(savedVoice.getUser().getUsername())
                .musicTitle(savedVoice.getMusicTitle())
                .origSinger(savedVoice.getOrigSinger())
                .content(savedVoice.getContent())
                .thumbnailName(thumbnailEntity.getFileName())
                .videoName(videoEntity.getFileName())
                .build();
    }

    public String generateUniqueFilename(String origName, String type) {
        String timestamp = Long.toString(System.currentTimeMillis());

        return type + "_" + timestamp + "_" + origName;
    }

    public FileEntity saveFile(String origName, String newName, String filePath) {
        FileEntity file = FileEntity.builder()
                .origFileName(origName)
                .fileName(newName)
                .filePath(filePath)
                .build();

        return fileRepository.save(file);
    }

    public List<VoiceDTO> getAllVoices() {
        List<VoiceEntity> entities = voiceRepository.findAll();

        List<VoiceDTO> dtos = entities.stream().map(entity -> (
            VoiceDTO.builder()
                .id(entity.getId())
                .username(entity.getUser().getUsername())
                .content(entity.getContent())
                .musicTitle(entity.getMusicTitle())
                .origSinger(entity.getOrigSinger())
                .thumbnailName(findFileById(entity.getThumbnailId()).getFileName())
                .videoName(findFileById(entity.getVideoId()).getFileName()).build())).collect(Collectors.toList());

        return dtos;
    }

    public VoiceDTO getVoiceById(Long voiceId) {
        if (voiceId == null) {
            throw new RuntimeException("validation error");
        }
        VoiceEntity entity = voiceRepository.findById(voiceId)
                .orElseThrow(() -> new RuntimeException("can not find voice ["+voiceId+"]"));

        return VoiceDTO.builder()
                .id(entity.getId())
                .username(entity.getUser().getUsername())
                .content(entity.getContent())
                .musicTitle(entity.getMusicTitle())
                .origSinger(entity.getOrigSinger())
                .thumbnailName(findFileById(entity.getThumbnailId()).getFileName())
                .videoName(findFileById(entity.getVideoId()).getFileName())
                .build();
    }

    public List<VoiceDTO> getVoiceByUserId(Long userId) {
        if (userId == null) {
            throw new RuntimeException("validation error");
        }
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("user not found"));

        List<VoiceEntity> entities = voiceRepository.findAllByUser(user);

        List<VoiceDTO> dtos = entities.stream().map(entity -> (
                VoiceDTO.builder()
                        .id(entity.getId())
                        .username(entity.getUser().getUsername())
                        .content(entity.getContent())
                        .musicTitle(entity.getMusicTitle())
                        .origSinger(entity.getOrigSinger())
                        .thumbnailName(findFileById(entity.getThumbnailId()).getFileName())
                        .videoName(findFileById(entity.getVideoId()).getFileName())
                        .build()
                )).collect(Collectors.toList());

        return dtos;
    }

    public void delete(Long voiceId, String username) {
        if (voiceId == null) {
            throw new RuntimeException("validation error");
        }
        VoiceEntity entity = voiceRepository.findById(voiceId)
                .orElseThrow(() -> new RuntimeException("존재하지 않거나 삭제된 보이스입니다."));

        if (entity.getUser().getUsername().equals(username)) {
            voiceRepository.delete(entity);
        } else {
            throw new RuntimeException("보이스 작성자가 아닙니다.");
        }
    }
    public FileEntity findFileById(Long id) {
        return fileRepository.findById(id).get();
    }


}
