--create table
CREATE TABLE BPMN_NODE_RECIPIENT_SET(
	recipient_set_id               NUMBER(19,0) NOT NULL primary key,
	usertask_id                    NUMBER(19,0) NOT NULL,
	rule_code                      VARCHAR2(30),
	rule_sequence                  NUMBER(10,0),
	recipient_sequence             NUMBER(10,0),
	parameter_1_value              VARCHAR2(1000),
	parameter_1_desc               VARCHAR2(1000),
	parameter_2_value              VARCHAR2(1000),
	parameter_2_desc               VARCHAR2(1000),
	parameter_3_value              VARCHAR2(1000),
	parameter_3_desc               VARCHAR2(1000),
	parameter_4_value              VARCHAR2(1000),
	parameter_4_desc               VARCHAR2(1000),
	created_by                     NUMBER(10,0),
	creation_date                  DATE,
	last_updated_by                NUMBER(10,0),
	last_update_date               DATE
);
--create Index
create INDEX bpmn_node_recipient_set_n1 on bpmn_node_recipient_set(usertask_id);
--create sequence
create sequence bpmn_node_recipient_set_s;
--add table comment
comment on table  bpmn_node_recipient_set is '工作流节点审批者定义表';
--add column comments
comment on column bpmn_node_recipient_set.recipient_set_id is 'RECIPIENT_SET_ID';
comment on column bpmn_node_recipient_set.usertask_id is '工作流节点ID';
comment on column bpmn_node_recipient_set.rule_code is '规则CODE';
comment on column bpmn_node_recipient_set.rule_sequence is '序号';
comment on column bpmn_node_recipient_set.recipient_sequence is '审批顺序';
comment on column bpmn_node_recipient_set.parameter_1_value is '参数1值';
comment on column bpmn_node_recipient_set.parameter_1_desc is '参数1描述';
comment on column bpmn_node_recipient_set.parameter_2_value is '参数2值';
comment on column bpmn_node_recipient_set.parameter_2_desc is '参数2描述';
comment on column bpmn_node_recipient_set.parameter_3_value is '参数3值';
comment on column bpmn_node_recipient_set.parameter_3_desc is '参数3描述';
comment on column bpmn_node_recipient_set.parameter_4_value is '参数4值';
comment on column bpmn_node_recipient_set.parameter_4_desc is '参数4描述';
comment on column bpmn_node_recipient_set.created_by is '创建用户ID';
comment on column bpmn_node_recipient_set.creation_date is '创建日期';
comment on column bpmn_node_recipient_set.last_updated_by is '最后更新用户ID';
comment on column bpmn_node_recipient_set.last_update_date is '最后更新日期';

