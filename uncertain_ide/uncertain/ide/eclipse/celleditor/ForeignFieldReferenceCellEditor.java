package uncertain.ide.eclipse.celleditor;

import java.util.Iterator;

import org.eclipse.jface.window.Window;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.widgets.Shell;

import uncertain.composite.CompositeLoader;
import uncertain.composite.CompositeMap;
import uncertain.ide.eclipse.editor.bm.GridDialog;
import uncertain.ide.eclipse.editor.widgets.CustomDialog;
import uncertain.ide.eclipse.editor.widgets.GridViewer;
import uncertain.ide.eclipse.editor.widgets.config.ProjectProperties;
import uncertain.ide.util.LocaleMessage;

public class ForeignFieldReferenceCellEditor extends StringTextCellEditor {


	public ForeignFieldReferenceCellEditor(CellProperties cellProperties) {
		super(cellProperties);
	}


	protected void addCellListener() {
		getCellControl().addMouseListener(new MouseListener() {
			
			public void mouseUp(MouseEvent e) {
				
			}
			
			public void mouseDown(MouseEvent e) {
				try {
					fireEvent();
				} catch (Exception e1) {
					CustomDialog.showExceptionMessageBox(e1);
				}
				
			}
			
			public void mouseDoubleClick(MouseEvent e) {
				
			}
		});
		getCellControl().addFocusListener(new FocusListener() {
			
			public void focusLost(FocusEvent e) {
				if(isTableItemEditor())
					fillTableCellEditor(cellProperties.getTableItem());				
			}
			
			public void focusGained(FocusEvent e) {
				
			}
		});
	}
	private void fireEvent() throws Exception{
		CompositeMap parent = cellProperties.getRecord().getParent();
		String fileName = parent.getString("refModel");
		if(fileName == null)
			throw new RuntimeException(LocaleMessage.getString("its.parent's")+"'refModel'"+LocaleMessage.getString("attribute.value.is.null"));
		CompositeLoader loader = new CompositeLoader();
		String path = fileName.replace('.', '/') +'.' + "bm";
		String fullPath = ProjectProperties.getBMBaseDir()+"/"+path;
		CompositeMap root = loader.loadByFullFilePath(fullPath);
		CompositeMap fields = root.getChild("fields");
		CompositeMap filedNames = new CompositeMap();
		for(Iterator it = fields.getChildsNotNull().iterator();it.hasNext();){
			CompositeMap child = (CompositeMap)it.next();
			String targetNode = child.getString("name");
			if(targetNode == null)
				continue;
			CompositeMap newChild = new CompositeMap();
			newChild.put("name", targetNode);
			filedNames.addChild(newChild);

		}
		String[] columnProperties = {"name"};
		GridViewer grid = new GridViewer();
		grid.setData(filedNames);
		grid.setFilterColumn("name");
		grid.setGridProperties(columnProperties);
		GridDialog dialog = new GridDialog(new Shell(),grid);
		if (dialog.open() == Window.OK) {
			String value = dialog.getSelected().getString("name");
			setValue(value);
			cellProperties.getRecord().put(cellProperties.getColumnName(), value);
			cellProperties.getTableViewer().refresh(true);
		}
	}
	private boolean isTableItemEditor(){
		return cellProperties.getTableItem() != null;
	}
}
