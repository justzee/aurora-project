/*
 * Created on 2008-11-12
 */
package org.lwap.database.oracle;

import java.sql.Driver;

import oracle.jdbc.driver.OracleDriver;

public class JDBCVersion {

    /**
     * @param args
     */
    public static void main(String[] args) {
        /*
        DriverManager.registerDriver (new OracleDriver());

        Connection conn = DriverManager.getConnection
             ("jdbc:oracle:thin:@hostname:1526:orcl", "scott", "tiger");
        */
        java.sql.Driver driver = new oracle.jdbc.driver.OracleDriver();
        System.out.println("Oracle jdbc driver:"+driver.getMajorVersion()+" "+driver.getMinorVersion());
    }

}
