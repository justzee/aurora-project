/**
 * Created on: 2003-9-9 16:18:37
 * Author:     zhoufan
 */
package org.lwap.mvc.excel;

import org.lwap.mvc.BuildSession;

import uncertain.composite.CompositeMap;

/**
 * 
 */
public class ExcelUtil {
	
	public static final String KEY_ROW 					= "Row";
	public static final String KEY_COL 					= "Col";
    public static final String VALUE_FOOTER             = "footer";
    public static final String KEY_ROWOFFSET               = "RowOffset";
	
	
	public static String getColumnName( int col){
		col -= 1;
		int m = col % 26;
		int n = (int)(col/26);
				
		int ch = 'A' + m;		
		String str = Character.toString((char)ch);		
		if( n <=0 ) return str;
		else return getColumnName(n) + str;
	}
	
	public static String getCellName( int row, int col){
		return getColumnName( col) + row;
	}
	
	public static String getRangeName( int row_start, int col_start, int row_end, int col_end ){
		return getCellName(row_start, col_start) + ":" + getCellName(row_end, col_end);
	}
	
	public static String getRangeName( int row, int col){
		return "R" + row + 'C' + col;
	}
    
    public static String getStartingRange( CompositeMap view ){
        return getStartingRange(view,null);
    }
	
	/** to be enhanced */
	public static String getStartingRange( CompositeMap view, BuildSession session ){
		String range = null;
        int row_start = 1; 
        String row = view.getString(KEY_ROW);
        if(VALUE_FOOTER.equals(row) && session!=null){
            Integer end = ExcelDataTable.getRowEnd(session);            
            if(end==null) throw new IllegalStateException("No previous data table has been created, so can't locate to data footer");
            int offset = view.getInt(KEY_ROWOFFSET, 0);
            row_start = end.intValue()+1+offset;            
        } 
        else row_start = view.getInt(KEY_ROW, 1);		
        int col_start = view.getInt(KEY_COL, 1);
		
		if( row_start >=0 && col_start>=0) range = "Cells(" + row_start + "," + col_start + ")";
		String rg_str = view.getString(ExcelDataTable.KEY_TABLE_START_RANGE);
		if( range == null) range = "Range(\"" + rg_str + "\")";
		if( range == null) range = "ActiveCell";
		
		return range;	
	}

	public static void main(String[] args) throws Exception {
		
		System.out.println( getRangeName(2,3, 10, 8));
	}
	

}
