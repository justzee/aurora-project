package aurora.ide.editor.outline;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;

import uncertain.composite.CompositeMap;
import aurora.ide.editor.textpage.TextPage;
import aurora.ide.editor.widgets.CompositeMapTreeContentProvider;
import aurora.ide.editor.widgets.CompositeMapTreeLabelProvider;
import aurora.ide.helpers.ApplicationException;

public class BaseOutlinePage extends ContentOutlinePage {
	protected TextPage editor;
	private CompositeMap input;
	private CompositeMap selectMap;
	private TreeItem selectItem;
	private boolean isSelect=false;

	public BaseOutlinePage(TextPage editor) {
		this.editor = editor;
		try {
			this.input = editor.toCompoisteMap();
		} catch (ApplicationException e) {
			e.printStackTrace();
		}
	}

	public void setSelect(boolean isSelect) {
		this.isSelect = isSelect;
	}
	
	public void selectNode(CompositeMap selectMap) {
		if(!isSelect)return;
		this.selectMap = selectMap;
		getTreeItem((getTreeViewer().getTree().getItem(0)));
		getTreeViewer().getTree().setSelection(selectItem);
	}

	private TreeItem getTreeItem(TreeItem parent) {
		CompositeMap map = (CompositeMap) parent.getData();
		if (selectMap.equals(map)) {
			selectItem = parent;
		}
		if (0 == parent.getItemCount()) {
			return parent;
		} else {
			for (int i = 0; i < parent.getItemCount(); i++) {
				getTreeItem(parent.getItem(i));
			}
		}
		return null;
	}

	@Override
	public void createControl(Composite parent) {
		super.createControl(parent);
		getTreeViewer().setLabelProvider(new CompositeMapTreeLabelProvider());
		getTreeViewer().setContentProvider(new OutlineContentProvider(input));
		getTreeViewer().addSelectionChangedListener(new ElementSelectionListener());
		refresh(input.getParent());
	}

	public void refresh(CompositeMap parentData) {
		if (parentData == null) {
			parentData = createVirtualParentNode(input);
		}
		getTreeViewer().setInput(parentData);
		getTreeViewer().expandAll();
	}
	
	/*public void refresh(TextPage editor) throws ApplicationException {
		CompositeMap parentData = editor.toCompoisteMap().getParent();
		if (parentData == null) {
			parentData = createVirtualParentNode(input);
		}
		getTreeViewer().setInput(parentData);
		getTreeViewer().expandAll();
	}*/

	private CompositeMap createVirtualParentNode(CompositeMap node) {
		if (node == null)
			return null;
		CompositeMap parentNode = node.getParent();
		if (parentNode != null)
			return parentNode;
		CompositeMap virtualNode = new CompositeMap("VirtualNode");
		virtualNode.addChild(node);
		return virtualNode;
	}

	class ElementSelectionListener implements ISelectionChangedListener {
		public void selectionChanged(SelectionChangedEvent event) {
			TreeSelection selection = (TreeSelection) event.getSelection();
			CompositeMap data = (CompositeMap) selection.getFirstElement();
			int startLine = data.getLocation().getStartLine() - 1;
			try {
				int lineOffset = editor.getInputDocument().getLineOffset(startLine);
				int lineLength = editor.getInputDocument().getLineLength(startLine);
				String s = editor.getInputDocument().get(lineOffset, lineLength - 1);
				TextSelection tt = new TextSelection(lineOffset + s.length() - s.trim().length() - 1, s.trim().length());
				editor.getEditorSite().getSelectionProvider().setSelection(tt);
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
		}
	}

	@SuppressWarnings("unchecked")
	class OutlineContentProvider extends CompositeMapTreeContentProvider {
		public OutlineContentProvider(CompositeMap rootElement) {
			super(rootElement);
		}

		public Object[] getChildren(Object parentElement) {
			if (parentElement == null)
				return null;
			CompositeMap map = (CompositeMap) parentElement;
			List<CompositeMap> childs = new LinkedList<CompositeMap>(map.getChildsNotNull());
			return childs.toArray();
		}
	}
}
