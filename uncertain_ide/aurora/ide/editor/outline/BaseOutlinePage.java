package aurora.ide.editor.outline;

import java.util.Iterator;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TreeItem;
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
	private TreeItem selectItem;
	private LabelTree<CompositeMap> labelRoot;

	public BaseOutlinePage(TextPage editor) {
		this.editor = editor;
		loadInput();
	}

	public void selectNode(int offset) {
		try {
			selectMap = QuickAssistUtil.findMap(input, document, offset);
		} catch (Exception e) {
			return;
		}
		if (null == selectMap || 0 >= getTreeViewer().getTree().getItemCount()) {
			return;
		}
		CompositeMapInfo selectMapInfo = new CompositeMapInfo(selectMap, document);
		findTreeItem(selectMapInfo, getTreeViewer().getTree().getItem(0));
		if (null != selectItem && !selectItem.isDisposed() && null != selectItem.getData()) {
			getTreeViewer().getTree().setSelection(selectItem);
		}
	}

	private void loadInput() {
		try {
			this.document = editor.getInputDocument();
			this.input = CompositeMapUtil.loaderFromString(document.get());
			CompositeMap virtualNode = new CompositeMap("VirtualNode");
			labelRoot = new LabelTree<CompositeMap>(virtualNode);
			labelRoot.add(input);
			fillLabel(input, labelRoot.getChild(0));
		} catch (ApplicationException e) {
			// e.printStackTrace();
		}
	}

	private void findTreeItem(CompositeMapInfo selectMapInfo, TreeItem item) {
		CompositeMapInfo mapInfo = new CompositeMapInfo(((LabelTree<CompositeMap>) item.getData()).getData(), document);
		if (mapInfo.getMapRegion().equals(selectMapInfo.getMapRegion())) {
			selectItem = item;
			return;
		}
		for (int i = 0; i < item.getItemCount(); i++) {
			findTreeItem(selectMapInfo, item.getItem(0));
		}
	}

	@SuppressWarnings("unchecked")
	private void fillLabel(CompositeMap map, LabelTree<CompositeMap> labelTree) {
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
		getTreeViewer().expandAll();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void selectionChanged(SelectionChangedEvent event) {
		TreeSelection selection = (TreeSelection) event.getSelection();
		LabelTree<CompositeMap> lt = (LabelTree<CompositeMap>) selection.getFirstElement();
		CompositeMap data = lt.getData();
		if (null == data) {
			return;
		}
		CompositeMapInfo info = new CompositeMapInfo(data, document);
		IRegion region = info.getMapNameRegion();
		TextSelection tt = new TextSelection(region.getOffset(), region.getLength());
		editor.getEditorSite().getSelectionProvider().setSelection(tt);
	}

	public void refresh() {
		loadInput();

		get(getTreeViewer().getTree().getItem(0), labelRoot.getChild(0));
	}

	void get(TreeItem ti, LabelTree<CompositeMap> lt) {
		LabelTree<CompositeMap> cm = (LabelTree<CompositeMap>) ti.getData();

		if (cm.equals(lt)) {
			System.out.println(lt.getId() + lt.getData().getRawName());
			System.out.println(cm.getId() + cm.getData().getRawName());
			System.out.println(selectItem);
		}
	}

	@SuppressWarnings("unchecked")
	class OutlineContentProvider implements ITreeContentProvider {
		public Object[] getChildren(Object parentElement) {
			if (parentElement == null)
				return null;
			LabelTree<String> lt = (LabelTree<String>) parentElement;
			return lt.getChildren().toArray();
		}

		public Object getParent(Object element) {
			if (element == null)
				return null;
			LabelTree<String> lt = (LabelTree<String>) element;
			return lt.getParent();
		}

		public boolean hasChildren(Object element) {
			if (element == null)
				return false;
			LabelTree<String> lt = (LabelTree<String>) element;
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

	class OutlineLabelProvider extends BaseLabelProvider implements ILabelProvider {
		@SuppressWarnings("unchecked")
		public String getText(Object obj) {
			LabelTree<CompositeMap> lt = (LabelTree<CompositeMap>) obj;
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
			LabelTree<String> lt = (LabelTree<String>) element;
			if (lt.getChildrenCount() > 0) {
				return AuroraPlugin.getImageDescriptor(LocaleMessage.getString("array.icon")).createImage();
			} else {
				String defaultPath = LocaleMessage.getString("element.icon");
				return AuroraPlugin.getImageDescriptor(defaultPath).createImage();
			}
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
