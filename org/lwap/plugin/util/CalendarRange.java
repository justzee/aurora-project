package org.lwap.plugin.util;

import uncertain.composite.CompositeMap;
import uncertain.composite.TextParser;
import uncertain.proc.AbstractEntry;
import uncertain.proc.ProcedureRunner;

public class CalendarRange extends AbstractEntry{
	final String KEY_DAY_OF_MONTH="day_of_month";
	final String KEY_MONTH_OF_YEAR="month_of_year";
	final String KEY_MONTH="month";
	String dateFom=null;
	String dateTo=null;
	String unit;
	String target;
	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public String getDateFom() {
		return dateFom;
	}

	public void setDateFom(String dateFom) {
		this.dateFom = dateFom;
	}

	public String getDateTo() {
		return dateTo;
	}

	public void setDateTo(String dateTo) {
		this.dateTo = dateTo;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		DateUtil tool=new DateUtil();
		String dateFom="2009-10-10";
		String dateTo="2009-12-1";
		try {
			CompositeMap child=tool.getRange(dateFom, dateTo, DateUtil.KEY_MONTH);
			System.out.println(child.toXML());

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void run(ProcedureRunner runner) throws Exception {
		// TODO Auto-generated method stub
		CompositeMap config = runner.getContext();
		CompositeMap model=config.getChild("model");
		unit=TextParser.parse(unit,model);
		dateFom=TextParser.parse(dateFom,model);
		if("".equalsIgnoreCase(dateFom)){
			return;
		}
		dateTo=TextParser.parse(dateTo,model);
		CompositeMap child=new CompositeMap();
		DateUtil tool=new DateUtil();
		if(KEY_DAY_OF_MONTH.equalsIgnoreCase(unit))
			child =tool.getRange(dateFom, dateTo, DateUtil.KEY_DAY_OF_MONTH);
		else if(KEY_MONTH_OF_YEAR.equalsIgnoreCase(unit))
			child =tool.getRange(dateFom, dateTo, DateUtil.KEY_MONTH_OF_YEAR);
		else if(KEY_MONTH.equalsIgnoreCase(unit))
			child =tool.getRange(dateFom, dateTo, DateUtil.KEY_MONTH);
		child.setName(target);
		model.addChild(child);
	}

}
