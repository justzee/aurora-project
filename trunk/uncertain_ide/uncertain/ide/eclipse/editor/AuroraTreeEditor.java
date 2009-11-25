package uncertain.ide.eclipse.editor;

import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Tree;


import uncertain.composite.CompositeMap;
import uncertain.ide.Activator;
import uncertain.ide.eclipse.action.CompositeMapAction;
import uncertain.ide.eclipse.action.ElementDoubleClickListener;
import uncertain.ide.eclipse.action.IDirty;
import uncertain.ide.eclipse.action.IViewerDirty;

public class AuroraTreeEditor implements IViewerDirty{
	protected TreeViewer mTreeViewer;
	protected IDirty mDirtyAction;
	protected CompositeMap mSelectedData;
	protected CompositeMap mFocusData;
	public AuroraTreeEditor(Tree tree, IDirty mDirtyAction,
			CompositeMap data) {
		this.mDirtyAction = mDirtyAction;
		createEditorContent(tree, data);
	}

	protected void createEditorContent(Tree tree, CompositeMap data) {
		mTreeViewer = new TreeViewer(tree);
		mTreeViewer.setLabelProvider(new AuroraTreeLabelProvider());
		mTreeViewer.setContentProvider(new AuroraTreeContentProvider(Activator
				.getSchemaManager(), data));


		CompositeMap parent = data.getParent();
		
		if(parent==null){
			CompositeMap root = new CompositeMap("root");
			root.addChild(data);
			parent = root;
		}

		mTreeViewer.setInput(parent);
		
//		ServcieActionGroup servcieActionGroup = new ServcieActionGroup(this);
//		servcieActionGroup.fillContextMenu();
//		servcieActionGroup.fillDNDListener();
//		servcieActionGroup.fillKeyListener();
		CompositeMapAction.fillContextMenu(this);
		CompositeMapAction.fillDNDListener(this);
		CompositeMapAction.fillKeyListener(this);
		mTreeViewer.addDoubleClickListener(new ElementDoubleClickListener(mDirtyAction));
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
		mDirtyAction.setDirty(true);
		
	}

	public void refresh(boolean dirty) {
		if(dirty){
			mDirtyAction.setDirty(true);
		}
		try{
			mTreeViewer.refresh();
		}catch(Exception e){
			Object input = mTreeViewer.getInput();
			String cm = null;
			if(input != null){
				cm = ((CompositeMap)input).toXML();
			}
			throw new RuntimeException("cm:"+cm+" errorMessage:"+e.getLocalizedMessage());
		}
		
	}

}
