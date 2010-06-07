/**
 * Created on: 2003-12-29 16:55:12
 * Author:     zhoufan
 */
package org.lwap.ui.web;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import org.lwap.ui.DataControl;

import uncertain.composite.CompositeMap;
import uncertain.composite.DynamicObject;
	
	
public class DataCalendar extends DataControl {
	
	public static final String[] day_names = {"日","一","二","三","四","五","六"};
	
	public static final String CellWidth = "CellWidth";

	public static final String CellHeight = "CellHeight";

	public static final String CellClass = "CellClass";

	public static final String HeadClass = "HeadClass";

	public static final String BodyClass = "BodyClass";
	
	public static final int MAX_WEEK_COUNT = 6;

/*
	public static final String Month = "Month";

	public static final String Year = "Year";

	public static final String Date = "Date";
*/	
	public static final String FieldForDate = "FieldForDate";
	
	public static final String DisplayOtherMonth = "DisplayOtherMonth";

	public static final String StartingDateField = "StartingDateField";
	
	public static final String DataCell = "data-cell";
	
	boolean		inited = false;
	boolean		display_other_month = false;
	Date			param_starting_date;
	Calendar		grid_starting_date = Calendar.getInstance();
	Calendar	    month_starting_date = Calendar.getInstance();
	CompositeMap	date_grid[][] = new CompositeMap[MAX_WEEK_COUNT][7];
	
	static void trim_date( Calendar cl){
		cl.set(Calendar.HOUR,0);
		cl.set(Calendar.MINUTE,0);
		cl.set(Calendar.SECOND,0);
		cl.set(Calendar.MILLISECOND,0);
	}
	
	static void setCalendarToFirstDay( Calendar cl, Date date){
		cl.setTime(date);
		cl.set(Calendar.DAY_OF_MONTH, 1);
		cl.add(Calendar.DATE, -1*cl.get(Calendar.DAY_OF_WEEK) + 1);
		trim_date(cl);
	}
	
	
	
	public void init(){
		if( model == null) return;
		display_other_month = getDisplayOtherMonth();
		String date_fld = getStartingDateField();
		if( date_fld == null) throw new IllegalArgumentException("DateCalendar: StartingDateField not set");
		
		param_starting_date = (Date)model.getObject(date_fld);
		if(param_starting_date == null) throw new IllegalArgumentException("DateCalendar: can't get starting date from model with path" + date_fld);
		//System.out.println("starting date:"+param_starting_date);
		
		/* set month_starting_date to first day of month */
		month_starting_date.setTime(param_starting_date);
		month_starting_date.set(Calendar.DAY_OF_MONTH,1);
		trim_date(month_starting_date);
				
		/* set grid_starting_date to first day of week of ( first day of month of starting_date) */
		setCalendarToFirstDay(grid_starting_date, param_starting_date);
		// System.out.println("starting_date:"+starting_date+" init:"+this.grid_starting_date.getTime());

		Iterator it = model.getChildIterator();
		if( it == null) return;
		date_fld = this.getFieldForDate();
		if( date_fld == null)  throw new IllegalArgumentException("DateCalendar: FieldForDate not set");
		while( it.hasNext()){
			CompositeMap item = (CompositeMap) it.next();
			Date date = (Date)item.getObject(date_fld);
			if( date != null)
				this.setDataOfDay(date, item);
/*
			else{
				System.out.println("DateCalendar: warning: can't get " + date_fld + " attribute from model");
				System.out.println(item.toXML());
			}	
*/			
		}
		inited = true;
	}
	
	public boolean isInited(){
		return inited;
	}

	/**
	 * @see org.lwap.ui.DataControl#setModel(CompositeMap)
	 */
	public void bindModel(CompositeMap m) {
		super.bindModel(m);
		init();
	}

	/** return attribute CellWidth */
	public String getCellWidth (){
		return getObjectContext().getString(CellWidth, "14%");
	}

	/** return attribute CellHeight */
	public String getCellHeight (){
		return getObjectContext().getString(CellHeight);
	}

	/** return attribute CellClass */
	public String getCellClass (){
		return getObjectContext().getString(CellClass);
	}

	/** return attribute HeadClass */
	public String getHeadClass (){
		return getObjectContext().getString(HeadClass, "CalendarHead");
	}

	/** return attribute BodyClass */
	public String getBodyClass (){
		return getObjectContext().getString(BodyClass, "CalendarBody");
	}

	/*
	public Integer getMonth (){
		return getObjectContext().getInt(Month);
	}

	public Integer getYear (){
		return getObjectContext().getInt(Year);
	}

	public Date getDate (){
		return (Date)getObjectContext().getObject(Date);
	}
	*/

	/** set attribute CellWidth */
	public void setCellWidth (String _value ){
		getObjectContext().putString( CellWidth, _value);
	}

	/** set attribute CellHeight */
	public void setCellHeight (String _value ){
		getObjectContext().putString( CellHeight, _value);
	}

	/** set attribute CellClass */
	public void setCellClass (String _value ){
		getObjectContext().putString( CellClass, _value);
	}

	/** set attribute HeadClass */
	public void setHeadClass (String _value ){
		getObjectContext().putString( HeadClass, _value);
	}

	/** set attribute BodyClass */
	public void setBodyClass (String _value ){
		getObjectContext().putString( BodyClass, _value);
	}

	/*
	public void setMonth (Integer _value ){
		getObjectContext().put( Month, _value);
	}

	public void setYear (Integer _value ){
		getObjectContext().put( Year, _value);
	}

	public void setDate (Date _value ){
		getObjectContext().put( Date, _value);
	}
	*/
	
	public String getFieldForDate(){
		return getObjectContext().getString(FieldForDate);
	}
	
	/** return attribute DisplayOtherMonth */
	public boolean getDisplayOtherMonth (){
		return getObjectContext().getBoolean(DisplayOtherMonth, false);
	}

	/** return attribute StartingDateField */
	public String getStartingDateField (){
		return getObjectContext().getString(StartingDateField);
	}

	/** set attribute DisplayOtherMonth */
	public void setDisplayOtherMonth (boolean _value ){
		getObjectContext().putBoolean( DisplayOtherMonth, _value);
	}

	/** set attribute StartingDateField */
	public void setStartingDateField (String _value ){
		getObjectContext().putString( StartingDateField, _value);
	}
	

	public String getDayName( int day_of_week ){
		//assert day_of_week>=1 && day_of_week<=7;
		return day_names[day_of_week];
	}
	
	public int getWeekCount(){
		return MAX_WEEK_COUNT;
	}
	
	public CompositeMap getDataOfDay(int week, int day_of_week){
		return date_grid[week][day_of_week];
	}
	
	public boolean accepts( int week, int day_of_week){
		//assert week>0 &&  day_of_week>0 && day_of_week<7;
		if(week>=this.getWeekCount()) return false;
		Date date = this.getDate(week, day_of_week);
		Calendar cl = Calendar.getInstance();
		cl.setTime(date);
		if( cl.get(Calendar.YEAR)  == month_starting_date.get(Calendar.YEAR)
		 && cl.get(Calendar.MONTH) == month_starting_date.get(Calendar.MONTH)
		) 
			return true;
		else
			return this.display_other_month;		
	}
	
	public void setDataOfDay( Date date, CompositeMap item){
		Calendar cl = Calendar.getInstance();
		cl.setTime(date);
		long offset_days = (long)Math.round((date.getTime()-grid_starting_date.getTimeInMillis())/86400000D);
		//System.out.println(offset_days);
		if(offset_days<0) return;
		int week = (int)(offset_days/7);
		if( week>getWeekCount()) return;
		int dow = (int)(offset_days % 7);
		//System.out.println(date+" at " + week+","+dow + " offset:"+offset_days);
		this.date_grid[week][dow] = item;
	}
	
	/** get date of No.day_of_week in No. week 
	 *  @param week No. of week, starting with 0
	 *  @param day_of_week, starting with 0
	 * */
	public Date getDate( int week, int day_of_week ){
		Calendar cl = (Calendar)grid_starting_date.clone();
		cl.add( Calendar.DATE, week*7 + day_of_week );
		return cl.getTime();
	}
	
	public Collection getDataView(){
		CompositeMap data_cell =  getObjectContext().getChild(DataCell);
		if( data_cell != null) return data_cell.getChilds();
		else return null;
	}
	
	public static void main(String[] args) throws Exception {
		/*
		CompositeMap n[][] = new CompositeMap[2][3];
		for( int i=0; i<2; i++){
			for( int j=0; j<3; j++){
				System.out.print( (n[i][j] == null? "null ":"not null "));
			}
			System.out.println();
		}
		*/
		Calendar cl = Calendar.getInstance();
		CompositeMap model = new CompositeMap("root");
		model.put("DATE", cl.getTime());
		model.putObject("@TEST", "test", '@');
		CompositeMap view = new CompositeMap();
		view.put(StartingDateField, "@DATE");
		
		DataCalendar dc = (DataCalendar)DynamicObject.cast(view, DataCalendar.class);
		dc.bindModel(model);
		
		DateFormat df = DateFormat.getDateInstance();
	
//		System.out.println(df.format(cl.getTime()));
		System.out.println(dc.getDate(2,2));
		
		cl.set(Calendar.DAY_OF_MONTH,23);
		dc.setDataOfDay(cl.getTime(), model);
		System.out.println(dc.getDataOfDay(3,2));
		
		System.out.println(dc.accepts(0,0));		

		
	}
		

}
		
	
