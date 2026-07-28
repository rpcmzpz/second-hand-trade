CREATE DATABASE IF NOT EXISTS second_hand_trade CHARACTER SET utf8mb4;
USE second_hand_trade;

SELECT 'users' AS tbl, COUNT(*) AS cnt FROM user
UNION ALL SELECT 'products', COUNT(*) FROM product
UNION ALL SELECT 'orders', COUNT(*) FROM `order`
UNION ALL SELECT 'reviews', COUNT(*) FROM review
UNION ALL SELECT 'messages', COUNT(*) FROM message
UNION ALL SELECT 'categories', COUNT(*) FROM category;
