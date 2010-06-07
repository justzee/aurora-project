/**
 * Created on: 2003-9-10 16:02:06
 * Author:     zhoufan
 */
package org.lwap.database.oracle;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import oracle.sql.BLOB;

import org.lwap.database.DBUtil;

/**
 *  write binary stream to blob field
 */
public class BlobUtil {

  /**
   * get object from resultset
   * @param ResultSet rs
   * @param int fld
   * @return Object
   */
  public static Object loadObject(ResultSet rs, int fld) throws SQLException,
      IOException, ClassNotFoundException {
    Blob blb = rs.getBlob(fld);
    if (blb == null) {
      return null;
    }
    else {
      ObjectInputStream ois = new ObjectInputStream(blb.getBinaryStream());
      Object obj = ois.readObject();
      ois.close();
      return obj;
    }
  }

  public static Object loadObject(Connection conn, String sql) throws
      SQLException, IOException, ClassNotFoundException 
  {
    PreparedStatement pst = null;
    ResultSet rs = null;
    try{
        Object returnValue = null;
        pst = conn.prepareStatement(sql);
        rs = pst.executeQuery();
        while (rs.next())
            returnValue = loadObject(rs,1);
        return returnValue;
    }finally{
        DBUtil.closeResultSet(rs);
        DBUtil.closeStatement(pst);
    }
  }

  /**
   * save Object to Table
   * @param Connection cn
   * @param String table_name
   * @param String column_name
   * @param String where_clause
   * @param object obj
   */
  public static void saveObject(Connection conn, String table_name,String column_name,
                                String where_clause, Serializable obj) throws
      SQLException, IOException, ClassNotFoundException {
    String sql_update = "UPDATE "+ table_name + " SET "+ column_name +" = empty_blob() WHERE "+where_clause;
    String sql_select = "SELECT "+ column_name + " FROM "+ table_name +" WHERE "+ where_clause +" FOR UPDATE";
    BLOB blob = null;
    //save blob
    conn.setAutoCommit(false);
    Statement st = null;
    ResultSet rs= null;
    try{
        st = conn.createStatement();
        int p1 = st.executeUpdate(sql_update);
    
        st.execute("commit");
        rs =  st.executeQuery(sql_select);
        while (rs.next())
             blob=((oracle.jdbc.driver.OracleResultSet)rs).getBLOB(1);
        if(blob==null) throw new IllegalStateException("can't get blob from sql: "+sql_select);
    
        //get InputStream from object
        InputStream instream = getInstreamFromObj(obj);
    
        //save InputStream to blob
        OutputStream outstream = blob.getBinaryOutputStream(0);// blob.getBinaryOutputStream();
        int chunk = blob.getChunkSize();
        byte[] buff = new byte[chunk];
        int le;
        while ( (le = instream.read(buff)) != -1) {
          outstream.write(buff, 0, le);
        }
        outstream.close();
        st.execute("commit");    
        instream.close();
        conn.commit();
    } finally{
        DBUtil.closeResultSet(rs);
        DBUtil.closeStatement(st);
    }

  }

  /**
   * get InputStream from object
   * @param object
   * @return InputStream
   */
  static InputStream getInstreamFromObj(Serializable obj) throws IOException{
    ByteArrayOutputStream stream = new ByteArrayOutputStream();
    ObjectOutputStream oops = new ObjectOutputStream(stream);
    oops.writeObject(obj);
    oops.flush();
    oops.close();
    byte bytes[] = stream.toByteArray();
    stream.close();
    InputStream instream = new BufferedInputStream(new ByteArrayInputStream(bytes));
    return instream;
  }

}
