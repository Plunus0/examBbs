package com.exam.examBbs.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class FileUploadService {

    private final String uploadDir = "C:/dev/java_projects/examBbs/src/main/java/com/exam/examBbs/image";

    public List<String> uploadFiles(List<MultipartFile> files) throws IOException {
        List<String> filePaths = new ArrayList<>();

        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                // 원본 파일 확장자와 함께 UUID를 파일 이름으로 사용
                String originalFilename = file.getOriginalFilename();
                String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
                String filename = UUID.randomUUID().toString() + fileExtension;

                Path destinationFile = Paths.get(uploadDir).resolve(Paths.get(filename)).normalize().toAbsolutePath();

                try (InputStream inputStream = file.getInputStream()) {
                    Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
                }
                filePaths.add(destinationFile.toString());
            }
        }
        return filePaths;
    }
}
