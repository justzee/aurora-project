CREATE TABLE `bpmn_complex_gateway_status` (
  `status_id` int(11) NOT NULL AUTO_INCREMENT,
  `instance_id` int(11) NOT NULL,
  `node_id` varchar(100) NOT NULL,
  `wait_for_start` varchar(10) DEFAULT 'TRUE',
  PRIMARY KEY (`status_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
