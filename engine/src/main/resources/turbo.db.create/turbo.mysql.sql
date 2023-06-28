DROP TABLE IF EXISTS `em_flow_definition`;
CREATE TABLE IF NOT EXISTS `em_flow_definition`
(
    `id`             bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增主键',
    `flow_module_id` varchar(128)        NOT NULL DEFAULT '' COMMENT '流程模型id',
    `flow_name`      varchar(64)         NOT NULL DEFAULT '' COMMENT '流程名称',
    `flow_key`       varchar(32)         NOT NULL DEFAULT '' COMMENT '流程业务标识',
    `tenant_id`      varchar(16)         NOT NULL DEFAULT '' COMMENT '业务方标识',
    `flow_model`     mediumtext COMMENT '表单定义',
    `status`         tinyint(4)          NOT NULL DEFAULT '0' COMMENT '状态(1.初始态 2.编辑中 3.已下线)',
    `create_time`    datetime            NOT NULL DEFAULT '1970-01-01 00:00:00' COMMENT '流程创建时间',
    `modify_time`    datetime            NOT NULL DEFAULT '1970-01-01 00:00:00' COMMENT '流程修改时间',
    `operator`       varchar(32)         NOT NULL DEFAULT '' COMMENT '操作人',
    `remark`         varchar(512)        NOT NULL DEFAULT '' COMMENT '备注',
    `archive`        tinyint(4)          NOT NULL DEFAULT '0' COMMENT '归档状态(0未删除，1删除)',
    `tenant`         varchar(100)        NOT NULL DEFAULT '' COMMENT '租户',
    `caller`         varchar(100)        NOT NULL DEFAULT '' COMMENT '调用方',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uniq_flow_module_id` (`flow_module_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  ROW_FORMAT = COMPACT COMMENT ='流程定义表';

DROP TABLE IF EXISTS `em_flow_deployment`;
CREATE TABLE IF NOT EXISTS `em_flow_deployment`
(
    `id`             bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增主键',
    `flow_deploy_id` varchar(128)        NOT NULL DEFAULT '' COMMENT '流程模型部署id',
    `flow_module_id` varchar(128)        NOT NULL DEFAULT '' COMMENT '流程模型id',
    `flow_name`      varchar(64)         NOT NULL DEFAULT '' COMMENT '流程名称',
    `flow_key`       varchar(32)         NOT NULL DEFAULT '' COMMENT '流程业务标识',
    `tenant_id`      varchar(16)         NOT NULL DEFAULT '' COMMENT '业务方标识',
    `flow_model`     mediumtext COMMENT '表单定义',
    `status`         tinyint(4)          NOT NULL DEFAULT '0' COMMENT '状态(1.已部署 3.已下线)',
    `create_time`    datetime            NOT NULL DEFAULT '1970-01-01 00:00:00' COMMENT '流程创建时间',
    `modify_time`    datetime            NOT NULL DEFAULT '1970-01-01 00:00:00' COMMENT '流程修改时间',
    `operator`       varchar(32)         NOT NULL DEFAULT '' COMMENT '操作人',
    `remark`         varchar(512)        NOT NULL DEFAULT '' COMMENT '备注',
    `archive`        tinyint(4)          NOT NULL DEFAULT '0' COMMENT '归档状态(0未删除，1删除)',
    `tenant`         varchar(100)        NOT NULL DEFAULT '' COMMENT '租户',
    `caller`         varchar(100)        NOT NULL DEFAULT '' COMMENT '调用方',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uniq_flow_deploy_id` (`flow_deploy_id`),
    KEY `idx_flow_module_id` (`flow_module_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  ROW_FORMAT = COMPACT COMMENT ='流程部署表';

DROP TABLE IF EXISTS `ei_flow_instance`;
CREATE TABLE IF NOT EXISTS `ei_flow_instance`
(
    `id`                      bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增主键',
    `flow_instance_id`        varchar(128)        NOT NULL DEFAULT '' COMMENT '流程执行实例id',
    `parent_flow_instance_id` varchar(128)        NOT NULL DEFAULT '' COMMENT '父流程执行实例id',
    `flow_deploy_id`          varchar(128)        NOT NULL DEFAULT '' COMMENT '流程模型部署id',
    `flow_module_id`          varchar(128)        NOT NULL DEFAULT '' COMMENT '流程模型id',
    `tenant_id`               varchar(16)         NOT NULL DEFAULT '' COMMENT '业务方标识',
    `status`                  tinyint(4)          NOT NULL DEFAULT '0' COMMENT '状态(1.执行完成 2.执行中 3.执行终止(强制终止))',
    `create_time`             datetime            NOT NULL DEFAULT '1970-01-01 00:00:00' COMMENT '流程创建时间',
    `modify_time`             datetime            NOT NULL DEFAULT '1970-01-01 00:00:00' COMMENT '流程修改时间',
    `archive`                 tinyint(4)          NOT NULL DEFAULT '0' COMMENT '归档状态(0未删除，1删除)',
    `tenant`                  varchar(100)        NOT NULL DEFAULT '' COMMENT '租户',
    `caller`                  varchar(100)        NOT NULL DEFAULT '' COMMENT '调用方',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uniq_flow_instance_id` (`flow_instance_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  ROW_FORMAT = COMPACT COMMENT ='流程执行实例表';

DROP TABLE IF EXISTS `ei_flow_instance_mapping`;
CREATE TABLE `ei_flow_instance_mapping`
(
    `id`                   bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增主键',
    `flow_instance_id`     varchar(128)        NOT NULL DEFAULT '' COMMENT '流程执行实例id',
    `node_instance_id`     varchar(128)        NOT NULL DEFAULT '' COMMENT '节点执行实例id',
    `node_key`             varchar(64)         NOT NULL DEFAULT '' COMMENT '节点唯一标识',
    `sub_flow_instance_id` varchar(128)        NOT NULL DEFAULT '' COMMENT '子流程执行实例id',
    `type`                 tinyint(4)          NOT NULL DEFAULT '0' COMMENT '状态(1.执行 2.回滚)',
    `create_time`          datetime            NOT NULL DEFAULT '1970-01-01 00:00:00' COMMENT '流程创建时间',
    `modify_time`          datetime            NOT NULL DEFAULT '1970-01-01 00:00:00' COMMENT '流程修改时间',
    `archive`              tinyint(4)          NOT NULL DEFAULT '0' COMMENT '归档状态(0未删除，1删除)',
    `tenant`               varchar(100)        NOT NULL DEFAULT 'didi' COMMENT '租户',
    `caller`               varchar(100)        NOT NULL DEFAULT 'optimus-prime' COMMENT '调用方',
    PRIMARY KEY (`id`),
    KEY `idx_fii` (`flow_instance_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  ROW_FORMAT = COMPACT COMMENT ='父子流程实例映射表';

DROP TABLE IF EXISTS `ei_node_instance`;
CREATE TABLE IF NOT EXISTS `ei_node_instance`
(
    `id`                      bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增主键',
    `node_instance_id`        varchar(128)        NOT NULL DEFAULT '' COMMENT '节点执行实例id',
    `flow_instance_id`        varchar(128)        NOT NULL DEFAULT '' COMMENT '流程执行实例id',
    `source_node_instance_id` varchar(128)        NOT NULL DEFAULT '' COMMENT '上一个节点执行实例id',
    `instance_data_id`        varchar(128)        NOT NULL DEFAULT '' COMMENT '实例数据id',
    `flow_deploy_id`          varchar(128)        NOT NULL DEFAULT '' COMMENT '流程模型部署id',
    `node_key`                varchar(64)         NOT NULL DEFAULT '' COMMENT '节点唯一标识',
    `source_node_key`         varchar(64)         NOT NULL DEFAULT '' COMMENT '上一个流程节点唯一标识',
    `tenant_id`               varchar(16)         NOT NULL DEFAULT '' COMMENT '业务方标识',
    `status`                  tinyint(4)          NOT NULL DEFAULT '0' COMMENT '状态(1.处理成功 2.处理中 3.处理失败 4.处理已撤销)',
    `create_time`             datetime            NOT NULL DEFAULT '1970-01-01 00:00:00' COMMENT '流程创建时间',
    `modify_time`             datetime            NOT NULL DEFAULT '1970-01-01 00:00:00' COMMENT '流程修改时间',
    `archive`                 tinyint(4)          NOT NULL DEFAULT '0' COMMENT '归档状态(0未删除，1删除)',
    `tenant`                  varchar(100)        NOT NULL DEFAULT '' COMMENT '租户',
    `caller`                  varchar(100)        NOT NULL DEFAULT '' COMMENT '调用方',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uniq_node_instance_id` (`node_instance_id`),
    KEY `idx_fiid_sniid_nk` (`flow_instance_id`, `source_node_instance_id`, `node_key`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  ROW_FORMAT = COMPACT COMMENT ='节点执行实例表';

DROP TABLE IF EXISTS `ei_node_instance_log`;
CREATE TABLE IF NOT EXISTS `ei_node_instance_log`
(
    `id`               bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增主键',
    `node_instance_id` varchar(128)        NOT NULL DEFAULT '' COMMENT '节点执行实例id',
    `flow_instance_id` varchar(128)        NOT NULL DEFAULT '' COMMENT '流程执行实例id',
    `instance_data_id` varchar(128)        NOT NULL DEFAULT '' COMMENT '实例数据id',
    `node_key`         varchar(64)         NOT NULL DEFAULT '' COMMENT '节点唯一标识',
    `tenant_id`        varchar(16)         NOT NULL DEFAULT '' COMMENT '业务方标识',
    `type`             tinyint(4)          NOT NULL DEFAULT '0' COMMENT '操作类型(1.系统执行 2.任务提交 3.任务撤销)',
    `status`           tinyint(4)          NOT NULL DEFAULT '0' COMMENT '状态(1.处理成功 2.处理中 3.处理失败 4.处理已撤销)',
    `create_time`      datetime            NOT NULL DEFAULT '1970-01-01 00:00:00' COMMENT '流程创建时间',
    `archive`          tinyint(4)          NOT NULL DEFAULT '0' COMMENT '归档状态(0未删除，1删除)',
    `tenant`           varchar(100)        NOT NULL DEFAULT '' COMMENT '租户',
    `caller`           varchar(100)        NOT NULL DEFAULT '' COMMENT '调用方',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  ROW_FORMAT = COMPACT COMMENT ='节点执行记录表';

DROP TABLE IF EXISTS `ei_instance_data`;
CREATE TABLE IF NOT EXISTS `ei_instance_data`
(
    `id`               bigint(20) unsigned                     NOT NULL AUTO_INCREMENT COMMENT '自增主键',
    `node_instance_id` varchar(128) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '节点执行实例id',
    `flow_instance_id` varchar(128) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '流程执行实例id',
    `instance_data_id` varchar(128) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '实例数据id',
    `flow_deploy_id`   varchar(128) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '流程模型部署id',
    `flow_module_id`   varchar(128) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '流程模型id',
    `node_key`         varchar(64) COLLATE utf8mb4_unicode_ci  NOT NULL DEFAULT '' COMMENT '节点唯一标识',
    `tenant_id`        varchar(16) COLLATE utf8mb4_unicode_ci  NOT NULL DEFAULT '' COMMENT '业务方标识',
    `instance_data`    longtext COLLATE utf8mb4_unicode_ci COMMENT '数据列表json',
    `type`             tinyint(4)                              NOT NULL DEFAULT '0' COMMENT '操作类型(1.实例初始化 2.系统执行 3.系统主动获取 4.上游更新 5.任务提交 6.任务撤回)',
    `create_time`      datetime                                NOT NULL DEFAULT '1970-01-01 00:00:00' COMMENT '流程创建时间',
    `archive`          tinyint(4)                              NOT NULL DEFAULT '0' COMMENT '归档状态(0未删除，1删除)',
    `tenant`           varchar(100) CHARACTER SET utf8         NOT NULL DEFAULT '' COMMENT '租户',
    `caller`           varchar(100) CHARACTER SET utf8         NOT NULL DEFAULT '' COMMENT '调用方',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uniq_instance_data_id` (`instance_data_id`),
    KEY `idx_flow_instance_id` (`flow_instance_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci
  ROW_FORMAT = COMPACT COMMENT ='实例数据表';
