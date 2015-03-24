--create table
CREATE TABLE BPMN_PATH_INSTANCE(
	path_id                        NUMBER(19,0) NOT NULL primary key,
	instance_id                    NUMBER(19,0) NOT NULL,
	status                         VARCHAR2(45) NOT NULL,
	prev_node                      VARCHAR2(45),
	current_node                   VARCHAR2(45),
	node_id                        VARCHAR2(45) NOT NULL,
	created_by                     NUMBER(19,0),
	creation_date                  DATE,
	last_updated_by                NUMBER(19,0),
	last_update_date               DATE
);
--create Index
create INDEX bpmn_path_instance_n1 on bpmn_path_instance(instance_id);
--create sequence
create sequence bpmn_path_instance_s;
--add table comment
--add column comments
comment on column bpmn_path_instance.path_id is 'PK';
comment on column bpmn_path_instance.instance_id is '所属工作流实例';
comment on column bpmn_path_instance.status is '当前状态';
comment on column bpmn_path_instance.prev_node is '上一步来自节点ID';
comment on column bpmn_path_instance.current_node is '当前所在节点ID';
comment on column bpmn_path_instance.node_id is 'sequence flow id';

