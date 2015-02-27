CREATE TABLE `bpmn_process_token` (
  `token_id` int(11) NOT NULL AUTO_INCREMENT,
  `instance_id` int(11) NOT NULL,
  `path_id` int(11) NOT NULL,
  `node_id` varchar(100) NOT NULL COMMENT 'target ref of path',
  PRIMARY KEY (`token_id`),
  KEY `bpmn_process_token_n1` (`instance_id`,`node_id`)
) ENGINE=InnoDB AUTO_INCREMENT=105 DEFAULT CHARSET=utf8;
