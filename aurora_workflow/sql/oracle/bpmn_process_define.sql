--create table
CREATE TABLE BPMN_PROCESS_DEFINE(
	define_id                      NUMBER(19,0) NOT NULL primary key,
	process_code                   VARCHAR2(45) NOT NULL,
	process_version                VARCHAR2(45) NOT NULL,
	description                    VARCHAR2(1000),
	current_version_flag           VARCHAR2(1),
	defines                        CLOB,
	name                           VARCHAR2(100),
	approve_status                 VARCHAR2(45) NOT NULL,
	valid_flag                     VARCHAR2(1) NOT NULL,
	category_id                    NUMBER(19,0) NOT NULL,
	created_by                     NUMBER(19,0),
	creation_date                  DATE,
	last_updated_by                NUMBER(19,0),
	last_update_date               DATE
);
--create Index
create UNIQUE INDEX bpmn_process_define_u1 on bpmn_process_define(process_version,process_code);
create INDEX bpmn_process_define_n1 on bpmn_process_define(category_id);
create INDEX bpmn_process_define_n2 on bpmn_process_define(process_code);
--create sequence
create sequence bpmn_process_define_s;
--add table comment
--add column comments
comment on column bpmn_process_define.define_id is 'PK';
comment on column bpmn_process_define.process_code is '工作流代码';
comment on column bpmn_process_define.process_version is '版本号';
comment on column bpmn_process_define.description is '说明';
comment on column bpmn_process_define.current_version_flag is '是否当前版本';
comment on column bpmn_process_define.defines is 'XML形式存储的BPMN配置';
comment on column bpmn_process_define.name is '流程名称';
comment on column bpmn_process_define.approve_status is '审批标记,未审批:NONE,审批中:APPROVING,审批通过:APPROVED';
comment on column bpmn_process_define.valid_flag is '有效标记.Y,N';
comment on column bpmn_process_define.category_id is '类别ID';

