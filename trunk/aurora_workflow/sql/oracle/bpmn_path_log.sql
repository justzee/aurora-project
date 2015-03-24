--create table
CREATE TABLE BPMN_PATH_LOG(
	log_id                         NUMBER(19,0) NOT NULL primary key,
	instance_id                    NUMBER(19,0) NOT NULL,
	path_id                        NUMBER(19,0) NOT NULL,
	log_date                       DATE,
	user_id                        VARCHAR2(100),
	current_node                   VARCHAR2(100),
	prev_node                      VARCHAR2(100),
	event_type                     VARCHAR2(100),
	log_content                    VARCHAR2(1000),
	created_by                     NUMBER(19,0),
	creation_date                  DATE,
	last_updated_by                NUMBER(19,0),
	last_update_date               DATE
);
--create Index
create INDEX BPMN_PATH_LOG_I1 on bpmn_path_log(instance_id);
create INDEX BPMN_PATH_LOG_I2 on bpmn_path_log(path_id);
create INDEX BPMN_PATH_LOG_I3 on bpmn_path_log(log_date);
--create sequence
create sequence bpmn_path_log_s;
--add table comment
--add column comments
comment on column bpmn_path_log.log_id is 'PK';
comment on column bpmn_path_log.instance_id is '所属工作流ID';
comment on column bpmn_path_log.path_id is '所属路径实例ID';
comment on column bpmn_path_log.log_date is '发生时间';
comment on column bpmn_path_log.user_id is '触发该日志记录的用户ID';
comment on column bpmn_path_log.current_node is '当前所在节点';
comment on column bpmn_path_log.prev_node is '之前来自节点';
comment on column bpmn_path_log.event_type is '所发生日志的的类型';
comment on column bpmn_path_log.log_content is '日志内容';

