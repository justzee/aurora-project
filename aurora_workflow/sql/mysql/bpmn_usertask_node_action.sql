CREATE TABLE `bpmn_usertask_node_action` (
  `record_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '工作流节点动作ID',
  `usertask_id` bigint(20) DEFAULT NULL COMMENT '工作流节点ID',
  `action_code` varchar(20) DEFAULT NULL COMMENT '工作流动作代码(标准or CUSTOM)',
  `action_code_custom` varchar(45) DEFAULT NULL COMMENT '当action_code 为 CUSTOM时的自定义值',
  `action_title_id` bigint(20) DEFAULT NULL COMMENT '动作描述(多语言字段)',
  `order_num` decimal(10,0) DEFAULT NULL COMMENT '动作排列顺序',
  `creation_date` datetime DEFAULT NULL COMMENT '创建日期',
  `created_by` decimal(10,0) DEFAULT NULL COMMENT '创建用户ID',
  `last_update_date` datetime DEFAULT NULL COMMENT '最后更新日期',
  `last_updated_by` decimal(10,0) DEFAULT NULL COMMENT '最后更新用户ID',
  PRIMARY KEY (`record_id`),
  UNIQUE KEY `bpmn_usertask_node_action_u1` (`usertask_id`,`action_code`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8 COMMENT='工作流节点动作定义表';
