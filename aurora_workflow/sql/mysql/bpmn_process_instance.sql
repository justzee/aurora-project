CREATE TABLE `bpmn_process_instance` (
  `instance_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `status` varchar(45) NOT NULL COMMENT '当前状态',
  `parent_id` bigint(20) DEFAULT NULL COMMENT '父流程ID',
  `process_code` varchar(45) DEFAULT NULL COMMENT '所属工作流代码',
  `process_version` varchar(45) DEFAULT NULL COMMENT '所属工作流版本',
  `description` varchar(1000) DEFAULT NULL COMMENT '流程创建是的一段描述',
  `instance_param` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`instance_id`),
  KEY `bpmn_process_instance_n1` (`parent_id`)
) ENGINE=InnoDB AUTO_INCREMENT=28 DEFAULT CHARSET=gb2312;
