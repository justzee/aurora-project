CREATE TABLE `bpmn_complex_gateway_status` (
  `status_id` int(11) NOT NULL AUTO_INCREMENT,
  `instance_id` int(11) NOT NULL,
  `node_id` varchar(100) NOT NULL,
  `wait_for_start` varchar(10) DEFAULT 'TRUE',
  `created_by` bigint(20) DEFAULT NULL,
  `creation_date` datetime NULL DEFAULT NULL,
  `last_updated_by` bigint(20) DEFAULT NULL,
  `last_update_date` datetime NULL DEFAULT NULL,
  PRIMARY KEY (`status_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
