package uncertain.ide.eclipse.celleditor;

import java.io.IOException;
import java.util.Iterator;

import org.eclipse.jface.window.Window;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.widgets.Shell;
import org.xml.sax.SAXException;

import uncertain.composite.CompositeLoader;
import uncertain.composite.CompositeMap;
import uncertain.ide.eclipse.bm.editor.GridDialog;
import uncertain.ide.eclipse.editor.widgets.GridViewer;
import uncertain.ide.eclipse.editor.widgets.core.IGridViewer;
import uncertain.ide.eclipse.project.propertypage.ProjectPropertyPage;
import uncertain.ide.help.ApplicationException;
import uncertain.ide.help.AuroraResourceUtil;
import uncertain.ide.help.CustomDialog;
import uncertain.ide.help.LocaleMessage;

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
	private void fireEvent() throws ApplicationException{
		CompositeMap parent = cellProperties.getRecord().getParent();
		String fileName = parent.getString("refModel");
		if(fileName == null)
			throw new ApplicationException(LocaleMessage.getString("its.parent's")+"'refModel'"+LocaleMessage.getString("attribute.value.is.null"));
		CompositeLoader loader = new CompositeLoader();
		String path = fileName.replace('.', '/') +'.' + "bm";
		String fullPath = ProjectPropertyPage.getBMBaseLocalDir(AuroraResourceUtil.getIProjectFromSelection())+"/"+path;
		CompositeMap root;
		try {
			root = loader.loadByFullFilePath(fullPath);
		} catch (IOException e) {
			throw new ApplicationException("文件路径："+fullPath+"不正确", e);
		} catch (SAXException e) {
			throw new ApplicationException("文件解析不正确", e);
		}
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
		GridViewer grid = new GridViewer(IGridViewer.filterBar|IGridViewer.NoToolBar);
		grid.setData(filedNames);
		grid.setFilterColumn("name");
		grid.setGridProperties(columnProperties);
		GridDialog dialog = new GridDialog(new Shell(),grid);
		if (dialog.open() == Window.OK && dialog.getSelected() != null) {
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
