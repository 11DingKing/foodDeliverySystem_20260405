-- Food Delivery System Database Init Script
SET NAMES 'utf8mb4' COLLATE 'utf8mb4_unicode_ci';
SET CHARACTER_SET_CLIENT = utf8mb4;
SET CHARACTER_SET_CONNECTION = utf8mb4;
SET CHARACTER_SET_RESULTS = utf8mb4;

CREATE DATABASE IF NOT EXISTS food_delivery DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE food_delivery;

SET NAMES 'utf8mb4' COLLATE 'utf8mb4_unicode_ci';

CREATE TABLE IF NOT EXISTS `user` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `username` VARCHAR(50) NOT NULL UNIQUE,
    `password` VARCHAR(255) NOT NULL,
    `nickname` VARCHAR(50),
    `phone` VARCHAR(20),
    `avatar` VARCHAR(500),
    `role` TINYINT DEFAULT 0,
    `status` TINYINT DEFAULT 1,
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX `idx_username` (`username`),
    INDEX `idx_phone` (`phone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `user_address` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL,
    `contact_name` VARCHAR(50) NOT NULL,
    `contact_phone` VARCHAR(20) NOT NULL,
    `province` VARCHAR(50),
    `city` VARCHAR(50),
    `district` VARCHAR(50),
    `detail` VARCHAR(255) NOT NULL,
    `is_default` TINYINT DEFAULT 0,
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `shop` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL,
    `name` VARCHAR(100) NOT NULL,
    `category` VARCHAR(50) NOT NULL,
    `logo` VARCHAR(500),
    `description` VARCHAR(500),
    `phone` VARCHAR(20),
    `province` VARCHAR(50),
    `city` VARCHAR(50),
    `district` VARCHAR(50),
    `address` VARCHAR(255),
    `min_price` DECIMAL(10,2) DEFAULT 0,
    `delivery_fee` DECIMAL(10,2) DEFAULT 0,
    `rating` DECIMAL(2,1) DEFAULT 5.0,
    `monthly_sales` INT DEFAULT 0,
    `status` TINYINT DEFAULT 0,
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_status` (`status`),
    INDEX `idx_category` (`category`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


CREATE TABLE IF NOT EXISTS `dish_category` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `shop_id` BIGINT NOT NULL,
    `name` VARCHAR(50) NOT NULL,
    `sort` INT DEFAULT 0,
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX `idx_shop_id` (`shop_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `dish` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `shop_id` BIGINT NOT NULL,
    `category_id` BIGINT,
    `name` VARCHAR(100) NOT NULL,
    `image` VARCHAR(500),
    `description` VARCHAR(500),
    `price` DECIMAL(10,2) NOT NULL,
    `original_price` DECIMAL(10,2),
    `sales` INT DEFAULT 0,
    `status` TINYINT DEFAULT 1,
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX `idx_shop_id` (`shop_id`),
    INDEX `idx_category_id` (`category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `order` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `order_no` VARCHAR(32) NOT NULL UNIQUE,
    `user_id` BIGINT NOT NULL,
    `shop_id` BIGINT NOT NULL,
    `rider_id` BIGINT,
    `total_amount` DECIMAL(10,2) NOT NULL,
    `delivery_fee` DECIMAL(10,2) DEFAULT 0,
    `pay_amount` DECIMAL(10,2) NOT NULL,
    `status` TINYINT DEFAULT 0,
    `contact_name` VARCHAR(50) NOT NULL,
    `contact_phone` VARCHAR(20) NOT NULL,
    `delivery_address` VARCHAR(255) NOT NULL,
    `remark` VARCHAR(255),
    `pay_time` DATETIME,
    `accept_time` DATETIME,
    `pickup_time` DATETIME,
    `deliver_time` DATETIME,
    `cancel_time` DATETIME,
    `cancel_reason` VARCHAR(255),
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX `idx_order_no` (`order_no`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_shop_id` (`shop_id`),
    INDEX `idx_rider_id` (`rider_id`),
    INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `order_item` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `order_id` BIGINT NOT NULL,
    `dish_id` BIGINT NOT NULL,
    `dish_name` VARCHAR(100) NOT NULL,
    `dish_image` VARCHAR(500),
    `price` DECIMAL(10,2) NOT NULL,
    `quantity` INT NOT NULL,
    `subtotal` DECIMAL(10,2) NOT NULL,
    INDEX `idx_order_id` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `rider_application` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL,
    `real_name` VARCHAR(50) NOT NULL,
    `id_card` VARCHAR(18) NOT NULL,
    `phone` VARCHAR(20) NOT NULL,
    `status` TINYINT DEFAULT 0,
    `reject_reason` VARCHAR(255),
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `operation_log` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `user_id` BIGINT,
    `username` VARCHAR(50),
    `module` VARCHAR(50),
    `operation` VARCHAR(50),
    `method` VARCHAR(200),
    `params` TEXT,
    `ip` VARCHAR(50),
    `status` TINYINT,
    `error_msg` TEXT,
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- =====================================================
-- Mock Data (Password: 123456)
-- =====================================================

INSERT INTO `user` (`id`, `username`, `password`, `nickname`, `phone`, `role`, `status`) VALUES
(1, 'admin', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZRGdjGj/n3.rsQ0fEnxRbPYBniGCy', '管理员', '13800000000', 0, 1),
(2, 'user1', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZRGdjGj/n3.rsQ0fEnxRbPYBniGCy', '张三', '13800000001', 0, 1),
(3, 'user2', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZRGdjGj/n3.rsQ0fEnxRbPYBniGCy', '李四', '13800000002', 0, 1),
(4, 'merchant1', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZRGdjGj/n3.rsQ0fEnxRbPYBniGCy', '王老板', '13800000003', 1, 1),
(5, 'merchant2', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZRGdjGj/n3.rsQ0fEnxRbPYBniGCy', '李老板', '13800000004', 1, 1),
(6, 'merchant3', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZRGdjGj/n3.rsQ0fEnxRbPYBniGCy', '赵老板', '13800000005', 1, 1),
(7, 'rider1', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZRGdjGj/n3.rsQ0fEnxRbPYBniGCy', '骑手小王', '13800000006', 2, 1),
(8, 'rider2', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZRGdjGj/n3.rsQ0fEnxRbPYBniGCy', '骑手小李', '13800000007', 2, 1);

INSERT INTO `user_address` (`user_id`, `contact_name`, `contact_phone`, `province`, `city`, `district`, `detail`, `is_default`) VALUES
(2, '张三', '13800000001', '北京市', '北京市', '朝阳区', '建国路88号SOHO现代城A座1001', 1),
(2, '张三', '13800000001', '北京市', '北京市', '海淀区', '中关村大街1号', 0),
(3, '李四', '13800000002', '上海市', '上海市', '浦东新区', '陆家嘴环路1000号', 1);

INSERT INTO `shop` (`id`, `user_id`, `name`, `category`, `logo`, `description`, `phone`, `province`, `city`, `district`, `address`, `min_price`, `delivery_fee`, `rating`, `monthly_sales`, `status`) VALUES
(1, 4, '老王黄焖鸡', '快餐', '/images/shops/shop1.jpg', '正宗黄焖鸡米饭 鲜嫩多汁', '13800000003', '北京市', '北京市', '朝阳区', '建国路66号', 15.00, 3.00, 4.8, 2580, 1),
(2, 5, '川味小厨', '美食', '/images/shops/shop2.jpg', '地道川菜 麻辣鲜香', '13800000004', '北京市', '北京市', '朝阳区', '朝阳北路100号', 20.00, 4.00, 4.6, 1890, 1),
(3, 6, '日式料理屋', '日料', '/images/shops/shop3.jpg', '精选日料 寿司 刺身 拉面', '13800000005', '北京市', '北京市', '海淀区', '中关村南大街5号', 30.00, 5.00, 4.9, 980, 1);

INSERT INTO `dish_category` (`id`, `shop_id`, `name`, `sort`) VALUES
(1, 1, '招牌推荐', 1),
(2, 1, '主食', 2),
(3, 1, '小吃', 3),
(4, 1, '饮品', 4),
(5, 2, '热销菜品', 1),
(6, 2, '川菜经典', 2),
(7, 2, '凉菜', 3),
(8, 2, '主食', 4),
(9, 3, '刺身', 1),
(10, 3, '寿司', 2),
(11, 3, '拉面', 3),
(12, 3, '定食', 4);


INSERT INTO `dish` (`shop_id`, `category_id`, `name`, `image`, `description`, `price`, `original_price`, `sales`, `status`) VALUES
(1, 1, '招牌黄焖鸡', '/images/dishes/dish1.jpg', '精选三黄鸡腿肉，秘制酱料焖制，鲜嫩入味', 28.00, 32.00, 1580, 1),
(1, 1, '黄焖鸡套餐', '/images/dishes/dish2.jpg', '黄焖鸡配卤蛋，营养美味', 32.00, 38.00, 890, 1),
(1, 2, '米饭', '/images/dishes/dish3.jpg', '东北优质大米', 2.00, NULL, 2000, 1),
(1, 2, '拌饭', '/images/dishes/dish4.jpg', '配黄焖鸡汤汁拌饭', 3.00, NULL, 1200, 1),
(1, 3, '卤蛋', '/images/dishes/dish1.jpg', '秘制卤蛋，入味香浓', 3.00, NULL, 800, 1),
(1, 3, '土豆片', '/images/dishes/dish1.jpg', '新鲜土豆，软糯可口', 5.00, NULL, 600, 1),
(1, 4, '可乐', '/images/dishes/dish5.jpg', '冰镇可口可乐', 5.00, NULL, 500, 1),
(1, 4, '雪碧', '/images/dishes/dish6.jpg', '冰镇雪碧', 5.00, NULL, 400, 1);

INSERT INTO `dish` (`shop_id`, `category_id`, `name`, `image`, `description`, `price`, `original_price`, `sales`, `status`) VALUES
(2, 5, '麻婆豆腐', '/images/dishes/dish7.jpg', '正宗川味麻婆豆腐，麻辣鲜香', 22.00, 26.00, 980, 1),
(2, 5, '宫保鸡丁', '/images/dishes/dish8.jpg', '花生酥脆，鸡丁嫩滑', 28.00, 32.00, 860, 1),
(2, 6, '水煮肉片', '/images/dishes/dish9.jpg', '麻辣鲜香，肉片嫩滑', 38.00, 45.00, 720, 1),
(2, 6, '回锅肉', '/images/dishes/dish10.jpg', '肥而不腻，香辣可口', 32.00, 38.00, 650, 1),
(2, 6, '鱼香肉丝', '/images/dishes/dish11.jpg', '酸甜微辣，下饭神器', 26.00, 30.00, 580, 1),
(2, 7, '夫妻肺片', '/images/dishes/dish11.jpg', '麻辣爽口，开胃凉菜', 28.00, NULL, 420, 1),
(2, 7, '口水鸡', '/images/dishes/dish1.jpg', '红油浇淋，鲜嫩多汁', 32.00, NULL, 380, 1),
(2, 8, '蛋炒饭', '/images/dishes/dish13.jpg', '粒粒分明，蛋香四溢', 12.00, NULL, 900, 1);

INSERT INTO `dish` (`shop_id`, `category_id`, `name`, `image`, `description`, `price`, `original_price`, `sales`, `status`) VALUES
(3, 9, '三文鱼刺身', '/images/dishes/dish14.jpg', '挪威进口三文鱼，新鲜肥美', 68.00, 78.00, 320, 1),
(3, 9, '金枪鱼刺身', '/images/dishes/dish15.jpg', '深海金枪鱼，口感细腻', 88.00, 98.00, 180, 1),
(3, 10, '三文鱼寿司', '/images/dishes/dish16.jpg', '新鲜三文鱼配醋饭', 38.00, NULL, 280, 1),
(3, 10, '鳗鱼寿司', '/images/dishes/dish17.jpg', '蒲烧鳗鱼，甜香可口', 48.00, NULL, 220, 1),
(3, 10, '寿司拼盘', '/images/dishes/dish16.jpg', '多种寿司组合，一次满足', 98.00, 118.00, 150, 1),
(3, 11, '豚骨拉面', '/images/dishes/dish18.jpg', '浓郁豚骨汤底，叉烧软嫩', 42.00, 48.00, 380, 1),
(3, 11, '味噌拉面', '/images/dishes/dish19.jpg', '日式味噌汤底，风味独特', 38.00, NULL, 260, 1),
(3, 12, '照烧鸡腿定食', '/images/dishes/dish2.jpg', '照烧鸡腿配米饭味噌汤', 45.00, 52.00, 200, 1),
(3, 12, '炸猪排定食', '/images/dishes/dish20.jpg', '酥脆炸猪排配特制酱汁', 48.00, 55.00, 180, 1);

ALTER TABLE `user` AUTO_INCREMENT = 100;
ALTER TABLE `shop` AUTO_INCREMENT = 100;
ALTER TABLE `dish_category` AUTO_INCREMENT = 100;
ALTER TABLE `dish` AUTO_INCREMENT = 100;
