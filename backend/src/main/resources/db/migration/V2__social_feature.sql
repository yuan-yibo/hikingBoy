-- V2: 社区功能表结构

-- 帖子表
CREATE TABLE IF NOT EXISTS post (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '作者ID',
    title VARCHAR(200) COMMENT '标题',
    content TEXT COMMENT '正文内容',
    images TEXT COMMENT '图片列表（JSON数组）',
    topics VARCHAR(500) COMMENT '话题标签（JSON数组）',
    location VARCHAR(200) COMMENT '位置名称',
    latitude DOUBLE COMMENT '纬度',
    longitude DOUBLE COMMENT '经度',
    track_id BIGINT COMMENT '关联的轨迹ID',
    like_count INT DEFAULT 0 COMMENT '点赞数',
    collect_count INT DEFAULT 0 COMMENT '收藏数',
    comment_count INT DEFAULT 0 COMMENT '评论数',
    view_count INT DEFAULT 0 COMMENT '浏览数',
    status TINYINT DEFAULT 1 COMMENT '状态: 0-已删除 1-正常 2-审核中',
    create_time DATETIME NOT NULL COMMENT '创建时间',
    update_time DATETIME NOT NULL COMMENT '更新时间',
    INDEX idx_user_id (user_id),
    INDEX idx_status (status),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='帖子表';

-- 评论表
CREATE TABLE IF NOT EXISTS comment (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    post_id BIGINT NOT NULL COMMENT '帖子ID',
    user_id BIGINT NOT NULL COMMENT '评论者ID',
    parent_id BIGINT DEFAULT 0 COMMENT '父评论ID（0表示一级评论）',
    reply_to_user_id BIGINT COMMENT '回复的用户ID',
    content VARCHAR(1000) NOT NULL COMMENT '评论内容',
    like_count INT DEFAULT 0 COMMENT '点赞数',
    status TINYINT DEFAULT 1 COMMENT '状态: 0-已删除 1-正常',
    create_time DATETIME NOT NULL COMMENT '创建时间',
    update_time DATETIME NOT NULL COMMENT '更新时间',
    INDEX idx_post_id (post_id),
    INDEX idx_user_id (user_id),
    INDEX idx_parent_id (parent_id),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评论表';

-- 点赞表（支持帖子和评论点赞）
CREATE TABLE IF NOT EXISTS user_like (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    target_id BIGINT NOT NULL COMMENT '目标ID（帖子或评论ID）',
    target_type TINYINT NOT NULL COMMENT '目标类型: 1-帖子 2-评论',
    create_time DATETIME NOT NULL COMMENT '创建时间',
    UNIQUE INDEX idx_user_target (user_id, target_id, target_type),
    INDEX idx_target (target_id, target_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='点赞表';

-- 收藏表
CREATE TABLE IF NOT EXISTS user_collection (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    post_id BIGINT NOT NULL COMMENT '帖子ID',
    create_time DATETIME NOT NULL COMMENT '创建时间',
    UNIQUE INDEX idx_user_post (user_id, post_id),
    INDEX idx_user_id (user_id),
    INDEX idx_post_id (post_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收藏表';

-- 关注表
CREATE TABLE IF NOT EXISTS user_follow (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '关注者ID',
    follow_user_id BIGINT NOT NULL COMMENT '被关注者ID',
    create_time DATETIME NOT NULL COMMENT '关注时间',
    UNIQUE INDEX idx_user_follow (user_id, follow_user_id),
    INDEX idx_user_id (user_id),
    INDEX idx_follow_user_id (follow_user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='关注表';

-- 轨迹记录表
CREATE TABLE IF NOT EXISTS track (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    name VARCHAR(200) COMMENT '轨迹名称',
    start_time DATETIME COMMENT '开始时间',
    end_time DATETIME COMMENT '结束时间',
    distance DOUBLE DEFAULT 0 COMMENT '总距离（米）',
    duration INT DEFAULT 0 COMMENT '总时长（秒）',
    avg_speed DOUBLE COMMENT '平均速度（米/秒）',
    max_altitude DOUBLE COMMENT '最高海拔（米）',
    min_altitude DOUBLE COMMENT '最低海拔（米）',
    points MEDIUMTEXT COMMENT '轨迹点数据（JSON数组）',
    photos TEXT COMMENT '途中拍摄的照片（JSON数组）',
    status TINYINT DEFAULT 1 COMMENT '状态: 0-已删除 1-正常',
    create_time DATETIME NOT NULL COMMENT '创建时间',
    update_time DATETIME NOT NULL COMMENT '更新时间',
    INDEX idx_user_id (user_id),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='轨迹记录表';

-- 消息通知表
CREATE TABLE IF NOT EXISTS notification (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '接收者ID',
    sender_id BIGINT COMMENT '发送者ID（系统消息为空）',
    type VARCHAR(50) NOT NULL COMMENT '消息类型: LIKE/COMMENT/FOLLOW/SYSTEM',
    target_id BIGINT COMMENT '关联目标ID',
    target_type VARCHAR(50) COMMENT '目标类型: POST/COMMENT',
    content VARCHAR(500) COMMENT '消息内容',
    is_read TINYINT DEFAULT 0 COMMENT '是否已读: 0-未读 1-已读',
    create_time DATETIME NOT NULL COMMENT '创建时间',
    INDEX idx_user_id (user_id),
    INDEX idx_is_read (is_read),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='消息通知表';

-- 更新用户表，添加社交字段
ALTER TABLE user ADD COLUMN IF NOT EXISTS bio VARCHAR(500) COMMENT '个人简介';
ALTER TABLE user ADD COLUMN IF NOT EXISTS fans_count INT DEFAULT 0 COMMENT '粉丝数';
ALTER TABLE user ADD COLUMN IF NOT EXISTS following_count INT DEFAULT 0 COMMENT '关注数';
ALTER TABLE user ADD COLUMN IF NOT EXISTS post_count INT DEFAULT 0 COMMENT '帖子数';
