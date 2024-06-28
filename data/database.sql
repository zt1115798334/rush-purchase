drop database if exists rush_purchase;
create schema rush_purchase;

use rush_purchase;

drop table if exists t_user;
create table t_user
(
    id              bigint auto_increment
        primary key,
    account         varchar(20)                                           not null comment '账户',
    password        varchar(255)                                          not null comment '密码',
    salt            varchar(255)                                          not null comment '盐值',
    user_name       varchar(20)                                           null comment '用户名',
    phone           varchar(20)                                           not null comment '手机号',
    created_time    datetime                    default CURRENT_TIMESTAMP not null comment '创建时间',
    updated_time    datetime                                              null comment '更新时间',
    last_login_time datetime                                              null comment '最后登录时间',
    delete_state    enum ('UN_DELETE','DELETE') default 'UN_DELETE'       not null comment '删除状态'
) comment '用户表';

INSERT INTO t_user (id, account, password, salt, user_name, phone,
                    created_time, updated_time, last_login_time, delete_state)
VALUES (1, 'admin', 'd75646ba3433331fbf8b4fb3b11066e9d313bb138de904b49ac294e695553516', 'P275Y2MY28',
        'test', '15130097582', '2021-09-13 17:32:25', null, null, 'UN_DELETE');

drop table if exists t_user_log;
create table t_user_log
(
    id       bigint auto_increment
        primary key,
    user_id  bigint                             null comment '用户id',
    name     varchar(255)                       null comment '名字',
    type     varchar(255)                       null comment '类型',
    content  text                               null comment '内容',
    ip       varchar(20)                        null comment 'ip',
    time     datetime default CURRENT_TIMESTAMP null comment '时间',
    classify varchar(255)                       null comment '类路径',
    fun      varchar(255)                       null comment '方法',
    response text                               null comment '返回值'
) comment '用户日志表';

