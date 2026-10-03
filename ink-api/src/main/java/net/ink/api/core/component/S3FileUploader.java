package net.ink.api.core.component;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.internal.Mimetypes;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.util.IOUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.ink.api.core.util.PathUtil;
import net.ink.core.core.exception.InkException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class S3FileUploader implements FileUploader {
    private final AmazonS3 s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    /**
     * Multipart 요청 파일을 S3 에 업로드하고 웹상의 경로를 반환한다.
     *
     * @param mFile Controller 에서 Http Multipart 요청으로 들어온 File
     * @param filePath 버킷 루트 기준 디렉터리 경로 (예: /reply)
     * @return "/" 로 시작하는 웹 경로. S3 object key 는 여기서 맨 앞 "/" 를 뺀 값이다.
     */
    @Override
    public String uploadMultiPartFile(MultipartFile mFile, String filePath) {
        String fullFilePath = PathUtil.replaceWindowPathToLinuxPath(filePath) + "/" + generateFileName(mFile);
        String objectKey = fullFilePath.replaceFirst("^/+", "");
        S3ObjectUploadDto s3ObjectUploadDto = buildObjectUploadDto(mFile);

        s3Client.putObject(new PutObjectRequest(
                bucket, objectKey, s3ObjectUploadDto.getByteArrayInputStream(), s3ObjectUploadDto.getObjectMetadata()
        ).withCannedAcl(CannedAccessControlList.PublicRead));

        return "/" + objectKey;
    }

    private S3ObjectUploadDto buildObjectUploadDto(MultipartFile file) {
        try {
            ObjectMetadata objMeta = new ObjectMetadata();
            String originalFileName = file.getOriginalFilename() == null ? "" : file.getOriginalFilename();
            objMeta.setContentType(Mimetypes.getInstance().getMimetype(originalFileName));
            byte[] bytes = IOUtils.toByteArray(file.getInputStream());
            objMeta.setContentLength(bytes.length);
            return new S3ObjectUploadDto(
                    new ByteArrayInputStream(bytes),
                    objMeta
            );
        } catch (IOException e) {
            throw new InkException(e);
        }
    }

    private String generateFileName(MultipartFile mFile) {
        String currentTimeStamp = String.valueOf(System.currentTimeMillis());
        String originalFileName = mFile.getOriginalFilename() == null ? "" : mFile.getOriginalFilename();
        String extension = getExtension(originalFileName);
        return currentTimeStamp + extension;
    }

    private String getExtension(String originalFileName) {
        int lastIndex = originalFileName.lastIndexOf(".");
        if (lastIndex == -1) {
            return "";
        }
        return originalFileName.substring(lastIndex);
    }

    @Getter
    @RequiredArgsConstructor
    private static class S3ObjectUploadDto {
        private final ByteArrayInputStream byteArrayInputStream;
        private final ObjectMetadata objectMetadata;
    }
}
