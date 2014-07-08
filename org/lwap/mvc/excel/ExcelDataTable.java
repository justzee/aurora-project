/**
 * Created on: 2003-9-9 10:11:28
 * Author:     zhoufan
 */
package org.lwap.mvc.excel;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspWriter;

import org.lwap.mvc.BuildSession;
import org.lwap.mvc.DataBindingConvention;

import uncertain.composite.CompositeMap;
import uncertain.composite.DynamicObject;
import uncertain.composite.transform.GroupCompositeMapWithFields;

/**
 * 
 */
public class ExcelDataTable extends DynamicObject {

/*	
	public static final String KEY_CURRENT_ROW 			= "CURRENT_ROW";
	public static final String KEY_CURRENT_COL 			= "CURRENT_COL";
	
	public static final String KEY_DATA_START_ROW 		= "DATA_START_ROW";
	public static final String KEY_DATA_START_COL 		= "DATA_START_COL";
	public static final String KEY_DATA_END_ROW 			= "DATA_END_ROW";
	public static final String KEY_DATA_END_COL 			= "DATA_END_COL";
*/	
	public static final String KEY_STATE = "_state";
    public static final String GETDATA = "getdata";
    public static final String KEY_COLUMN_CONFIG 		= "ColumnConfig";
	public static final String KEY_TABLE_START_RANGE 	= "StartRange";
	
	public static final String KEY_COLUMN_COUNT 			= "ColumnCount";
	public static final String KEY_COLUMN_SPACE 			= "ColumnSpace";	
	public static final String KEY_TABLE_HEAD 			= "CreateTableHead";
	public static final String KEY_TABLE_HEAD_EACHROW 	= "TableHeadEachRow";
	public static final String KEY_STYLE 				= "Style";
	public static final String KEY_FORMAT				= "Format";
	public static final String KEY_TABLE_HEAD_STYLE		= "TableHeadStyle";
	public static final String KEY_TABLE_CELL_STYLE		= "TableCellStyle";	
	public static final String KEY_SUMMARY_FUNCTION		= "SummaryFunction";
	public static final String KEY_TABLE_TYPE			= "TableType";
	public static final String KEY_GROUP_TYPE			= "GroupType";	
	public static final String KEY_GROUP_LEVEL			= "GroupLevel";	
	public static final String KEY_COLUMN_GRAND			= "ColumnGrand";
	public static final String KEY_ROW_GRAND			= "RowGrand";
	public static final String TYPE_PIVOT_TABLE			= "PivotTable";
	public static final String TYPE_SUB_TOTAL			= "SubTotal";
    public static final String KEY_ROWNUM               = "rownum";
    public static final String KEY_ROWEND               = "excel_datatable_row_end";
    public static final String KEY_GROUP_FIELDS         = "GroupFields";
    
	public static final String KEY_PIVOT_TABLE_FUNCTION		= "PivotTableFunction";
	public static HashMap pivot_function_name = new HashMap(20);
	static{
	    pivot_function_name.put("count","-4112");
	    pivot_function_name.put("sum","-4157");
	    pivot_function_name.put("average","-4106");
	    pivot_function_name.put("max","-4136");
	    pivot_function_name.put("min","-4139");
	}
	
	public static String getFunctionVBACode(String fun_name){
	    return (String)pivot_function_name.get(fun_name.toLowerCase());
	}
			
	int 		RowStart;
	int			ColStart;
	int			ColumnCount;
	int			ColumnSpace;
	boolean		CreateTableHead;
	boolean		TableHeadEachRow;
	char 		separator_char = '\t';
	String[]    GroupFields;
	
	PrintWriter   out;
	
	/** total number of records */
	int		total_record_count =0;
	
	/** number of rows created */
	int		total_row_count    =0;
	
	/** count of columns defined in view config */
	public int		table_column_count =0;
	
	CompositeMap model;
	List		 columns;
	
	public static boolean isGenerateData( HttpServletRequest request){
	    return GETDATA.equalsIgnoreCase(request.getParameter(KEY_STATE));
	}

	public void setWriter( Writer o){
		out = new PrintWriter(o);
	}
   
    public void setWriter( JspWriter w){
        out = new PrintWriter(w);
    }
   
	
	void initTableColumns(){
		columns = this.getObjectContext().getChilds();
		
		String col_config = getString(KEY_COLUMN_CONFIG);
		if( col_config != null){
			CompositeMap cols = (CompositeMap)model.getObject(col_config);
			if( cols != null) columns = cols.getChilds();
		}
		
		if( columns != null && model != null){
			int i=0;
			int size = columns.size();
			while( i<size){		
		
				CompositeMap column = (CompositeMap)columns.get(i); //(CompositeMap)it.next();
				col_config = column.getString(KEY_COLUMN_CONFIG);
				if( col_config != null){
					columns.remove(i);
					CompositeMap cols = (CompositeMap)model.getObject(col_config);
					if( cols != null) 
						columns.addAll(i, cols.getChilds());
				}
				i++;
				size = columns.size();
			}
		}

		if( columns != null) table_column_count = columns.size();
	}
	
	public void setModel( CompositeMap m ){
		model = m;
		if( model != null){
			initTableColumns();	
			Collection cl = model.getChilds();
			if( cl != null){ 
				total_record_count = cl.size();
				total_row_count = (int)(total_record_count/ColumnCount);
				if( total_record_count % ColumnCount>0 ) total_row_count++;
			}

		}
			
	}	
		
	static void appendRequestParam(HttpServletRequest request, StringBuffer buf, String key){
		String value = request.getParameter(key);
		buf.append(key).append("=");
		if(value != null) buf.append(value);
	}
	
	public static String getDataURL( HttpServletRequest request ){
		StringBuffer url = request.getRequestURL();
		url.append("?");
		String param = request.getQueryString();
		if(param!=null)
		    url.append(param).append("&");
		url.append(KEY_STATE).append("=").append(GETDATA).append("&");
		Object sid = request.getAttribute(ExcelExportService.KEY_SESSION_ID_STRING);
		if(sid==null) sid = request.getParameter(ExcelExportService.KEY_SESSION_ID_STRING);
		if(sid!=null)
		    url.append(ExcelExportService.KEY_SESSION_ID_STRING).append("=").append(sid.toString());
//		appendRequestParam( request, url, ExcelExportService.KEY_SESSION_ID_STRING);
		return url.toString();
//		appendRequestParam( request, url, ExcelExportService);		
	}
    
    public static Integer getRowEnd(BuildSession session){
        return (Integer)session.getProperty(KEY_ROWEND);
    }
	
	public List getColumns(){
		return columns;
	}
	
	public Iterator getColumnIterator(){
		List columns = getColumns();
		if (columns != null) return columns.iterator();
		else return null;
	}
	
	public static String getWebPath( HttpServletRequest request ){
		StringBuffer url = request.getRequestURL();
		return url.substring(0, url.lastIndexOf("/") + 1);
	}
	
	void printTableHead()
	throws IOException {
		if( getColumns() == null) return;
		for( int i=0; i<ColumnCount; i++){
			Iterator it = getColumnIterator();
			while( it.hasNext()){
				CompositeMap col = (CompositeMap) it.next();
				//out.print(col.getString("Prompt", ""));
				out.print( DataBindingConvention.parseAttribute("Prompt", model, col));
				out.print(separator_char);
			}
			for (int n=0; n<ColumnSpace; n++) out.print(separator_char);
		}
		out.println();
	}
	
	void printTableData( Iterator it, int row )
	throws IOException {
	   if( getColumns() == null) return;
	   int col = 0;
	   //while( it.hasNext()){		   
		   for( int i=0; i<ColumnCount; i++){
		   	   if( !it.hasNext()) break;
		   	   CompositeMap item = (CompositeMap) it.next();
		   	   //System.out.println(item.keySet());
	   		   Iterator columns = getColumnIterator();		   	   
		       
            while( columns.hasNext()){
		       		CompositeMap column = (CompositeMap) columns.next();
                    String fld = column.getString(DataBindingConvention.KEY_DATAFIELD);
		       		Object	value = null;
                    if(KEY_ROWNUM.equals(fld)) value = Integer.toString(row);
                    else value = DataBindingConvention.getDataField(item,column);
		       		if(value==null){
		       			Object fld_name = column.get("Name");
		       			if(fld_name!=null)
		       		  		value = item.get(fld_name);
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
	   //}
	}
	
	public void printTable( CompositeMap model )
	throws IOException
	{
		if(CreateTableHead) printTableHead();
		if( model.getChilds() != null ){ 
			total_record_count = model.getChilds().size();		
			Iterator it = model.getChildIterator();
			if( it == null) return;
			int row=0;
			while( it.hasNext()){
				if( row>0 && TableHeadEachRow && it.hasNext()) {
					printTableHead();
				}
				printTableData( it, row+1);
				row++;
			}
		}
	}
	
	
	public void printTable()
	throws IOException
	{
		printTable( model);	
	}
		
	public List getTableHeadRange(){
		LinkedList	lst = new LinkedList();
		if( this.table_column_count ==0 || !CreateTableHead) return lst;
		int n = TableHeadEachRow? total_row_count: 1;
		for( int i=0; i< n; i++){
			for( int j=0; j<ColumnCount; j++){
				int row = RowStart + i * 2;
				int col_begin = ColStart + j * (table_column_count + ColumnSpace);
				int col_end = col_begin + table_column_count - 1;
				lst.add(ExcelUtil.getRangeName(row,col_begin,row,col_end));
			}
		}
		return lst;
	}
	
	int getTableDataStart(){
		return  RowStart + (CreateTableHead? 1: 0);

	}
	
	int getTableDataEnd(){
		int row_end = RowStart + this.total_row_count * (TableHeadEachRow?2:1) ;
		if( TableHeadEachRow &&  CreateTableHead) row_end--; 
		return row_end;
	}
	
	/** Get all ranges for a certain column 
	 *  @param column_id: No. of column in view config, start from 0 */
	public List getColumnRange( int column_id ){
		LinkedList	lst = new LinkedList();
		int row_start = getTableDataStart();
		/*
		int row_end = RowStart + this.total_row_count * (TableHeadEachRow?2:1) ;
		if( TableHeadEachRow &&  CreateTableHead) row_end--; 
		*/
		int row_end = getTableDataEnd();
		for( int i=0; i<ColumnCount; i++){
			int col = ColStart + column_id + ( this.table_column_count + ColumnSpace ) * i ;
			lst.add(ExcelUtil.getRangeName(row_start, col, row_end, col));
		}
		return lst;
	}
	
	public String getSummaryCell( int column_id, int col_num ){
		int row = getTableDataEnd() + 1 ;
		int col = ColStart + column_id + ( this.table_column_count + ColumnSpace ) * col_num ;
		return ExcelUtil.getCellName(row,col);	
	}
	
	public List getSummaryCells( int column_id) {
		LinkedList	lst = new LinkedList();
		for( int i=0; i<ColumnCount; i++){
			lst.add(getSummaryCell(column_id, i) );
		}
		return lst;		
	}
	
	public List getTableRange(){
		LinkedList	lst = new LinkedList();
		for( int i=0; i<ColumnCount; i++){
			int row_start = RowStart;
			int row_end = getTableDataEnd(); 
			int col_begin = ColStart + ( this.table_column_count + ColumnSpace ) * i;
			int col_end = col_begin + table_column_count - 1;
			lst.add(ExcelUtil.getRangeName(row_start, col_begin, row_end, col_end));
		}
		return lst;
	}
	
	public String getEntireRange(){
		return ExcelUtil.getRangeName( RowStart, ColStart, RowStart + total_row_count, ColStart + table_column_count - 1);
	}

	/**
	 * Returns the colStart.
	 * @return int
	 */
	public int getColStart() {
		return ColStart;
	}

	/**
	 * Returns the columnCount.
	 * @return int
	 */
	public int getColumnCount() {
		return ColumnCount;
	}

	/**
	 * Returns the columnSpace.
	 * @return int
	 */
	public int getColumnSpace() {
		return ColumnSpace;
	}

	/**
	 * Returns the createTableHead.
	 * @return boolean
	 */
	public boolean isCreateTableHead() {
		return CreateTableHead;
	}

	/**
	 * Returns the rowStart.
	 * @return int
	 */
	public int getRowStart() {
		return RowStart;
	}
    
    public int getRowEnd(){
        return getTableDataEnd();
    }

	/**
	 * Returns the tableHeadEachRow.
	 * @return boolean
	 */
	public boolean isTableHeadEachRow() {
		return TableHeadEachRow;
	}

	/**
	 * Sets the colStart.
	 * @param colStart The colStart to set
	 */
	public void setColStart(int colStart) {
		ColStart = colStart;
	}

	/**
	 * Sets the columnCount.
	 * @param columnCount The columnCount to set
	 */
	public void setColumnCount(int columnCount) {
		ColumnCount = columnCount;
	}

	/**
	 * Sets the columnSpace.
	 * @param columnSpace The columnSpace to set
	 */
	public void setColumnSpace(int columnSpace) {
		ColumnSpace = columnSpace;
	}

	/**
	 * Sets the createTableHead.
	 * @param createTableHead The createTableHead to set
	 */
	public void setCreateTableHead(boolean createTableHead) {
		CreateTableHead = createTableHead;
	}

	/**
	 * Sets the rowStart.
	 * @param rowStart The rowStart to set
	 */
	public void setRowStart(int rowStart) {
		RowStart = rowStart;
	}

	/**
	 * Sets the tableHeadEachRow.
	 * @param tableHeadEachRow The tableHeadEachRow to set
	 */
	public void setTableHeadEachRow(boolean tableHeadEachRow) {
		TableHeadEachRow = tableHeadEachRow;
	}
	
	public String getTableHeadStyle(){
		return getString(KEY_TABLE_HEAD_STYLE);
	}
	
	public String getTableCellStyle(){
		return getString(KEY_TABLE_CELL_STYLE);
	}


	/**
	 * @see uncertain.composite.DynamicObject#initialize(CompositeMap)
	 */
	public DynamicObject initialize(CompositeMap context)
		throws ClassCastException {
		super.initialize(context);

		RowStart    = context.getInt(ExcelUtil.KEY_ROW, 1);
		ColStart    = context.getInt(ExcelUtil.KEY_COL, 1);
		ColumnCount = context.getInt(KEY_COLUMN_COUNT, 1);
		ColumnSpace = context.getInt(KEY_COLUMN_SPACE, 1);
		CreateTableHead	= context.getBoolean(KEY_TABLE_HEAD, true);
		TableHeadEachRow= context.getBoolean(KEY_TABLE_HEAD_EACHROW, false);
		String groupfields = context.getString(KEY_GROUP_FIELDS);
		if(groupfields != null)
			GroupFields = groupfields.split("[\\s,]+");
		//initTableColumns();		
		return this;
	}

    /**
     * @return Returns the separator_char.
     */
    public char getSeparatorChar() {
        return separator_char;
    }
    /**
     * @param separator_char The separator_char to set.
     */
    public void setSeparatorChar(char separator_char) {
        this.separator_char = separator_char;
    }
    public List getMergeRange(){
    	return GroupCompositeMapWithFields.getInstance().groupCompositeMap(model, GroupFields);
    }
 }
