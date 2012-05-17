package aurora.ide.editor.outline;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;
import org.xml.sax.SAXException;

import aurora.ide.AuroraPlugin;
import aurora.ide.editor.textpage.TextPage;
import aurora.ide.helpers.LocaleMessage;

public class BaseOutlinePage extends ContentOutlinePage {
	protected TextPage editor;
	private IDocument document;

	private OutlineTree root;
	private Selected selected = new Selected();

	public BaseOutlinePage(TextPage editor) {
		this.editor = editor;
		IDocument inputDocument = editor.getInputDocument();
		inputDocument.addDocumentListener(new DocumentListener());
	}

	@Override
	public void createControl(Composite parent) {
		super.createControl(parent);
		getTreeViewer().addSelectionChangedListener(selected);
		getTreeViewer().setLabelProvider(new OutlineLabelProvider());
		getTreeViewer().setContentProvider(new OutlineContentProvider());
		root = loadTree();
		getTreeViewer().setInput(root);
		// getTreeViewer().expandAll();
	}

	@Override
	public void setActionBars(IActionBars actionBars) {
		// IAction action = new Action("aurora.outline", SWT.TOGGLE) {
		// @Override
		// public void run() {
		//
		// }
		// };
		// action.setToolTipText("action");
		// //
		// action.setImageDescriptor(AuroraPlugin.getImageDescriptor("icons/collapseall.gif"));
		// actionBars.getToolBarManager().add(action);
		super.setActionBars(actionBars);
	}

	private OutlineTree loadTree() {
		this.document = editor.getInputDocument();
		OutlineParser p = new OutlineParser(document.get());
		try {
			p.parser();
			return p.getTree();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			// e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void selectNode(int offset) {
		OutlineTree tree = getTree(root == null ? null : root.getChild(0), offset);
		if (tree == null) {
			if (root.getChild(0) != null) {
				tree = root.getChild(0);
			} else {
				return;
			}
		}
		getTreeViewer().removeSelectionChangedListener(selected);
		getTreeViewer().setSelection(new StructuredSelection(tree));
		getTreeViewer().addSelectionChangedListener(selected);
	}

	private OutlineTree getTree(OutlineTree tree, int offset) {
		if (tree == null) {
			return null;
		}
		if (tree.getRegion() != null) {
			if (tree.getRegion().getOffset() <= offset
					&& tree.getRegion().getOffset() + tree.getRegion().getLength() > offset) {
				return tree;
			} else {
				return null;
			}
		}
		if (tree.getStartRegion().getOffset() <= offset
				&& tree.getEndRegion().getOffset() + tree.getEndRegion().getLength() + 1 > offset) {
			for (OutlineTree child : tree.getChildren()) {
				OutlineTree t = getTree(child, offset);
				if (t != null) {
					return t;
				}
			}
		} else {
			return null;
		}
		return tree;
	}

	private boolean eq(Object o1, Object o2) {
		if (o1 == null) {
			return o1 == o2;
		}
		return o1.equals(o2);
	}

	private void refresh(OutlineTree tree, OutlineTree input) {
		if (tree == null || input == null) {
			return;
		}
		if (!eq(tree, input)) {
			if (eq(tree.getText(), input.getText()) && eq(tree.getOther(), input.getOther())) {
				input.copy(tree);
			} else {
				input.copy(tree);
				getTreeViewer().refresh(input);
			}
		}
		if (tree.getChildrenCount() != input.getChildrenCount()) {
			input.removeAll();
			for (int i = 0; i < tree.getChildrenCount(); i++) {
				input.add(tree.getChild(i));
			}
			getTreeViewer().refresh(input);
		} else {
			for (int i = 0; i < tree.getChildrenCount(); i++) {
				refresh(tree.getChild(i), input.getChild(i));
			}
		}
	}

	private Image getOutlineTreeImage(OutlineTree tree) {
		if ("array".equals(tree.getImage())) {
			return AuroraPlugin.getImageDescriptor(LocaleMessage.getString("array.icon")).createImage();
		} else if ("script".equals(tree.getImage())) {
			return AuroraPlugin.getImageDescriptor("icons/script.png").createImage();
		} else if ("method".equals(tree.getImage())) {
			return AuroraPlugin.getImageDescriptor("icons/method.gif").createImage();
		} else if ("variable".equals(tree.getImage())) {
			return AuroraPlugin.getImageDescriptor("icons/variable.gif").createImage();
		}
		String defaultPath = LocaleMessage.getString("element.icon");
		return AuroraPlugin.getImageDescriptor(defaultPath).createImage();
	}

	class OutlineLabelProvider extends BaseLabelProvider implements ILabelProvider {
		public String getText(Object obj) {
			return obj.toString();
		}

		public Image getImage(Object element) {
			return getOutlineTreeImage((OutlineTree) element);
		}
	}

	class Selected implements ISelectionChangedListener {
		public void selectionChanged(SelectionChangedEvent event) {
			TreeSelection selection = (TreeSelection) event.getSelection();
			OutlineTree lt = (OutlineTree) selection.getFirstElement();
			if (lt == null) {
				return;
			}
			IRegion region = lt.getStartRegion();
			// IRegion region = lt.getEndRegion();
			TextSelection tt = new TextSelection(region.getOffset(), region.getLength());
			editor.getEditorSite().getSelectionProvider().setSelection(tt);
		}
	}

	class OutlineContentProvider implements ITreeContentProvider {
		public void dispose() {
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}

		public Object[] getElements(Object inputElement) {
			return ((OutlineTree) inputElement).getChildren().toArray();
		}

		public Object[] getChildren(Object parentElement) {
			return ((OutlineTree) parentElement).getChildren().toArray();
		}

		public Object getParent(Object element) {
			return ((OutlineTree) element).getParent();
		}

		public boolean hasChildren(Object element) {
			return ((OutlineTree) element).getChildren().size() > 0;
		}

	}

	class DocumentListener implements IDocumentListener {
		public void documentAboutToBeChanged(DocumentEvent event) {

		}

		public void documentChanged(DocumentEvent event) {
			OutlineTree tree = loadTree();
			if (tree == null) {
				return;
			}
			refresh(tree, (OutlineTree) getTreeViewer().getInput());
		}
	}
}
