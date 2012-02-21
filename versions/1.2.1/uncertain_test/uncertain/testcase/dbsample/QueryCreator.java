/*
 * Created on 2006-11-20
 */
package uncertain.testcase.dbsample;

import uncertain.proc.ProcedureRunner;

public class QueryCreator {
    
    StringBuffer    fields;
    StringBuffer    entity;
    StringBuffer    whereClause;
    String          sql;
    Table           table;
    
    public QueryCreator(){
        fields = new StringBuffer();
        entity = new StringBuffer();
        whereClause = new StringBuffer();
    }
    
    public void onGetEntity(Table tbl){
        table = tbl;
        entity.setLength(0);
        entity.append(tbl.getName());
    }
    
    public void onCreateSqlFields(){
        Column[] columns = table.getColumnArray();
        for(int i=0; i<columns.length; i++){
            if(i>0) fields.append(',');
            fields.append(columns[i].Name);
        }
    }
    
    public void onCreateWhereClause(){
        whereClause.append("author_id is not null");
    }
    
    public void onCreateSql(ProcedureRunner runner, StringBuffer _fields, StringBuffer _entity, StringBuffer _where){
        if(runner==null) throw new IllegalArgumentException("runner is null");
        if(_fields == null) throw new IllegalArgumentException("_fields is null");
        if(_entity == null) throw new IllegalArgumentException("_entity is null");
        if(_where == null) throw new IllegalArgumentException("_where is null");        
        sql = "select " + _fields.toString() + " from " + _entity.toString() + " where " + _where.toString();
        //System.out.println(sql);
    }

    /**
     * @return the entity
     */
    public StringBuffer getEntity() {
        return entity;
    }

    /**
     * @param entity the entity to set
     */
    public void setEntity(StringBuffer entity) {
        this.entity = entity;
    }

    /**
     * @return the fields
     */
    public StringBuffer getFields() {
        return fields;
    }

    /**
     * @param fields the fields to set
     */
    public void setFields(StringBuffer fields) {
        this.fields = fields;
    }

    /**
     * @return the sql
     */
    public String getSql() {
        return sql;
    }

    /**
     * @param sql the sql to set
     */
    public void setSql(String sql) {
        this.sql = sql;
    }

    /**
     * @return the whereClause
     */
    public StringBuffer getWhereClause() {
        return whereClause;
    }

    /**
     * @param whereClause the whereClause to set
     */
    public void setWhereClause(StringBuffer whereClause) {
        this.whereClause = whereClause;
    }
    
    /**
     * @return the table
     */
    public Table getTable() {
        return table;
    }

    /**
     * @param table the table to set
     */
    public void setTable(Table table) {
        this.table = table;
    }
    
    public void setHint(String hint){
        
    }
    
    public String getHint(){
        return "auto generated sql";
    }

}
