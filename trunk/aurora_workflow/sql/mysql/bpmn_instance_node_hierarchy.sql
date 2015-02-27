CREATE TABLE `bpmn_instance_node_hierarchy` (
  `hierarchy_record_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '审批层次记录ID',
  `instance_id` bigint(20) NOT NULL COMMENT '工作流实例ID',
  `usertask_id` varchar(20) NOT NULL COMMENT '工作流节点ID',
  `seq_number` decimal(10,0) DEFAULT NULL COMMENT '审批顺序号',
  `approver_id` bigint(20) DEFAULT NULL COMMENT '审批者ID',
  `posted_flag` varchar(1) DEFAULT NULL COMMENT '是否生成待办记录标志',
  `disabled_flag` varchar(1) DEFAULT NULL COMMENT '失效标志',
  `note` varchar(1000) DEFAULT NULL COMMENT '说明',
  `rule_record_id` bigint(20) DEFAULT NULL COMMENT '审批规则ID',
  `rule_detail_id` bigint(20) DEFAULT NULL COMMENT '审批规则明细ID',
  `creation_date` datetime DEFAULT NULL COMMENT '创建日期',
  `created_by` decimal(10,0) DEFAULT NULL COMMENT '创建用户ID',
  `last_update_date` datetime DEFAULT NULL COMMENT '最后更新日期',
  `last_updated_by` decimal(10,0) DEFAULT NULL COMMENT '最后更新用户ID',
  `added_order` varchar(10) DEFAULT NULL COMMENT '此节点被添加的顺序，之前添加为BEFORE,之后添加为AFTER，平行添加为PARALLEL，如果不是被添加节点则该属性为空',
  PRIMARY KEY (`hierarchy_record_id`),
  KEY `bpmn_instance_node_hierarchy_n1` (`instance_id`,`usertask_id`,`seq_number`,`rule_record_id`)
) ENGINE=InnoDB AUTO_INCREMENT=64 DEFAULT CHARSET=utf8 COMMENT='工作流实例节点审批层次表';
