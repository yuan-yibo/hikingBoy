-- V1: 初始化数据库表结构

-- 用户表
CREATE TABLE IF NOT EXISTS user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    open_id VARCHAR(128) NOT NULL UNIQUE COMMENT '微信OpenID',
    nickname VARCHAR(100) COMMENT '用户昵称',
    avatar VARCHAR(500) COMMENT '用户头像',
    create_time DATETIME NOT NULL COMMENT '创建时间',
    update_time DATETIME NOT NULL COMMENT '更新时间',
    INDEX idx_open_id (open_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 团队表
CREATE TABLE IF NOT EXISTS team (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL COMMENT '团队名称',
    description VARCHAR(500) COMMENT '团队描述',
    invite_code VARCHAR(20) NOT NULL UNIQUE COMMENT '邀请码',
    owner_id BIGINT NOT NULL COMMENT '创建者ID',
    create_time DATETIME NOT NULL COMMENT '创建时间',
    update_time DATETIME NOT NULL COMMENT '更新时间',
    INDEX idx_owner_id (owner_id),
    INDEX idx_invite_code (invite_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='团队表';

-- 团队成员表
CREATE TABLE IF NOT EXISTS team_member (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    team_id BIGINT NOT NULL COMMENT '团队ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    role VARCHAR(20) NOT NULL COMMENT '角色: OWNER/ADMIN/MEMBER',
    status VARCHAR(20) NOT NULL COMMENT '状态: PENDING/APPROVED/REJECTED',
    join_time DATETIME COMMENT '加入时间',
    create_time DATETIME NOT NULL COMMENT '创建时间',
    update_time DATETIME NOT NULL COMMENT '更新时间',
    INDEX idx_team_id (team_id),
    INDEX idx_user_id (user_id),
    UNIQUE INDEX idx_team_user (team_id, user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='团队成员表';

-- 徒步记录表
CREATE TABLE IF NOT EXISTS hiking_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id VARCHAR(128) NOT NULL COMMENT '用户ID（微信openid）',
    owner_id BIGINT COMMENT '所有者ID（关联user表）',
    team_id BIGINT COMMENT '团队ID（可选）',
    mountain_name VARCHAR(255) NOT NULL COMMENT '山名',
    hiking_date DATE NOT NULL COMMENT '徒步日期',
    distance DOUBLE COMMENT '距离（公里）',
    duration VARCHAR(50) COMMENT '时长',
    weather_type VARCHAR(50) COMMENT '天气类型',
    weather_icon VARCHAR(50) COMMENT '天气图标',
    photos TEXT COMMENT '照片列表（JSON格式）',
    notes TEXT COMMENT '笔记',
    create_time DATETIME NOT NULL COMMENT '创建时间',
    update_time DATETIME NOT NULL COMMENT '更新时间',
    INDEX idx_user_id (user_id),
    INDEX idx_owner_id (owner_id),
    INDEX idx_team_id (team_id),
    INDEX idx_hiking_date (hiking_date),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='徒步记录表';
