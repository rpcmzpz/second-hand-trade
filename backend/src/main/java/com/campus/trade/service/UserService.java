package com.campus.trade.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.trade.entity.User;

import java.util.Map;

public interface UserService extends IService<User> {

    User register(User user);

    Map<String, Object> login(String username, String password);

    User getUserInfo(Long userId);

    void updateUserInfo(User user);

    void changePassword(Long userId, String oldPassword, String newPassword);
}
