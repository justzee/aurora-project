--create table
CREATE TABLE BPMN_PROCESS_TOKEN(
	token_id                       NUMBER(10,0) NOT NULL primary key,
	instance_id                    NUMBER(10,0) NOT NULL,
	path_id                        NUMBER(10,0) NOT NULL,
	node_id                        VARCHAR2(100) NOT NULL
);
--create Index
create INDEX bpmn_process_token_n1 on bpmn_process_token(instance_id,node_id);
--create sequence
create sequence bpmn_process_token_s;
--add table comment
--add column comments
comment on column bpmn_process_token.node_id is 'target ref of path';

