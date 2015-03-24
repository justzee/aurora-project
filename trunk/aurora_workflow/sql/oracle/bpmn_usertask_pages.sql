--create table
CREATE TABLE BPMN_USERTASK_PAGES(
	page_id                        NUMBER(10,0) NOT NULL primary key,
	description_id                 NUMBER(19,0),
	page_name                      VARCHAR2(200) NOT NULL,
	created_by                     NUMBER(19,0),
	creation_date                  DATE,
	last_updated_by                NUMBER(19,0),
	last_update_date               DATE
);
--create Index
--create sequence
create sequence bpmn_usertask_pages_s;
--add table comment
comment on table  bpmn_usertask_pages is '节点页面定义';
--add column comments
comment on column bpmn_usertask_pages.page_id is 'PK';
comment on column bpmn_usertask_pages.description_id is '描述ID(多语言字段)';
comment on column bpmn_usertask_pages.page_name is '页面路径';

