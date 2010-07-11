package org.lwap.plugin.util;

import uncertain.composite.CompositeMap;
import uncertain.proc.AbstractEntry;
import uncertain.proc.ProcedureRunner;

public class ConvertDateByUnit extends AbstractEntry{
	final String KEY_DAY_OF_MONTH="day_of_month";
	final String KEY_MONTH_OF_YEAR="month_of_year";
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
		String dateTo=null;
		try {
			CompositeMap child=tool.convetDateByUnit(dateFom, dateTo, DateUtil.KEY_DAY_OF_MONTH);
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
		CompositeMap child=null;
		DateUtil tool=new DateUtil();
		if(KEY_DAY_OF_MONTH.equalsIgnoreCase(unit))
			child =tool.convetDateByUnit(dateFom, dateTo, DateUtil.KEY_DAY_OF_MONTH);
		else if(KEY_MONTH_OF_YEAR.equalsIgnoreCase(unit))
			child =tool.convetDateByUnit(dateFom, dateTo, DateUtil.KEY_MONTH_OF_YEAR);
		child.setName(target);
		if(child!=null)
			model.addChild(child);
	}

}
