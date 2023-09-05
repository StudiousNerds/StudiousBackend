package nerds.studiousTestProject.common.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class StorageService {
    private final AmazonS3 s3Client;

    @Value("${application.bucket.name}")
    private String bucketName;

    /**
     * 하나의 사진을 S3에 저장하고 URL을 반환하는 메소드
     * @param file
     * @return 사진이 저장된 URL
     */
    public String uploadFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return "";
        }

        File fileObject = convertMultiFileToFile(file);
        String originalFilename = file.getOriginalFilename();
        String extension = getFileExtension(originalFilename);
        String fileName = UUID.randomUUID() + "." + extension;

        log.info("upload file name : {}", fileName);
        s3Client.putObject(new PutObjectRequest(bucketName, fileName, fileObject));
        fileObject.delete();

        return s3Client.getUrl(bucketName, fileName).toString();
    }

    public String deleteFile(String url) {
        s3Client.deleteObject(bucketName, getFileNameFromUrl(url));
        return url + " removed ...";
    }

    private File convertMultiFileToFile(MultipartFile file) {
        File convertedFile = new File(file.getOriginalFilename());
        try (FileOutputStream fileOutputStream = new FileOutputStream(convertedFile)){
            fileOutputStream.write(file.getBytes());
        } catch (IOException e) {
            log.error("Error converting multipartFile to file", e);
        }

        return convertedFile;
    }

    private String getFileExtension(String originalFileName) {
        return originalFileName.substring(originalFileName.lastIndexOf(".") + 1);
    }
}
