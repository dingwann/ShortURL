-- 创建数据库
create database if not exists short;

-- 切换数据库
use short;

-- 用户表
create table if not exists user
(
    id              bigint auto_increment comment 'id' primary key,
    userAccount     varchar(256)                           not null comment '账号',
    userPassword    varchar(512)                           not null comment '密码',
    userName        varchar(256)                           null comment '用户昵称',
    userAvatar      varchar(1024)                          null comment '用户头像',
    userProfile     varchar(512)                           null comment '用户简介',
    userRole        varchar(256) default 'user'            not null comment '用户角色：user/admin',
    preferences     varchar(512)                           null comment '用户偏好标签',
    editTime        datetime     default CURRENT_TIMESTAMP not null comment '编辑时间',
    createTime      datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime      datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    logicDelete     tinyint      default 0                 not null comment '是否删除(逻辑)',
    UNIQUE KEY uk_userAccount (userAccount),
    INDEX idx_userName (userName)
) comment '用户' collate = utf8mb4_unicode_ci;

-- 短链接表
create table if not exists short_link
(
    id              bigint auto_increment comment 'id' primary key,
    userId          bigint                                not null comment '创建用户id',
    originalUrl     varchar(2048)                         not null comment '原始链接',
    shortCode       varchar(16)                           not null comment '短链接码',
    customCode      tinyint      default 0                not null comment '是否自定义短码(0-否, 1-是)',
    expireTime      datetime                              null comment '过期时间(null表示永不过期)',
    description     varchar(512)                          null comment '链接描述',
    clickCount      bigint       default 0                not null comment '点击次数',
    createTime      datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime      datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    logicDelete     tinyint      default 0                not null comment '是否删除(逻辑)',
    UNIQUE KEY uk_shortCode (shortCode),
    INDEX idx_userId (userId)
) comment '短链接' collate = utf8mb4_unicode_ci;

-- 短链接访问记录表
create table if not exists short_link_access
(
    id              bigint auto_increment comment 'id' primary key,
    linkId          bigint                                not null comment '短链接id',
    ip              varchar(128)                          null comment '访问者IP',
    userAgent       varchar(512)                          null comment '用户代理',
    referer         varchar(1024)                         null comment '来源页面',
    accessTime      datetime     default CURRENT_TIMESTAMP not null comment '访问时间',
    INDEX idx_linkId (linkId),
    INDEX idx_accessTime (accessTime)
) comment '短链接访问记录' collate = utf8mb4_unicode_ci;
