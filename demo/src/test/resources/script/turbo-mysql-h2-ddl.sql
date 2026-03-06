DROP TABLE IF EXISTS `em_flow_definition`;
CREATE TABLE IF NOT EXISTS `em_flow_definition` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `flow_module_id` varchar(128) NOT NULL DEFAULT '',
  `flow_name` varchar(64) NOT NULL DEFAULT '',
  `flow_key` varchar(32) NOT NULL DEFAULT '',
  `tenant_id` varchar(16) NOT NULL DEFAULT '',
  `flow_model` mediumtext,
  `status` tinyint NOT NULL DEFAULT 0,
  `create_time` datetime NOT NULL DEFAULT '1970-01-01 00:00:00',
  `modify_time` datetime NOT NULL DEFAULT '1970-01-01 00:00:00',
  `operator` varchar(32) NOT NULL DEFAULT '',
  `remark` varchar(512) NOT NULL DEFAULT '',
  `archive` tinyint NOT NULL DEFAULT 0,
  `tenant` varchar(100) NOT NULL DEFAULT '',
  `caller` varchar(100) NOT NULL DEFAULT '',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniq_flow_module_id` (`flow_module_id`)
);

DROP TABLE IF EXISTS `em_flow_deployment`;
CREATE TABLE IF NOT EXISTS `em_flow_deployment` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `flow_deploy_id` varchar(128) NOT NULL DEFAULT '',
  `flow_module_id` varchar(128) NOT NULL DEFAULT '',
  `flow_name` varchar(64) NOT NULL DEFAULT '',
  `flow_key` varchar(32) NOT NULL DEFAULT '',
  `tenant_id` varchar(16) NOT NULL DEFAULT '',
  `flow_model` mediumtext,
  `status` tinyint NOT NULL DEFAULT 0,
  `create_time` datetime NOT NULL DEFAULT '1970-01-01 00:00:00',
  `modify_time` datetime NOT NULL DEFAULT '1970-01-01 00:00:00',
  `operator` varchar(32) NOT NULL DEFAULT '',
  `remark` varchar(512) NOT NULL DEFAULT '',
  `archive` tinyint NOT NULL DEFAULT 0,
  `tenant` varchar(100) NOT NULL DEFAULT '',
  `caller` varchar(100) NOT NULL DEFAULT '',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniq_flow_deploy_id` (`flow_deploy_id`),
  KEY `idx_flow_module_id` (`flow_module_id`)
);

DROP TABLE IF EXISTS `ei_flow_instance`;
CREATE TABLE IF NOT EXISTS `ei_flow_instance` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `flow_instance_id` varchar(128) NOT NULL DEFAULT '',
  `parent_flow_instance_id` varchar(128) NOT NULL DEFAULT '',
  `flow_deploy_id` varchar(128) NOT NULL DEFAULT '',
  `flow_module_id` varchar(128) NOT NULL DEFAULT '',
  `tenant_id` varchar(16) NOT NULL DEFAULT '',
  `status` tinyint NOT NULL DEFAULT 0,
  `create_time` datetime NOT NULL DEFAULT '1970-01-01 00:00:00',
  `modify_time` datetime NOT NULL DEFAULT '1970-01-01 00:00:00',
  `archive` tinyint NOT NULL DEFAULT 0,
  `tenant` varchar(100) NOT NULL DEFAULT '',
  `caller` varchar(100) NOT NULL DEFAULT '',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniq_flow_instance_id` (`flow_instance_id`)
);

DROP TABLE IF EXISTS `ei_flow_instance_mapping`;
CREATE TABLE IF NOT EXISTS `ei_flow_instance_mapping` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `flow_instance_id` varchar(128) NOT NULL DEFAULT '',
  `node_instance_id` varchar(128) NOT NULL DEFAULT '',
  `node_key` varchar(64) NOT NULL DEFAULT '',
  `sub_flow_instance_id` varchar(128) NOT NULL DEFAULT '',
  `type` tinyint NOT NULL DEFAULT 0,
  `create_time` datetime NOT NULL DEFAULT '1970-01-01 00:00:00',
  `modify_time` datetime NOT NULL DEFAULT '1970-01-01 00:00:00',
  `archive` tinyint NOT NULL DEFAULT 0,
  `tenant` varchar(100) NOT NULL DEFAULT 'didi',
  `caller` varchar(100) NOT NULL DEFAULT 'optimus-prime',
  PRIMARY KEY (`id`),
  KEY `idx_fii` (`flow_instance_id`)
);

DROP TABLE IF EXISTS `ei_node_instance`;
CREATE TABLE IF NOT EXISTS `ei_node_instance` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `node_instance_id` varchar(128) NOT NULL DEFAULT '',
  `flow_instance_id` varchar(128) NOT NULL DEFAULT '',
  `source_node_instance_id` varchar(128) NOT NULL DEFAULT '',
  `instance_data_id` varchar(128) NOT NULL DEFAULT '',
  `flow_deploy_id` varchar(128) NOT NULL DEFAULT '',
  `node_key` varchar(64) NOT NULL DEFAULT '',
  `node_type` int NOT NULL DEFAULT 0,
  `source_node_key` varchar(64) NOT NULL DEFAULT '',
  `tenant_id` varchar(16) NOT NULL DEFAULT '',
  `status` tinyint NOT NULL DEFAULT 0,
  `create_time` datetime NOT NULL DEFAULT '1970-01-01 00:00:00',
  `modify_time` datetime NOT NULL DEFAULT '1970-01-01 00:00:00',
  `archive` tinyint NOT NULL DEFAULT 0,
  `tenant` varchar(100) NOT NULL DEFAULT '',
  `caller` varchar(100) NOT NULL DEFAULT '',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniq_node_instance_id` (`node_instance_id`),
  KEY `idx_fiid_sniid_nk` (`flow_instance_id`,`source_node_instance_id`,`node_key`)
);

DROP TABLE IF EXISTS `ei_node_instance_log`;
CREATE TABLE IF NOT EXISTS `ei_node_instance_log` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `node_instance_id` varchar(128) NOT NULL DEFAULT '',
  `flow_instance_id` varchar(128) NOT NULL DEFAULT '',
  `instance_data_id` varchar(128) NOT NULL DEFAULT '',
  `node_key` varchar(64) NOT NULL DEFAULT '',
  `tenant_id` varchar(16) NOT NULL DEFAULT '',
  `type` tinyint NOT NULL DEFAULT 0,
  `status` tinyint NOT NULL DEFAULT 0,
  `create_time` datetime NOT NULL DEFAULT '1970-01-01 00:00:00',
  `archive` tinyint NOT NULL DEFAULT 0,
  `tenant` varchar(100) NOT NULL DEFAULT '',
  `caller` varchar(100) NOT NULL DEFAULT '',
  PRIMARY KEY (`id`)
);

DROP TABLE IF EXISTS `ei_instance_data`;
CREATE TABLE IF NOT EXISTS `ei_instance_data` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `node_instance_id` varchar(128) NOT NULL DEFAULT '',
  `flow_instance_id` varchar(128) NOT NULL DEFAULT '',
  `instance_data_id` varchar(128) NOT NULL DEFAULT '',
  `flow_deploy_id` varchar(128) NOT NULL DEFAULT '',
  `flow_module_id` varchar(128) NOT NULL DEFAULT '',
  `node_key` varchar(64) NOT NULL DEFAULT '',
  `tenant_id` varchar(16) NOT NULL DEFAULT '',
  `instance_data` longtext,
  `type` tinyint NOT NULL DEFAULT 0,
  `create_time` datetime NOT NULL DEFAULT '1970-01-01 00:00:00',
  `archive` tinyint NOT NULL DEFAULT 0,
  `tenant` varchar(100) NOT NULL DEFAULT '',
  `caller` varchar(100) NOT NULL DEFAULT '',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniq_instance_data_id` (`instance_data_id`),
  KEY `idx_flow_instance_id` (`flow_instance_id`)
);

DROP TABLE IF EXISTS `ei_node_instance_parallel`;
CREATE TABLE IF NOT EXISTS `ei_node_instance_parallel` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `execute_id` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
);

DROP TABLE IF EXISTS `ei_node_instance_log_parallel`;
CREATE TABLE IF NOT EXISTS `ei_node_instance_log_parallel` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `execute_id` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
);
