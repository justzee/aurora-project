CREATE TABLE `bpmn_process_data` (
  `data_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `instance_id` bigint(20) NOT NULL COMMENT 'instance_id',
  `data_object` text NOT NULL COMMENT 'JSON 形式的数据',
  `created_by` bigint(20) DEFAULT NULL,
  `creation_date` datetime NULL DEFAULT NULL,
  `last_updated_by` bigint(20) DEFAULT NULL,
  `last_update_date` datetime NULL DEFAULT NULL,
  PRIMARY KEY (`data_id`),
  UNIQUE KEY `bpmn_process_data_u1` (`instance_id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8;
