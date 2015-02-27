--create table
CREATE TABLE BPMN_RECIPIENT_RULES(
	rule_id                        NUMBER(19,0) NOT NULL primary key,
	rule_code                      VARCHAR2(30) NOT NULL,
	rule_type                      VARCHAR2(30) NOT NULL,
	name_id                        NUMBER(19,0),
	description_id                 NUMBER(19,0),
	procedure_name                 VARCHAR2(200),
	sys_flag                       VARCHAR2(1),
	parameter_1_type               VARCHAR2(30),
	parameter_1_desc               VARCHAR2(1000),
	parameter_1_url                VARCHAR2(1000),
	parameter_2_type               VARCHAR2(30),
	parameter_2_desc               VARCHAR2(1000),
	parameter_2_url                VARCHAR2(1000),
	parameter_3_type               VARCHAR2(30),
	parameter_3_desc               VARCHAR2(1000),
	parameter_3_url                VARCHAR2(1000),
	parameter_4_type               VARCHAR2(30),
	parameter_4_desc               VARCHAR2(1000),
	parameter_4_url                VARCHAR2(1000),
	created_by                     NUMBER(10,0),
	creation_date                  DATE,
	last_updated_by                NUMBER(10,0),
	last_update_date               DATE
);
--create Index
create UNIQUE INDEX bpmn_recipient_rules_u1 on bpmn_recipient_rules(rule_code);
--create sequence
create sequence bpmn_recipient_rules_s;
--add table comment
comment on table  bpmn_recipient_rules is '审批规则定义表';
--add column comments
comment on column bpmn_recipient_rules.rule_code is '规则CODE';
comment on column bpmn_recipient_rules.rule_type is '审批规则类型';
comment on column bpmn_recipient_rules.name_id is '审批规则名称';
comment on column bpmn_recipient_rules.description_id is '描述ID';
comment on column bpmn_recipient_rules.procedure_name is '执行过程';
comment on column bpmn_recipient_rules.sys_flag is '系统创建标志';
comment on column bpmn_recipient_rules.parameter_1_type is 'SELECT：LOV；INPUT：textedit';
comment on column bpmn_recipient_rules.parameter_1_desc is '参数1描述';
comment on column bpmn_recipient_rules.parameter_1_url is 'Lov组件文件名';
comment on column bpmn_recipient_rules.parameter_2_type is 'SELECT：LOV；INPUT：textedit';
comment on column bpmn_recipient_rules.parameter_2_desc is '参数2描述';
comment on column bpmn_recipient_rules.parameter_2_url is 'Lov组件文件名';
comment on column bpmn_recipient_rules.parameter_3_type is 'SELECT：LOV；INPUT：textedit';
comment on column bpmn_recipient_rules.parameter_3_desc is '参数3描述';
comment on column bpmn_recipient_rules.parameter_3_url is 'Lov组件文件名';
comment on column bpmn_recipient_rules.parameter_4_type is 'SELECT：LOV；INPUT：textedit';
comment on column bpmn_recipient_rules.parameter_4_desc is '参数4描述';
comment on column bpmn_recipient_rules.parameter_4_url is 'Lov组件文件名';
comment on column bpmn_recipient_rules.created_by is '创建用户ID';
comment on column bpmn_recipient_rules.creation_date is '创建日期';
comment on column bpmn_recipient_rules.last_updated_by is '最后更新用户ID';
comment on column bpmn_recipient_rules.last_update_date is '最后更新日期';

