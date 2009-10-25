package uncertain.ide.eclipse.editor.service;

import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Tree;

import uncertain.composite.CompositeMap;
import uncertain.ide.eclipse.action.ElementDoubleClickListener;
import uncertain.ide.eclipse.action.IDirty;
import uncertain.ide.eclipse.action.IViewerDirty;
import uncertain.ide.eclipse.action.ServcieActionGroup;
import aurora_ide.Activator;

public class ServiceTreeEditor implements IViewerDirty{
	TreeViewer mTreeViewer;
	IDirty mDirtyAction;
	CompositeMap mSelectedData;
	CompositeMap mFocusData;
	public ServiceTreeEditor(Tree tree, IDirty mDirtyAction,
			CompositeMap data) {
		this.mDirtyAction = mDirtyAction;
		createEditorContent(tree, data);
	}

	private void createEditorContent(Tree tree, CompositeMap data) {
		mTreeViewer = new TreeViewer(tree);
		mTreeViewer.setLabelProvider(new ServiceTreeLabelProvider());
		mTreeViewer.setContentProvider(new ServiceTreeContentProvider(Activator
				.getSchemaManager(), data));


		CompositeMap parent = data.getParent();
		
		if(parent==null){
			CompositeMap root = new CompositeMap("root");
			root.addChild(data);
			parent = root;
		}

		mTreeViewer.setInput(parent);
		
		ServcieActionGroup servcieActionGroup = new ServcieActionGroup(this);
		servcieActionGroup.fillContextMenu();
		servcieActionGroup.fillDNDListener();
		servcieActionGroup.fillKeyListener();
		mTreeViewer.addDoubleClickListener(new ElementDoubleClickListener(this));
	}

	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		mTreeViewer.addSelectionChangedListener(listener);
	}

	public Control getControl() {
		return mTreeViewer.getControl();
	}

	public ColumnViewer getObject() {
		return mTreeViewer;
	}

	public void makeDirty() {
		mDirtyAction.setDirty(true);

	}

	public void setSelectedData(CompositeMap data) {
		mSelectedData = data;

	}

	public CompositeMap getSelectedData() {
		return mSelectedData;
	}

	public CompositeMap getFocusData(){
//		ISelection selection = mTreeViewer.getSelection();
//		Object obj = ((IStructuredSelection) selection).getFirstElement();
////		CompositeMap data = new CompositeMap((CompositeMap) obj);
//		CompositeMap data = (CompositeMap) obj;
//		return data;
		return mFocusData;
	}
	
	public void refresh() {
		mTreeViewer.refresh();
	}

	public void setFocusData(CompositeMap data) {
		mFocusData =  data;
	}

	public void setDirty(boolean dirty) {
		// TODO Auto-generated method stub
		
	}

}
