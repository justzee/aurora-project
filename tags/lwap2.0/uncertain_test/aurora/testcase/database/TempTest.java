/*
 * Created on 2009-9-1
 */
package aurora.testcase.database;

import java.io.FileInputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class TempTest {

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {

        URL url = Thread.currentThread().getContextClassLoader().getResource("aurora/testcase/database/test.sql");
        String file = url.getFile();
        StringBuffer sql = new StringBuffer();
        FileInputStream fis = new FileInputStream(file);
        int b = fis.read();
        while(b>0){
            sql.append((char)b);
            b = fis.read();
        }
        DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
        Connection conn = DriverManager.getConnection(
                "jdbc:oracle:thin:@192.168.11.246:1521:masdemo", "hec", "hec");
        long tick = System.currentTimeMillis();
        System.out.println(sql.toString());
        System.out.println("begin");

        PreparedStatement ps = conn.prepareCall(sql.toString());
        ps.setLong(1, 762);
        ps.setString(2, "COMPLETELY_APPROVED");
        ResultSet rs = ps.executeQuery();
        tick = System.currentTimeMillis() - tick;
        System.out.println("end: "+ tick);
        rs.close();
        ps.close();
        conn.close();

    }

}
