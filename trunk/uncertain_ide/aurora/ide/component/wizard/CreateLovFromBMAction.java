package aurora.ide.component.wizard;


import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import uncertain.composite.CompositeMap;
import uncertain.composite.QualifiedName;
import aurora.ide.AuroraPlugin;
import aurora.ide.api.composite.map.CommentCompositeMap;
import aurora.ide.bm.BMUtil;
import aurora.ide.bm.editor.GridDialog;
import aurora.ide.celleditor.BoolCellEditor;
import aurora.ide.celleditor.CellInfo;
import aurora.ide.celleditor.ICellEditor;
import aurora.ide.editor.core.IViewer;
import aurora.ide.editor.widgets.GridViewer;
import aurora.ide.editor.widgets.PropertyHashViewer;
import aurora.ide.editor.widgets.WizardPageRefreshable;
import aurora.ide.editor.widgets.core.IGridViewer;
import aurora.ide.helpers.ApplicationException;
import aurora.ide.helpers.AuroraConstant;
import aurora.ide.helpers.AuroraResourceUtil;
import aurora.ide.helpers.CompositeMapUtil;
import aurora.ide.helpers.DialogUtil;
import aurora.ide.helpers.LocaleMessage;
import aurora.ide.helpers.ProjectUtil;
import aurora.ide.node.action.AddElementAction;

public class CreateLovFromBMAction extends AddElementAction {
	final static String title = "创建Lov";
	public CreateLovFromBMAction(IViewer viewer, CompositeMap currentNode, QualifiedName childQN, int actionStyle) {
		super(viewer, currentNode, childQN, actionStyle);
	}
	public ImageDescriptor getDefaultImageDescriptor() {
		return AuroraPlugin.getImageDescriptor(LocaleMessage.getString("wizard.icon"));
	}
	public void run() {
		if (currentNode == null || !AuroraConstant.FieldsQN.equals(currentNode.getQName())) {
			DialogUtil.showErrorMessageBox("当前选中的不是Fields节点!");
			return;
		}
		LovWizard wizard = new LovWizard(currentNode);
		WizardDialog dialog = new WizardDialog(new Shell(), wizard);
		dialog.open();
		boolean successful = wizard.isSuccessful();
		if (viewer != null && successful) {
			viewer.refresh(true);
		}
	}
	class LovWizard extends Wizard implements IViewer {
		private boolean successful;
		private MainConfigPage mainConfigPage;
		private FieldPage fieldPage;
		private CompositeMap fieldsNode;

		public LovWizard(CompositeMap fieldsNode) {
			super();
			setText(title);
			this.fieldsNode = fieldsNode;
		}

		public void addPages() {
			mainConfigPage = new MainConfigPage(this, fieldsNode);
			mainConfigPage.setPageComplete(false);
			addPage(mainConfigPage);
			fieldPage = new FieldPage(this);
			addPage(fieldPage);
		}

		public boolean performFinish() {
			String prefix = CompositeMapUtil.getContextPrefix(fieldsNode, childQN);
			childQN.setPrefix(prefix);
			CompositeMap fieldNode = new CommentCompositeMap(childQN.getPrefix(),childQN.getNameSpace(),childQN.getLocalName());
			fieldNode.put("name", mainConfigPage.getFieldNameText().getText());
			fieldNode.put("lovservice", mainConfigPage.getBMText().getText());
			fieldNode.put("lovHeight", mainConfigPage.getLovHeightText().getText());
			fieldNode.put("lovWidth", mainConfigPage.getLovWidthText().getText());
			fieldNode.put("lovGridHeigh", mainConfigPage.getLovGridHeightText().getText());
			CompositeMap mappingNode = new CommentCompositeMap(childQN.getPrefix(),childQN.getNameSpace(),"mapping");
			CompositeMap fields = fieldPage.getSelection();
			if(fields ==  null||fields.getChilds()==null){
				fieldPage.updatePageStatus("请至少选择一个字段.");
				fieldPage.setPageComplete(true);
				return false;
			}
			for(Iterator it = fields.getChildIterator();it.hasNext();){
				CompositeMap field = (CompositeMap)it.next();
				CompositeMap mapNode = new CommentCompositeMap(childQN.getPrefix(),childQN.getNameSpace(),"map");
				String fieldName= field.getString("name");
				mapNode.put("from", fieldName);
				mapNode.put("to", fieldName);
				mappingNode.addChild(mapNode);
			}
			fieldNode.addChild(mappingNode);
			CompositeMapUtil.addElement(fieldsNode,fieldNode);
			successful = true;
			return true;
		}

		public boolean isSuccessful() {
			return successful;
		}

		public void createPageControls(Composite pageContainer) {
		}
		public void refresh(boolean isDirty) {

		}
		public CompositeMap getBMFields() {
			return mainConfigPage.getBMFields();
		}
	}

	class MainConfigPage extends WizardPageRefreshable {
		public static final String PAGE_NAME = "mainPage";
		private CompositeMap bmFields;
		private Text bmText;
		private Text fieldNameText;
		private Text lovHeightText;
		private Text lovWidthText;
		public CompositeMap getBMFields() {
			return bmFields;
		}
		public Text getBMText() {
			return bmText;
		}
		public Text getFieldNameText() {
			return fieldNameText;
		}
		public Text getLovHeightText() {
			return lovHeightText;
		}
		public Text getLovWidthText() {
			return lovWidthText;
		}
		public Text getLovGridHeightText() {
			return lovGridHeightText;
		}

		private Text lovGridHeightText;

		protected MainConfigPage(IViewer parent, CompositeMap fieldsNode) {
			super(PAGE_NAME);
			setTitle(title + "--" + LocaleMessage.getString("mainpage"));
		}
		public boolean canFlipToNextPage() {
			if (bmText != null && !("".equals(bmText.getText())) && bmFields == null) {
				DialogUtil.showErrorMessageBox("此dataSet没有可用字段.");
				return false;
			}
			return super.canFlipToNextPage();
		}
		public void checkPageValues() {
			String parentBMPath = bmText.getText();
			if (parentBMPath == null || "".equals(parentBMPath)) {
				updatePageStatus("必须指定父BM!");
				return;
			}
			IResource bmFile = null;
			try {
				bmFile = BMUtil.getBMResourceFromClassPath(parentBMPath);
			} catch (ApplicationException e) {
				DialogUtil.showExceptionMessageBox(e);
			}
			if (bmFile == null) {
				updatePageStatus("此BM文件不存在!");
				return;
			}
			try {
				bmFields = BMUtil.getFieldsFromBMPath(parentBMPath);
			} catch (ApplicationException e) {
				DialogUtil.showExceptionMessageBox(e);
			}
			if (bmFields == null) {
				updatePageStatus("此BM没有定义field字段.");
				return;
			}
			String fieldName = fieldNameText.getText();
			if (fieldName == null || fieldName.equals("")) {
				updatePageStatus("请输入字段名");
				return;
			}
			String lovHeightStr = lovHeightText.getText();
			try {
				Integer.parseInt(lovHeightStr);
			} catch (NumberFormatException e) {
				updatePageStatus("窗口高度值必须是数字.");
				return;
			}

			String lovWidthStr = lovWidthText.getText();
			try {
				Integer.parseInt(lovWidthStr);
			} catch (NumberFormatException e) {
				updatePageStatus("窗口宽度值必须是数字.");
				return;
			}
			String lovGridHeightStr = lovGridHeightText.getText();
			try {
				Integer.parseInt(lovGridHeightStr);
			} catch (NumberFormatException e) {
				updatePageStatus("数据显示区值必须是数字.");
				return;
			}
			updatePageStatus(null);
		}
		public void createControl(Composite parent) {
			Composite container = new Composite(parent, SWT.NULL);
			container.setLayout(new GridLayout(3, false));
			GridData gd = new GridData(GridData.FILL_HORIZONTAL);
			Label label = new Label(container, SWT.NULL);
			label.setText("选择BM");
			bmText = new Text(container, SWT.BORDER | SWT.SINGLE);
			bmText.setLayoutData(gd);
			bmText.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					checkPageValues();
				}
			});
			Button pickBMButton = new Button(container, SWT.PUSH);
			pickBMButton.setText(LocaleMessage.getString("openBrowse"));
			pickBMButton.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					try {
						pickBM();
					} catch (ApplicationException e) {
						DialogUtil.showExceptionMessageBox(e);
					}
				}
				private void pickBM() throws ApplicationException {
					IProject project = ProjectUtil.getIProjectFromSelection();
					List bmList = ProjectUtil.getBMSFromProject(project);
					CompositeMap bms = new CommentCompositeMap("bms");
					String[] columnNames = {"name", "fullpath"};
					for (Iterator it = bmList.iterator(); it.hasNext();) {
						IResource bmFile = (IResource) it.next();
						CompositeMap child = new CommentCompositeMap("record");
						child.put("name", bmFile.getName());
						child.put("fullpath", AuroraResourceUtil.getRegisterPath((IFile) bmFile));
						bms.addChild(child);
					}
					GridViewer grid = new GridViewer(null, IGridViewer.filterBar | IGridViewer.NoToolBar);
					grid.setData(bms);
					grid.setFilterColumn("name");
					grid.setColumnNames(columnNames);
					GridDialog dialog = new GridDialog(new Shell(), grid);
					if (dialog.open() == Window.OK) {
						String classPath = dialog.getSelected().getString("fullpath");
						if (classPath != null)
							bmText.setText(classPath);
					}
				}
			});
			label = new Label(container, SWT.NULL);
			label.setText("字段名");
			fieldNameText = new Text(container, SWT.BORDER | SWT.SINGLE);
			gd = new GridData(GridData.FILL_HORIZONTAL);
			gd.horizontalSpan = 2;
			fieldNameText.setLayoutData(gd);
			fieldNameText.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					checkPageValues();
				}
			});
			label = new Label(container, SWT.NULL);
			label.setText("窗口高度");
			lovHeightText = new Text(container, SWT.BORDER | SWT.SINGLE);
			lovHeightText.setLayoutData(gd);
			lovHeightText.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					checkPageValues();
				}
			});
			label = new Label(container, SWT.NULL);
			label.setText("窗口宽度");
			lovWidthText = new Text(container, SWT.BORDER | SWT.SINGLE);
			lovWidthText.setLayoutData(gd);
			lovWidthText.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					checkPageValues();
				}
			});
			label = new Label(container, SWT.NULL);
			label.setText("数据显示区高度");
			lovGridHeightText = new Text(container, SWT.BORDER | SWT.SINGLE);
			lovGridHeightText.setLayoutData(gd);
			lovGridHeightText.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					checkPageValues();
				}
			});
			setControl(container);
		}
	}

	class FieldPage extends WizardPageRefreshable {
		public static final String PAGE_NAME = "FiledPage";
		private LovWizard wizard;
		private GridViewer gridViewer;
		PropertyHashViewer hashViewer;
		HashMap field_properties = new HashMap();

		protected FieldPage(LovWizard wizard) {
			super(PAGE_NAME);
			setTitle(title + "--" + LocaleMessage.getString("filed.page"));
			this.wizard = wizard;
		}
		public void createControl(Composite parent) {
			Composite content = new Composite(parent, SWT.NONE);
			content.setLayout(new GridLayout());
//			String[] columnNames = {"name", "query", "display", "return"};
//			String[] columnTitles = {"字段", "筛选", "显示", "返回"};
			String[] columnNames = {"name"};
			String[] columnTitles = {"字段"};
			gridViewer = new GridViewer(columnNames, IGridViewer.isMulti | IGridViewer.isAllChecked
					| IGridViewer.isOnlyUpdate|IGridViewer.NoSeqColumn);
			gridViewer.setColumnTitles(columnTitles);
			try {
				gridViewer.createViewer(content);
				CellEditor[] celleditors = new CellEditor[columnNames.length];
				for (int i = 1; i < columnNames.length; i++) {
					CellInfo cellProperties = new CellInfo(gridViewer, columnNames[i], false);
					ICellEditor cellEditor = new BoolCellEditor(cellProperties);
					cellEditor.init();
					celleditors[i] = cellEditor.getCellEditor();
					gridViewer.addEditor(columnNames[i], cellEditor);
				}
				gridViewer.setCellEditors(celleditors);
				gridViewer.setData(wizard.getBMFields());
			} catch (ApplicationException e) {
				DialogUtil.showExceptionMessageBox(e);
			}
			setPageComplete(true);
			initPageValues();
			checkPageValues();
			setControl(content);
		}
		public void refreshPage() {
			if (!isInit()) {
				return;
			}
		}
		public CompositeMap getSelection(){
			return gridViewer.getSelection();
		}
		public void checkPageValues() {
		}
	}
}
