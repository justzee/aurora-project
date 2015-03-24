CREATE TABLE `bpmn_usertask_pages` (
  `page_id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `description_id` bigint(20) DEFAULT NULL COMMENT '描述ID(多语言字段)',
  `page_name` varchar(200) NOT NULL COMMENT '页面路径',
  `created_by` bigint(20) DEFAULT NULL,
  `creation_date` datetime DEFAULT NULL,
  `last_updated_by` bigint(20) DEFAULT NULL,
  `last_update_date` datetime DEFAULT NULL,
  PRIMARY KEY (`page_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='节点页面定义';
