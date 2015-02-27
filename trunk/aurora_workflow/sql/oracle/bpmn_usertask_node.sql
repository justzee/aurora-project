--create table
CREATE TABLE BPMN_USERTASK_NODE(
	usertask_id                    NUMBER(19,0) NOT NULL primary key,
	process_code                   VARCHAR2(45) NOT NULL,
	process_version                VARCHAR2(45) NOT NULL,
	node_id                        VARCHAR2(45) NOT NULL,
	recipient_type                 NUMBER(10,0),
	mail_notify                    NUMBER(3,0) default '0',
	is_date_limited                NUMBER(3,0) default '0',
	process_date                   NUMBER(10,0),
	process_date_unit_id           VARCHAR2(2),
	date_from                      DATE,
	date_to                        DATE,
	object_version_number          NUMBER(10,0) default '0',
	form_name                      VARCHAR2(200),
	approval_type                  NUMBER(10,0),
	recipient_proc                 VARCHAR2(300),
	name_id                        NUMBER(19,0),
	description_id                 NUMBER(19,0),
	form_width                     NUMBER(10,0),
	form_height                    NUMBER(10,0),
	can_deliver_to                 NUMBER(10,0) default '1',
	mail_template                  NUMBER(10,0),
	notify_period                  NUMBER(10,0),
	notify_period_length           VARCHAR2(2),
	notify_on_finish               NUMBER(10,0),
	check_proc                     VARCHAR2(255),
	can_auto_pass                  NUMBER(10,0) default '1',
	pre_node_proc                  VARCHAR2(500),
	is_comment_access_control      NUMBER(10,0) default '0',
	quantity                       NUMBER(10,0),
	is_self_re_commit              NUMBER(10,0) default '0',
	can_no_approver                NUMBER(10,0) default '1',
	can_add_approver               NUMBER(10,0),
	can_add_notification           NUMBER(10,0),
	created_by                     NUMBER(10,0),
	creation_date                  DATE,
	last_updated_by                NUMBER(10,0),
	last_update_date               DATE
);
--create Index
create INDEX bpmn_usertask_node_n1 on bpmn_usertask_node(process_code);
create INDEX bpmn_usertask_node_n2 on bpmn_usertask_node(process_code,process_version);
--create sequence
create sequence bpmn_usertask_node_s;
--add table comment
comment on table  bpmn_usertask_node is 'UserTask节点定义表';
--add column comments
comment on column bpmn_usertask_node.usertask_id is 'pk';
comment on column bpmn_usertask_node.process_code is 'process code';
comment on column bpmn_usertask_node.process_version is '工作流版本';
comment on column bpmn_usertask_node.node_id is 'user task 节点ID';
comment on column bpmn_usertask_node.recipient_type is '接收类型';
comment on column bpmn_usertask_node.mail_notify is '是否邮件提醒';
comment on column bpmn_usertask_node.is_date_limited is '处理日期限制';
comment on column bpmn_usertask_node.process_date is '处理日期';
comment on column bpmn_usertask_node.process_date_unit_id is '时间单位';
comment on column bpmn_usertask_node.date_from is '有效日期从';
comment on column bpmn_usertask_node.date_to is '有效日期到';
comment on column bpmn_usertask_node.form_name is '表单名称';
comment on column bpmn_usertask_node.approval_type is '审批类型';
comment on column bpmn_usertask_node.form_width is '表单宽度';
comment on column bpmn_usertask_node.form_height is '表单高度';
comment on column bpmn_usertask_node.can_deliver_to is '是否可以转交';
comment on column bpmn_usertask_node.mail_template is '邮件模板';
comment on column bpmn_usertask_node.notify_period is '提醒周期';
comment on column bpmn_usertask_node.notify_period_length is '时间单位';
comment on column bpmn_usertask_node.notify_on_finish is '结束时是否通知处理者';
comment on column bpmn_usertask_node.check_proc is '校验的存储过程';
comment on column bpmn_usertask_node.can_auto_pass is '无需重复审批';
comment on column bpmn_usertask_node.pre_node_proc is '节点前处理过程';
comment on column bpmn_usertask_node.is_comment_access_control is '审批意见查看限制';
comment on column bpmn_usertask_node.quantity is '数值';
comment on column bpmn_usertask_node.is_self_re_commit is '提交人是否需要审批';
comment on column bpmn_usertask_node.can_no_approver is '节点允许无审批人';
comment on column bpmn_usertask_node.can_add_approver is '允许添加审批人';
comment on column bpmn_usertask_node.can_add_notification is '允许增加通知人';
comment on column bpmn_usertask_node.created_by is '创建用户ID';
comment on column bpmn_usertask_node.creation_date is '创建日期';
comment on column bpmn_usertask_node.last_updated_by is '最后更新用户ID';
comment on column bpmn_usertask_node.last_update_date is '最后更新日期';

