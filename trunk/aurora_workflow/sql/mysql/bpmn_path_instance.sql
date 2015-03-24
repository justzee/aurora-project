CREATE TABLE `bpmn_path_instance` (
  `path_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `instance_id` bigint(20) NOT NULL COMMENT '所属工作流实例',
  `status` varchar(45) NOT NULL COMMENT '当前状态',
  `prev_node` varchar(45) DEFAULT NULL COMMENT '上一步来自节点ID',
  `current_node` varchar(45) DEFAULT NULL COMMENT '当前所在节点ID',
  `node_id` varchar(45) NOT NULL COMMENT 'sequence flow id',
  `created_by` bigint(20) DEFAULT NULL,
  `creation_date` datetime NULL DEFAULT NULL,
  `last_updated_by` bigint(20) DEFAULT NULL,
  `last_update_date` datetime NULL DEFAULT NULL,
  PRIMARY KEY (`path_id`),
  KEY `bpmn_path_instance_n1` (`instance_id`)
) ENGINE=InnoDB AUTO_INCREMENT=119 DEFAULT CHARSET=gb2312;
