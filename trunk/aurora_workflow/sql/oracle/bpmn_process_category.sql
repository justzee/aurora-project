--create table
CREATE TABLE BPMN_PROCESS_CATEGORY(
	id                             NUMBER(19,0) NOT NULL primary key,
	parent_id                      NUMBER(19,0),
	name                           VARCHAR2(100) NOT NULL
);
--create Index
create UNIQUE INDEX bpmn_process_category_u1 on bpmn_process_category(name);
create INDEX bpmn_process_category_n1 on bpmn_process_category(parent_id);
--create sequence
create sequence bpmn_process_category_s;
--add table comment
--add column comments
comment on column bpmn_process_category.id is 'PK';
comment on column bpmn_process_category.parent_id is '父类别ID';
comment on column bpmn_process_category.name is '类别名称';

