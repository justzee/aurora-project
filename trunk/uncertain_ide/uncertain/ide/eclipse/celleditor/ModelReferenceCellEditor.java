package uncertain.ide.eclipse.celleditor;

import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PlatformUI;

import uncertain.composite.CompositeMap;
import uncertain.ide.eclipse.editor.bm.GridDialog;
import uncertain.ide.eclipse.editor.widgets.CustomDialog;
import uncertain.ide.eclipse.editor.widgets.GridViewer;
import uncertain.ide.eclipse.editor.widgets.IGridViewer;
import uncertain.ide.eclipse.wizards.ProjectProperties;

public class ModelReferenceCellEditor extends StringTextCellEditor {

	public ModelReferenceCellEditor(CellProperties cellProperties) {
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

		IEditorInput input = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor().getEditorInput();
		IFile ifile = ((IFileEditorInput) input).getFile();
		IProject project = ifile.getProject();
		String bmFilesDir = ProjectProperties.getBMBaseDir(project);
		File baseDir = new File(bmFilesDir);
		String fullPath = baseDir.getAbsolutePath();
		CompositeMap bmFiles = getAllBMFiles(baseDir,fullPath);
		
		String[] columnProperties = {"name","fullpath"};
		GridViewer grid = new GridViewer(null,IGridViewer.filterBar);
		grid.setData(bmFiles);
		grid.setFilterColumn("name");
		grid.setGridProperties(columnProperties);
		GridDialog dialog = new GridDialog(new Shell(),grid);
		if (dialog.open() == Window.OK) {
			String value = dialog.getSelected().getString("fullpath");
			setValue(value);
			cellProperties.getRecord().put(cellProperties.getColumnName(), value);
			cellProperties.getTableViewer().refresh(true);
		}
	}


	private CompositeMap getAllBMFiles(File rootFile,String fullPath) {
		CompositeMap bmFiles = new CompositeMap();
		getChilds(rootFile,bmFiles,fullPath);
		return bmFiles;
		
	}
	private void getChilds(File file,CompositeMap parent,String fullPath){
		if(file.isDirectory()){
			File[] nextLevel = file.listFiles();
			for(int i = 0;i<nextLevel.length;i++){
				getChilds(nextLevel[i],parent,fullPath);
			}
		}
		else if(file.getName().toLowerCase().endsWith(".bm")){
			CompositeMap child = new CompositeMap();
			String fullpath = getClassName(file,fullPath);
			child.put("name",file.getName());
			child.put("fullpath",fullpath);
			parent.addChild(child);
		}
	}


	private String getClassName(File file,String fullpath) {
		String path = file.getPath();
		int end = path.indexOf(".");
		path = path.substring(fullpath.length()+1,end);
		path = path.replace(File.separatorChar, '.');
		return path;
	}
	private boolean isTableItemEditor(){
		return cellProperties.getTableItem() != null;
	}
}
