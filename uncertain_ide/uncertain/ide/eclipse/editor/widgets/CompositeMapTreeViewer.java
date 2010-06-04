package uncertain.ide.eclipse.editor.widgets;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;

import uncertain.composite.CompositeMap;
import uncertain.ide.eclipse.action.CompositeMapAction;
import uncertain.ide.eclipse.action.ElementDoubleClickListener;
import uncertain.ide.eclipse.editor.IContainer;
import uncertain.ide.eclipse.editor.IViewer;

public class CompositeMapTreeViewer implements IContainer {
	protected TreeViewer mTreeViewer;
	protected IViewer mParent;
	protected CompositeMap mSelectedData;
	protected CompositeMap mFocusData;
	private CompositeMap mData;

	public CompositeMapTreeViewer(Tree tree, IViewer parent, CompositeMap data) {
		this.mParent = parent;
		this.mData = data;
		createEditorContent1(tree, data);
	}
	
	protected void createEditorContent(Composite parentpanel, CompositeMap data) {
		FilteredTree filteredTree = new FilteredTree(parentpanel, SWT.BORDER
				| SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL,
				new PatternFilter(), true);
		mTreeViewer = filteredTree.getViewer();
		// mTreeViewer = new TreeViewer(tree);
		mTreeViewer.setLabelProvider(new CompositeMapTreeLabelProvider());
		mTreeViewer.setContentProvider(new CompositeMapTreeContentProvider(data));

		CompositeMap parent = data.getParent();

		if (parent == null) {
			CompositeMap root = new CompositeMap("root");
			root.addChild(data);
			parent = root;
		}

		mTreeViewer.setInput(parent);

		CompositeMapAction.fillContextMenu(this);
		CompositeMapAction.fillDNDListener(this);
		CompositeMapAction.fillKeyListener(this);
		mTreeViewer.addDoubleClickListener(new ElementDoubleClickListener(
				mParent));

	}

	protected void createEditorContent1(Tree tree, CompositeMap data) {
		mTreeViewer = new TreeViewer(tree);
		mTreeViewer.setLabelProvider(new CompositeMapTreeLabelProvider());
		mTreeViewer.setContentProvider(new CompositeMapTreeContentProvider(data));

		CompositeMap parent = data.getParent();

		if (parent == null) {
			CompositeMap root = new CompositeMap("root");
			root.addChild(data);
			parent = root;
		}

		mTreeViewer.setInput(parent);

		CompositeMapAction.fillContextMenu(this);
		CompositeMapAction.fillDNDListener(this);
		CompositeMapAction.fillKeyListener(this);
		// mTreeViewer.addDoubleClickListener(new

		// mTreeViewer.expandToLevel(2);
		// mTreeViewer.addDoubleClickListener(new IDoubleClickListener() {
		// public void doubleClick(DoubleClickEvent event) {
		// System.out.println("double click");
		// IStructuredSelection selection = (IStructuredSelection) event
		// .getSelection();
		// Object selectedNode = selection.getFirstElement();
		// boolean expanded = mTreeViewer.getExpandedState(selectedNode);
		// System.out.println("以前状态:"+expanded);
		// if (expanded) {
		// mTreeViewer.collapseToLevel(selectedNode, 1);
		// } else {
		// mTreeViewer.expandToLevel(selectedNode, 1);
		// }
		// System.out.println("现在状态:"+mTreeViewer.getExpandedState(selectedNode));
		// }
		// });
//		mTreeViewer
//				.addSelectionChangedListener(new ISelectionChangedListener() {
//
//					public void selectionChanged(SelectionChangedEvent event) {
//						// TODO Auto-generated method stub
//						StructuredSelection select = (StructuredSelection) event
//								.getSelection();
//						// CompositeMap r =
//						// (CompositeMap)select.getFirstElement();
//						CompositeMap r = (CompositeMap) select
//								.getFirstElement();
//						 System.out.println(mData.toXML());
//						// System.out.println("选中的:"+r.toXML());
//						String path = "/service/model/query/";
//						// CompositeMap formPanel = mData.getChild(path);
//						// if(formPanel != null ){
//						// System.out.println("1"+formPanel.toXML());
//						// mTreeViewer.expandToLevel(formPanel, 4);//若为文件夹,则展开树
//						// return;
//						// }
//						Object get = mData.get(path);
//						//
//						// if(get != null ){
//						// CompositeMap cm = (CompositeMap)get;
//						// System.out.println("2"+cm.toXML());
//						// mTreeViewer.expandToLevel(cm, 4);//若为文件夹,则展开树
//						// return;
//						// }
//						get = mData.getObject(path);
//						System.out.println("get:"+get);
//						if (get != null) {
//							CompositeMap cm = (CompositeMap) get;
////							CompositeMap cm1 = (CompositeMap)cm.clone();
////							CompositeMap cm1 = new CompositeMap(cm);
//							CompositeMap cm1 = cm;
////							CompositeMap parent = cm.getParent();
////							CompositeMap copy = cm1;
////							while(parent != null){
////								copy.setParent(parent);
////								parent = parent.getParent();
////								copy = copy.getParent();
////							}
////							System.out.println(cm.equals(cm1));
////							System.out.println("3" + cm.toXML());
//							// if (!tv.getExpandedState(obj))
//							// tv.expandToLevel(obj, 1);
//							// mTreeViewer.collapseAll();
////							if (!mTreeViewer.getExpandedState(cm1)) {
////								System.out.println("cm没有展开");
//							System.out.println("before："
//							+ mTreeViewer.getExpandedState(cm1));
//							mTreeViewer.collapseAll();
//								mTreeViewer.expandToLevel(cm1, 1);// 若为文件夹,则展开树
//							
////							}
//							System.out.println("现在状态："
//									+ mTreeViewer.getExpandedState(cm1));
//							// System.out.println(cm.getParent().toXML());
//							// mTreeViewer.expandToLevel(4);//
//							return;
//						}
//					}
//
//				});
	}

	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		mTreeViewer.addSelectionChangedListener(listener);
	}

	public Control getControl() {
		return mTreeViewer.getControl();
	}

	public Object getViewer() {
		return mTreeViewer;
	}

	public void setSelection(Object data) {
		mSelectedData = (CompositeMap)data;

	}

	public Object getSelection() {
		return mSelectedData;
	}

	public Object getFocus() {
		// ISelection selection = mTreeViewer.getSelection();
		// Object obj = ((IStructuredSelection) selection).getFirstElement();
		// // CompositeMap data = new CompositeMap((CompositeMap) obj);
		// CompositeMap data = (CompositeMap) obj;
		// return data;
		return mFocusData;
	}

	public void refresh() {
		mTreeViewer.refresh();
	}

	public void setFocus(Object data) {
		mFocusData = (CompositeMap)data;
	}

	public void setDirty(boolean dirty) {
		mParent.refresh(true);

	}

	public void refresh(boolean dirty) {
		if (dirty) {
			mParent.refresh(true);
		}
		try {
			mTreeViewer.refresh();
		} catch (Exception e) {
			Object input = mTreeViewer.getInput();
			String cm = null;
			if (input != null) {
				cm = ((CompositeMap) input).toXML();
			}
			throw new RuntimeException("cm:" + cm + " errorMessage:"
					+ e.getLocalizedMessage());
		}

	}

	public void setInput(CompositeMap data) {
		mTreeViewer.setContentProvider(new CompositeMapTreeContentProvider(data));
		CompositeMap parent = data.getParent();

		if (parent == null) {
			CompositeMap root = new CompositeMap("root");
			root.addChild(data);
			parent = root;
		}

		mTreeViewer.setInput(parent);
	}

	public CompositeMap getInput() {
		return mData;
	}

}
