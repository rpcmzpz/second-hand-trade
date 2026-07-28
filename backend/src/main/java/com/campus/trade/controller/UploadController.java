package com.campus.trade.controller;

import cn.hutool.core.util.IdUtil;
import com.campus.trade.common.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/upload")
public class UploadController {

    private static final Set<String> ALLOWED_MIME_TYPES = Set.of(
            "image/jpeg", "image/png", "image/gif", "image/webp"
    );

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB

    @Value("${file.upload-path:uploads}")
    private String uploadPath;

    @PostMapping
    public Result<Map<String, String>> upload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.error(400, "文件不能为空");
        }

        // MIME 类型白名单校验
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_MIME_TYPES.contains(contentType)) {
            return Result.error(400, "不支持的文件类型，仅允许 jpg / png / gif / webp");
        }

        // 文件大小校验
        if (file.getSize() > MAX_FILE_SIZE) {
            return Result.error(400, "文件大小不能超过 5MB");
        }

        try {
            File dir = new File(uploadPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (StringUtils.hasText(originalFilename) && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String newFilename = IdUtil.fastSimpleUUID() + extension;

            File dest = new File(dir, newFilename);
            file.transferTo(dest);

            String fileUrl = "/uploads/" + newFilename;
            Map<String, String> result = new HashMap<>();
            result.put("url", fileUrl);
            result.put("filename", originalFilename);
            return Result.success("上传成功", result);
        } catch (IOException e) {
            return Result.error(500, "文件上传失败");
        }
    }
}
