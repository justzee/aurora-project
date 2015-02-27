--create table
CREATE TABLE BPMN_INSTANCE_NODE_RULE(
	rule_record_id                 NUMBER(19,0) NOT NULL primary key,
	instance_id                    NUMBER(19,0) NOT NULL,
	usertask_id                    NUMBER(19,0) NOT NULL,
	recipient_type                 VARCHAR2(30),
	recipient_set_id               NUMBER(19,0),
	rule_sequence                  NUMBER(10,0),
	recipient_sequence             NUMBER(10,0),
	parameter_1_value              VARCHAR2(1000),
	parameter_2_value              VARCHAR2(1000),
	parameter_3_value              VARCHAR2(1000),
	parameter_4_value              VARCHAR2(1000),
	rule_code                      VARCHAR2(30),
	rule_type                      VARCHAR2(30),
	creation_date                  DATE,
	created_by                     NUMBER(10,0),
	last_update_date               DATE,
	last_updated_by                NUMBER(10,0)
);
--create Index
create INDEX bpmn_instance_node_rule_n1 on bpmn_instance_node_rule(instance_id,usertask_id);
--create sequence
create sequence bpmn_instance_node_rule_s;
--add table comment
comment on table  bpmn_instance_node_rule is '工作里实例节点审批规则表';
--add column comments
comment on column bpmn_instance_node_rule.rule_record_id is '审批规则ID';
comment on column bpmn_instance_node_rule.instance_id is '工作流实例ID';
comment on column bpmn_instance_node_rule.usertask_id is '工作流节点ID';
comment on column bpmn_instance_node_rule.recipient_type is '审批者来源类型';
comment on column bpmn_instance_node_rule.recipient_set_id is '审批者来源记录ID';
comment on column bpmn_instance_node_rule.rule_sequence is '序号';
comment on column bpmn_instance_node_rule.recipient_sequence is '规则顺序';
comment on column bpmn_instance_node_rule.parameter_1_value is '参数1值';
comment on column bpmn_instance_node_rule.parameter_2_value is '参数2值';
comment on column bpmn_instance_node_rule.parameter_3_value is '参数3值';
comment on column bpmn_instance_node_rule.parameter_4_value is '参数4值';
comment on column bpmn_instance_node_rule.rule_code is '规则CODE';
comment on column bpmn_instance_node_rule.rule_type is '审批者规则类型';
comment on column bpmn_instance_node_rule.creation_date is '创建日期';
comment on column bpmn_instance_node_rule.created_by is '创建用户ID';
comment on column bpmn_instance_node_rule.last_update_date is '最后更新日期';
comment on column bpmn_instance_node_rule.last_updated_by is '最后更新用户ID';

