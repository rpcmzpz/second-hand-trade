package com.campus.trade.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.trade.entity.User;
import com.campus.trade.mapper.UserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class InitUserPasswordRunner implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(InitUserPasswordRunner.class);

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserMapper userMapper;

    @Override
    public void run(String... args) {
        String rawPassword = "123456";
        String encodedPassword = passwordEncoder.encode(rawPassword);
        log.info("BCrypt加密密码: 明文={}, 密文={}", rawPassword, encodedPassword);

        resetPassword("admin", encodedPassword);
        resetPassword("stu001", encodedPassword);
        resetPassword("stu002", encodedPassword);
    }

    private void resetPassword(String username, String encodedPassword) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        User user = userMapper.selectOne(wrapper);
        if (user != null) {
            user.setPassword(encodedPassword);
            userMapper.updateById(user);
            log.info("已重置用户 {} 的密码", username);
        }
    }
}
