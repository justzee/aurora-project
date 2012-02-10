/**
 * Created on: 2003-9-25 11:29:57
 * Author:     zhoufan
 */
package org.lwap.mvc.excel;

import java.io.IOException;

import javax.servlet.jsp.JspWriter;

import org.lwap.mvc.BuildSession;

import uncertain.composite.CompositeMap;

/**
 * 
 */
public class ExcelSheet {
	
	public static final String KEY_LAST_SHEET_NUM = "LastSheetNum";
	public static final String KEY_SHEET_NUM = "SheetNum";	
	
	
	public static int getSheetNum( BuildSession _session ){
	   Integer last_sheet_num = (Integer)_session.getProperty(KEY_LAST_SHEET_NUM);
	   if( last_sheet_num == null){
	      last_sheet_num = new Integer(1);
	   } else {
	      last_sheet_num = new Integer( last_sheet_num.intValue() + 1 );
	   }
	   _session.setProperty(KEY_LAST_SHEET_NUM, last_sheet_num); 
	   return last_sheet_num.intValue();  
	}
	
	public static int getSheetNum( CompositeMap view, BuildSession _session){
		int sheet_num = view.getInt("SheetNum", -1);
		if( sheet_num == -1 ){
		   sheet_num = getSheetNum(_session);
		}
		return sheet_num;
	}
	
	public static void createNewSheet( JspWriter out, int sheet_num, String title ) throws IOException {
		out.println("If Excel.Sheets.Count < " + sheet_num + " Then");
		out.println("	Excel.Sheets.Add");
		out.println("Else");
		out.println("	Excel.Sheets("+sheet_num+").Select");		
		out.println("End If");

		out.println("With Excel.ActiveSheet");
		out.println("	.Select");
	    if(title != null)
		out.println("	.Name = \"" + title + "\"");
		out.println("End With");
	}

}
