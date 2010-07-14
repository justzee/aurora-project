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
	public CompositeMap convetDateByUnit(String dateFromString,String dateToString,int unit) throws Exception{
		Date fromDate=df.parse(dateFromString);
		Date ToDate=null;
		if(dateToString!=null&&!"".equalsIgnoreCase(dateToString)){
			ToDate=df.parse(dateToString);
		}		
		return convetDateByUnit(fromDate,ToDate,unit);
	}
	public CompositeMap convetDateByUnit(Date dateFrom,Date dateTo,int unit){
		CompositeMap map=null;
		switch(unit){
			case 0:map=convetDateByDayOfMonth(dateFrom);break;
			case 1:map=convetDateByMonthOfYear(dateFrom);break;
			case 2:map=convetDateByMonday(dateFrom,dateTo);break;
			case 3:map=convetDateByTuesday(dateFrom,dateTo);break;
			case 4:map=convetDateByWednesday(dateFrom,dateTo);break;
			case 5:map=convetDateByThursday(dateFrom,dateTo);break;
			case 6:map=convetDateByFriday(dateFrom,dateTo);break;
			case 7:map=convetDateBySaturday(dateFrom,dateTo);break;
			case 8:map=convetDateBySunday(dateFrom,dateTo);break;
		}		
		return map;
	}
	private CompositeMap convetDateByDayOfMonth(Date date){
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
	private CompositeMap convetDateByMonthOfYear(Date date){
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
	private CompositeMap convetDateByMonday(Date dateFrom,Date dateTo){
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
	private CompositeMap convetDateByTuesday(Date dateFrom,Date dateTo){
		return null;
	}
	private CompositeMap convetDateByWednesday(Date dateFrom,Date dateTo){
		return null;
	}
	private CompositeMap convetDateByThursday(Date dateFrom,Date dateTo){
		return null;
	}
	private CompositeMap convetDateByFriday(Date dateFrom,Date dateTo){
		return null;
	}
	private CompositeMap convetDateBySaturday(Date dateFrom,Date dateTo){
		return null;
	}
	private CompositeMap convetDateBySunday(Date dateFrom,Date dateTo){
		return null;
	}
}
