CREATE TABLE `bpmn_path_log` (
  `log_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `instance_id` bigint(20) NOT NULL COMMENT '所属工作流ID',
  `path_id` bigint(20) NOT NULL COMMENT '所属路径实例ID',
  `log_date` datetime DEFAULT NULL COMMENT '发生时间',
  `user_id` varchar(100) DEFAULT NULL COMMENT '触发该日志记录的用户ID',
  `current_node` varchar(100) DEFAULT NULL COMMENT '当前所在节点',
  `prev_node` varchar(100) DEFAULT NULL COMMENT '之前来自节点',
  `event_type` varchar(100) DEFAULT NULL COMMENT '所发生日志的的类型',
  `log_content` varchar(1000) DEFAULT NULL COMMENT '日志内容',
  PRIMARY KEY (`log_id`),
  KEY `BPMN_PATH_LOG_I2` (`path_id`),
  KEY `BPMN_PATH_LOG_I3` (`log_date`),
  KEY `BPMN_PATH_LOG_I1` (`instance_id`)
) ENGINE=InnoDB AUTO_INCREMENT=114 DEFAULT CHARSET=gb2312;
