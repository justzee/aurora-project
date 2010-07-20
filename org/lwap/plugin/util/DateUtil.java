package org.lwap.plugin.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import uncertain.composite.CompositeMap;
public class DateUtil {
    public final static int KEY_DAY_OF_MONTH=0;
    public final static int KEY_MONTH_OF_YEAR=1;
    public final static int KEY_MONDAY=2;
    public final static int KEY_TUESDAY=3;
    public final static int KEY_WEDNESDAY=4;
    public final static int KEY_THURSDAY=5;
    public final static int KEY_FRIDAY=6;
    public final static int KEY_SATURDAY=7;
    public final static int KEY_SUNDAY=8;
    public final static int KEY_MONTH=9;
	DateFormat df=new SimpleDateFormat("yyyy-MM-dd");		
	TimeZone timeZone=TimeZone.getDefault();	
	Calendar calendar =Calendar.getInstance(timeZone);
	public DateFormat getDf() {
		return df;
	}
	public void setDf(DateFormat df) {
		this.df = df;
	}
	public TimeZone getTimeZone() {
		return timeZone;
	}
	public void setTimeZone(TimeZone timeZone) {
		calendar.setTimeZone(timeZone);
		this.timeZone = timeZone;
	}	
	public CompositeMap getRange(String dateFromString,String dateToString,int unit) throws Exception{
		Date fromDate=df.parse(dateFromString);
		Date ToDate=null;
		if(dateToString!=null&&!"".equalsIgnoreCase(dateToString)){
			ToDate=df.parse(dateToString);
		}		
		return getRange(fromDate,ToDate,unit);
	}
	public CompositeMap getRange(Date dateFrom,Date dateTo,int unit){
		CompositeMap map=null;
		switch(unit){
			case KEY_DAY_OF_MONTH:map=getRangeByDayOfMonth(dateFrom);break;
			case KEY_MONTH_OF_YEAR:map=getRangeByMonthOfYear(dateFrom);break;
			case KEY_MONDAY:map=getRangeByMonday(dateFrom,dateTo);break;
			case KEY_TUESDAY:map=getRangeByTuesday(dateFrom,dateTo);break;
			case KEY_WEDNESDAY:map=getRangeByWednesday(dateFrom,dateTo);break;
			case KEY_THURSDAY:map=getRangeByThursday(dateFrom,dateTo);break;
			case KEY_FRIDAY:map=getRangeByFriday(dateFrom,dateTo);break;
			case KEY_SATURDAY:map=getRangeBySaturday(dateFrom,dateTo);break;
			case KEY_SUNDAY:map=getRangeBySunday(dateFrom,dateTo);break;
			case KEY_MONTH:map=getRangeByMonth(dateFrom,dateTo);break;
		}		
		return map;
	}
	private CompositeMap getRangeByDayOfMonth(Date date){
		CompositeMap map=new CompositeMap();
		CompositeMap record;		
		calendar.setTime(date);
		calendar.set(Calendar.DAY_OF_MONTH,1);
		while(calendar.getTime().getMonth()==date.getMonth()){
			record=new CompositeMap("record");
			record.put("PROMPT", new String(df.format(calendar.getTime())));
			record.put("DATA_INDEX", new String(df.format(calendar.getTime())));
			map.addChild(record);
			calendar.add(Calendar.DATE, 1);
		}
		return map;
	}
	private CompositeMap getRangeByMonth(Date dateFrom,Date dateTo){
		CompositeMap map=new CompositeMap();
		CompositeMap record;
		calendar.setTime(dateFrom);
		calendar.set(Calendar.DAY_OF_MONTH,1);
		while(dateTo.compareTo(calendar.getTime())!=-1){
			record=new CompositeMap("record");
			record.put("PROMPT", new String(df.format(calendar.getTime())));
			record.put("DATA_INDEX", new String(df.format(calendar.getTime())));
			map.addChild(record);
			calendar.add(Calendar.MONTH, 1);
		}	
		return map;
	}
	private CompositeMap getRangeByMonthOfYear(Date date){
		CompositeMap map=new CompositeMap();
		CompositeMap record;
		calendar.setTime(date);
		calendar.set(Calendar.DAY_OF_MONTH,1);
		for(int i=0;i<12;i++){			
			record=new CompositeMap("record");
			record.put("PROMPT", new String(df.format(calendar.getTime())));
			record.put("DATA_INDEX", new String(df.format(calendar.getTime())));
			map.addChild(record);
			calendar.add(Calendar.MONTH, 1);
		}
		return map;
	}
	private CompositeMap getRangeByMonday(Date dateFrom,Date dateTo){
		CompositeMap map=new CompositeMap();
		CompositeMap record;
		calendar.setTime(dateFrom);
		calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		while(calendar.getTime().compareTo(dateTo)!=1){
			record=new CompositeMap("record");
			record.put("PROMPT", new String(df.format(calendar.getTime())));
			record.put("DATA_INDEX", new String(df.format(calendar.getTime())));
			map.addChild(record);
			calendar.add(Calendar.DATE, 7);
		}
		return map;
	}
	private CompositeMap getRangeByTuesday(Date dateFrom,Date dateTo){
		return null;
	}
	private CompositeMap getRangeByWednesday(Date dateFrom,Date dateTo){
		return null;
	}
	private CompositeMap getRangeByThursday(Date dateFrom,Date dateTo){
		return null;
	}
	private CompositeMap getRangeByFriday(Date dateFrom,Date dateTo){
		return null;
	}
	private CompositeMap getRangeBySaturday(Date dateFrom,Date dateTo){
		return null;
	}
	private CompositeMap getRangeBySunday(Date dateFrom,Date dateTo){
		return null;
	}	
}
