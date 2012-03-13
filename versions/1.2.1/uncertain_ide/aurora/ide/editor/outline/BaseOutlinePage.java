package aurora.ide.editor.outline;

import java.util.Iterator;

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
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;

import uncertain.composite.CompositeMap;
import uncertain.composite.TextParser;
import uncertain.schema.Element;
import aurora.ide.AuroraPlugin;
import aurora.ide.builder.CompositeMapInfo;
import aurora.ide.editor.textpage.TextPage;
import aurora.ide.editor.textpage.quickfix.QuickAssistUtil;
import aurora.ide.helpers.ApplicationException;
import aurora.ide.helpers.CompositeMapUtil;
import aurora.ide.helpers.LocaleMessage;

public class BaseOutlinePage extends ContentOutlinePage {
	protected TextPage editor;
	private CompositeMap input;
	private CompositeMap selectMap;
	private IDocument document;
	private OutlineTree<CompositeMap> labelRoot;
	private OutlineTree<CompositeMap> selectTree;
	private Selected selected = new Selected();
	private boolean error = false;

	private class DocumentListener implements IDocumentListener {

		public void documentAboutToBeChanged(DocumentEvent event) {

		}

		public void documentChanged(DocumentEvent event) {
//			event.
			if (null == selectTree) {
				return;
			}
			loadInput();
			if (error) {
				return;
			}
			OutlineTree<CompositeMap> rootNode = null;
			if (null == selectTree.getParent()) {
				rootNode = selectTree;
			} else {
				rootNode = selectTree.getParent();
			}
			correct(rootNode, labelRoot.findChild(rootNode.getId()));
			getTreeViewer().refresh(rootNode);
		}

	}

	public BaseOutlinePage(TextPage editor) {
		this.editor = editor;
		IDocument inputDocument = editor.getInputDocument();
		inputDocument.addDocumentListener(new DocumentListener());
		loadInput();
	}

	public void selectNode(int offset) {
		if (!error) {
			try {
				CompositeMap temp = QuickAssistUtil.findMap(input, document,
						offset);
				if (null != selectMap
						&& equalsRange(temp.getLocationNotNull().getRange(),
								selectMap.getLocationNotNull().getRange())) {
					return;
				} else {
					selectMap = temp;
					findOutlineTree(labelRoot);
					getTreeViewer().removeSelectionChangedListener(selected);
					getTreeViewer().setSelection(
							new StructuredSelection(selectTree));
					getTreeViewer().addSelectionChangedListener(selected);
				}
			} catch (Exception e) {
				// e.printStackTrace();
			}
		}
	}

	private void loadInput() {
		try {
			input = CompositeMapUtil.loaderFromString(editor.getInputDocument()
					.get());
			document = editor.getInputDocument();
			error = false;
		} catch (ApplicationException e) {
			error = true;
			return;
		}
		CompositeMap virtualNode = new CompositeMap("VirtualNode");
		labelRoot = new OutlineTree<CompositeMap>(virtualNode);
		labelRoot.add(input);
		fillLabel(input, labelRoot.getChild(0));
	}

	private void findOutlineTree(OutlineTree<CompositeMap> label) {
		CompositeMap map = label.getData();
		if (equalsRange(map.getLocationNotNull().getRange(), selectMap
				.getLocationNotNull().getRange())) {
			selectTree = label;
			return;
		}
		for (int i = 0; i < label.getChildrenCount(); i++) {
			findOutlineTree(label.getChild(i));
		}
	}

	private boolean equalsRange(int[] a, int[] b) {
		for (int i = 0; i < a.length; i++) {
			if (a[i] != b[i]) {
				return false;
			}
		}
		return true;
	}

	@SuppressWarnings("unchecked")
	private void fillLabel(CompositeMap map, OutlineTree<CompositeMap> labelTree) {
		Iterator<CompositeMap> it = map.getChildIterator();
		for (int i = 0; null != it && it.hasNext(); i++) {
			CompositeMap cm = it.next();
			labelTree.add(cm);
			fillLabel(cm, labelTree.getChild(i));
		}
	}

	@Override
	public void createControl(Composite parent) {
		super.createControl(parent);
		getTreeViewer().setLabelProvider(new OutlineLabelProvider());
		getTreeViewer().setContentProvider(new OutlineContentProvider());

		getTreeViewer().setInput(labelRoot);
		getTreeViewer().addSelectionChangedListener(selected);
	}

	public void refresh() {
		// if (null == selectTree) {
		// return;
		// }
		// loadInput();
		// if (error) {
		// return;
		// }
		// OutlineTree<CompositeMap> rootNode = null;
		// if (null == selectTree.getParent()) {
		// rootNode = selectTree;
		// } else {
		// rootNode = selectTree.getParent();
		// }
		// correct(rootNode, labelRoot.findChild(rootNode.getId()));
		// getTreeViewer().refresh(rootNode);
	}

	private void correct(OutlineTree<CompositeMap> ing,
			OutlineTree<CompositeMap> ed) {
		if (!ing.getData().equals(ed.getData())) {
			ing.setData(ed.getData());
		}
		if (ing.getChildrenCount() != ed.getChildrenCount()) {
			ing.removeAll();
			for (int i = 0; i < ed.getChildrenCount(); i++) {
				ing.add(ed.getChild(i));
			}
		} else {
			for (int i = 0; i < ed.getChildrenCount(); i++) {
				correct(ing.getChild(i), ed.getChild(i));
			}
		}
	}

	class Selected implements ISelectionChangedListener {
		@SuppressWarnings("unchecked")
		public void selectionChanged(SelectionChangedEvent event) {
			TreeSelection selection = (TreeSelection) event.getSelection();
			OutlineTree<CompositeMap> lt = (OutlineTree<CompositeMap>) selection
					.getFirstElement();
			if (null == lt || null == lt.getData()) {
				return;
			}
			CompositeMap data = lt.getData();
			CompositeMapInfo info = new CompositeMapInfo(data, document);
			IRegion region = info.getMapNameRegion();
			if (region == null) {
				System.out.println();
				return;
			}

			TextSelection tt = new TextSelection(region.getOffset(),
					region.getLength());
			editor.getEditorSite().getSelectionProvider().setSelection(tt);
		}
	}

	@SuppressWarnings("unchecked")
	class OutlineContentProvider implements ITreeContentProvider {
		public Object[] getChildren(Object parentElement) {
			if (parentElement == null)
				return null;
			OutlineTree<CompositeMap> lt = (OutlineTree<CompositeMap>) parentElement;
			return lt.getChildren().toArray();
		}

		public Object getParent(Object element) {
			if (element == null)
				return null;
			OutlineTree<CompositeMap> lt = (OutlineTree<CompositeMap>) element;
			return lt.getParent();
		}

		public boolean hasChildren(Object element) {
			if (element == null)
				return false;
			OutlineTree<CompositeMap> lt = (OutlineTree<CompositeMap>) element;
			return lt.getChildrenCount() > 0;
		}

		public Object[] getElements(Object inputElement) {
			return getChildren(inputElement);
		}

		public void dispose() {
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}

	}

	class OutlineLabelProvider extends BaseLabelProvider implements
			ILabelProvider {
		@SuppressWarnings("unchecked")
		public String getText(Object obj) {
			OutlineTree<CompositeMap> lt = (OutlineTree<CompositeMap>) obj;
			String elementText = null;
			CompositeMap elemenntCm = lt.getData();
			String tagName = elemenntCm.getRawName();
			String elementName = getElementName(elemenntCm);
			if (elementName != null && !elementName.equals("")) {
				elementText = elementName;
			} else {
				elementText = tagName;
			}
			return elementText;
		}

		@SuppressWarnings("unchecked")
		public Image getImage(Object element) {
			OutlineTree<CompositeMap> lt = (OutlineTree<CompositeMap>) element;
			CompositeMap elemenntCm = lt.getData();
			//
			Element ele = CompositeMapUtil.getElement(elemenntCm);
			if (ele != null) {
				if (ele.isArray()) {
					return AuroraPlugin.getImageDescriptor(
							LocaleMessage.getString("array.icon"))
							.createImage();
				}
			}
			String defaultPath = LocaleMessage.getString("element.icon");
			return AuroraPlugin.getImageDescriptor(defaultPath).createImage();
		}

		protected String getElementName(CompositeMap element) {
			String tagName = element.getRawName();
			Element elm = CompositeMapUtil.getElement(element);
			String elemDesc = null;
			if (elm != null && !elm.isArray()) {
				if (elm.getDisplayMask() != null) {
					elemDesc = TextParser.parse(elm.getDisplayMask(), element);
				}
				if (elemDesc != null) {
					tagName = tagName + " " + elemDesc;
				}
			}
			if (elemDesc == null) {
				if (element.get("id") != null) {
					elemDesc = element.getString("id");
				} else if (element.get("name") != null) {
					elemDesc = element.get("name").toString();
				} else if (element.get("Name") != null) {
					elemDesc = element.get("Name").toString();
				}
				if (elemDesc != null) {
					tagName = tagName + " (" + elemDesc + ")";
				}
			}
			return tagName;
		}
	}
}
