-- 补充用户
INSERT IGNORE INTO user (user_id, username, password, real_name, phone, email, role, status) VALUES
(4, 'stu003', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5Eu', '王五', '13800000003', 'stu003@campus.com', 'user', 1),
(5, 'stu004', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5Eu', '赵六', '13800000004', 'stu004@campus.com', 'user', 1),
(6, 'stu005', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5Eu', '孙七', '13800000005', 'stu005@campus.com', 'user', 1);

-- 商品关联分类
UPDATE product SET category_id = 1 WHERE product_id = 1;
UPDATE product SET category_id = 2 WHERE product_id = 2;
UPDATE product SET category_id = 3 WHERE product_id = 3;
UPDATE product SET category_id = 4 WHERE product_id = 4;

-- 补充商品
INSERT INTO product (title, description, price, original_price, `condition`, location, images, seller_id, category_id, status, view_count, favorite_count, create_time) VALUES
('大学英语四级真题 2025新版', '全新未拆封，附带听力光盘和答题卡', 25.00, 59.80, 'NEW', '图书馆二楼', '["https://via.placeholder.com/300x300?text=CET4"]', 2, 1, 'ON_SALE', 89, 12, NOW()),
('iPad Air 5 64G WiFi版', '深空灰，保修期内，屏幕无划痕，送保护壳', 3200.00, 4399.00, 'GOOD', '南区宿舍3号楼', '["https://via.placeholder.com/300x300?text=iPad"]', 3, 2, 'ON_SALE', 456, 38, NOW()),
('全新未拆 漫步者蓝牙耳机 W820NB', '年会奖品，未拆封，降噪效果好', 199.00, 349.00, 'NEW', '东区快递点', '["https://via.placeholder.com/300x300?text=Headphone"]', 4, 2, 'ON_SALE', 167, 22, NOW()),
('宿舍用小冰箱 50L', '九成新，用了不到半年，制冷效果很好', 280.00, 599.00, 'LIKE_NEW', '北区7号楼', '["https://via.placeholder.com/300x300?text=Fridge"]', 5, 3, 'ON_SALE', 95, 15, NOW()),
('考研数学李永乐复习全书', '只用铅笔勾画了前两章，其余全新', 18.00, 98.00, 'GOOD', '教学楼A区', '["https://via.placeholder.com/300x300?text=Math"]', 6, 1, 'ON_SALE', 56, 8, NOW()),
('尤尼克斯羽毛球拍 NR-700', '八成新，拉线25磅，附带拍套', 150.00, 450.00, 'GOOD', '体育馆门口', '["https://via.placeholder.com/300x300?text=Badminton"]', 2, 4, 'ON_SALE', 78, 18, NOW()),
('森马男士外套 秋季新款 L码', '买小了只穿了一次', 89.00, 259.00, 'LIKE_NEW', '北区食堂', '["https://via.placeholder.com/300x300?text=Jacket"]', 3, 5, 'ON_SALE', 34, 6, NOW()),
('考研政治肖秀荣1000题', '全新正版，买错了科目', 15.00, 49.80, 'NEW', '图书馆门口', '["https://via.placeholder.com/300x300?text=Politics"]', 4, 1, 'ON_SALE', 122, 20, NOW()),
('二手电动车 雅迪小龟王', '续航30km，带充电器和锁，校园代步神器', 600.00, 1899.00, 'FAIR', '东区车棚', '["https://via.placeholder.com/300x300?text=Ebike"]', 5, 6, 'ON_SALE', 312, 55, NOW()),
('苹果原装20W快充头+数据线', '换安卓手机了，闲置出售', 50.00, 149.00, 'GOOD', '南区宿舍', '["https://via.placeholder.com/300x300?text=Charger"]', 6, 2, 'ON_SALE', 88, 10, NOW());

-- 测试订单（覆盖各种状态）
INSERT INTO `order` (order_id, buyer_id, seller_id, product_id, amount, address, status, create_time) VALUES
(1001, 3, 2, 2, 89.00, '东区宿舍5号楼 301室', 'COMPLETED', '2026-05-20 10:30:00'),
(1002, 4, 2, 1, 15.00, '西区宿舍2号楼 102室', 'COMPLETED', '2026-05-21 14:20:00'),
(1003, 5, 3, 4, 5.00, '南区宿舍1号楼 201室', 'SHIPPED', '2026-05-23 09:15:00'),
(1004, 6, 3, 3, 35.00, '北区宿舍7号楼 503室', 'PENDING', '2026-05-24 08:00:00'),
(1005, 2, 5, 7, 280.00, '教师公寓A栋 102', 'PAID', '2026-05-24 11:30:00'),
(1006, 2, 4, 8, 18.00, '教师公寓A栋 102', 'CANCELED', '2026-05-22 16:45:00');

-- 测试评价
INSERT INTO review (review_id, order_id, reviewer_id, reviewee_id, rating, content, create_time) VALUES
(1, 1001, 3, 2, 5, '学长人很好，蓝牙键盘成色很棒，还送了一个鼠标垫，好评！', '2026-05-20 18:00:00'),
(2, 1002, 4, 2, 4, '书不错只有少量笔记，就是发货慢了一天', '2026-05-21 20:30:00');

-- 测试站内信
INSERT INTO message (message_id, sender_id, receiver_id, content, is_read, create_time) VALUES
(1, 3, 2, '学长你好，键盘还在吗？', 1, '2026-05-20 09:00:00'),
(2, 2, 3, '在的，95新，可以当面看货', 1, '2026-05-20 09:05:00'),
(3, 3, 2, '好的，今天下午可以吗？图书馆门口见', 1, '2026-05-20 09:08:00'),
(4, 2, 3, '没问题，下午3点见', 1, '2026-05-20 09:10:00'),
(5, 4, 2, '高数书还有吗？我想买', 0, '2026-05-21 10:00:00'),
(6, 1, 2, '系统通知：您的商品已售出，买家已将款项托管，请尽快发货', 1, '2026-05-20 10:31:00');
