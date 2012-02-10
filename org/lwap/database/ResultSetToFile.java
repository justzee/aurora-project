package org.lwap.database;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class ResultSetToFile {
	public String write(ResultSet rs) throws Exception {
		boolean is_write=false;
		PrintWriter pw=null;
		File file=File.createTempFile("excel", ".txt");
		System.out.println("****TempFilePath:"+file.getPath()+"****");
		try{		
			file.deleteOnExit();
			pw=new PrintWriter(file);
			ResultSetMetaData metaData=rs.getMetaData();
			int count=metaData.getColumnCount();
			while(rs.next()){
				is_write=true;
				for(int i=1;i<=count;i++){
					String value=rs.getString(i);
					if(value!=null){
						value=value.replace('\t', ' ');
						value=value.replace('\n', ' ');
						value=value.replace('\r', ' ');						
					}else
						value="";
					pw.print(metaData.getColumnName(i)+"="+value);
					pw.print("\t");
				}
				pw.println();
				pw.flush();
			}
		}finally{
			if(pw!=null)
				pw.close();
		}
		if(is_write)
			return file.getPath();
		else
			return null;
	}
}
