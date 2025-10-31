package com.kstt.common.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * 文件上传服务接口
 * 
 * @author kstt
 * @date 2025-01-01
 */
public interface FileUploadService {
    
    /**
     * 上传文件
     * 
     * @param file 文件
     * @param category 文件分类（如：articles, avatars, documents等）
     * @return 文件信息（包含文件名、路径、大小等）
     * @throws IOException 文件处理异常
     */
    Map<String, Object> uploadFile(MultipartFile file, String category) throws IOException;
    
    /**
     * 上传文件并指定文件名
     * 
     * @param file 文件
     * @param category 文件分类
     * @param fileName 指定文件名（不含扩展名）
     * @return 文件信息
     * @throws IOException 文件处理异常
     */
    Map<String, Object> uploadFileWithName(MultipartFile file, String category, String fileName) throws IOException;
    
    /**
     * 上传文件到指定子目录（用于需要进一步分类的场景，如articles/articleId）
     * 
     * @param file 文件
     * @param category 文件分类
     * @param subDirectory 子目录（如articleId）
     * @return 文件信息
     * @throws IOException 文件处理异常
     */
    Map<String, Object> uploadFileToSubDir(MultipartFile file, String category, String subDirectory) throws IOException;
    
    /**
     * 删除文件
     * 
     * @param filePath 文件路径
     * @return 是否删除成功
     */
    boolean deleteFile(String filePath);
    
    /**
     * 获取文件
     * 
     * @param filePath 文件路径
     * @return 文件对象
     */
    File getFile(String filePath);
    
    /**
     * 获取文件上传根目录
     * 
     * @return 根目录路径
     */
    String getUploadRoot();
}

