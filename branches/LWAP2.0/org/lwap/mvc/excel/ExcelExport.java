package org.lwap.mvc.excel;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;

import org.lwap.mvc.DataBindingConvention;

import uncertain.composite.CompositeMap;

public class ExcelExport extends ExcelDataTable{
	ResultSet resultSet;	
	public void setResultSet(ResultSet resultSet,CompositeMap model){
		this.resultSet=resultSet;
		this.model=model;
		if(resultSet!=null){
			initTableColumns();			
		}
	}
	void printTableData(ResultSet resultSet, int row){
		 if(getColumns() == null) return;
		   int col = 0;		      
		   for( int i=0; i<ColumnCount; i++){
	   		   Iterator columns = getColumnIterator();		
            while( columns.hasNext()){
		       		CompositeMap column = (CompositeMap) columns.next();
                    String fld = column.getString(DataBindingConvention.KEY_DATAFIELD);
		       		String	value = null;
                    if(KEY_ROWNUM.equals(fld)) value = Integer.toString(row);
//                    else value = DataBindingConvention.getDataField(item,column);
		       		if(value==null){
		       			String fld_name = fld.replace("@", "");
		       			if(fld_name!=null)
							try {
								value = resultSet.getString(fld_name);
							} catch (SQLException e) {
								value="";
							}
		       		}
		       		if(value != null){
                        String str = value.toString().replace('\n', ' ');
                        str = str.replace('\r', ' ');
                        str = str.replace(separator_char, ' ');
                        boolean flag=false;
                		if(str.indexOf(",")>-1){
                			flag=true;
                		}else if(str.indexOf("\'")>-1){
                			flag=true;			
                		}	
                		if(str.indexOf("\"")>-1){
                			StringBuffer buf=new StringBuffer();
                			boolean is_first=true;
                			String[] strs=str.split("\"");
                			for(int index=0;index<strs.length;index++){
                				if(!is_first)
                					buf.append("\"\"");
                				buf.append(strs[index]);				
                				is_first=false;
                			}			
                			flag=true;
                			str=buf.toString();
                		}		
                		if(flag)
                			str="\""+str+"\"";
                        out.print(str);
                    }
		       		else{
		       		    // do nothing yet
		       		}
		       		out.print(separator_char);
		       		col++;
			   }	   
			   for (int n=0; n<ColumnSpace; n++) out.print(separator_char);
		   }
		   out.println();	
	}
	void printTable(ResultSet resultSet)
	throws IOException
	{
		if(CreateTableHead) printTableHead();
		try {
			int row=0;
			while(resultSet.next()){
				total_record_count++;
				printTableData(resultSet, row+1);
				row++;
			}
		} catch (SQLException e) {			
			e.printStackTrace();
		}		
	}
	
	public void printTable()
	throws IOException
	{
		printTable(resultSet);	
	}
}
