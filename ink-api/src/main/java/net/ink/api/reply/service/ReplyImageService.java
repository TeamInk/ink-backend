package net.ink.api.reply.service;

import lombok.RequiredArgsConstructor;
import net.ink.api.core.component.FileUploader;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReplyImageService {
    private static final String REPLY_IMAGE_DIR_NAME = "/reply";

    // 기본 이미지는 S3 에 직접 올려두고 경로만 관리한다. (기존 답변이 참조하는 이전 버전 파일은 S3 에 그대로 유지)
    private static final List<String> REPLY_DEFAULT_IMAGES = List.of(
            "/reply/default/v2/01.webp",
            "/reply/default/v2/02.webp",
            "/reply/default/v2/03.webp",
            "/reply/default/v2/04.webp",
            "/reply/default/v2/05.webp",
            "/reply/default/v2/06.webp"
    );

    private final FileUploader fileUploader;

    /**
     * Multipart File 을 저장하고, 저장 경로를 리턴한다.
     *
     * @param imageFile 요청으로 들어온 Multipart File
     * @return 웹상에서 저장된 경로
     */
    public String uploadReplyImageFile(MultipartFile imageFile) {
        return fileUploader.uploadMultiPartFile(imageFile, REPLY_IMAGE_DIR_NAME);
    }

    public List<String> getReplyDefaultImageList() {
        return REPLY_DEFAULT_IMAGES;
    }
}
