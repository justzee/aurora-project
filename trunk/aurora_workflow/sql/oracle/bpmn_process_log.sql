--create table
CREATE TABLE BPMN_PROCESS_LOG(
	log_id                         NUMBER(19,0) NOT NULL primary key,
	instance_id                    NUMBER(19,0) NOT NULL,
	log_date                       DATE,
	user_id                        VARCHAR2(45),
	event_type                     VARCHAR2(45),
	log_content                    VARCHAR2(1000)
);
--create Index
create INDEX bpmn_process_log_n1 on bpmn_process_log(instance_id);
create INDEX bpmn_process_log_n2 on bpmn_process_log(log_date);
--create sequence
create sequence bpmn_process_log_s;
--add table comment
--add column comments
comment on column bpmn_process_log.log_id is 'PK';
comment on column bpmn_process_log.instance_id is '所属工作流ID';
comment on column bpmn_process_log.log_date is '创建时间';
comment on column bpmn_process_log.user_id is '触发该日志记录的用户ID';
comment on column bpmn_process_log.event_type is '事件类型';
comment on column bpmn_process_log.log_content is '日志内容';

