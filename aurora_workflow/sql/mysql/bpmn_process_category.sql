CREATE TABLE `bpmn_process_category` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `parent_id` bigint(20) DEFAULT NULL COMMENT '父类别ID',
  `name` varchar(100) NOT NULL COMMENT '类别名称',
  PRIMARY KEY (`id`),
  UNIQUE KEY `bpmn_process_category_u1` (`name`),
  KEY `bpmn_process_category_n1` (`parent_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
