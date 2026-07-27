CREATE DATABASE IF NOT EXISTS idle_trade DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE idle_trade;

create table sys_user
(
    id          bigint auto_increment comment '主键'
        primary key,
    username    varchar(30)                           not null comment '账号',
    password    varchar(100)                          not null comment '加密后的密码，BCrypt加密',
    nickname    varchar(30)                           null comment '昵称',
    avatar      varchar(255)                          null comment '头像OSS地址',
    phone       varchar(11)                           null comment '手机号',
    role        varchar(10) default 'user'            not null comment '角色 user普通用户 admin管理员',
    status      tinyint     default 1                 null comment '0禁用1正常',
    create_time datetime    default CURRENT_TIMESTAMP null,
    update_time datetime    default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    constraint uk_username
        unique (username)
)
    comment '用户表' charset = utf8mb4;





create table goods_category
(
    id            bigint auto_increment
        primary key,
    category_name varchar(30)                        not null comment '分类名称：电子产品、书籍、衣物、生活用品',
    sort          int      default 0                 null comment '排序字段',
    create_time   datetime default CURRENT_TIMESTAMP null
)
    comment '商品分类' charset = utf8mb4;



create table idle_goods
(
    id             bigint auto_increment
        primary key,
    user_id        bigint                             not null comment '发布者id关联sys_user',
    category_id    bigint                             not null comment '分类id',
    title          varchar(100)                       not null comment '商品标题',
    content        text                               null comment '商品描述',
    price          decimal(10, 2)                     not null comment '售价',
    original_price decimal(10, 2)                     null comment '原价',
    goods_img      varchar(500)                       null comment '多张图片逗号隔开，OSS地址',
    status         tinyint  default 0                 null comment '0待审核 1上架出售 2下架 3已售出',
    view_count     int      default 0                 null comment '浏览量（放入Redis缓存定时刷回数据库）',
    create_time    datetime default CURRENT_TIMESTAMP null,
    update_time    datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP
)
    comment '闲置商品' charset = utf8mb4;

create index idx_category_id
    on idle_goods (category_id);

create index idx_user_id
    on idle_goods (user_id);




create table trade_order
(
    id          bigint auto_increment
        primary key,
    order_no    varchar(32)                        not null comment '订单编号UUID',
    goods_id    bigint                             not null,
    seller_id   bigint                             not null comment '卖家id',
    buyer_id    bigint                             not null comment '买家id',
    price       decimal(10, 2)                     not null comment '成交价格',
    status      tinyint  default 0                 null comment '0待确认交易 1交易完成 2取消订单',
    create_time datetime default CURRENT_TIMESTAMP null,
    update_time datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    constraint uk_order_no
        unique (order_no)
)
    comment '订单' charset = utf8mb4;

create index idx_goods
    on trade_order (goods_id);






create table goods_message
(
    id           bigint auto_increment
        primary key,
    goods_id     bigint                             not null,
    from_user_id bigint                             not null comment '留言人id',
    to_user_id   bigint                             not null comment '接收人id',
    content      varchar(500)                       not null comment '留言内容',
    create_time  datetime default CURRENT_TIMESTAMP null,
    read_status  int      default 0                 null comment '消息状态 0未读 1已读'
)
    comment '商品留言' charset = utf8mb4;

create index idx_goods_id
    on goods_message (goods_id);





create table goods_comment
(
    id          bigint auto_increment comment '评价id'
        primary key,
    goods_id    bigint                             not null comment '被评价的商品id',
    order_id    bigint                             not null comment '对应的订单id，一个订单只能评价一次',
    buyer_id    bigint                             not null comment '买家id（评价人）',
    seller_id   bigint                             not null comment '卖家id（商品发布者）',
    score       int                                not null comment '评分1‑5分',
    content     varchar(1000)                      null comment '文字评价内容',
    img_list    varchar(500)                       null comment '多张图片地址，逗号隔开',
    create_time datetime default CURRENT_TIMESTAMP null comment '评价时间',
    constraint uk_order_id
        unique (order_id),
    constraint goods_comment_ibfk_1
        foreign key (goods_id) references idle_goods (id),
    constraint goods_comment_ibfk_2
        foreign key (order_id) references trade_order (id),
    constraint goods_comment_ibfk_3
        foreign key (buyer_id) references sys_user (id),
    constraint goods_comment_ibfk_4
        foreign key (seller_id) references sys_user (id)
)
    comment '商品评价表' charset = utf8mb4;

create index buyer_id
    on goods_comment (buyer_id);

create index goods_id
    on goods_comment (goods_id);

create index seller_id
    on goods_comment (seller_id);




create table goods_collect
(
    id          bigint auto_increment comment '主键id'
        primary key,
    user_id     bigint                             not null comment '用户id',
    goods_id    bigint                             not null comment '商品id',
    create_time datetime default CURRENT_TIMESTAMP null comment '收藏时间',
    constraint uk_user_goods
        unique (user_id, goods_id)
)
    comment '商品收藏表';


-- 初始化分类数据
INSERT INTO goods_category(category_name,sort) VALUES
                                                   ('电子产品',1),
                                                   ('图书教材',2),
                                                   ('衣物鞋帽',3),
                                                   ('生活用品',4),
                                                   ('运动器材',5);