--create table
CREATE TABLE BPMN_PROCESS_INSTANCE(
	instance_id                    NUMBER(19,0) NOT NULL primary key,
	status                         VARCHAR2(45) NOT NULL,
	parent_id                      NUMBER(19,0),
	process_code                   VARCHAR2(45),
	process_version                VARCHAR2(45),
	description                    VARCHAR2(1000),
	instance_param                 NUMBER(19,0)
);
--create Index
create INDEX bpmn_process_instance_n1 on bpmn_process_instance(parent_id);
--create sequence
create sequence bpmn_process_instance_s;
--add table comment
--add column comments
comment on column bpmn_process_instance.instance_id is 'PK';
comment on column bpmn_process_instance.status is '当前状态';
comment on column bpmn_process_instance.parent_id is '父流程ID';
comment on column bpmn_process_instance.process_code is '所属工作流代码';
comment on column bpmn_process_instance.process_version is '所属工作流版本';
comment on column bpmn_process_instance.description is '流程创建是的一段描述';

