--create table
CREATE TABLE BPMN_COMPLEX_GATEWAY_STATUS(
	status_id                      NUMBER(10,0) NOT NULL primary key,
	instance_id                    NUMBER(10,0) NOT NULL,
	node_id                        VARCHAR2(100) NOT NULL,
	wait_for_start                 VARCHAR2(10) default 'TRUE',
	created_by                     NUMBER(19,0),
	creation_date                  DATE,
	last_updated_by                NUMBER(19,0),
	last_update_date               DATE
);
--create Index
--create sequence
create sequence bpmn_complex_gateway_status_s;
--add table comment
--add column comments

