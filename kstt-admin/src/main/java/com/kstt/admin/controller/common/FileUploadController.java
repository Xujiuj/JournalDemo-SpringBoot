package com.kstt.admin.controller.common;

import com.kstt.common.annotation.Log;
import com.kstt.common.core.controller.BaseController;
import com.kstt.common.core.domain.AjaxResult;
import com.kstt.common.enums.BusinessType;
import com.kstt.common.service.FileUploadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

/**
 * 文件上传控制器
 * 
 * @author kstt
 * @date 2025-01-01
 */
@Tag(name = "文件上传管理", description = "统一文件上传接口，支持异步处理")
@RestController
@RequestMapping("/api/upload")
public class FileUploadController extends BaseController {
    
    private static final Logger log = LoggerFactory.getLogger(FileUploadController.class);
    
    @Autowired
    private FileUploadService fileUploadService;
    
    /**
     * 上传单个文件
     */
    @Log(title = "文件上传", businessType = BusinessType.INSERT)
    @PostMapping("/single")
    @Operation(summary = "上传单个文件", description = "上传单个文件到指定分类目录，返回文件信息")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "上传成功",
                    content = @Content(schema = @Schema(implementation = AjaxResult.class))),
            @ApiResponse(responseCode = "400", description = "文件无效或类型不允许"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public AjaxResult uploadSingle(
            @Parameter(description = "上传的文件", required = true)
            @RequestParam("file") MultipartFile file,
            @Parameter(description = "文件分类（如：articles, avatars, documents等）", required = true)
            @RequestParam(value = "category", defaultValue = "default") String category) {
        
        try {
            log.info("Uploading file: {} with category: {}", file.getOriginalFilename(), category);
            
            Map<String, Object> fileInfo = fileUploadService.uploadFile(file, category);
            
            log.info("File uploaded successfully: {}", fileInfo.get("fileName"));
            
            return AjaxResult.success("File uploaded successfully", fileInfo);
            
        } catch (IOException e) {
            log.error("Failed to upload file: {}", file.getOriginalFilename(), e);
            return AjaxResult.error("Failed to upload file: " + e.getMessage());
        }
    }
    
    /**
     * 批量上传文件
     */
    @Log(title = "批量文件上传", businessType = BusinessType.INSERT)
    @PostMapping("/batch")
    @Operation(summary = "批量上传文件", description = "批量上传多个文件到指定分类目录")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "上传成功"),
            @ApiResponse(responseCode = "400", description = "文件无效或类型不允许"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public AjaxResult uploadBatch(
            @Parameter(description = "上传的文件列表", required = true)
            @RequestParam("files") MultipartFile[] files,
            @Parameter(description = "文件分类", required = true)
            @RequestParam(value = "category", defaultValue = "default") String category) {
        
        if (files == null || files.length == 0) {
            return AjaxResult.error("No files uploaded");
        }
        
        try {
            log.info("Uploading {} files with category: {}", files.length, category);
            
            Map<String, Object> result = new java.util.HashMap<>();
            java.util.List<Map<String, Object>> fileInfoList = new java.util.ArrayList<>();
            
            for (MultipartFile file : files) {
                try {
                    Map<String, Object> fileInfo = fileUploadService.uploadFile(file, category);
                    fileInfoList.add(fileInfo);
                } catch (IOException e) {
                    log.error("Failed to upload file: {}", file.getOriginalFilename(), e);
                }
            }
            
            result.put("total", files.length);
            result.put("success", fileInfoList.size());
            result.put("failed", files.length - fileInfoList.size());
            result.put("files", fileInfoList);
            
            log.info("Batch upload completed: {}/{} files uploaded successfully", 
                    fileInfoList.size(), files.length);
            
            return AjaxResult.success("Batch upload completed", result);
            
        } catch (Exception e) {
            log.error("Failed to upload files", e);
            return AjaxResult.error("Failed to upload files: " + e.getMessage());
        }
    }
    
    /**
     * 上传文件到子目录（用于需要进一步分类的场景）
     */
    @Log(title = "文件上传到子目录", businessType = BusinessType.INSERT)
    @PostMapping("/subdir")
    @Operation(summary = "上传文件到子目录", description = "上传文件到指定分类的子目录（如articles/articleId），适用于论文投稿等需要按文章ID分类的场景")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "上传成功",
                    content = @Content(schema = @Schema(implementation = AjaxResult.class))),
            @ApiResponse(responseCode = "400", description = "文件无效或类型不允许"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public AjaxResult uploadToSubDir(
            @Parameter(description = "上传的文件", required = true)
            @RequestParam("file") MultipartFile file,
            @Parameter(description = "文件分类（如：articles）", required = true)
            @RequestParam("category") String category,
            @Parameter(description = "子目录（如：articleId）", required = true)
            @RequestParam("subDirectory") String subDirectory) {
        
        try {
            log.info("Uploading file: {} with category: {} and subDirectory: {}", 
                    file.getOriginalFilename(), category, subDirectory);
            
            Map<String, Object> fileInfo = fileUploadService.uploadFileToSubDir(file, category, subDirectory);
            
            log.info("File uploaded successfully to subdirectory: {}", fileInfo.get("fileName"));
            
            return AjaxResult.success("File uploaded successfully", fileInfo);
            
        } catch (IOException e) {
            log.error("Failed to upload file: {}", file.getOriginalFilename(), e);
            return AjaxResult.error("Failed to upload file: " + e.getMessage());
        }
    }
    
    /**
     * 下载文件
     */
    @GetMapping("/download/{filePath:.+}")
    @Operation(summary = "下载文件", description = "根据文件路径下载文件")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "下载成功"),
            @ApiResponse(responseCode = "404", description = "文件不存在")
    })
    public ResponseEntity<Resource> downloadFile(
            @Parameter(description = "文件相对路径", required = true)
            @PathVariable String filePath) {
        
        try {
            java.io.File file = fileUploadService.getFile(filePath);
            
            if (file == null || !file.exists()) {
                log.warn("File not found: {}", filePath);
                return ResponseEntity.notFound().build();
            }
            
            Resource resource = new FileSystemResource(file);
            String contentType = "application/octet-stream";
            
            // 根据文件类型设置Content-Type
            String fileName = file.getName();
            if (fileName.endsWith(".pdf")) {
                contentType = "application/pdf";
            } else if (fileName.endsWith(".doc") || fileName.endsWith(".docx")) {
                contentType = "application/msword";
            } else if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
                contentType = "image/jpeg";
            } else if (fileName.endsWith(".png")) {
                contentType = "image/png";
            }
            
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, 
                            "attachment; filename=\"" + java.net.URLEncoder.encode(fileName, "UTF-8") + "\"")
                    .body(resource);
                    
        } catch (Exception e) {
            log.error("Failed to download file: {}", filePath, e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * 删除文件
     */
    @Log(title = "文件删除", businessType = BusinessType.DELETE)
    @DeleteMapping("/{filePath:.+}")
    @Operation(summary = "删除文件", description = "根据文件路径删除文件")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "删除成功"),
            @ApiResponse(responseCode = "404", description = "文件不存在")
    })
    public AjaxResult deleteFile(
            @Parameter(description = "文件相对路径", required = true)
            @PathVariable String filePath) {
        
        try {
            boolean deleted = fileUploadService.deleteFile(filePath);
            
            if (deleted) {
                log.info("File deleted successfully: {}", filePath);
                return AjaxResult.success("File deleted successfully");
            } else {
                log.warn("File not found for deletion: {}", filePath);
                return AjaxResult.error("File not found");
            }
            
        } catch (Exception e) {
            log.error("Failed to delete file: {}", filePath, e);
            return AjaxResult.error("Failed to delete file: " + e.getMessage());
        }
    }
    
    /**
     * 获取上传根目录
     */
    @GetMapping("/root")
    @Operation(summary = "获取上传根目录", description = "返回文件上传的根目录路径")
    public AjaxResult getUploadRoot() {
        String root = fileUploadService.getUploadRoot();
        return AjaxResult.success("Upload root directory", root);
    }
}

