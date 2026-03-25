-- 用户库

DROP DATABASE fluxsync_channel;
DROP DATABASE fluxsync_collab;
DROP DATABASE fluxsync_minio;
DROP DATABASE fluxsync_user;

CREATE DATABASE fluxsync_user;

USE fluxsync_user;

CREATE TABLE users
(
    uid         INT          NOT NULL AUTO_INCREMENT COMMENT '用户唯一标识符',
    username    VARCHAR(255) NOT NULL COMMENT '用户名',
    nickname    VARCHAR(255) COMMENT '用户名称',
    password    CHAR(32)     NOT NULL COMMENT '用户密码',
    regtime     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
    role        ENUM('admin', 'normal')  NOT NULL DEFAULT 'normal' COMMENT '用户级别',
    description TEXT COMMENT '用户简介',
    PRIMARY KEY (uid),
    UNIQUE KEY uk_user_username (username)
) ENGINE=InnoDB AUTO_INCREMENT=10001 DEFAULT CHARSET=utf8mb4 COMMENT='用户信息表';

CREATE TABLE avatar
(
    username VARCHAR(255) NOT NULL COMMENT '用户名',
    avatar   varchar(255) not null default 'cat',
    PRIMARY KEY (username),
    CONSTRAINT fk_avatar_user
        FOREIGN KEY (username) REFERENCES users (username)
            ON DELETE CASCADE
            ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='头像表';

CREATE TABLE totp_info
(
    username    VARCHAR(255) NOT NULL COMMENT '用户名',
    totp_secret VARCHAR(32)  NOT NULL COMMENT 'totp密钥',
    totp_enable BOOLEAN DEFAULT FALSE COMMENT '是否开启totp校验',
    PRIMARY KEY (username),
    FOREIGN KEY (username) REFERENCES users (username)
        ON DELETE CASCADE
        ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='totp信息表';

-- MinIO 资源库
CREATE DATABASE fluxsync_minio;
USE fluxsync_minio;

create table drive
(
    uid        int          not null comment '用户唯一标识符',
    uploadtime timestamp    not null default CURRENT_TIMESTAMP comment '上传时间',
    updatetime timestamp    not null default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP comment '最近一次更新时间',
    filename   varchar(255) not null comment '文件名',
    sizeBytes  bigint unsigned not null comment '文件大小(字节)',
    path       varchar(255) not null comment '文件定位符',

    key        idx_uid (uid),
    key        idx_path (path),
    primary key (uid, path),
    constraint fk_user_drive
        foreign key (uid) references fluxsync_user.users (uid)
            ON DELETE CASCADE
            ON UPDATE CASCADE
) engine=InnoDB default charset=utf8mb4 comment='用户云盘资源索引';

create table driveinfo
(
    uid int not null comment '用户唯一标识符',
    plan enum('free', 'plus', 'pro') default 'free' comment '用户订阅套餐',
    usedBytes bigint unsigned not null default 0 comment '已用空间(字节)',
    limitBytes bigint unsigned not null default 5368709120 comment '空间上限(初始 5 GiB)',
    primary key (uid),
    foreign key (uid) references fluxsync_user.users (uid)
        on delete cascade on update cascade
) engine=InnoDB default charset=utf8mb4 comment='用户云盘状态';

-- 协作层资源库
create database fluxsync_collab;
use fluxsync_collab;

create table user_doc
(
    uid        int          not null comment '用户唯一标识符',
    createtime timestamp    not null default CURRENT_TIMESTAMP comment '创建时间',
    updatetime timestamp    not null default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP comment '最近一次更新时间',
    docname    varchar(255) not null default '未命名文档' comment '文档名称',
    data       json         not null comment '结构化文档内容',

    key        idx_uid (uid),
    primary key (uid, docname),
    constraint fk_user_doc
        foreign key (uid) references fluxsync_user.users (uid)
            on delete cascade on update cascade
) engine=InnoDB default charset=utf8mb4 comment='用户文档库';

-- 频道库
create database fluxsync_channel;
use fluxsync_channel;

create table channel
(

    cid         varchar(10)  not null comment '频道唯一标识符',
    name        varchar(255) not null comment '频道名',
    description text comment '频道描述',
    createtime  timestamp    not null default CURRENT_TIMESTAMP comment '创建时间',

    primary key (cid)

) engine=InnoDB default charset=utf8mb4 comment='频道信息表';

create table channel_user
(

    uid      int         not null comment '用户唯一标识符',
    cid      varchar(10) not null comment '频道唯一标识符',
    jointime timestamp   not null default CURRENT_TIMESTAMP comment '加入时间',
    role     enum('creator', 'admin', 'normal') not null default 'normal' comment '用户角色',

    primary key (uid, cid),
    constraint fk_channel_user_users
        foreign key (uid) references fluxsync_user.users (uid)
            on delete cascade on update cascade,
    constraint fk_channel_user_channel
        foreign key (cid) references fluxsync_channel.channel (cid)
            on delete cascade on update cascade

) engine=InnoDB default charset=utf8mb4 comment='频道用户集合';

create table message
(
    cid     varchar(10) not null comment '频道唯一标识符',
    payload text        not null comment '消息协议载荷',
    seq     BIGINT      not null comment '消息序列号',

    primary key (cid, seq)

) engine=InnoDB default charset=utf8mb4 comment='消息缓存表';