package aurora.ide.preferencepages;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Item;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.dialogs.ContainerCheckedTreeViewer;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

import uncertain.composite.QualifiedName;
import uncertain.schema.AbstractQualifiedNamed;
import uncertain.schema.Extension;
import uncertain.schema.ISchemaManager;
import aurora.ide.AuroraPlugin;
import aurora.ide.dialog.AddTagDialog;
import aurora.ide.helpers.LoadSchemaManager;

public class StatisticianPropertyPage extends PreferencePage implements IWorkbenchPreferencePage {
	private Map<String, List<String>> baseMap = new HashMap<String, List<String>>();
	private TagTree baseMapTree = new TagTree(null, "root", "root");

	private ContainerCheckedTreeViewer treeViewer = null;
	private IPreferenceStore store = AuroraPlugin.getDefault().getPreferenceStore();
	private String[] noNamespace = { "query-fields", "columns", "center", "event", "features", "data-filter", "form", "table", "UL", "view", "IMG", "model-delete", "service-output", "model-update", "param", "A", "events", "mapping", "map", "model-load", "model-query", "a", "field", "H2", "tr", "img", "ref-field", "td", "br", "TABLE", "model-insert", "parameters", "font", "label", "script", "input", "iframe", "column", "query-field", "span", "model-execute", "model", "TD", "batch-apply", "div", "TR", "datas", "pk-field", "parameter", "style", "tbody", "DIV", "service", "LI" };
	private String selectNamespace = "";
	private String selectTag = "";
	private boolean modify = false;
	private boolean defaul = false;
	private String custom = "";
	private String checked = "";

	public StatisticianPropertyPage() {
		initBaseTree();
		baseMap = baseMapTree.getMap();
		String[] ss = store.getString("statistician.custom").split("!");
		String namespace = "";
		for (String s : ss) {
			if ("".equals(s.trim())) {
				continue;
			} else if (s.indexOf("*") == 0) {
				s = s.substring(1);
				namespace = s;
			} else {
				baseMapTree.Add(namespace, s);
			}
		}
		baseMapTree.sort(false);
	}

	private void initBaseTree() {
		ISchemaManager schemaManager = LoadSchemaManager.getSchemaManager();
		for (Object object : schemaManager.getAllTypes()) {
			String nameSpace = "";
			String tag = "";
			AbstractQualifiedNamed absQualifiedNamed = (AbstractQualifiedNamed) object;
			nameSpace = absQualifiedNamed.getQName().getNameSpace();
			tag = absQualifiedNamed.getQName().getLocalName();
			if (nameSpace.equals("http://www.uncertain-framework.org/schema/simple-schema")) {
				continue;
			}
			baseMapTree.Add(nameSpace, tag);
			if (absQualifiedNamed.getChilds() == null) {
				continue;
			}
			for (Object ele : absQualifiedNamed.getChilds()) {
				if (ele instanceof uncertain.schema.Extension) {
					QualifiedName qn = ((Extension) ele).getBaseType();
					nameSpace = qn.getNameSpace();
					tag = qn.getLocalName();
				} else if (!(ele instanceof uncertain.schema.Attribute)) {
					AbstractQualifiedNamed aqn = (AbstractQualifiedNamed) ele;
					if (null != aqn.getQName()) {
						QualifiedName qn = aqn.getQName();
						nameSpace = qn.getNameSpace();
						tag = qn.getLocalName();
						if (nameSpace == null) {
							nameSpace = "No namespace";
						}
					}
				}
				if (nameSpace.equals("http://www.uncertain-framework.org/schema/simple-schema")) {
					continue;
				}
				baseMapTree.Add(nameSpace, tag);
			}
		}
		for (String tag : noNamespace) {
			baseMapTree.Add("No namespace", tag);
		}
	}

	@Override
	protected Control createContents(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.makeColumnsEqualWidth = false;
		container.setLayout(layout);

		GridData gdTreeViewer = new GridData(GridData.FILL_BOTH);
		gdTreeViewer.verticalSpan = 4;
		treeViewer = new ContainerCheckedTreeViewer(container);
		TextCellEditor cellEditor = new TextCellEditor(treeViewer.getTree());
		treeViewer.setCellEditors(new CellEditor[] { cellEditor });
		treeViewer.setCellModifier(new CellModifier(treeViewer));
		treeViewer.setColumnProperties(new String[] { "item" });

		GridData gdBtnAdd = new GridData();
		gdBtnAdd.verticalAlignment = SWT.TOP;
		gdBtnAdd.widthHint = 100;
		Button btnAdd = new Button(container, SWT.NULL);
		btnAdd.setText("Add");
		btnAdd.setLayoutData(gdBtnAdd);
		btnAdd.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				String[] namespaces = new String[baseMapTree.getChildren().size()];
				for (int i = 0; i < baseMapTree.getChildren().size(); i++) {
					namespaces[i] = baseMapTree.getChildren().get(i).getNamespace();
				}
				AddTagDialog dialog = new AddTagDialog(getShell(), namespaces, baseMapTree.getMap());
				if (dialog.open() == Dialog.OK) {
					StringBuffer sb = null;
					Map<String, List<String>> customMap = dialog.getCustomMap();
					for (String namespace : customMap.keySet()) {
						for (String tag : customMap.get(namespace)) {
							baseMapTree.Add(namespace, tag);
						}
						if ("".equals(custom)) {
							custom = store.getString("statistician.custom");
						}
						int start = custom.indexOf("*" + namespace);
						if (start == -1) {
							custom += dialog.getStoreValue();
							continue;
						}
						int end = start + namespace.length();
						sb = new StringBuffer(custom);
						sb.insert(end + 2, dialog.getStoreValue().substring(namespace.length() + 2));
					}
					if (sb != null) {
						custom = sb.toString();
					}
				}
				baseMapTree.sort(false);
				modify = true;
				treeViewer.refresh();
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		GridData gdBtnRemove = new GridData();
		gdBtnRemove.verticalAlignment = SWT.TOP;
		gdBtnRemove.widthHint = 100;
		final Button btnRemove = new Button(container, SWT.NULL);
		btnRemove.setText("Remove");
		btnRemove.setLayoutData(gdBtnRemove);
		btnRemove.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				TagTree t = baseMapTree.remove(selectNamespace, selectTag);
				treeViewer.refresh(t.getParent());
				if (custom.equals("")) {
					custom = store.getString("statistician.custom");
				}
				StringBuffer sb = new StringBuffer(custom);
				int start = custom.indexOf("*" + t.getNamespace());
				int end = custom.indexOf(t.getTag(), start) + t.getTag().length();
				if (t.getNamespace().equals(t.getTag())) {
					end = custom.indexOf("*", start + 1) == -1 ? custom.length() : custom.indexOf("*", start + 1) - 1;
				} else {
					start = custom.indexOf(t.getTag(), start + 1);
				}
				sb.delete(start, end + 1);
				modify = true;
				custom = sb.toString();
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		btnRemove.setEnabled(false);

		GridData gdBtnSelectAll = new GridData();
		gdBtnSelectAll.verticalAlignment = SWT.TOP;
		gdBtnSelectAll.widthHint = 100;
		Button btnSelectAll = new Button(container, SWT.NULL);
		btnSelectAll.setText("Select All");
		btnSelectAll.setLayoutData(gdBtnSelectAll);
		btnSelectAll.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				for (TagTree t : baseMapTree.getChildren()) {
					treeViewer.setChecked(t, true);
				}
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		GridData gdBtnDeselectAll = new GridData();
		gdBtnDeselectAll.verticalAlignment = SWT.TOP;
		gdBtnDeselectAll.widthHint = 100;
		Button btnDeselectAll = new Button(container, SWT.NULL);
		btnDeselectAll.setText("Deselect All");
		btnDeselectAll.setLayoutData(gdBtnDeselectAll);
		btnDeselectAll.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				for (TagTree t : baseMapTree.getChildren()) {
					treeViewer.setChecked(t, false);
				}
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		treeViewer.getTree().setLinesVisible(true);
		treeViewer.getTree().setLayoutData(gdTreeViewer);
		treeViewer.setContentProvider(new ITreeContentProvider() {
			public void dispose() {
			}

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			}

			public Object[] getElements(Object inputElement) {
				return ((TagTree) inputElement).getChildren().toArray();
			}

			public Object[] getChildren(Object parentElement) {
				return ((TagTree) parentElement).getChildren().toArray();
			}

			public Object getParent(Object element) {
				return ((TagTree) element).getParent();
			}

			public boolean hasChildren(Object element) {
				return ((TagTree) element).getChildren().size() > 0;
			}
		});
		treeViewer.setLabelProvider(new LabelProvider() {
			public String getText(Object element) {
				return element.toString();
			}

			public Image getImage(Object element) {
				return null;
			}
		});
		treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				TreeSelection ts = (TreeSelection) event.getSelection();
				TagTree tree = (TagTree) ts.getFirstElement();
				if (null == tree) {
					btnRemove.setEnabled(false);
					return;
				} else {
					selectNamespace = tree.getNamespace();
					selectTag = tree.getTag();
				}
				if (baseMap.containsKey(selectNamespace)) {
					if (selectNamespace.equals(selectTag)) {
						btnRemove.setEnabled(false);
					} else if (baseMap.get(selectNamespace).contains(selectTag)) {
						btnRemove.setEnabled(false);
					} else {
						btnRemove.setEnabled(true);
					}
				} else {
					btnRemove.setEnabled(true);
				}
			}
		});
		treeViewer.setInput(baseMapTree);
		load();
		return container;
	}

	@Override
	protected void performDefaults() {
		updateApplyButton();
		baseMapTree.removeAll();
		for (String namespace : baseMap.keySet()) {
			for (String tag : baseMap.get(namespace)) {
				baseMapTree.Add(namespace, tag);
			}
		}
		baseMapTree.sort(false);
		treeViewer.setInput(baseMapTree);
		defaul = true;
	}

	@Override
	public boolean performOk() {
		save();
		if (defaul) {
			store.setValue("statistician.custom", "");
			store.setValue("statistician.checked", "");
		}
		if (modify) {
			store.setValue("statistician.custom", custom);
		}
		store.setValue("statistician.checked", checked);
		try {
			((ScopedPreferenceStore) store).save();
			return super.performOk();
		} catch (IOException e) {
			return false;
		}
	}

	private void save() {
		StringBuffer sb = new StringBuffer();
		for (Object o : treeViewer.getCheckedElements()) {
			if (baseMapTree.contains(o)) {
				sb.append("*");
			}
			sb.append(o.toString());
			sb.append("!");
		}
		checked = sb.toString();
	}

	public void init(IWorkbench workbench) {
	}

	private void load() {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				String[] storeTag = store.getString("statistician.checked").split("!");
				String namespace = "";
				for (String s : storeTag) {
					if ("".equals(s.trim())) {
						continue;
					}
					if (s.startsWith("*")) {
						s = s.substring(1);
						namespace = s;
					} else {
						treeViewer.setChecked(baseMapTree.find(namespace, s), true);
					}
				}
			}
		});
	}

	class CellModifier implements ICellModifier {
		boolean isDoubleClick = false;
		private Object element = null;
		private TreeViewer treeViewer;

		public CellModifier(TreeViewer treeViewer) {
			this.treeViewer = treeViewer;
			this.treeViewer.getTree().addMouseListener(new MouseAdapter() {
				public void mouseDoubleClick(MouseEvent e) {
					isDoubleClick = true;
					if (element != null) {
						CellModifier.this.treeViewer.editElement(element, 0);
					}
					isDoubleClick = false;
				}
			});
		}

		public boolean canModify(Object element, String property) {
			this.element = element;
			if (isDoubleClick && (modify(element))) {
				isDoubleClick = false;
				return true;
			}
			return false;
		}

		private boolean modify(Object element) {
			if (!(element instanceof TagTree)) {
				return false;
			}
			TagTree t = (TagTree) element;
			if (baseMap.containsKey(t.getNamespace())) {
				if (baseMap.get(t.getNamespace()).contains(t.getTag())) {
					return false;
				}
				if (t.getNamespace().equals(t.getTag())) {
					return false;
				}
			}
			return true;
		}

		public Object getValue(Object element, String property) {
			return ((TagTree) element).getTag();
		}

		public void modify(Object element, String property, Object value) {
			if (element instanceof Item) {
				element = ((Item) element).getData();
			}
			for (TagTree t : ((TagTree) element).getParent().getChildren()) {
				if (t.getTag().equals(value)) {
					return;
				}
			}
			if ("".equals(custom)) {
				custom = store.getString("statistician.custom");
			}
			StringBuffer sb = new StringBuffer(custom);
			if (((TagTree) element).getNamespace().equals(((TagTree) element).getTag())) {
				int start = custom.indexOf("*" + ((TagTree) element).getNamespace());
				int end = start + ((TagTree) element).getNamespace().length() + 1;
				sb.replace(start, end, "*" + (String) value);
				((TagTree) element).setNamespace((String) value);
				((TagTree) element).setTag((String) value);
				for (TagTree t : ((TagTree) element).getChildren()) {
					t.setNamespace((String) value);
				}
			} else {
				int start = custom.indexOf("*" + ((TagTree) element).getNamespace());
				start = custom.indexOf(((TagTree) element).getTag(), start);
				int end = start + ((TagTree) element).getTag().length();
				sb.replace(start, end, (String) value);
				((TagTree) element).setTag((String) value);
			}
			custom = sb.toString();
			baseMapTree.sort(false);
			treeViewer.refresh(((TagTree) element).getParent());
			modify = true;
		}
	}
}

class TagTree {
	private TagTree parent;
	private String namespace;
	private String tag;
	private List<TagTree> children = new ArrayList<TagTree>();

	public TagTree() {
	}

	public TagTree(TagTree parent, String namespace, String tag) {
		this.parent = parent;
		this.namespace = namespace;
		this.tag = tag;
	}

	public void Add(String namespace, String tag) {
		for (TagTree te : children) {
			if (te.namespace.equals(namespace)) {
				TagTree t = new TagTree(te, namespace, tag);
				if (!te.children.contains(t)) {
					te.children.add(t);
				}
				return;
			}
		}
		TagTree te = new TagTree(this, namespace, namespace);
		te.children.add(new TagTree(te, namespace, tag));
		children.add(te);
	}

	public boolean equals(Object anObject) {
		if (anObject instanceof TagTree) {
			TagTree tt = (TagTree) anObject;
			return tt.namespace.equals(namespace) && tt.tag.equals(tag);
		}
		return false;
	}

	public boolean contains(Object anObject) {
		if (anObject instanceof TagTree) {
			TagTree object = (TagTree) anObject;
			for (TagTree te : children) {
				if (te.namespace.equals(object.tag)) {
					return true;
				}
			}
		}
		return false;
	}

	public TagTree find(String namespace, String tag) {
		if (this.namespace.equals(namespace) && this.tag.equals(tag)) {
			return this;
		}
		if (this.children.size() == 0) {
			return null;
		}
		for (TagTree child : this.children) {
			TagTree t = child.find(namespace, tag);
			if (t != null) {
				return t;
			}
		}
		return null;
	}

	public TagTree remove(String namespace, String tag) {
		TagTree t = this.find(namespace, tag);
		if (t != null) {
			t.getParent().getChildren().remove(t);
			if (t.getParent().getChildren().size() == 0) {
				return remove(t.getParent().getNamespace(), t.getParent().getTag());
			}
			return t;
		}
		return null;
	}

	public void sort(boolean ignoreCase) {
		List<TagTree> list = this.children;
		for (int i = 0; null != list && i < list.size(); i++) {
			for (int j = i + 1; j < list.size(); j++) {
				if (!ignoreCase) {
					if ((list.get(i).getTag()).compareTo(list.get(j).getTag()) > 0) {
						TagTree temp = list.get(i);
						list.set(i, list.get(j));
						list.set(j, temp);
					}
				} else {
					if ((list.get(i).getTag()).compareToIgnoreCase(list.get(j).getTag()) > 0) {
						TagTree temp = list.get(i);
						list.set(i, list.get(j));
						list.set(j, temp);
					}
				}
			}
			list.get(i).sort(true);
		}
	}

	public Map<String, List<String>> getMap() {
		Map<String, List<String>> map = new HashMap<String, List<String>>();
		for (TagTree child : this.children) {
			map.put(child.namespace, new ArrayList<String>());
			for (TagTree t : child.children) {
				map.get(child.namespace).add(t.tag);
			}
		}
		return map;
	}

	public void removeAll() {
		this.children.clear();
	}

	public String toString() {
		return tag;
	}

	public TagTree getParent() {
		return parent;
	}

	public void setParent(TagTree parent) {
		this.parent = parent;
	}

	public String getNamespace() {
		return namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public List<TagTree> getChildren() {
		return children;
	}

	public void setChildren(List<TagTree> children) {
		this.children = children;
	}
}
