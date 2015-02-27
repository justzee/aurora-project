--create table
CREATE TABLE BPMN_PROCESS_DATA(
	data_id                        NUMBER(19,0) NOT NULL primary key,
	instance_id                    NUMBER(19,0) NOT NULL,
	data_object                    CLOB NOT NULL
);
--create Index
create UNIQUE INDEX bpmn_process_data_u1 on bpmn_process_data(instance_id);
--create sequence
create sequence bpmn_process_data_s;
--add table comment
--add column comments
comment on column bpmn_process_data.data_id is 'PK';
comment on column bpmn_process_data.instance_id is 'instance_id';
comment on column bpmn_process_data.data_object is 'JSON 形式的数据';

