package com.campus.trade.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.trade.common.Result;
import com.campus.trade.dto.UpdateStatusDTO;
import com.campus.trade.dto.UserPageDTO;
import com.campus.trade.service.AdminService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @GetMapping("/dashboard")
    public Result<Map<String, Object>> dashboard() {
        return Result.success(adminService.getDashboard());
    }

    @GetMapping("/users")
    public Result<Page<UserPageDTO>> users(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword) {
        return Result.success(adminService.getUsers(page, size, keyword));
    }

    @PutMapping("/users/{userId}/status")
    public Result<Void> updateUserStatus(@PathVariable Long userId,
                                         @Valid @RequestBody UpdateStatusDTO dto) {
        adminService.updateUserStatus(userId, dto.getStatus());
        return Result.success(dto.getStatus() == 1 ? "用户已启用" : "用户已禁用");
    }
}
