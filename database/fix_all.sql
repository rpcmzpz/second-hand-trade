SET NAMES utf8mb4;

TRUNCATE message;
TRUNCATE review;
TRUNCATE `order`;
TRUNCATE product;
TRUNCATE category;
TRUNCATE user;

INSERT INTO user (user_id, username, password, real_name, phone, email, role, status) VALUES
(1,'admin','$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5Eu','管理员','13800000000','admin@campus.com','admin',1),
(2,'stu001','$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5Eu','张三','13800000001','stu001@campus.com','user',1),
(3,'stu002','$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5Eu','李四','13800000002','stu002@campus.com','user',1),
(4,'stu003','$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5Eu','王五','13800000003','stu003@campus.com','user',1),
(5,'stu004','$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5Eu','赵六','13800000004','stu004@campus.com','user',1),
(6,'stu005','$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5Eu','孙七','13800000005','stu005@campus.com','user',1);

INSERT INTO category (category_id, category_name, icon, sort_order) VALUES
(1,'教材教辅','📚',1),
(2,'数码电子','💻',2),
(3,'生活用品','👔',3),
(4,'运动娱乐','🎮',4),
(5,'服饰鞋包','👗',5),
(6,'其他闲置','🔧',99);

INSERT INTO product VALUES
(1,'高等数学第七版 同济大学','九成新，只有少量铅笔笔记，附课后习题解答，考研必备',15.00,45.00,'LIKE_NEW','图书馆门口','["https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=math+textbook+stack+clean+condition&image_size=square"]',2,1,'ON_SALE',128,12,'2026-05-15 10:00:00',NULL),
(2,'罗技K380蓝牙键盘 白色','用了不到半年，功能完好，包装配件齐全，送电池',89.00,199.00,'GOOD','北区食堂门口','["https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=white+Logitech+K380+bluetooth+keyboard+desk&image_size=square"]',2,2,'ON_SALE',256,38,'2026-05-16 14:00:00',NULL),
(3,'落地台灯 LED护眼 可调光','可调光调色温，带遥控器，宿舍学习必备',35.00,89.00,'GOOD','7号宿舍楼下','["https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=modern+LED+floor+lamp+dorm+room+warm+lighting&image_size=square"]',3,3,'ON_SALE',67,15,'2026-05-17 09:00:00',NULL),
(4,'iPhone14透明手机壳 全新','买错了型号，全新未拆封，透明软壳',5.00,29.00,'NEW','东区快递驿站','["https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=clear+transparent+iPhone+case+packaging&image_size=square"]',3,2,'ON_SALE',42,6,'2026-05-18 11:00:00',NULL),
(5,'大学英语四级真题 2025新版','全新未拆封，附带听力光盘和答题卡，星火英语',25.00,59.80,'NEW','图书馆二楼自习区','["https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=CET4+English+exam+book+CD+brand+new&image_size=square"]',2,1,'ON_SALE',89,20,'2026-05-19 08:00:00',NULL),
(6,'iPad Air 5 64G WiFi版 深空灰','保修期内，屏幕无划痕，送原装保护壳和充电器',3200.00,4399.00,'GOOD','南区宿舍3号楼','["https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=iPad+Air+5+space+gray+wooden+desk+case&image_size=square"]',3,2,'ON_SALE',456,55,'2026-05-19 10:00:00',NULL),
(7,'漫步者W820NB蓝牙耳机 全新','年会奖品，全新未拆封，主动降噪，续航给力',199.00,349.00,'NEW','东区快递驿站','["https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=Edifier+W820NB+wireless+headphones+unopened+box&image_size=square"]',4,2,'ON_SALE',167,22,'2026-05-19 14:00:00',NULL),
(8,'宿舍用小冰箱 50L 志高','用了不到半年，制冷效果很好，噪音低不扰民',280.00,599.00,'LIKE_NEW','北区7号楼503','["https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=compact+50L+mini+fridge+dorm+room+white+modern&image_size=square"]',5,3,'ON_SALE',95,18,'2026-05-20 09:00:00',NULL),
(9,'考研数学李永乐复习全书','只用铅笔勾画了前两章，其余全新，2025版',18.00,98.00,'GOOD','教学楼A区大厅','["https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=math+review+book+Chinese+graduate+exam+clean&image_size=square"]',6,1,'ON_SALE',56,8,'2026-05-20 10:00:00',NULL),
(10,'尤尼克斯羽毛球拍 NR-700','八成新，已拉线25磅，附送原装拍套和手胶',150.00,450.00,'GOOD','体育馆羽毛球场','["https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=Yonex+badminton+racket+sports+equipment+clean&image_size=square"]',2,4,'ON_SALE',78,18,'2026-05-20 14:00:00',NULL),
(11,'森马男士秋季外套 L码 卡其色','买小了只穿了一次，吊牌已摘但如新',89.00,259.00,'LIKE_NEW','北区食堂门口','["https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=khaki+mens+autumn+jacket+laid+flat+clean&image_size=square"]',3,5,'ON_SALE',34,6,'2026-05-21 09:00:00',NULL),
(12,'考研政治肖秀荣1000题 全新','全新正版，买错了考试科目，未使用过',15.00,49.80,'NEW','图书馆一楼大厅','["https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=Chinese+politics+exam+book+brand+new+clean+cover&image_size=square"]',4,1,'ON_SALE',122,20,'2026-05-21 10:00:00',NULL),
(13,'雅迪小龟王电动车 二手','续航约30公里，带原装充电器和U型锁，校园代步超方便',600.00,1899.00,'FAIR','东区车棚A区','["https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=used+electric+scooter+university+campus+path+outdoor&image_size=square"]',5,6,'ON_SALE',312,55,'2026-05-21 14:00:00',NULL),
(14,'苹果原装20W快充头+数据线','换了安卓手机闲置，充电头和线都原装正品',50.00,149.00,'GOOD','南区宿舍1号楼','["https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=Apple+20W+USB-C+charger+lightning+cable+white+surface&image_size=square"]',6,2,'ON_SALE',88,10,'2026-05-22 08:00:00',NULL);

INSERT INTO `order` VALUES
(1001,3,2,2,89.00,'东区宿舍5号楼 301室','COMPLETED','2026-05-20 10:30:00',NULL),
(1002,4,2,1,15.00,'西区宿舍2号楼 102室','COMPLETED','2026-05-21 14:20:00',NULL),
(1003,5,3,4,5.00,'南区宿舍1号楼 201室','SHIPPED','2026-05-23 09:15:00',NULL),
(1004,6,3,3,35.00,'北区宿舍7号楼 503室','PENDING','2026-05-24 08:00:00',NULL),
(1005,2,5,8,280.00,'教师公寓A栋 102','PAID','2026-05-24 11:30:00',NULL),
(1006,2,4,9,18.00,'教师公寓A栋 102','CANCELED','2026-05-22 16:45:00',NULL);

INSERT INTO review VALUES
(1,1001,3,2,5,'学长人超好！键盘成色很棒跟新的差不多，还送了一个鼠标垫，强烈推荐！','2026-05-20 18:00:00'),
(2,1002,4,2,4,'书不错，只有少量笔记不影响使用，就是发货慢了一天，其他都挺好','2026-05-21 20:30:00');

INSERT INTO message VALUES
(1,3,2,'学长你好，看到你发布的蓝牙键盘，还在吗？',1,'2026-05-20 09:00:00'),
(2,2,3,'在的，用了不到半年，95新，可以当面看货',1,'2026-05-20 09:05:00'),
(3,3,2,'好的，今天下午方便吗？图书馆门口见面交易可以吗？',1,'2026-05-20 09:08:00'),
(4,2,3,'没问题，下午3点图书馆门口见！',1,'2026-05-20 09:10:00'),
(5,4,2,'你好，高数书还有吗？我想买',0,'2026-05-21 10:00:00'),
(6,1,2,'【系统通知】您的商品「罗技K380蓝牙键盘」已售出，买家已托管款项，请尽快发货',1,'2026-05-20 10:31:00');
