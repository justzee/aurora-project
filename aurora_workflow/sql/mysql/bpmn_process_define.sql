CREATE TABLE `bpmn_process_define` (
  `define_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `process_code` varchar(45) NOT NULL COMMENT '工作流代码',
  `process_version` varchar(45) NOT NULL COMMENT '版本号',
  `description` varchar(1000) DEFAULT NULL COMMENT '说明',
  `current_version_flag` varchar(1) DEFAULT NULL COMMENT '是否当前版本',
  `defines` longtext COMMENT 'XML形式存储的BPMN配置',
  `name` varchar(100) DEFAULT NULL COMMENT '流程名称',
  `approve_status` varchar(45) NOT NULL COMMENT '审批标记,未审批:NONE,审批中:APPROVING,审批通过:APPROVED',
  `valid_flag` varchar(1) NOT NULL COMMENT '有效标记.Y,N',
  `category_id` bigint(20) NOT NULL COMMENT '类别ID',
  PRIMARY KEY (`define_id`),
  UNIQUE KEY `bpmn_process_define_u1` (`process_version`,`process_code`),
  KEY `bpmn_process_define_n1` (`category_id`),
  KEY `bpmn_process_define_n2` (`process_code`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=gb2312;
