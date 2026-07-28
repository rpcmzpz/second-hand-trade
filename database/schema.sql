-- ==========================================
-- 校园二手交易平台 数据库初始化脚本
-- ==========================================

DROP DATABASE IF EXISTS second_hand_trade;
CREATE DATABASE second_hand_trade DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE second_hand_trade;

-- ----------------------------
-- 用户表
-- ----------------------------
CREATE TABLE `user` (
  `user_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` VARCHAR(50) NOT NULL COMMENT '用户名',
  `password` VARCHAR(255) NOT NULL COMMENT '密码(BCrypt加密)',
  `real_name` VARCHAR(50) DEFAULT NULL COMMENT '真实姓名',
  `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
  `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
  `avatar` VARCHAR(500) DEFAULT NULL COMMENT '头像URL',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态 1:正常 0:禁用',
  `role` VARCHAR(20) NOT NULL DEFAULT 'user' COMMENT '角色 admin/user',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- ----------------------------
-- 商品分类表
-- ----------------------------
CREATE TABLE `category` (
  `category_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `category_name` VARCHAR(50) NOT NULL COMMENT '分类名称',
  `icon` VARCHAR(20) DEFAULT NULL COMMENT '图标',
  `sort_order` INT DEFAULT 0 COMMENT '排序',
  PRIMARY KEY (`category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品分类表';

-- ----------------------------
-- 商品表
-- ----------------------------
CREATE TABLE `product` (
  `product_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '商品ID',
  `title` VARCHAR(200) NOT NULL COMMENT '商品标题',
  `description` TEXT COMMENT '商品描述',
  `price` DECIMAL(10,2) NOT NULL COMMENT '价格',
  `original_price` DECIMAL(10,2) DEFAULT NULL COMMENT '原价',
  `condition` VARCHAR(20) NOT NULL DEFAULT 'GOOD' COMMENT '成色 NEW/LIKE_NEW/GOOD/FAIR',
  `location` VARCHAR(100) DEFAULT NULL COMMENT '交易地点',
  `images` TEXT COMMENT '图片JSON数组',
  `seller_id` BIGINT NOT NULL COMMENT '卖家ID',
  `category_id` BIGINT DEFAULT NULL COMMENT '分类ID',
  `status` VARCHAR(20) NOT NULL DEFAULT 'ON_SALE' COMMENT '商品状态 ON_SALE/SOLD/OFF_SHELF',
  `view_count` INT NOT NULL DEFAULT 0 COMMENT '浏览次数',
  `favorite_count` INT NOT NULL DEFAULT 0 COMMENT '收藏次数',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
  `update_time` DATETIME DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`product_id`),
  KEY `idx_seller_id` (`seller_id`),
  KEY `idx_category_id` (`category_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品表';

-- ----------------------------
-- 订单表
-- ----------------------------
CREATE TABLE `order` (
  `order_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '订单ID',
  `buyer_id` BIGINT NOT NULL COMMENT '买家ID',
  `seller_id` BIGINT NOT NULL COMMENT '卖家ID',
  `product_id` BIGINT NOT NULL COMMENT '商品ID',
  `amount` DECIMAL(10,2) NOT NULL COMMENT '订单金额',
  `address` VARCHAR(300) DEFAULT NULL COMMENT '收货地址',
  `status` VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT '订单状态 PENDING/PAID/SHIPPED/COMPLETED/CANCELED',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '下单时间',
  `update_time` DATETIME DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`order_id`),
  KEY `idx_buyer_id` (`buyer_id`),
  KEY `idx_seller_id` (`seller_id`),
  KEY `idx_product_id` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- ----------------------------
-- 评价表
-- ----------------------------
CREATE TABLE `review` (
  `review_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '评价ID',
  `order_id` BIGINT NOT NULL COMMENT '订单ID',
  `reviewer_id` BIGINT NOT NULL COMMENT '评价人ID',
  `reviewee_id` BIGINT NOT NULL COMMENT '被评价人ID',
  `rating` INT NOT NULL COMMENT '评分 1-5',
  `content` VARCHAR(1000) DEFAULT NULL COMMENT '评价内容',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '评价时间',
  PRIMARY KEY (`review_id`),
  UNIQUE KEY `uk_order_reviewer` (`order_id`, `reviewer_id`),
  KEY `idx_reviewee_id` (`reviewee_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评价表';

-- ----------------------------
-- 站内信表
-- ----------------------------
CREATE TABLE `message` (
  `message_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '消息ID',
  `sender_id` BIGINT NOT NULL COMMENT '发送者ID',
  `receiver_id` BIGINT NOT NULL COMMENT '接收者ID',
  `content` TEXT NOT NULL COMMENT '消息内容',
  `is_read` TINYINT NOT NULL DEFAULT 0 COMMENT '是否已读 0:未读 1:已读',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发送时间',
  PRIMARY KEY (`message_id`),
  KEY `idx_sender_id` (`sender_id`),
  KEY `idx_receiver_id` (`receiver_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='站内信表';

-- ==========================================
-- 测试数据
-- ==========================================

-- 管理员账号: admin / 123456 (BCrypt加密)
INSERT INTO `user` (`username`, `password`, `real_name`, `phone`, `email`, `role`, `status`) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5Eu', '管理员', '13800000000', 'admin@campus.com', 'admin', 1);

-- 普通学生: stu001 / 123456
INSERT INTO `user` (`username`, `password`, `real_name`, `phone`, `email`, `role`, `status`) VALUES
('stu001', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5Eu', '张三', '13800000001', 'stu001@campus.com', 'user', 1);

-- 另一个普通学生
INSERT INTO `user` (`username`, `password`, `real_name`, `phone`, `email`, `role`, `status`) VALUES
('stu002', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5Eu', '李四', '13800000002', 'stu002@campus.com', 'user', 1);

-- 商品分类
INSERT INTO `category` (`category_name`, `icon`, `sort_order`) VALUES
('📚 教材教辅', '📚', 1),
('💻 数码电子', '💻', 2),
('👔 生活用品', '👔', 3),
('🎮 运动娱乐', '🎮', 4),
('👗 服饰鞋包', '👗', 5),
('🔧 其他', '🔧', 99);

-- 测试商品
INSERT INTO `product` (`title`, `description`, `price`, `original_price`, `condition`, `location`, `seller_id`, `status`, `view_count`) VALUES
('高等数学（第七版）同济大学', '九成新，只有少量笔记，附习题解答', 15.00, 45.00, 'LIKE_NEW', '图书馆门口', 2, 'ON_SALE', 128),
('罗技K380蓝牙键盘', '用了半年，功能正常，包装齐全', 89.00, 199.00, 'GOOD', '北区食堂', 2, 'ON_SALE', 256),
('落地台灯 LED护眼', '可调光调色温，带遥控器', 35.00, NULL, 'GOOD', '7号楼', 3, 'ON_SALE', 67),
('iPhone 14 手机壳 全新', '买错了型号，全新未使用，透明款', 5.00, 29.00, 'NEW', '东区快递点', 3, 'ON_SALE', 42);
