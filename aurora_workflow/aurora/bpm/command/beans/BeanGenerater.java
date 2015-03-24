package aurora.bpm.command.beans;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;

public class BeanGenerater {

	static StringBuilder buff = new StringBuilder();
	static String className;
	static File dir;

	public static void main(String[] args) throws Exception {
		URL url = BeanGenerater.class.getResource("BeanGenerater.class");
		dir = new File(url.getFile()).getParentFile();
		//dir = new File("/Users/jessen/work/Workspaces/WEB/HAP_Blue/src/fnd");
		new BeanGenerater().gen("bpmn_usertask_node");
	}

	void gen(String tableName) throws Exception {
		buff.delete(0, buff.length());
		Connection conn = getConnection();
		try {
			printHeaderInfo(tableName);
			getTableInfo(conn, tableName);
			System.out.println(buff);
			Files.write(Paths.get(dir.getAbsolutePath(), className + ".java"),
					Arrays.asList(buff.toString().split("\\n")));
		} finally {
			closeConnection(conn);
		}
	}

	void printHeaderInfo(String tableName) {
		String[] parts = tableName.split("_");
		StringBuilder sb = new StringBuilder(tableName.length());
		for (String s : parts) {
			sb.append(Character.toUpperCase(s.charAt(0)));
			sb.append(s.substring(1));
		}
		className = sb.toString();
		
		buff.append("package ").append(BeanGenerater.class.getPackage().getName()).append(";\n");
		buff.append("import aurora.sqlje.core.annotation.*;\n");
		

		buff.append(String.format("@Table(name=\"%s\",stdwho=true)\n",
				tableName.toUpperCase()));
		buff.append(String.format("public class %s\n{\n", className));
	}

	void getTableInfo(Connection conn, String tn) throws Exception {
		ResultSet rs = conn.getMetaData().getColumns(null, null,
				tn.toUpperCase(), "%");
		while (rs.next()) {
			String col_name = rs.getString("COLUMN_NAME").toLowerCase();
			String db_type = rs.getString("TYPE_NAME");
			String javaType = type_map.get(db_type);
			if(javaType==null)
				javaType=db_type;
			String comment = rs.getString("REMARKS");
			if(comment!=null&&comment.length()>0)
				buff.append(String.format("\t/**%s*/\n",rs.getString("REMARKS")));
			if("YES".equals(rs.getString("IS_AUTOINCREMENT")))
				buff.append("\t@PK\n");
			buff.append(String.format("\tpublic %s %s;\n", javaType, col_name));
			// System.out.printf("column name:%30s type:%s\n",rs.getString("COLUMN_NAME"),rs.getString("TYPE_NAME"));
		}
		buff.append("}");
	}

	Connection getConnection() throws Exception {
		Class.forName("com.mysql.jdbc.Driver");
		return DriverManager.getConnection(
				"jdbc:mysql://172.20.0.38:3306/aurora_workflow", "bpmn_dev",
				"bpmn_dev");
	}

	void closeConnection(Connection conn) {
		if (conn != null)
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
	}

	static HashMap<String, String> type_map = new HashMap<String, String>();
	static {
		type_map.put("INT", "Long");
		type_map.put("BIGINT", "Long");
		type_map.put("DECIMAL", "Long");
		type_map.put("TINYINT", "Integer");
		type_map.put("DATETIME", "java.sql.Date");
		type_map.put("DATE", "java.sql.Date");
		type_map.put("VARCHAR", "String");
		type_map.put("TEXT", "String");
	}

}
