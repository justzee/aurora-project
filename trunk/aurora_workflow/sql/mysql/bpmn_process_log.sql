CREATE TABLE `bpmn_process_log` (
  `log_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `instance_id` bigint(20) NOT NULL COMMENT '所属工作流ID',
  `log_date` datetime DEFAULT NULL COMMENT '创建时间',
  `user_id` varchar(45) DEFAULT NULL COMMENT '触发该日志记录的用户ID',
  `event_type` varchar(45) DEFAULT NULL COMMENT '事件类型',
  `log_content` varchar(1000) DEFAULT NULL COMMENT '日志内容',
  `created_by` bigint(20) DEFAULT NULL,
  `creation_date` datetime NULL DEFAULT NULL,
  `last_updated_by` bigint(20) DEFAULT NULL,
  `last_update_date` datetime NULL DEFAULT NULL,
  PRIMARY KEY (`log_id`),
  KEY `bpmn_process_log_n1` (`instance_id`),
  KEY `bpmn_process_log_n2` (`log_date`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=gb2312;
