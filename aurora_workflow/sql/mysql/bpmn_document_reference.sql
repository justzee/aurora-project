CREATE TABLE `bpmn_document_reference` (
  `reference_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `category_id` varchar(30) NOT NULL COMMENT '工作流类型',
  `description_id` varchar(100) DEFAULT NULL COMMENT '描述ID',
  `document_table_name` varchar(100) DEFAULT NULL COMMENT '单据表名',
  `ref_id_column_name` varchar(100) DEFAULT NULL COMMENT '引用单据表ID列名',
  `ref_num_column_name` varchar(100) DEFAULT NULL COMMENT '引用单据号列名',
  `ref_company_column_name` varchar(100) DEFAULT NULL COMMENT '引用公司列名',
  `ref_created_column_name` varchar(100) DEFAULT NULL COMMENT '引用创建用户列名',
  `ref_detail` varchar(2000) DEFAULT NULL COMMENT '配置SQL',
  `sys_flag` varchar(1) DEFAULT NULL COMMENT '是否系统创建标志',
  `created_by` decimal(10,0) DEFAULT NULL COMMENT '创建用户ID',
  `creation_date` datetime DEFAULT NULL COMMENT '创建日期',
  `last_updated_by` decimal(10,0) DEFAULT NULL COMMENT '最后更新用户ID',
  `last_update_date` datetime DEFAULT NULL COMMENT '最后更新日期',
  PRIMARY KEY (`reference_id`),
  UNIQUE KEY `bpmn_document_reference_u1` (`category_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COMMENT='工作流引用单据表对应关系定义';
