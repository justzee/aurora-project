--create table
CREATE TABLE BPMN_INSTANCE_NODE_RECIPIENT(
	record_id                      NUMBER(19,0) NOT NULL primary key,
	instance_id                    NUMBER(19,0) NOT NULL,
	usertask_id                    NUMBER(19,0) NOT NULL,
	seq_number                     NUMBER(10,0),
	user_id                        NUMBER(19,0),
	date_limit                     DATE,
	commision_by                   NUMBER(19,0),
	commision_desc                 VARCHAR2(2000),
	last_notify_date               DATE,
	attachment_id                  NUMBER(19,0),
	hierarchy_record_id            NUMBER(19,0),
	creation_date                  DATE,
	created_by                     NUMBER(10,0),
	last_update_date               DATE,
	last_updated_by                NUMBER(10,0)
);
--create Index
create INDEX bpmn_instance_node_recipien_n1 on bpmn_instance_node_recipient(user_id);
create INDEX bpmn_instance_node_recipien_n2 on bpmn_instance_node_recipient(instance_id);
--create sequence
create sequence bpmn_instance_node_recipient_s;
--add table comment
comment on table  bpmn_instance_node_recipient is '工作流实例待办事项表';
--add column comments
comment on column bpmn_instance_node_recipient.record_id is '审批记录ID';
comment on column bpmn_instance_node_recipient.instance_id is '工作流实例ID';
comment on column bpmn_instance_node_recipient.usertask_id is '工作流节点ID';
comment on column bpmn_instance_node_recipient.seq_number is '工作流节点序号';
comment on column bpmn_instance_node_recipient.user_id is '用户ID';
comment on column bpmn_instance_node_recipient.date_limit is '审批时限';
comment on column bpmn_instance_node_recipient.commision_by is '转交人ID';
comment on column bpmn_instance_node_recipient.commision_desc is '转交人';
comment on column bpmn_instance_node_recipient.last_notify_date is '最后通知时间';
comment on column bpmn_instance_node_recipient.attachment_id is '附件ID';
comment on column bpmn_instance_node_recipient.hierarchy_record_id is '审批层次记录ID';
comment on column bpmn_instance_node_recipient.creation_date is '创建日期';
comment on column bpmn_instance_node_recipient.created_by is '创建用户ID';
comment on column bpmn_instance_node_recipient.last_update_date is '最后更新日期';
comment on column bpmn_instance_node_recipient.last_updated_by is '最后更新用户ID';

