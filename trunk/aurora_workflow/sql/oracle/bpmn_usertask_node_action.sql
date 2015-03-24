--create table
CREATE TABLE BPMN_USERTASK_NODE_ACTION(
	record_id                      NUMBER(19,0) NOT NULL primary key,
	usertask_id                    NUMBER(19,0),
	action_code                    VARCHAR2(20),
	action_code_custom             VARCHAR2(45),
	action_title_id                NUMBER(19,0),
	order_num                      NUMBER(10,0),
	creation_date                  DATE,
	created_by                     NUMBER(10,0),
	last_update_date               DATE,
	last_updated_by                NUMBER(10,0)
);
--create Index
create UNIQUE INDEX bpmn_usertask_node_action_u1 on bpmn_usertask_node_action(usertask_id,action_code);
--create sequence
create sequence bpmn_usertask_node_action_s;
--add table comment
comment on table  bpmn_usertask_node_action is '工作流节点动作定义表';
--add column comments
comment on column bpmn_usertask_node_action.record_id is '工作流节点动作ID';
comment on column bpmn_usertask_node_action.usertask_id is '工作流节点ID';
comment on column bpmn_usertask_node_action.action_code is '工作流动作代码(标准or CUSTOM)';
comment on column bpmn_usertask_node_action.action_code_custom is '当action_code 为 CUSTOM时的自定义值';
comment on column bpmn_usertask_node_action.action_title_id is '动作描述(多语言字段)';
comment on column bpmn_usertask_node_action.order_num is '动作排列顺序';
comment on column bpmn_usertask_node_action.creation_date is '创建日期';
comment on column bpmn_usertask_node_action.created_by is '创建用户ID';
comment on column bpmn_usertask_node_action.last_update_date is '最后更新日期';
comment on column bpmn_usertask_node_action.last_updated_by is '最后更新用户ID';

