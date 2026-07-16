CREATE DATABASE IF NOT EXISTS idle_trade DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE idle_trade;

-- 1.用户表
DROP TABLE IF EXISTS sys_user;
CREATE TABLE sys_user(
                         id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
                         username VARCHAR(30) NOT NULL COMMENT '账号',
                         password VARCHAR(100) NOT NULL COMMENT '加密后的密码，BCrypt加密',
                         nickname VARCHAR(30) COMMENT '昵称',
                         avatar VARCHAR(255) COMMENT '头像OSS地址',
                         phone VARCHAR(11) COMMENT '手机号',
                         role VARCHAR(10) NOT NULL DEFAULT 'user' COMMENT '角色 user普通用户 admin管理员',
                         status TINYINT DEFAULT 1 COMMENT '0禁用1正常',
                         create_time DATETIME DEFAULT NOW(),
                         update_time DATETIME DEFAULT NOW() ON UPDATE NOW(),
                         UNIQUE KEY uk_username(username)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 2.商品分类表
DROP TABLE IF EXISTS goods_category;
CREATE TABLE goods_category(
                               id BIGINT AUTO_INCREMENT PRIMARY KEY,
                               category_name VARCHAR(30) NOT NULL COMMENT '分类名称：电子产品、书籍、衣物、生活用品',
                               sort INT DEFAULT 0 COMMENT '排序字段',
                               create_time DATETIME DEFAULT NOW()
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品分类';

-- 3.闲置商品表
DROP TABLE IF EXISTS idle_goods;
CREATE TABLE idle_goods(
                           id BIGINT AUTO_INCREMENT PRIMARY KEY,
                           user_id BIGINT NOT NULL COMMENT '发布者id关联sys_user',
                           category_id BIGINT NOT NULL COMMENT '分类id',
                           title VARCHAR(100) NOT NULL COMMENT '商品标题',
                           content TEXT COMMENT '商品描述',
                           price DECIMAL(10,2) NOT NULL COMMENT '售价',
                           original_price DECIMAL(10,2) COMMENT '原价',
                           goods_img VARCHAR(500) COMMENT '多张图片逗号隔开，OSS地址',
                           status TINYINT DEFAULT 0 COMMENT '0待审核 1上架出售 2下架 3已售出',
                           view_count INT DEFAULT 0 COMMENT '浏览量（放入Redis缓存定时刷回数据库）',
                           create_time DATETIME DEFAULT NOW(),
                           update_time DATETIME DEFAULT NOW() ON UPDATE NOW(),
                           INDEX idx_user_id(user_id),
                           INDEX idx_category_id(category_id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='闲置商品';

-- 4.订单表
DROP TABLE IF EXISTS trade_order;
CREATE TABLE trade_order(
                            id BIGINT AUTO_INCREMENT PRIMARY KEY,
                            order_no VARCHAR(32) NOT NULL COMMENT '订单编号UUID',
                            goods_id BIGINT NOT NULL,
                            seller_id BIGINT NOT NULL COMMENT '卖家id',
                            buyer_id BIGINT NOT NULL COMMENT '买家id',
                            price DECIMAL(10,2) NOT NULL COMMENT '成交价格',
                            status TINYINT DEFAULT 0 COMMENT '0待确认交易 1交易完成 2取消订单',
                            create_time DATETIME DEFAULT NOW(),
                            update_time DATETIME DEFAULT NOW() ON UPDATE NOW(),
                            UNIQUE KEY uk_order_no(order_no),
                            INDEX idx_goods(goods_id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单';

-- 5.留言评论表（简易版，后期升级WebSocket实时聊天）
DROP TABLE IF EXISTS goods_message;
CREATE TABLE goods_message(
                              id BIGINT AUTO_INCREMENT PRIMARY KEY,
                              goods_id BIGINT NOT NULL,
                              from_user_id BIGINT NOT NULL COMMENT '留言人id',
                              content VARCHAR(500) NOT NULL COMMENT '留言内容',
                              create_time DATETIME DEFAULT NOW(),
                              INDEX idx_goods_id(goods_id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品留言';

-- 6.举报表
DROP TABLE IF EXISTS report;
CREATE TABLE report(
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       goods_id BIGINT NOT NULL,
                       report_user_id BIGINT NOT NULL COMMENT '举报人',
                       reason VARCHAR(500) NOT NULL COMMENT '举报理由',
                       handle_status TINYINT DEFAULT 0 COMMENT '0未处理 1已处理',
                       create_time DATETIME DEFAULT NOW()
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品举报表';

-- 7. 收藏表
CREATE TABLE goods_collect (
                               id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键id',
                               user_id BIGINT NOT NULL COMMENT '用户id',
                               goods_id BIGINT NOT NULL COMMENT '商品id',
                               create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
                               UNIQUE KEY uk_user_goods (user_id,goods_id)
) COMMENT '商品收藏表';

-- 8. 评价表
CREATE TABLE `goods_comment` (
                                 `id` bigint AUTO_INCREMENT PRIMARY KEY COMMENT '评价id',
                                 `goods_id` bigint NOT NULL COMMENT '被评价的商品id',
                                 `order_id` bigint NOT NULL COMMENT '对应的订单id，一个订单只能评价一次',
                                 `buyer_id` bigint NOT NULL COMMENT '买家id（评价人）',
                                 `seller_id` bigint NOT NULL COMMENT '卖家id（商品发布者）',
                                 `score` int NOT NULL COMMENT '评分1‑5分',
                                 `content` varchar(1000) DEFAULT NULL COMMENT '文字评价内容',
                                 `img_list` varchar(500) DEFAULT NULL COMMENT '多张图片地址，逗号隔开',
                                 `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '评价时间',
                                 FOREIGN KEY (`goods_id`) REFERENCES `idle_goods`(`id`),
                                 FOREIGN KEY (`order_id`) REFERENCES `trade_order`(`id`),
                                 FOREIGN KEY (`buyer_id`) REFERENCES `sys_user`(`id`),
                                 FOREIGN KEY (`seller_id`) REFERENCES `sys_user`(`id`),
                                 UNIQUE KEY `uk_order_id` (`order_id`) -- 约束：一个订单只能评价1次
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品评价表';

-- 初始化分类数据
INSERT INTO goods_category(category_name,sort) VALUES
                                                   ('电子产品',1),
                                                   ('图书教材',2),
                                                   ('衣物鞋帽',3),
                                                   ('生活用品',4),
                                                   ('运动器材',5);