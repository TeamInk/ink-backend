package net.ink.api.core.component;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class S3FileUploaderTest {
    private final AmazonS3 s3Client = mock(AmazonS3.class);
    private final S3FileUploader fileUploader = new S3FileUploader(s3Client);

    @Test
    public void 업로드_경로와_S3_키_테스트() {
        // given
        MockMultipartFile mockMultipartFile = new MockMultipartFile(
                "image",
                "hello.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "Hello, World!".getBytes()
        );

        // when
        String filePath = fileUploader.uploadMultiPartFile(mockMultipartFile, "/reply");

        // then
        ArgumentCaptor<PutObjectRequest> captor = ArgumentCaptor.forClass(PutObjectRequest.class);
        verify(s3Client).putObject(captor.capture());
        PutObjectRequest putObjectRequest = captor.getValue();

        assertTrue(filePath.matches("/reply/\\d+\\.jpg"));
        assertEquals(filePath.substring(1), putObjectRequest.getKey());
        assertEquals(MediaType.IMAGE_JPEG_VALUE, putObjectRequest.getMetadata().getContentType());
    }
}
