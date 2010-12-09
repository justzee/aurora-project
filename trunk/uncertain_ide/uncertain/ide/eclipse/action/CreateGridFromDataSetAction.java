package uncertain.ide.eclipse.action;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import uncertain.composite.CompositeLoader;
import uncertain.composite.CompositeMap;
import uncertain.composite.QualifiedName;
import uncertain.ide.Activator;
import uncertain.ide.LoadSchemaManager;
import uncertain.ide.LocaleMessage;
import uncertain.ide.eclipse.celleditor.CellProperties;
import uncertain.ide.eclipse.celleditor.ComboxCellEditor;
import uncertain.ide.eclipse.celleditor.ICellEditor;
import uncertain.ide.eclipse.editor.IViewer;
import uncertain.ide.eclipse.editor.bm.GridDialog;
import uncertain.ide.eclipse.editor.widgets.CustomDialog;
import uncertain.ide.eclipse.editor.widgets.GridViewer;
import uncertain.ide.eclipse.editor.widgets.ICellModifierListener;
import uncertain.ide.eclipse.editor.widgets.IGridViewer;
import uncertain.ide.eclipse.wizards.ProjectProperties;
import uncertain.schema.ComplexType;
import uncertain.schema.Element;

public class CreateGridFromDataSetAction extends AddElementAction {

	public CreateGridFromDataSetAction(IViewer viewer, CompositeMap parentCM,
			String prefix, String uri, String cmName, String text,int actionStyle) {
		super(viewer, parentCM, prefix, uri, cmName, text,actionStyle);

	}

	public CreateGridFromDataSetAction(IViewer viewer, CompositeMap parentCM,
			QualifiedName qName,int actionStyle) {
		super(viewer, parentCM, qName,actionStyle);

	}
	public ImageDescriptor getDefaultImageDescriptor() {
		return Activator.getImageDescriptor(LocaleMessage.getString("wizard.icon"));
	}
	public void run() {
		
		CompositeMap view = parent.getParent().getParent();
		
		CompositeMap dataSets = getAvailableDataSets(view);
		if (dataSets == null || dataSets.getChildsNotNull().size() == 0) {
			CustomDialog.showWarningMessageBox("no.dataSet.available");
			return;
		}
		boolean successful = createGrid(view, dataSets);
		if (viewer != null && successful) {
			viewer.refresh(true);
		}
	}

	private CompositeMap getAvailableDataSets(CompositeMap parentCM) {
		CompositeMap dataSets = parentCM.getChild("dataSets");
		if (dataSets == null || dataSets.getChildsNotNull().size() == 0)
			return null;
		CompositeMap availableDataSets = new CompositeMap(dataSets.getPrefix(),
				dataSets.getNamespaceURI(), "dataSets");
		Iterator childs = dataSets.getChildsNotNull().iterator();
		for (; childs.hasNext();) {
			CompositeMap child = (CompositeMap) childs.next();
			String id = child.getString("id");
			String model = child.getString("model");
			if (id != null && !id.equals("") && model != null
					&& !model.equals("")) {
				CompositeMap newChild = new CompositeMap(child.getPrefix(),
						child.getNamespaceURI(), "dataSet");
				newChild.put("id", id);
				newChild.put("model", model);
				availableDataSets.addChild(newChild);
			}
		}
		return availableDataSets;
	}

	private boolean createGrid(CompositeMap parent, CompositeMap dataSets) {
		GridWizard wizard = new GridWizard(parent, dataSets);
		WizardDialog dialog = new WizardDialog(new Shell(), wizard);
		dialog.open();
		return wizard.isSuccessful();
	}

	class GridWizard extends Wizard implements IViewer {
		private boolean successful;
		private MainConfigPage mainConfigPage;
		// private EditorPage editorPage;
		private CompositeMap dataSets;
		private FieldPage fieldPage;
		private CompositeMap parent;

		public GridWizard(CompositeMap parent, CompositeMap dataSets) {
			super();
			this.parent = parent;
			this.dataSets = dataSets;
		}

		public void addPages() {
			mainConfigPage = new MainConfigPage(this, dataSets);
			mainConfigPage.setPageComplete(false);
			addPage(mainConfigPage);
			// editorPage= new EditorPage();
			// addPage(editorPage);
			fieldPage = new FieldPage(this);
			addPage(fieldPage);
		}

		public boolean performFinish() {
			String prefix = dataSets.getPrefix();
			String uri = dataSets.getNamespaceURI();
			CompositeMap grid = mainConfigPage.getGrid();
			CompositeMap columns = new CompositeMap(prefix, uri, "columns");
			CompositeMap editors = new CompositeMap(prefix, uri, "editors");
			grid.addChild(columns);
			grid.addChild(editors);
			Iterator selection = fieldPage.getSelection().getChildsNotNull()
					.iterator();
			HashMap hash = fieldPage.getMap();

			HashMap useEditor = new HashMap();
			for (; selection.hasNext();) {
				CompositeMap column = (CompositeMap) selection.next();
				String name = column.getString("name");
				CompositeMap record = null;
				if (hash.get(name) == null) {
					record = new CompositeMap("column");
					record.put("name", name);
				} else {
					record = (CompositeMap) hash.get(name);
					String editorType = record.getString("editor");
					record.put("editor", getEditorId(useEditor, grid
							.getString("id"), editorType));
				}
				columns.addChild(record);
			}
			Iterator it = useEditor.keySet().iterator();
			if (it != null) {
				for (; it.hasNext();) {
					String type = (String) it.next();
					CompositeMap newRecord = new CompositeMap(type);
					newRecord.put("id", useEditor.get(type));
					editors.addChild(newRecord);
				}
			}
			parent.addChild(grid);
			successful = true;
			return true;
		}

		public CompositeMap getFields() {
			return mainConfigPage.getFields();
		}

		public boolean isSuccessful() {
			return successful;
		}

		// public CompositeMap getEditors(){
		// return editorPage.getEditors();
		// }
		public void createPageControls(Composite pageContainer) {
		}

		private String getEditorId(HashMap editors, String gridId, String type) {
			String editorId = null;
			if (type != null && editors.get(type) == null) {
				editorId = gridId + "_" + type;
				editors.put(type, editorId);
			} else {
				editorId = (String) editors.get(type);
			}
			return editorId;
		}

		public void refresh(boolean isDirty) {
			fieldPage.repaint();
		}

	}

	class MainConfigPage extends WizardPage {
		public static final String PAGE_NAME = "mainPage";
		private CompositeMap dataSets;
		private String bindTarget;
		private String id;
		private boolean navBar;
		private String width;
		private String height;
		private Set allIds;

		private boolean addButton;
		private boolean deleteButton;
		private boolean saveButton;
		private CompositeMap bindBM;
		IViewer parentViewer;
		private String bmDir;
		private CompositeMap fields;

		protected MainConfigPage(IViewer parent, CompositeMap dataSets) {
			super(PAGE_NAME);
			setTitle(LocaleMessage.getString("mainpage"));
			this.dataSets = dataSets;
			this.parentViewer = parent;
		}
		
		public boolean canFlipToNextPage() {
			if (bindBM != null) {
				CompositeLoader loader = new CompositeLoader();
				String path = bindBM.getString("model").replace('.', '/') + '.'
						+ "bm";
				CompositeMap root = null;
				try {
					String fullPath = getBMFileDir() + File.separator + path;
					root = loader.loadByFullFilePath(fullPath);
				} catch (Exception e) {
					CustomDialog.showExceptionMessageBox(e);
				}
				fields = root.getChild("fields");
				if (fields == null || fields.getChildsNotNull().size() == 0) {
					CustomDialog.showErrorMessageBox("no.field.in.bm");
					return false;
				}
			}
			return super.canFlipToNextPage();
		}

		private String getBMFileDir() {
			if (bmDir == null) {
				try {
					bmDir = ProjectProperties.getBMBaseDir();
				} catch (Exception e) {
					CustomDialog.showExceptionMessageBox(e);
				}
			}
			return bmDir;
		}

		public void createControl(Composite parent) {
			Composite content = new Composite(parent, SWT.NONE);
			content.setLayout(new GridLayout(4, false));

			GridData gridData = new GridData(GridData.FILL_HORIZONTAL);

			Group dataSetGroup = new Group(content, SWT.NONE);
			gridData.horizontalSpan = 4;
			dataSetGroup.setLayoutData(gridData);
			dataSetGroup.setText(LocaleMessage.getString("bindtarget"));
			GridLayout layout = new GridLayout();
			layout.numColumns = 2;
			dataSetGroup.setLayout(layout);

			final Text bindTargetText = new Text(dataSetGroup, SWT.NONE);
			gridData = new GridData(GridData.FILL_HORIZONTAL);
			bindTargetText.setLayoutData(gridData);

			Button browseButton = new Button(dataSetGroup, SWT.PUSH);
			browseButton.setText(LocaleMessage.getString("openBrowse"));
			browseButton.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					bindBM = selectDataSet();
					if (bindBM != null) {
						bindTargetText.setText(bindBM.getString("id"));
						parentViewer.refresh(true);
					}
				}
			});

			Label label = new Label(content, SWT.CANCEL);
			label.setText(LocaleMessage.getString("please.input.id"));

			final Text idText = new Text(content, SWT.NONE);
			gridData = new GridData(GridData.FILL_HORIZONTAL);
			gridData.horizontalSpan = 3;
			idText.setLayoutData(gridData);

			bindTargetText.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					if (bindTargetText.getText() != null
							&& !(bindTargetText.getText().equals(""))) {
						bindTarget = bindTargetText.getText();
						if (idText.getText() == null
								|| idText.getText().equals("")) {
							idText.setText(bindTarget + "_grid");
						}
					}
					checkDialog();
				}
			});

			idText.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					if (idText.getText() != null
							&& !(idText.getText().equals(""))) {
						id = idText.getText();
					}
					checkDialog();
				}
			});

			Label widthLabel = new Label(content, SWT.CANCEL);
			widthLabel.setText(LocaleMessage.getString("please.input.width"));
			final Text widthText = new Text(content, SWT.NONE);
			gridData = new GridData(GridData.FILL_HORIZONTAL);
			gridData.horizontalSpan = 1;
			gridData.grabExcessHorizontalSpace = true;
			widthText.setLayoutData(gridData);
			widthText.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					if (widthText.getText() != null
							&& !(widthText.getText().equals(""))) {
						width = widthText.getText();
					}
					checkDialog();
				}
			});

			Label heightLabel = new Label(content, SWT.CANCEL);
			heightLabel.setText(LocaleMessage.getString("please.input.height"));
			final Text heightText = new Text(content, SWT.NONE);
			heightText.setLayoutData(gridData);
			heightText.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					if (heightText.getText() != null
							&& !(heightText.getText().equals(""))) {
						height = heightText.getText();
					}
					checkDialog();
				}
			});
			final Button navBarButton = new Button(content, SWT.CHECK);
			navBarButton.setText(LocaleMessage.getString("enbaleNavBar"));
			gridData = new GridData(GridData.FILL_HORIZONTAL);
			gridData.horizontalSpan = 4;
			navBarButton.setLayoutData(gridData);
			navBarButton.addSelectionListener(new SelectionListener() {
				public void widgetSelected(SelectionEvent e) {
					if (navBarButton.getSelection()) {
						navBar = true;
					} else
						navBar = false;
				}

				public void widgetDefaultSelected(SelectionEvent e) {
					widgetSelected(e);
				}
			});

			Group buttonGroup = new Group(content, SWT.NONE);
			gridData = new GridData(GridData.FILL_HORIZONTAL);
			gridData.horizontalSpan = 4;
			buttonGroup.setLayoutData(gridData);
			buttonGroup.setText(LocaleMessage.getString("selectbuttons"));
			layout = new GridLayout();
			layout.numColumns = 3;
			buttonGroup.setLayout(layout);

			final Button add = new Button(buttonGroup, SWT.CHECK);
			add.setText(LocaleMessage.getString("addbutton"));
			add.addSelectionListener(new SelectionListener() {
				public void widgetSelected(SelectionEvent e) {
					if (add.getSelection()) {
						addButton = true;
					} else
						addButton = false;
				}

				public void widgetDefaultSelected(SelectionEvent e) {
					widgetSelected(e);
				}
			});

			final Button delete = new Button(buttonGroup, SWT.CHECK);
			delete.setText(LocaleMessage.getString("deletebutton"));
			delete.addSelectionListener(new SelectionListener() {
				public void widgetSelected(SelectionEvent e) {
					if (delete.getSelection()) {
						deleteButton = true;
					} else
						deleteButton = false;
				}

				public void widgetDefaultSelected(SelectionEvent e) {
					widgetSelected(e);
				}
			});

			final Button save = new Button(buttonGroup, SWT.CHECK);
			save.setText(LocaleMessage.getString("savebutton"));
			save.addSelectionListener(new SelectionListener() {
				public void widgetSelected(SelectionEvent e) {
					if (save.getSelection()) {
						saveButton = true;
					} else
						saveButton = false;
				}

				public void widgetDefaultSelected(SelectionEvent e) {
					widgetSelected(e);
				}
			});
			setControl(content);
		}

		public CompositeMap getFields() {
			return fields;
		}

		public CompositeMap getGrid() {
			String prefix = dataSets.getPrefix();
			String uri = dataSets.getNamespaceURI();
			CompositeMap grid = new CompositeMap(prefix, uri, "grid");
			grid.put("id", id);
			grid.put("bindTarget", bindTarget);
			grid.put("width", width);
			grid.put("height", height);
			grid.put("navBar", String.valueOf(navBar));
			if (addButton || deleteButton || saveButton) {
				CompositeMap toolBar = new CompositeMap(prefix, uri, "toolBar");
				grid.addChild(toolBar);
				createButton(toolBar, addButton, "add");
				createButton(toolBar, deleteButton, "delete");
				createButton(toolBar, saveButton, "save");
			}
			return grid;
		}

		private void createButton(CompositeMap toobar, boolean create,
				String type) {
			if (create) {
				CompositeMap button = new CompositeMap(prefix, uri, "button");
				button.put("type", type);
				toobar.addChild(button);
			}
		}

		private CompositeMap selectDataSet() {
			String[] columnProperties = { "id", "model" };
			GridViewer grid = new GridViewer(dataSets, columnProperties,
					IGridViewer.NONE);
			grid.setFilterColumn("id");
			GridDialog dialog = new GridDialog(new Shell(), grid);
			if (dialog.open() == Window.OK) {
				return dialog.getSelected();
			}
			return null;
		}

		private String outputErrorMessage() {
			if (allIds == null) {
				allIds = new HashSet();
				CompositeMapAction.collectAttribueValues(allIds, "id", dataSets
						.getRoot());
			}

			if (bindTarget == null || bindTarget.equals("")) {
				return LocaleMessage
						.getString("DataSet.selection.can.not.be.null");

			}
			if (id == null || id.equals("")) {
				return LocaleMessage.getString("id.can.not.be.null");
			}
			if (allIds.contains(id)) {
				return LocaleMessage
						.getString("This.id.has.exists.please.change.it");
			}
			return null;
		}

		private void checkDialog() {
			String errorMessage = outputErrorMessage();
			setErrorMessage(errorMessage);
			if (errorMessage != null) {
				setPageComplete(false);
			} else {
				setPageComplete(true);
			}
		}

	}

	class EditorPage extends WizardPage implements IViewer {
		public static final String PAGE_NAME = "EditorPage";
		public static final String uri = "http://www.aurora-framework.org/application";
		private CompositeMap data;
		GridViewer editorViewer;

		protected EditorPage() {
			super(PAGE_NAME);
			setTitle("Editor Page");
		}

		public void createControl(Composite parent) {
			Composite content = new Composite(parent, SWT.NONE);
			content.setLayout(new GridLayout());

			CompositeMap grid = new CompositeMap("a", uri, "grid");
			data = new CompositeMap("a", uri, "editors");
			data.setParent(grid);
			editorViewer = new GridViewer(null, IGridViewer.isOnlyUpdate);
			editorViewer.setParent(this);
			editorViewer.createViewer(content, data);

			setControl(content);
		}

		public CompositeMap getEditors() {
			return data;
		}

		public void refresh(boolean isDirty) {
			editorViewer.refresh(false);
		}

	}

	class FieldPage extends WizardPage implements IViewer {
		public static final String PAGE_NAME = "FiledPage";
		public static final String uri = "http://www.aurora-framework.org/application";
		private GridWizard wizard;
		private GridViewer grid;
		ModifyCompositeMapListener newCompositeMap;

		protected FieldPage(GridWizard wizard) {
			super(PAGE_NAME);
			setTitle("Filed Page");
			this.wizard = wizard;
		}

		public void createControl(Composite parent) {
			Composite content = new Composite(parent, SWT.NONE);
			content.setLayout(new GridLayout());
			CompositeMap fields = wizard.getFields();
			CompositeMap filedNames = new CompositeMap();
			Iterator it = fields.getChildsNotNull().iterator();
			for (; it.hasNext();) {
				CompositeMap child = (CompositeMap) it.next();
				String targetNode = child.getString("name");
				if (targetNode == null)
					continue;
				CompositeMap newChild = new CompositeMap();
				newChild.put("name", targetNode);
				newChild.put("prompt", child.getString("prompt"));
				filedNames.addChild(newChild);

			}
			String[] columnProperties = { "name", "prompt", "editor" };

			grid = new GridViewer(columnProperties, IGridViewer.isMulti
					| IGridViewer.fullEditable | IGridViewer.isAllChecked);
			grid.setParent(this);
			grid.createViewer(content);

			TableViewer tableView = grid.getViewer();
			CellEditor[] celleditors = new CellEditor[columnProperties.length + 1];
			for (int i = 0; i < columnProperties.length - 1; i++) {
				celleditors[i + 1] = new TextCellEditor(tableView.getTable());
			}
			// CompositeMap editors = wizard.getEditors();
			QualifiedName qn = new QualifiedName(uri, "Field");
			ComplexType type = LoadSchemaManager.getSchemaManager()
					.getComplexType(qn);
			List editors = LoadSchemaManager.getSchemaManager()
					.getElementsOfType(type);
			Iterator editorIt = editors.iterator();
			String[] items = new String[editors.size()];
			for (int i = 0; editorIt.hasNext(); i++) {
				Element editor = (Element) editorIt.next();
				items[i] = editor.getLocalName();
			}
			CellProperties cellProperties = new CellProperties(grid, "editor",
					false);
			cellProperties.setItems(items);
			ICellEditor cellEditor = new ComboxCellEditor(cellProperties);
			cellEditor.init();
			celleditors[columnProperties.length] = cellEditor.getCellEditor();
			grid.addEditor("editor", cellEditor);
			grid.setCellEditors(celleditors);
			grid.setData(filedNames);
			newCompositeMap = new ModifyCompositeMapListener();
			grid.addCellModifierListener(newCompositeMap);

			setControl(content);
		}

		public CompositeMap getSelection() {
			return grid.getSelection();
		}

		public HashMap getMap() {
			return newCompositeMap.getSelection();
		}

		public void refresh(boolean isDirty) {
			grid.refresh(false);
		}

		public void repaint() {

			// if this page not init
			if (newCompositeMap == null)
				return;

			CompositeMap fields = wizard.getFields();
			CompositeMap filedNames = new CompositeMap();
			for (Iterator it = fields.getChildsNotNull().iterator(); it
					.hasNext();) {
				CompositeMap child = (CompositeMap) it.next();
				String targetNode = child.getString("name");
				if (targetNode == null)
					continue;
				CompositeMap newChild = new CompositeMap();
				newChild.put("name", targetNode);
				newChild.put("prompt", child.getString("prompt"));
				filedNames.addChild(newChild);

			}
			grid.setData(filedNames);
			newCompositeMap.clear();
			// grid.refresh(false);
		}

		class ModifyCompositeMapListener implements ICellModifierListener {
			HashMap records = new HashMap();

			public void modify(CompositeMap record, String property,
					String value) {
				String name = record.getString("name");
				CompositeMap newRecord = null;
				if (records.get(name) == null) {
					newRecord = new CompositeMap("column");
					newRecord.put("name", name);
					records.put(name, newRecord);
				} else {
					newRecord = (CompositeMap) records.get(name);
				}
				newRecord.put(property, value);
			}

			public HashMap getSelection() {
				return records;
			}

			public void clear() {
				records.clear();
			}
		}

	}
}
