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

@RestController
@RequestMapping("/api/upload")
public class UploadController {

    @Value("${file.upload-path:uploads}")
    private String uploadPath;

    @PostMapping
    public Result<Map<String, String>> upload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.error(400, "文件不能为空");
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
