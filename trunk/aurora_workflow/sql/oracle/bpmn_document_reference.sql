--create table
CREATE TABLE BPMN_DOCUMENT_REFERENCE(
	reference_id                   NUMBER(19,0) NOT NULL primary key,
	category_id                    VARCHAR2(30) NOT NULL,
	description_id                 VARCHAR2(100),
	document_table_name            VARCHAR2(100),
	ref_id_column_name             VARCHAR2(100),
	ref_num_column_name            VARCHAR2(100),
	ref_company_column_name        VARCHAR2(100),
	ref_created_column_name        VARCHAR2(100),
	ref_detail                     VARCHAR2(2000),
	sys_flag                       VARCHAR2(1),
	created_by                     NUMBER(10,0),
	creation_date                  DATE,
	last_updated_by                NUMBER(10,0),
	last_update_date               DATE
);
--create Index
create UNIQUE INDEX bpmn_document_reference_u1 on bpmn_document_reference(category_id);
--create sequence
create sequence bpmn_document_reference_s;
--add table comment
comment on table  bpmn_document_reference is '工作流引用单据表对应关系定义';
--add column comments
comment on column bpmn_document_reference.category_id is '工作流类型';
comment on column bpmn_document_reference.description_id is '描述ID';
comment on column bpmn_document_reference.document_table_name is '单据表名';
comment on column bpmn_document_reference.ref_id_column_name is '引用单据表ID列名';
comment on column bpmn_document_reference.ref_num_column_name is '引用单据号列名';
comment on column bpmn_document_reference.ref_company_column_name is '引用公司列名';
comment on column bpmn_document_reference.ref_created_column_name is '引用创建用户列名';
comment on column bpmn_document_reference.ref_detail is '配置SQL';
comment on column bpmn_document_reference.sys_flag is '是否系统创建标志';
comment on column bpmn_document_reference.created_by is '创建用户ID';
comment on column bpmn_document_reference.creation_date is '创建日期';
comment on column bpmn_document_reference.last_updated_by is '最后更新用户ID';
comment on column bpmn_document_reference.last_update_date is '最后更新日期';

