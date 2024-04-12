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

    @Override
    public String uploadMultiPartFile(MultipartFile mFile, String filePath) {
        String fullFilePath = PathUtil.replaceWindowPathToLinuxPath(filePath) + "/" + generateFileName(mFile);
        S3ObjectUploadDto s3ObjectUploadDto = buildObjectUploadDto(mFile);

        s3Client.putObject(new PutObjectRequest(
                bucket, fullFilePath, s3ObjectUploadDto.getByteArrayInputStream(), s3ObjectUploadDto.getObjectMetadata()
        ).withCannedAcl(CannedAccessControlList.PublicRead));

        return fullFilePath;
    }

    private S3ObjectUploadDto buildObjectUploadDto(MultipartFile file) {
        try {
            ObjectMetadata objMeta = new ObjectMetadata();
            objMeta.setContentType(Mimetypes.getInstance().getMimetype(file.getName()));
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
