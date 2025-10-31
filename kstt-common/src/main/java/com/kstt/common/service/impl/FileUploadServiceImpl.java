package com.kstt.common.service.impl;

import com.kstt.common.service.FileUploadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 文件上传服务实现类
 * 
 * @author kstt
 * @date 2025-01-01
 */
@Service
public class FileUploadServiceImpl implements FileUploadService {
    
    private static final Logger log = LoggerFactory.getLogger(FileUploadServiceImpl.class);
    
    /**
     * 上传文件根目录
     */
    @Value("${file.upload.path:./uploads}")
    private String uploadPath;
    
    /**
     * 最大文件大小（字节）
     */
    @Value("${file.upload.max-size:20971520}")
    private Long maxFileSize;
    
    /**
     * 允许的文件类型
     */
    @Value("${file.upload.allowed-types:pdf,doc,docx,zip}")
    private String allowedTypes;
    
    @Override
    public Map<String, Object> uploadFile(MultipartFile file, String category) throws IOException {
        return uploadFileWithName(file, category, null);
    }
    
    @Override
    public Map<String, Object> uploadFileWithName(MultipartFile file, String category, String fileName) throws IOException {
        // 1. 验证文件
        validateFile(file);
        
        // 2. 生成文件名
        String originalFilename = file.getOriginalFilename();
        String extension = getFileExtension(originalFilename);
        String savedFileName;
        
        if (StringUtils.hasText(fileName)) {
            savedFileName = fileName + extension;
        } else {
            // 使用UUID生成唯一文件名
            savedFileName = UUID.randomUUID().toString().replace("-", "") + extension;
        }
        
        // 3. 构建存储路径（按日期分类存储）
        String dateDir = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String categoryDir = StringUtils.hasText(category) ? category : "default";
        String relativePath = categoryDir + "/" + dateDir + "/" + savedFileName;
        String fullPath = uploadPath + File.separator + relativePath;
        
        // 4. 创建目录
        Path targetPath = Paths.get(fullPath);
        Files.createDirectories(targetPath.getParent());
        
        // 5. 保存文件
        Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
        
        log.info("File uploaded successfully: {} -> {}", originalFilename, fullPath);
        
        // 6. 返回文件信息
        Map<String, Object> fileInfo = new HashMap<>();
        fileInfo.put("originalName", originalFilename);
        fileInfo.put("fileName", savedFileName);
        fileInfo.put("filePath", relativePath);
        fileInfo.put("fileSize", file.getSize());
        fileInfo.put("fileType", file.getContentType());
        fileInfo.put("category", categoryDir);
        fileInfo.put("uploadTime", new Date());
        
        return fileInfo;
    }
    
    @Override
    public Map<String, Object> uploadFileToSubDir(MultipartFile file, String category, String subDirectory) throws IOException {
        // 1. 验证文件
        validateFile(file);
        
        // 2. 生成文件名（保留原始文件名，避免冲突）
        String originalFilename = file.getOriginalFilename();
        String extension = getFileExtension(originalFilename);
        String baseName = originalFilename != null ? 
            originalFilename.substring(0, originalFilename.lastIndexOf('.')) : 
            UUID.randomUUID().toString().replace("-", "");
        // 如果文件名太长，使用UUID
        if (baseName.length() > 50) {
            baseName = UUID.randomUUID().toString().replace("-", "");
        }
        String savedFileName = baseName + extension;
        
        // 3. 构建存储路径（模块/子目录/文件名）
        // 例如：articles/123/manuscript.pdf
        String categoryDir = StringUtils.hasText(category) ? category : "default";
        String relativePath = categoryDir + "/" + subDirectory + "/" + savedFileName;
        String fullPath = uploadPath + File.separator + relativePath;
        
        // 如果文件已存在，添加序号
        int counter = 1;
        Path targetPath = Paths.get(fullPath);
        while (Files.exists(targetPath)) {
            String nameWithoutExt = baseName + "_" + counter;
            savedFileName = nameWithoutExt + extension;
            relativePath = categoryDir + "/" + subDirectory + "/" + savedFileName;
            fullPath = uploadPath + File.separator + relativePath;
            targetPath = Paths.get(fullPath);
            counter++;
        }
        
        // 4. 创建目录
        Files.createDirectories(targetPath.getParent());
        
        // 5. 保存文件
        Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
        
        log.info("File uploaded successfully to subdirectory: {} -> {}", originalFilename, fullPath);
        
        // 6. 返回文件信息
        Map<String, Object> fileInfo = new HashMap<>();
        fileInfo.put("originalName", originalFilename);
        fileInfo.put("fileName", savedFileName);
        fileInfo.put("filePath", relativePath);
        fileInfo.put("fileSize", file.getSize());
        fileInfo.put("fileType", file.getContentType());
        fileInfo.put("category", categoryDir);
        fileInfo.put("subDirectory", subDirectory);
        fileInfo.put("uploadTime", new Date());
        
        return fileInfo;
    }
    
    @Override
    public boolean deleteFile(String filePath) {
        try {
            String fullPath = uploadPath + File.separator + filePath;
            Path path = Paths.get(fullPath);
            boolean deleted = Files.deleteIfExists(path);
            
            if (deleted) {
                log.info("File deleted successfully: {}", fullPath);
            } else {
                log.warn("File not found for deletion: {}", fullPath);
            }
            
            return deleted;
        } catch (IOException e) {
            log.error("Failed to delete file: {}", filePath, e);
            return false;
        }
    }
    
    @Override
    public File getFile(String filePath) {
        String fullPath = uploadPath + File.separator + filePath;
        File file = new File(fullPath);
        
        if (file.exists() && file.isFile()) {
            return file;
        }
        
        log.warn("File not found: {}", fullPath);
        return null;
    }
    
    @Override
    public String getUploadRoot() {
        return uploadPath;
    }
    
    /**
     * 验证文件
     */
    private void validateFile(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IOException("File is empty");
        }
        
        if (file.getSize() > maxFileSize) {
            throw new IOException("File size exceeds maximum allowed size: " + maxFileSize);
        }
        
        String originalFilename = file.getOriginalFilename();
        if (!StringUtils.hasText(originalFilename)) {
            throw new IOException("Filename is empty");
        }
        
        String extension = getFileExtension(originalFilename);
        if (!isAllowedType(extension)) {
            throw new IOException("File type not allowed: " + extension);
        }
    }
    
    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String filename) {
        if (!StringUtils.hasText(filename)) {
            return "";
        }
        
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1 || lastDotIndex == filename.length() - 1) {
            return "";
        }
        
        return filename.substring(lastDotIndex);
    }
    
    /**
     * 检查文件类型是否允许
     */
    private boolean isAllowedType(String extension) {
        if (!StringUtils.hasText(extension)) {
            return false;
        }
        
        String[] types = allowedTypes.split(",");
        String ext = extension.toLowerCase();
        if (ext.startsWith(".")) {
            ext = ext.substring(1);
        }
        
        return Arrays.asList(types).contains(ext);
    }
}

