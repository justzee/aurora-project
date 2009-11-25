package uncertain.ide.eclipse.editor;

import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import uncertain.composite.CompositeMap;
import uncertain.ide.Activator;
import uncertain.ide.eclipse.action.IDirty;
import uncertain.schema.Array;
import uncertain.schema.Attribute;
import uncertain.schema.Element;
import uncertain.schema.IType;

public class AuroraPropertyArrayEditor {

	public static final String COLUMN_PROPERTY = "PROPERTY";
	public static final String COLUMN_VALUE = "VALUE";
	public static final String[] TABLE_COLUMN_PROPERTIES = {COLUMN_PROPERTY,COLUMN_VALUE};
	TableViewer mPropertyViewer;
	Table mTable;
	CompositeMap mData;
	IDirty mDirtyObject;

	public AuroraPropertyArrayEditor(IDirty dirtyObject) {
		super();
		mDirtyObject = dirtyObject;
	}

	public void setData(CompositeMap data) {
	    mData = data;
	    if(mTable != null){
	        createEditor( mTable.getParent() );
		   	createTableColumns();
		   	if(mData.getChilds() != null)
		   		mPropertyViewer.setInput(mData);
	    }
	}

	public CompositeMap getData() {
	    return mData;
	}

	public void clearAll() {
		if(mPropertyViewer != null)
			mPropertyViewer.getTable().dispose();
	}

	protected void createTableColumns() {
	    Element elm = Activator.getSchemaManager().getElement(mData);
	    if(elm == null)
	        throw new IllegalArgumentException("Can't get element schema from "+mData.toXML());
	    if(elm instanceof Array ){
	        Array array = (Array)elm;
	        IType type = array.getElementType();
	        if(type==null)
	            throw new IllegalArgumentException("Can't get array type from "+array.getQName());
	        if(type instanceof Element){
	            Element type_element = (Element)type;
	            List attrib_list = type_element.getAllAttributes();
	            if( attrib_list==null ) return;
	            String[] column_index = new String[attrib_list.size()];
	            CellEditor[] editors = new CellEditor[attrib_list.size()];
	            int id=0;
	            for(Iterator it = attrib_list.iterator(); it.hasNext(); ){                    
	                Attribute attrib = (Attribute)it.next();
	                editors[id] = new TextCellEditor(mTable);
	                column_index[id++] = attrib.getName(); 
	            }
	            mPropertyViewer.setColumnProperties(column_index);
	            mPropertyViewer.setLabelProvider(new PropertyArrayLabelProvider(attrib_list.toArray()));
	            for( Iterator it = attrib_list.iterator(); it.hasNext(); ){
	                Attribute attrib = (Attribute)it.next();
	                TableColumn column = new TableColumn(mTable, SWT.LEFT);
	                column.setText(attrib.getName());
	            	String imagePath="icons/attribute_obj.gif";
	                column.setImage(Activator.getImageDescriptor(imagePath).createImage());
	                column.setWidth(80);
	            }
	            mPropertyViewer.setCellEditors(editors);
	        }else{
	            throw new IllegalArgumentException("Type "+type.getQName()+" is not element");
	        }
	    }else
	        throw new IllegalArgumentException("Type "+elm.getQName()+" is not array");
	}

	public void createEditor(Composite parent) {
	    mPropertyViewer = new TableViewer(parent,SWT.BORDER|SWT.FULL_SELECTION);
	    mTable = mPropertyViewer.getTable();
	    mPropertyViewer.setContentProvider(new PropertyArrayContentProvider() );
	    mPropertyViewer.setCellModifier( new AuroraPropertyArrayCellModifier(mPropertyViewer,mDirtyObject));
	    mTable.setLinesVisible(true);
	    mTable.setHeaderVisible(true);
	
	
	}

	public void createEditor(Composite parent, CompositeMap data) {
		createEditor( parent );
		setData( data );
	    
	    
	}

	public TableViewer getTableViewer() {
	    return mPropertyViewer;
	}

	public Table getTable() {
	    return mTable;
	}

	public boolean IsCategory() {
		// TODO Auto-generated method stub
		return false;
	}

	public void setIsCategory(boolean isCategory) {
		// TODO Auto-generated method stub
		
	}

	public ColumnViewer getObject() {
		// TODO Auto-generated method stub
		return null;
	}

	public CompositeMap getSelectedData() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setSelectedData(CompositeMap data) {
		// TODO Auto-generated method stub
		
	}

	public void makeDirty() {
		// TODO Auto-generated method stub
		
	}

	public void setFocusData(CompositeMap data) {
		// TODO Auto-generated method stub
		
	}

}