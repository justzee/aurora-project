package aurora.ide.preferencepages;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.dialogs.ContainerCheckedTreeViewer;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

import uncertain.composite.QualifiedName;
import uncertain.schema.AbstractQualifiedNamed;
import uncertain.schema.Extension;
import uncertain.schema.ISchemaManager;
import aurora.ide.AuroraPlugin;
import aurora.ide.dialog.AddSxsdDialog;
import aurora.ide.dialog.ModifySxsdDialog;
import aurora.ide.helpers.DialogUtil;
import aurora.ide.helpers.LoadSchemaManager;

public class StatisticianPropertyPage extends PreferencePage implements IWorkbenchPreferencePage {
	// private List<TagTree> baselist = new ArrayList<TagTree>();
	// private List<TagTree> advancedlist = new ArrayList<TagTree>();

	private Map<String, List<String>> baseMap = new TreeMap<String, List<String>>();
	private Map<String, List<String>> advancedMap = new TreeMap<String, List<String>>();

	private ContainerCheckedTreeViewer treeViewer = null;
	private IPreferenceStore store = AuroraPlugin.getDefault().getPreferenceStore();
	private String[] noNamespace = { "query-fields", "columns", "center", "event", "features", "data-filter", "form", "table", "UL", "view", "IMG", "model-delete", "service-output", "model-update", "param", "A", "events", "mapping", "map", "model-load", "model-query", "a", "field", "H2", "tr", "img", "ref-field", "td", "br", "TABLE", "model-insert", "parameters", "font", "label", "script", "input", "iframe", "column", "query-field", "span", "model-execute", "model", "TD", "batch-apply", "div", "TR", "datas", "pk-field", "parameter", "style", "tbody", "DIV", "service", "LI" };
	private String selectNamespace;

	public StatisticianPropertyPage() {
		// initBaseList();
		// advancedlist.addAll(baselist);
		initBaseMap();
		advancedMap.putAll(baseMap);
		String[] ss = store.getString("statistician.custom").split("!");
		String key = "";
		for (String s : ss) {
			if ("".equals(s.trim())) {
				continue;
			} else if (s.indexOf("*") == 0) {
				s = s.substring(1);
				key = s;
				advancedMap.put(s, new ArrayList<String>());
			} else {
				advancedMap.get(key).add(s);
			}
		}
		for (String s : advancedMap.keySet()) {
			sort(advancedMap.get(s));
		}
	}

	private void initBaseMap() {
		ISchemaManager schemaManager = LoadSchemaManager.getSchemaManager();
		for (Object object : schemaManager.getAllTypes()) {
			String nameSpace = "";
			String name = "";
			AbstractQualifiedNamed absQualifiedNamed = (AbstractQualifiedNamed) object;
			nameSpace = absQualifiedNamed.getQName().getNameSpace();
			name = absQualifiedNamed.getQName().getFullName();
			if (!baseMap.containsKey(nameSpace)) {
				baseMap.put(nameSpace, new ArrayList<String>());
			}
			if (!baseMap.get(nameSpace).contains(name)) {
				baseMap.get(nameSpace).add(name);
			}
			if (absQualifiedNamed.getChilds() != null) {
				for (Object ele : absQualifiedNamed.getChilds()) {
					if (ele instanceof uncertain.schema.Extension) {
						QualifiedName qn = ((Extension) ele).getBaseType();
						nameSpace = qn.getNameSpace();
						name = qn.getFullName();
						if (!baseMap.containsKey(nameSpace)) {
							baseMap.put(nameSpace, new ArrayList<String>());
						}
						if (!baseMap.get(nameSpace).contains(name)) {
							baseMap.get(nameSpace).add(name);
						}
					} else if (!(ele instanceof uncertain.schema.Attribute)) {
						AbstractQualifiedNamed aqn = (AbstractQualifiedNamed) ele;
						if (null != aqn.getQName()) {
							QualifiedName qn = aqn.getQName();
							nameSpace = qn.getNameSpace();
							name = qn.getFullName();
							if (nameSpace == null) {
								nameSpace = "No namespace";
							}
							if (!baseMap.containsKey(nameSpace)) {
								baseMap.put(nameSpace, new ArrayList<String>());
							}
							if (!baseMap.get(nameSpace).contains(name)) {
								baseMap.get(nameSpace).add(name);
							}
						}
					}
				}
			}
		}
		if (!baseMap.containsKey("No namespace")) {
			baseMap.put("No namespace", new ArrayList<String>());
		}
		for (String s : noNamespace) {
			baseMap.get("No namespace").add(s);
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
		gdTreeViewer.verticalSpan = 5;
		treeViewer = new ContainerCheckedTreeViewer(container);

		GridData gdBtnAdd = new GridData();
		gdBtnAdd.verticalAlignment = SWT.TOP;
		gdBtnAdd.widthHint = 100;
		Button btnAdd = new Button(container, SWT.NULL);
		btnAdd.setText("Add");
		btnAdd.setLayoutData(gdBtnAdd);
		btnAdd.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				AddSxsdDialog dialog = new AddSxsdDialog(getShell(), advancedMap);
				if (Dialog.OK == dialog.open()) {
					Map<String, List<String>> m = dialog.getCustomMap();
					for (String s : m.keySet()) {
						if (advancedMap.containsKey(s)) {
							for (String t : m.get(s)) {
								if (!advancedMap.get(s).contains(t)) {
									advancedMap.get(s).add(t);
								}
							}
						} else {
							advancedMap.put(s, m.get(s));
						}
						sort(advancedMap.get(s));
					}
					save();
					treeViewer.setInput(advancedMap);
					load();
				}
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		GridData gdBtnModify = new GridData();
		gdBtnModify.verticalAlignment = SWT.TOP;
		gdBtnModify.widthHint = 100;
		final Button btnModify = new Button(container, SWT.NULL);
		btnModify.setText("Modify");
		btnModify.setLayoutData(gdBtnModify);
		btnModify.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				ModifySxsdDialog dialog = new ModifySxsdDialog(getShell());
				dialog.open();
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		btnModify.setEnabled(false);

		GridData gdBtnRemove = new GridData();
		gdBtnRemove.verticalAlignment = SWT.TOP;
		gdBtnRemove.widthHint = 100;
		final Button btnRemove = new Button(container, SWT.NULL);
		btnRemove.setText("Remove");
		btnRemove.setLayoutData(gdBtnRemove);
		btnRemove.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				advancedMap.remove(selectNamespace);
				store.setValue("statistician.custom", "");
				StringBuffer sb = new StringBuffer();
				for (String s : advancedMap.keySet()) {
					if (!baseMap.containsKey(s)) {
						sb.append("*" + s + "!");
						for (Object s1 : advancedMap.get(s)) {
							sb.append((String) s1 + "!");
						}
					}
				}
				store.setValue("statistician.custom", sb.toString());
				save();
				treeViewer.setInput(advancedMap);
				load();
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
				for (String s : advancedMap.keySet()) {
					treeViewer.setChecked(s, true);
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
				for (String s : advancedMap.keySet()) {
					treeViewer.setChecked(s, false);
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

			@SuppressWarnings("rawtypes")
			public Object[] getElements(Object inputElement) {
				return ((Map) inputElement).keySet().toArray();
			}

			public Object[] getChildren(Object parentElement) {
				if (null == advancedMap.get((String) parentElement)) {
					return null;
				}
				Object[] oo = advancedMap.get((String) parentElement).toArray();
				return oo;
			}

			public Object getParent(Object element) {
				return null;
			}

			public boolean hasChildren(Object element) {
				if (null != advancedMap.get((String) element) && advancedMap.get((String) element).size() > 0) {
					return true;
				}
				return false;
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
		treeViewer.setInput(advancedMap);
		treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				TreeSelection ts = (TreeSelection) event.getSelection();
				String s = (String) ts.getFirstElement();
				if (null == s) {
					btnModify.setEnabled(false);
					btnRemove.setEnabled(false);
					return;
				} else if (advancedMap.containsKey(s)) {
					selectNamespace = s;
				} else {
					for (String s1 : advancedMap.keySet()) {
						if (advancedMap.get(s1).contains(s)) {
							selectNamespace = s1;
							break;
						}
					}
				}
				if (baseMap.containsKey(selectNamespace)) {
					btnModify.setEnabled(false);
					btnRemove.setEnabled(false);
					if (selectNamespace.equals("No namespace")) {
						btnModify.setEnabled(true);
					}
				} else {
					btnModify.setEnabled(true);
					btnRemove.setEnabled(true);
				}
			}
		});
		load();
		return container;
	}

	@Override
	protected void performDefaults() {
		updateApplyButton();
		DialogUtil.showWarningMessageBox("Defaults");
	}

	@Override
	public boolean performOk() {
		if (save()) {
			return super.performOk();
		} else {
			return false;
		}
	}

	private boolean save() {
		StringBuffer sb = new StringBuffer();
		for (Object o : treeViewer.getCheckedElements()) {
			if ((o instanceof String) && advancedMap.containsKey(o)) {
				sb.append("*");
			}
			sb.append(o.toString());
			sb.append("!");
		}
		store.setValue("statistician.checked", sb.toString());
		try {
			((ScopedPreferenceStore) store).save();
			return true;
		} catch (IOException e) {
			return false;
		}
	}

	public void init(IWorkbench workbench) {
		// TODO ...
	}

	private void sort(List<String> list) {
		if (null != list) {
			for (int i = 0; i < list.size(); i++) {
				for (int j = i + 1; j < list.size(); j++) {
					if ((list.get(i)).compareToIgnoreCase(list.get(j)) > 0) {
						String temp = list.get(i);
						list.set(i, list.get(j));
						list.set(j, temp);
					}
				}
			}
		}
	}

	private void load() {
		String[] storeTag = store.getString("statistician.checked").replaceAll("\\*", "").split("!");
		treeViewer.getControl().setRedraw(false);

		for (int i = 0; i < storeTag.length; i++) {
			if (advancedMap.containsKey(storeTag[i])) {
				int j;
				treeViewer.expandToLevel(storeTag[i], 1);
				for (j = i + 1; j < storeTag.length; j++) {
					if (advancedMap.containsKey(storeTag[j])) {
						break;
					}
					treeViewer.setChecked(storeTag[j], true);
				}
				i = j;
				i--;
			}
		}

		treeViewer.collapseAll();
		treeViewer.getControl().setRedraw(true);
	}
}

class TagTree {
	private String namespace;
	private List<String> tags;

	public String getNamespace() {
		return namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	public boolean equals(Object anObject) {
		if (anObject instanceof TagTree) {
			TagTree tt = (TagTree) anObject;
			return tt.namespace.equals(namespace) && tags.equals(tt.tags);
		}
		return false;
	}
}
