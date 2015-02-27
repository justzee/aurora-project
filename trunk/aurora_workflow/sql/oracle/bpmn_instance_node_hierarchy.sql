--create table
CREATE TABLE BPMN_INSTANCE_NODE_HIERARCHY(
	hierarchy_record_id            NUMBER(19,0) NOT NULL primary key,
	instance_id                    NUMBER(19,0) NOT NULL,
	usertask_id                    VARCHAR2(20) NOT NULL,
	seq_number                     NUMBER(10,0),
	approver_id                    NUMBER(19,0),
	posted_flag                    VARCHAR2(1),
	disabled_flag                  VARCHAR2(1),
	note                           VARCHAR2(1000),
	rule_record_id                 NUMBER(19,0),
	rule_detail_id                 NUMBER(19,0),
	creation_date                  DATE,
	created_by                     NUMBER(10,0),
	last_update_date               DATE,
	last_updated_by                NUMBER(10,0),
	added_order                    VARCHAR2(10)
);
--create Index
create INDEX bpmn_instance_node_hierarch_n1 on bpmn_instance_node_hierarchy(instance_id,usertask_id,seq_number,rule_record_id);
--create sequence
create sequence bpmn_instance_node_hierarchy_s;
--add table comment
comment on table  bpmn_instance_node_hierarchy is '工作流实例节点审批层次表';
--add column comments
comment on column bpmn_instance_node_hierarchy.hierarchy_record_id is '审批层次记录ID';
comment on column bpmn_instance_node_hierarchy.instance_id is '工作流实例ID';
comment on column bpmn_instance_node_hierarchy.usertask_id is '工作流节点ID';
comment on column bpmn_instance_node_hierarchy.seq_number is '审批顺序号';
comment on column bpmn_instance_node_hierarchy.approver_id is '审批者ID';
comment on column bpmn_instance_node_hierarchy.posted_flag is '是否生成待办记录标志';
comment on column bpmn_instance_node_hierarchy.disabled_flag is '失效标志';
comment on column bpmn_instance_node_hierarchy.note is '说明';
comment on column bpmn_instance_node_hierarchy.rule_record_id is '审批规则ID';
comment on column bpmn_instance_node_hierarchy.rule_detail_id is '审批规则明细ID';
comment on column bpmn_instance_node_hierarchy.creation_date is '创建日期';
comment on column bpmn_instance_node_hierarchy.created_by is '创建用户ID';
comment on column bpmn_instance_node_hierarchy.last_update_date is '最后更新日期';
comment on column bpmn_instance_node_hierarchy.last_updated_by is '最后更新用户ID';
comment on column bpmn_instance_node_hierarchy.added_order is '此节点被添加的顺序，之前添加为BEFORE,之后添加为AFTER，平行添加为PARALLEL，如果不是被添加节点则该属性为空';

