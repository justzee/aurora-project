--create table
CREATE TABLE BPMN_APPROVE_RECORD(
	record_id                      NUMBER(19,0) NOT NULL primary key,
	instance_id                    NUMBER(19,0) NOT NULL,
	usertask_id                    NUMBER(19,0) NOT NULL,
	action_token                   VARCHAR2(45) NOT NULL,
	comment_text                   VARCHAR2(2000),
	approve_count                  NUMBER(10,0) default '1',
	attachment_id                  NUMBER(19,0),
	seq_number                     NUMBER(10,0),
	rcpt_record_id                 NUMBER(19,0),
	disabled_flag                  VARCHAR2(1),
	note                           VARCHAR2(1000),
	created_by                     NUMBER(10,0),
	creation_date                  DATE,
	last_update_date               DATE,
	last_updated_by                NUMBER(10,0)
);
--create Index
create INDEX bpmn_approve_record_n1 on bpmn_approve_record(instance_id);
create INDEX bpmn_approve_record_n2 on bpmn_approve_record(approve_count);
create INDEX bpmn_approve_record_n3 on bpmn_approve_record(rcpt_record_id);
create INDEX bpmn_approve_record_n4 on bpmn_approve_record(created_by);
--create sequence
create sequence bpmn_approve_record_s;
--add table comment
comment on table  bpmn_approve_record is '审批记录表';
--add column comments
comment on column bpmn_approve_record.record_id is '审批记录ID';
comment on column bpmn_approve_record.instance_id is '工作流实例ID';
comment on column bpmn_approve_record.usertask_id is '工作流节点ID';
comment on column bpmn_approve_record.action_token is '审批动作ID';
comment on column bpmn_approve_record.comment_text is '审批备注';
comment on column bpmn_approve_record.approve_count is '审批轮次';
comment on column bpmn_approve_record.attachment_id is '附件ID';
comment on column bpmn_approve_record.seq_number is '工作流节点序号';
comment on column bpmn_approve_record.rcpt_record_id is '待办记录ID';
comment on column bpmn_approve_record.disabled_flag is '失效标志';
comment on column bpmn_approve_record.note is '记录备注';
comment on column bpmn_approve_record.created_by is '创建用户ID';
comment on column bpmn_approve_record.creation_date is '创建日期';
comment on column bpmn_approve_record.last_update_date is '最后更新日期';
comment on column bpmn_approve_record.last_updated_by is '最后更新用户ID';

