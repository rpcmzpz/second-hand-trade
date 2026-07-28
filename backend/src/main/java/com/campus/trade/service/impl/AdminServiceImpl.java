package com.campus.trade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.trade.common.BusinessException;
import com.campus.trade.dto.UserPageDTO;
import com.campus.trade.entity.User;
import com.campus.trade.mapper.OrderMapper;
import com.campus.trade.mapper.ProductMapper;
import com.campus.trade.mapper.UserMapper;
import com.campus.trade.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Override
    public Map<String, Object> getDashboard() {
        Map<String, Object> data = new HashMap<>();
        data.put("totalUsers", userMapper.selectCount(null));
        data.put("totalProducts", productMapper.selectCount(null));
        data.put("totalOrders", orderMapper.selectCount(null));

        LocalDateTime todayStart = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime todayEnd = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.between(User::getCreateTime, todayStart, todayEnd);
        data.put("todayNewUsers", userMapper.selectCount(wrapper));

        return data;
    }

    @Override
    public Page<UserPageDTO> getUsers(Integer page, Integer size, String keyword) {
        Page<User> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(User::getUsername, keyword).or()
                    .like(User::getRealName, keyword);
        }
        wrapper.orderByDesc(User::getCreateTime);
        Page<User> userPage = userMapper.selectPage(pageParam, wrapper);

        // 转换为 DTO（去密码 + 脱敏）
        Page<UserPageDTO> dtoPage = new Page<>(page, size);
        dtoPage.setTotal(userPage.getTotal());
        dtoPage.setRecords(userPage.getRecords().stream()
                .map(UserPageDTO::from)
                .toList());
        return dtoPage;
    }

    @Override
    public void updateUserStatus(Long userId, Integer status) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        user.setStatus(status);
        userMapper.updateById(user);
    }
}
