package org.lwap.plugin.output;

import uncertain.composite.CompositeMap;

public class TextTable {
	boolean createtablehead = true;
	String separator="|";
	String datamodel;
	CompositeMap columns=new CompositeMap();
	
	public boolean getCreatetablehead() {
		return createtablehead;
	}

	public void setCreatetablehead(boolean createtablehead) {
		this.createtablehead = createtablehead;
	}

	public String getDatamodel() {
		return datamodel;
	}

	public void setDatamodel(String datamodel) {
		this.datamodel = datamodel;
	}

	public String getSeparator() {
		return separator;
	}

	public void setSeparator(String separator) {
		this.separator = separator;
	}	

	public CompositeMap getColumn() {
		return columns;
	}

	public void setColumn(CompositeMap column) {
		columns.addChild(column);
	}

}
