package org.lwap.plugin.excel;
import uncertain.composite.CompositeMap;

public class ExcelTable {
	int rownum=1;
	int colnum=1;
	boolean createTableHead=true;
	boolean tableHeadEachRow=false;
	String dataModel;
	CompositeMap columns=new CompositeMap();
	public String getDataModel() {
		return dataModel;
	}
	public void setDataModel(String dataModel) {
		this.dataModel = dataModel;
	}	
	public int getRow() {
		return rownum;
	}
	public void setRow(int rownum) {
		this.rownum = rownum;
	}
	public int getCol() {
		return colnum;
	}
	public void setCol(int colnum) {
		this.colnum = colnum;
	}
	public boolean getCreateTableHead() {
		return createTableHead;
	}
	public void setCreateTableHead(boolean createTableHead) {
		this.createTableHead = createTableHead;
	}
	public boolean getTableHeadEachRow() {
		return tableHeadEachRow;
	}
	public void setTableHeadEachRow(boolean tableHeadEachRow) {
		this.tableHeadEachRow = tableHeadEachRow;
	}	
	public CompositeMap getColumns() {
		return columns;
	}
	public void addColumn(CompositeMap child) {
		this.columns.addChild(child);		
	}
}
