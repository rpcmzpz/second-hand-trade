package com.campus.trade.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.trade.dto.UserPageDTO;

import java.util.Map;

public interface AdminService {

    Map<String, Object> getDashboard();

    Page<UserPageDTO> getUsers(Integer page, Integer size, String keyword);

    void updateUserStatus(Long userId, Integer status);
}
