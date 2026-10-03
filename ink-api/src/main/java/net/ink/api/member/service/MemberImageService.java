package net.ink.api.member.service;

import lombok.RequiredArgsConstructor;
import net.ink.api.core.component.FileUploader;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class MemberImageService {
    private static final String MEMBER_IMAGE_DIR_NAME = "/member";

    private final FileUploader fileUploader;

    /**
     * Multipart File 을 저장하고, 저장 경로를 리턴한다.
     *
     * @param imageFile 요청으로 들어온 Multipart File
     * @return 웹상에서 저장된 경로
     */
    public String uploadMemberImageFile(MultipartFile imageFile) {
        return fileUploader.uploadMultiPartFile(imageFile, MEMBER_IMAGE_DIR_NAME);
    }
}
