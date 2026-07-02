-- ============================================================
-- TravelShare 旅游共享平台 - 完整建表SQL
-- ============================================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS travelshare DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE travelshare;

-- 1. 用户表 tb_user
DROP TABLE IF EXISTS tb_user;
CREATE TABLE tb_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码(BCrypt加密)',
    email VARCHAR(100) COMMENT '邮箱',
    avatar VARCHAR(255) COMMENT '头像URL',
    role VARCHAR(20) DEFAULT 'USER' COMMENT '角色: USER/ADMIN/DISABLED',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB COMMENT='用户表';

-- 2. 城市表 tb_city
DROP TABLE IF EXISTS tb_city;
CREATE TABLE tb_city (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '城市ID',
    name VARCHAR(50) NOT NULL COMMENT '城市名称',
    province VARCHAR(50) NOT NULL COMMENT '所属省份',
    description TEXT COMMENT '城市简介',
    cover_image VARCHAR(255) COMMENT '封面图URL',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB COMMENT='城市表';

-- 3. 景点表 tb_attraction
DROP TABLE IF EXISTS tb_attraction;
CREATE TABLE tb_attraction (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '景点ID',
    name VARCHAR(100) NOT NULL COMMENT '景点名称',
    city_id BIGINT NOT NULL COMMENT '所属城市ID',
    description TEXT COMMENT '景点介绍',
    open_time VARCHAR(50) COMMENT '开放时间，如09:00-22:00',
    ticket_price DECIMAL(10,2) DEFAULT 0 COMMENT '门票价格',
    cover_image VARCHAR(255) COMMENT '封面图URL',
    rating DOUBLE DEFAULT 4.5 COMMENT '评分',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB COMMENT='景点表';

-- 4. 攻略表 tb_strategy
DROP TABLE IF EXISTS tb_strategy;
CREATE TABLE tb_strategy (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '攻略ID',
    title VARCHAR(200) NOT NULL COMMENT '攻略标题',
    content TEXT COMMENT '攻略正文',
    city VARCHAR(50) COMMENT '旅游城市',
    author_id BIGINT COMMENT '作者用户ID',
    status VARCHAR(20) DEFAULT 'PENDING' COMMENT '状态: PENDING/APPROVED/REJECTED',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB COMMENT='攻略表';

-- 5. 路线表 tb_route
DROP TABLE IF EXISTS tb_route;
CREATE TABLE tb_route (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '路线ID',
    province VARCHAR(50) COMMENT '省份',
    start_city VARCHAR(50) NOT NULL COMMENT '出发城市',
    middle_city VARCHAR(50) COMMENT '途经城市',
    end_city VARCHAR(50) NOT NULL COMMENT '终点城市',
    transport_type VARCHAR(50) COMMENT '交通方式: 高铁/大巴/打车/自驾',
    duration VARCHAR(50) COMMENT '耗时，如3小时',
    price DECIMAL(10,2) COMMENT '费用',
    description TEXT COMMENT '路线描述',
    publisher_id BIGINT COMMENT '发布者用户ID',
    status VARCHAR(20) DEFAULT 'PENDING' COMMENT '状态: PENDING/APPROVED/REJECTED',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB COMMENT='路线表';

-- 6. 实时旅游动态表 tb_travel_notice (核心创新模块)
DROP TABLE IF EXISTS tb_travel_notice;
CREATE TABLE tb_travel_notice (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '动态ID',
    city_id BIGINT COMMENT '关联城市ID',
    attraction_id BIGINT COMMENT '关联景点ID',
    city VARCHAR(50) COMMENT '城市名称',
    attraction_name VARCHAR(100) COMMENT '景点名称',
    title VARCHAR(200) NOT NULL COMMENT '动态标题',
    content TEXT COMMENT '动态内容',
    category VARCHAR(50) COMMENT '分类: 景区关闭/交通异常/游客拥堵/临时活动',
    level VARCHAR(20) DEFAULT 'info' COMMENT '预警级别: info/warning/danger',
    images VARCHAR(500) COMMENT '图片URL(多张逗号分隔)',
    publisher_id BIGINT COMMENT '发布者用户ID',
    status VARCHAR(20) DEFAULT 'PENDING' COMMENT '状态: PENDING/APPROVED/REJECTED',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB COMMENT='实时旅游动态表';

-- 7. 旅游搭子表 tb_partner
DROP TABLE IF EXISTS tb_partner;
CREATE TABLE tb_partner (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '搭子ID',
    title VARCHAR(200) NOT NULL COMMENT '搭子标题',
    start_city VARCHAR(50) COMMENT '出发城市',
    target_city VARCHAR(50) COMMENT '目的城市',
    departure_date DATE COMMENT '出发日期',
    required_people INT DEFAULT 1 COMMENT '需要人数',
    current_people INT DEFAULT 1 COMMENT '已有人数',
    description TEXT COMMENT '搭子描述',
    publisher_id BIGINT COMMENT '发布者用户ID',
    status VARCHAR(20) DEFAULT 'PENDING' COMMENT '状态: PENDING/APPROVED/REJECTED',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB COMMENT='旅游搭子表';

-- 8. 评论表 tb_comment
DROP TABLE IF EXISTS tb_comment;
CREATE TABLE tb_comment (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '评论ID',
    target_type VARCHAR(50) NOT NULL COMMENT '评论对象类型: strategy/route/travel_notice/partner',
    target_id BIGINT NOT NULL COMMENT '评论对象ID',
    user_id BIGINT NOT NULL COMMENT '评论者用户ID',
    content TEXT NOT NULL COMMENT '评论内容',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB COMMENT='评论表';

-- 9. 审核记录表 tb_admin_record
DROP TABLE IF EXISTS tb_admin_record;
CREATE TABLE tb_admin_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '审核记录ID',
    target_type VARCHAR(50) NOT NULL COMMENT '审核对象类型: strategy/travel_notice/route/partner',
    target_id BIGINT NOT NULL COMMENT '审核对象ID',
    admin_id BIGINT NOT NULL COMMENT '审核人(管理员)ID',
    result VARCHAR(20) NOT NULL COMMENT '审核结果: APPROVED/REJECTED',
    reason VARCHAR(500) COMMENT '审核原因/备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '审核时间'
) ENGINE=InnoDB COMMENT='审核记录表';

-- ============================================================
-- 初始数据
-- ============================================================

-- 管理员账号(密码: admin123, BCrypt加密)
INSERT INTO tb_user (username, password, email, role) VALUES
('admin', '$2a$10$MAVUbf5znJtvUvcpLhBmee0YfYg1FXPV.suaENGrBroAWGuYXdtTe', 'admin@travelshare.com', 'ADMIN'),
('testuser', '$2a$10$MAVUbf5znJtvUvcpLhBmee0YfYg1FXPV.suaENGrBroAWGuYXdtTe', 'test@travelshare.com', 'USER');

-- 城市数据
INSERT INTO tb_city (name, province, description) VALUES
('北京', '北京', '中国首都，历史文化名城'),
('上海', '上海', '中国经济中心，现代化都市'),
('广州', '广东', '广东省省会，美食之都'),
('深圳', '广东', '中国经济特区，创新之城'),
('珠海', '广东', '海滨城市，浪漫之城'),
('成都', '四川', '天府之国，休闲之都'),
('杭州', '浙江', '人间天堂，西湖美景'),
('重庆', '重庆', '山城火锅，8D魔幻城市'),
('西安', '陕西', '千年古都，兵马俑故乡'),
('厦门', '福建', '海上花园，鹭岛风光');

-- 景点数据
INSERT INTO tb_attraction (name, city_id, description, open_time, ticket_price, rating) VALUES
('故宫博物院', 1, '中国明清两代的皇家宫殿，世界上现存规模最大的宫殿型建筑', '08:30-17:00', 60.00, 4.8),
('外滩', 2, '上海的地标性景观，万国建筑博览群', '全天开放', 0.00, 4.6),
('广州塔', 3, '又称小蛮腰，广州地标建筑，登塔可俯瞰广州全景', '09:00-22:00', 150.00, 4.5),
('长隆欢乐世界', 3, '大型主题乐园，集乘骑游乐、特技剧场、巡游表演于一体', '09:30-18:00', 250.00, 4.7),
('越秀公园', 3, '广州最大的综合性公园，五羊雕塑所在地', '06:00-22:00', 0.00, 4.3),
('白云山', 3, '广州名山，被誉为羊城第一秀', '06:00-18:00', 5.00, 4.4),
('世界之窗', 4, '以微缩景观为主的大型主题公园', '09:00-22:30', 180.00, 4.4),
('大梅沙海滨公园', 5, '深圳东部黄金海岸线上的重要景点', '全天开放', 0.00, 4.2),
('珠海长隆海洋王国', 6, '世界级海洋主题乐园', '09:30-21:00', 350.00, 4.8),
('西湖', 7, '中国著名淡水湖，杭州标志性景点', '全天开放', 0.00, 4.9);

-- 示例攻略
INSERT INTO tb_strategy (title, content, city, author_id, status) VALUES
('广州三天两夜自由行攻略', '第一天：上午游览越秀公园看五羊雕塑，下午登广州塔看日落，晚上逛北京路步行街品尝广州小吃。第二天：上午去长隆欢乐世界玩一天，晚上回市区吃潮汕牛肉火锅。第三天：上午白云山登高，下午沙面岛拍照，晚上珠江夜游。', '广州', 2, 'APPROVED'),
('深圳两日游攻略', '第一天：世界之窗一日游，晚上去欢乐海岸。第二天：大梅沙海滨公园，下午华强北逛街购物。', '深圳', 2, 'APPROVED'),
('珠海一日游攻略', '上午长隆海洋王国，下午情侣路骑行，晚上海鲜大餐。', '珠海', 2, 'APPROVED');

-- 示例路线
INSERT INTO tb_route (province, start_city, middle_city, end_city, transport_type, duration, price, description, publisher_id, status) VALUES
('广东', '广州', '深圳', '珠海', '高铁', '约3小时全程', 169.00, '广州南→深圳北35分钟79元 + 深圳北→珠海1小时15分90元', 2, 'APPROVED'),
('广东', '广州', NULL, '深圳', '大巴', '约2小时', 65.00, '广州汽车站→深圳汽车站，每30分钟一班', 2, 'APPROVED'),
('广东', '广州', NULL, '珠海', '高铁', '约1小时', 90.00, '广州南→珠海站直达', 2, 'APPROVED');

-- 示例动态（核心创新模块）
INSERT INTO tb_travel_notice (city_id, attraction_id, city, attraction_name, title, content, category, level, publisher_id, status) VALUES
(3, 5, '广州', '越秀公园', '越秀公园西门暂停开放', '因设备维修，越秀公园西门区域暂停开放，预计三天恢复。请游客从东门或南门进入。', '景区关闭', 'warning', 2, 'APPROVED'),
(3, 6, '广州', '白云山', '白云山索道停运通知', '因例行检修，白云山索道于6月15日-6月17日停运，期间请徒步登山。', '景区关闭', 'danger', 2, 'APPROVED'),
(3, 4, '广州', '长隆欢乐世界', '长隆排队严重警告', '今日长隆欢乐世界游客爆满，热门项目排队超过3小时，建议错峰出行。', '游客拥堵', 'warning', 2, 'APPROVED'),
(4, 7, '深圳', '世界之窗', '世界之窗灯光秀今晚免费开放', '今晚8点世界之窗灯光秀免费开放，欢迎市民游客前往观赏。', '临时活动', 'info', 2, 'APPROVED');

-- 示例搭子
INSERT INTO tb_partner (title, start_city, target_city, departure_date, required_people, current_people, description, publisher_id, status) VALUES
('广东三城游找搭子', '广州', '珠海', '2026-06-20', 3, 1, '6月20日出发，广州-深圳-珠海三城游，高铁出行，费用约500元/人，已有1人，再找2人。', 2, 'APPROVED');

-- 示例评论
INSERT INTO tb_comment (target_type, target_id, user_id, content) VALUES
('strategy', 1, 2, '攻略很实用，按照这个走了一圈，体验很好！'),
('travel_notice', 1, 2, '感谢提醒，差点就去了西门。');

-- ============================================================
-- 完成！接下来可以启动Spring Boot后端和Vue前端

-- ============================================================
-- 增量更新：为攻略表和搭子表添加图片字段
-- ============================================================
ALTER TABLE tb_strategy ADD COLUMN IF NOT EXISTS images VARCHAR(500) COMMENT '图片URL(多张逗号分隔)' AFTER content;
ALTER TABLE tb_partner ADD COLUMN IF NOT EXISTS images VARCHAR(500) COMMENT '图片URL(多张逗号分隔)' AFTER description;

-- ============================================================
-- 增量更新：旅游记账本模块
-- ============================================================

-- 10. 记账房间表 tb_bill_room
DROP TABLE IF EXISTS tb_bill_room;
CREATE TABLE tb_bill_room (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '房间ID',
    room_number VARCHAR(8) NOT NULL UNIQUE COMMENT '房间号(6位数字字母)',
    name VARCHAR(100) NOT NULL COMMENT '房间名称',
    creator_id BIGINT NOT NULL COMMENT '创建者用户ID',
    status VARCHAR(20) DEFAULT 'ACTIVE' COMMENT '状态: ACTIVE/CLOSED',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB COMMENT='记账房间表';

-- 11. 房间成员表 tb_room_member
DROP TABLE IF EXISTS tb_room_member;
CREATE TABLE tb_room_member (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '成员记录ID',
    room_id BIGINT NOT NULL COMMENT '房间ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    role VARCHAR(20) DEFAULT 'MEMBER' COMMENT '角色: CREATOR/MEMBER',
    status VARCHAR(20) DEFAULT 'PENDING' COMMENT '状态: PENDING/APPROVED/REJECTED',
    join_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '加入时间',
    UNIQUE KEY uk_room_user (room_id, user_id)
) ENGINE=InnoDB COMMENT='房间成员表';

-- 12. 账单记录表 tb_bill_record
DROP TABLE IF EXISTS tb_bill_record;
CREATE TABLE tb_bill_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '账单ID',
    room_id BIGINT NOT NULL COMMENT '房间ID',
    payer_id BIGINT NOT NULL COMMENT '付款人用户ID',
    description VARCHAR(200) NOT NULL COMMENT '消费描述',
    amount DECIMAL(10,2) NOT NULL COMMENT '消费金额',
    bill_date DATE NOT NULL COMMENT '消费日期',
    images VARCHAR(500) COMMENT '图片凭证(多张逗号分隔)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB COMMENT='账单记录表';

-- 13. 账单分摊表 tb_bill_split
DROP TABLE IF EXISTS tb_bill_split;
CREATE TABLE tb_bill_split (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '分摊ID',
    bill_id BIGINT NOT NULL COMMENT '账单ID',
    user_id BIGINT NOT NULL COMMENT '参与分摊用户ID',
    UNIQUE KEY uk_bill_user (bill_id, user_id)
) ENGINE=InnoDB COMMENT='账单分摊表';