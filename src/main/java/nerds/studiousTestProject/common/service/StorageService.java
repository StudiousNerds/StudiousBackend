package nerds.studiousTestProject.common.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
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

    /**
     * 여러 개의 사진을 S3에 저장하고 URL을 반환하는 메소드
     * 이거 안쓸거 같음
     * @param files
     * @return 사진이 저장된 URL
     */
    public String uploadFiles(List<MultipartFile> files) {
        if (files == null || files.isEmpty()) {
            return "";
        }

        StringBuilder mergedUrl = new StringBuilder();
        for (int i = 0; i < files.size(); i++) {
            mergedUrl.append(uploadFile(files.get(i)));
            if(i < files.size() - 1) {
                mergedUrl.append(",");
            }
        }
        log.info("uploadFiles mergedUrl: {}", mergedUrl);
        return mergedUrl.toString();
    }

    public byte[] downloadFile(String fileName) {
        S3Object s3Object = s3Client.getObject(bucketName, fileName);
        S3ObjectInputStream inputStream = s3Object.getObjectContent();
        try {
            return IOUtils.toByteArray(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public String deleteFile(String fileName) {
        s3Client.deleteObject(bucketName, fileName);
        return fileName + " removed ...";
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

    private static String getFileExtension(String originalFileName) {
        return originalFileName.substring(originalFileName.lastIndexOf(".") + 1);
    }
}
