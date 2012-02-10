/*
 * Created on 2006-11-24
 */
package org.lwap.database;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

import javax.sql.DataSource;

import org.lwap.database.datatype.DataTypeManager;
import org.lwap.database.datatype.DatabaseTypeField;

import uncertain.composite.CompositeMap;

public class CompositePersistent {
    
    DataSource  data_source;
    
    public static PreparedStatement getTableStructureStatement(Connection conn, String table_name)
    throws SQLException
    {

        PreparedStatement stmt = null;
    
        StringBuffer sql = new StringBuffer();
        sql.append("select * from ").append(table_name).append(" where 1<>1");
    
        stmt = conn.prepareStatement(sql.toString());
        return stmt;
    }

    

    /**
     * @param data_source
     */
    public CompositePersistent(DataSource data_source) {
        super();
        this.data_source = data_source;
    }
    
    public void insert(CompositeMap root, String table_name) throws SQLException {
        List childs = root.getChilds();
        if(childs==null) return;
        if(childs.size()==0) return;
        Iterator it = childs.iterator();
        if(it==null) return;
        Connection conn = null;
        PreparedStatement ps = null;
        PreparedStatement stmt = null;
        ResultSet meta_rs = null;
        try{
            // get meta data
            conn = data_source.getConnection();
            stmt = getTableStructureStatement(conn, table_name);
            meta_rs = stmt.executeQuery();
            ResultSetMetaData meta = meta_rs.getMetaData();            
            if(meta==null) throw new SQLException("Can't get table structure for "+table_name);
            // generate insert sql
            StringBuffer sql = new StringBuffer();            
            sql.append("insert into ").append(table_name).append(" ( ");
            StringBuffer sql_fields = new StringBuffer(), sql_values = new StringBuffer();
            for(int i=0; i<meta.getColumnCount(); i++){
                String name = meta.getColumnName(i+1);
                if(i>0) {
                    sql_fields.append(',');
                    sql_values.append(',');
                }
                sql_fields.append(name);
                sql_values.append('?');
            }
            sql.append(sql_fields).append(" ) values ( ").append(sql_values).append(" )");
            ps = conn.prepareStatement(sql.toString());
            // batch add insert statement            
            while(it.hasNext()){
                CompositeMap record = (CompositeMap)it.next();
                for( int n=1; n<=meta.getColumnCount(); n++){
                    String name = meta.getColumnName(n);
                    Object value = record.get(name);
                    if(value==null) ps.setNull(n, meta.getColumnType(n));
                    else {
                        DatabaseTypeField field = DataTypeManager.getType(value);
                        field.setFieldObject(value, ps, n);
                    }
                }
                ps.addBatch();
            }
            try{
                ps.executeBatch();
                conn.commit();
            }catch(SQLException ex){
                conn.rollback();
                throw ex;
            }
        }finally {
            DBUtil.closeResultSet(meta_rs);
            DBUtil.closeStatement(stmt);
            DBUtil.closeStatement(ps);
            DBUtil.closeConnection(conn);
            
        }
    }

}
