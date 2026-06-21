package com.shop.backend.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/files")
@Slf4j
public class FileController {
    
    @Value("${app.csv.images-dir}")
    private String imagesDir;
    
    private Path imagesPath;
    
    @PostConstruct
    public void init() throws IOException {
        imagesPath = Paths.get(imagesDir);
        if (!Files.exists(imagesPath)) {
            Files.createDirectories(imagesPath);
        }
    }
    
    @PostMapping("/upload")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("文件为空");
            }
            
            // 验证文件类型
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return ResponseEntity.badRequest().body("只允许上传图片文件");
            }
            
            // 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String fileExtension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
            String uuid = UUID.randomUUID().toString().substring(0, 8);
            String newFilename = "product_" + timestamp + "_" + uuid + fileExtension;
            
            // 保存文件
            Path targetLocation = imagesPath.resolve(newFilename);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            
            // 构建文件URL
            String fileUrl = "/api/files/" + newFilename;
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "文件上传成功");
            response.put("filename", newFilename);
            response.put("originalFilename", originalFilename);
            response.put("size", file.getSize());
            response.put("contentType", contentType);
            response.put("url", fileUrl);
            response.put("timestamp", LocalDateTime.now());
            
            return ResponseEntity.ok(response);
            
        } catch (IOException e) {
            log.error("文件上传失败: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("文件上传失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/{filename:.+}")
    public ResponseEntity<Resource> getFile(@PathVariable String filename) {
        try {
            Path filePath = imagesPath.resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            
            if (resource.exists() && resource.isReadable()) {
                String contentType = Files.probeContentType(filePath);
                if (contentType == null) {
                    contentType = "application/octet-stream";
                }
                
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .header(HttpHeaders.CONTENT_DISPOSITION, 
                                "inline; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException e) {
            log.error("文件URL格式错误: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        } catch (IOException e) {
            log.error("读取文件失败: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @DeleteMapping("/{filename:.+}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseEntity<?> deleteFile(@PathVariable String filename) {
        try {
            Path filePath = imagesPath.resolve(filename).normalize();
            
            if (!Files.exists(filePath)) {
                return ResponseEntity.notFound().build();
            }
            
            Files.delete(filePath);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "文件删除成功");
            response.put("filename", filename);
            response.put("timestamp", LocalDateTime.now());
            
            return ResponseEntity.ok(response);
            
        } catch (IOException e) {
            log.error("删除文件失败: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("删除文件失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/list")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseEntity<?> listFiles() {
        try {
            Map<String, Object> response = new HashMap<>();
            
            if (Files.exists(imagesPath)) {
                var files = Files.list(imagesPath)
                        .filter(Files::isRegularFile)
                        .map(path -> {
                            Map<String, Object> fileInfo = new HashMap<>();
                            fileInfo.put("filename", path.getFileName().toString());
                            try {
                                fileInfo.put("size", Files.size(path));
                                fileInfo.put("lastModified", Files.getLastModifiedTime(path).toString());
                            } catch (IOException e) {
                                // 忽略错误
                            }
                            return fileInfo;
                        })
                        .toList();
                
                response.put("files", files);
                response.put("count", files.size());
            } else {
                response.put("files", java.util.Collections.emptyList());
                response.put("count", 0);
            }
            
            response.put("directory", imagesPath.toString());
            response.put("timestamp", LocalDateTime.now());
            
            return ResponseEntity.ok(response);
            
        } catch (IOException e) {
            log.error("列出文件失败: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("列出文件失败: " + e.getMessage());
        }
    }
}