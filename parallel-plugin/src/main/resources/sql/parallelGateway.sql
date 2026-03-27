CREATE TABLE IF NOT EXISTS `ei_node_instance_log_parallel` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `execute_id` TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '执行id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='节点执行记录平行表';

CREATE TABLE IF NOT EXISTS `ei_node_instance_parallel` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `execute_id` TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '执行id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='节点执行平行表';

CREATE TABLE IF NOT EXISTS `ei_node_instance_join_source` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `flow_instance_id` varchar(128) NOT NULL DEFAULT '' COMMENT '流程执行实例id',
  `join_node_instance_id` varchar(128) NOT NULL DEFAULT '' COMMENT '汇聚节点执行实例id',
  `source_node_instance_id` varchar(128) NOT NULL DEFAULT '' COMMENT '来源节点执行实例id',
  `source_node_key` varchar(64) NOT NULL DEFAULT '' COMMENT '来源节点唯一标识',
  `create_time` datetime NOT NULL DEFAULT '1970-01-01 00:00:00' COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_join_node_instance_id` (`join_node_instance_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='并行网关汇聚节点来源关联表';