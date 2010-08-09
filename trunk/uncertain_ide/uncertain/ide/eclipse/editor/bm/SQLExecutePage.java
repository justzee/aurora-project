package uncertain.ide.eclipse.editor.bm;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.logging.Level;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabFolder2Adapter;
import org.eclipse.swt.custom.CTabFolderEvent;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.ViewForm;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

import uncertain.composite.CompositeMap;
import uncertain.composite.DynamicObject;
import uncertain.core.UncertainEngine;
import uncertain.event.Configuration;
import uncertain.ide.Activator;
import uncertain.ide.Common;
import uncertain.ide.LocaleMessage;
import uncertain.ide.eclipse.action.ExecuteSqlAction;
import uncertain.ide.eclipse.editor.ISqlViewer;
import uncertain.ide.eclipse.editor.widgets.CustomDialog;
import uncertain.ide.eclipse.editor.widgets.PlainCompositeMapCellModifier;
import uncertain.ide.eclipse.editor.widgets.PlainCompositeMapContentProvider;
import uncertain.ide.eclipse.editor.widgets.PlainCompositeMapLabelProvider;
import uncertain.ide.eclipse.editor.widgets.PropertyGridViewer;
import uncertain.logging.LoggerProvider;
import uncertain.logging.LoggingContext;
import uncertain.ocm.IObjectRegistry;
import aurora.database.service.BusinessModelService;
import aurora.database.service.BusinessModelServiceContext;
import aurora.database.service.DatabaseServiceFactory;
import aurora.database.service.SqlServiceContext;

public class SQLExecutePage extends FormPage implements ISqlViewer {
	private static final String PageId = "SQLExecutePage";
	private static final String PageTitle = "Auto SQL Test";
	private CTabFolder mTabFolder;
	private SashForm sashForm;

	private Connection conn;

	private ToolBarManager toolBarManager;
	private static final String[] tabs = { "Query", "Insert", "Update",
			"Delete" };

	UncertainEngine uncertainEngine;
	private BusinessModelService service;
	private ViewForm viewForm;
	private TableViewer tableViewer;
	private boolean modify = false;

	public SQLExecutePage(FormEditor editor) {
		super(editor, PageId, PageTitle);
	}

	public SQLExecutePage(String id, String title) {
		super(id, title);
	}

	public SQLExecutePage(FormEditor editor, String id, String title) {
		super(editor, id, title);
	}

	protected void createFormContent(IManagedForm managedForm) {
		ScrolledForm form = managedForm.getForm();
		FormToolkit toolkit = managedForm.getToolkit();
		Composite shell = form.getBody();
		FillLayout layout = new FillLayout();
		shell.setLayout(layout);
		try {
			initConnection();
			createContent(shell, toolkit);
		} catch (Exception e) {
			CustomDialog.showExceptionMessageBox(e);
		}

	}

	protected void createContent(Composite shell, FormToolkit toolkit)
			throws Exception {
		viewForm = new ViewForm(shell, SWT.NONE);
		viewForm.setLayout(new FillLayout());
		createToolbar(viewForm);

		sashForm = new SashForm(viewForm, SWT.VERTICAL);
		createSqlContent(sashForm);
		createResultContent(sashForm);
		sashForm.setWeights(new int[] { 30, 70 });
		viewForm.setContent(sashForm);
	}

	protected void createResultContent(Composite parent) {
		tableViewer = new TableViewer(parent, SWT.FULL_SELECTION | SWT.BORDER);
		Table table = tableViewer.getTable();
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		tableViewer.setContentProvider(new PlainCompositeMapContentProvider());
		tableViewer.setCellModifier(new PlainCompositeMapCellModifier());

	}

	private void createToolbar(ViewForm viewForm) {
		ToolBar toolBar = new ToolBar(viewForm, SWT.RIGHT | SWT.FLAT);
		toolBarManager = new ToolBarManager(toolBar);
		ExecuteSqlAction action = new ExecuteSqlAction(this, ExecuteSqlAction
				.getDefaultImageDescriptor(), null);
		addActions(new Action[] { action });
		viewForm.setTopLeft(toolBar);
	}

	public void addActions(IAction[] actions) {
		if (actions == null)
			return;
		for (int i = 0; i < actions.length; i++) {
			toolBarManager.add(createActionContributionItem(actions[i]));
		}
		toolBarManager.update(true);
	}

	public void setActions(IAction[] actions) {
		toolBarManager.removeAll();
		if (actions == null)
			return;
		for (int i = 0; i < actions.length; i++) {
			toolBarManager.add(createActionContributionItem(actions[i]));
		}
		toolBarManager.update(true);
	}

	private void createSqlContent(Composite parent) throws Exception {
		mTabFolder = createTabFolder(parent);
		String TabHeighGrab = "           ";
		for (int i = 0; i < tabs.length; i++) {
			mTabFolder.getItem(i)
					.setText(TabHeighGrab + tabs[i] + TabHeighGrab);
			final StyledText st = createStyledText(mTabFolder);
			mTabFolder.getItem(i).setControl(st);
			final int itemIndex = i;
			final String sql = service.getSql(tabs[i]).toString();
			mTabFolder.addSelectionListener(new SelectionListener() {
				public void widgetSelected(SelectionEvent e) {
					if (mTabFolder.getSelectionIndex() == itemIndex
							&& (st.getText() == null || st.getText().equals(""))) {
						st.setText(sql);
					}
				}

				public void widgetDefaultSelected(SelectionEvent e) {
					widgetSelected(e);
				}
			});
		}

		// mTabFolder.setSelection(0);
		mTabFolder.layout(true);

	}

	public void createCustomerActions(PropertyGridViewer pae) {
	}

	private StyledText createStyledText(Composite parent) {
		StyledText mInnerText = new StyledText(parent, SWT.WRAP | SWT.V_SCROLL);
		mInnerText.setFont(new Font(mTabFolder.getDisplay(), "Courier New", 10,
				SWT.NORMAL));
		return mInnerText;
	}

	private CTabFolder createTabFolder(final Composite parent) {
		final CTabFolder tabFolder = new CTabFolder(parent, SWT.V_SCROLL
				| SWT.H_SCROLL | SWT.BORDER);
		tabFolder.setMaximizeVisible(true);
		tabFolder.addCTabFolder2Listener(new CTabFolder2Adapter() {
			public void minimize(CTabFolderEvent event) {
				tabFolder.setMinimized(true);
				tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
						false));
				parent.layout(true);
			}

			public void maximize(CTabFolderEvent event) {
				tabFolder.setMaximized(true);
				sashForm.setMaximizedControl(tabFolder);
				parent.layout(true);
			}

			public void restore(CTabFolderEvent event) {
				tabFolder.setMaximized(false);
				sashForm.setMaximizedControl(null);
				parent.layout(true);
			}
		});
		tabFolder.setSimple(false);
		tabFolder.setTabHeight(23);

		for (int i = 0; i < tabs.length; i++) {
			new CTabItem(tabFolder, SWT.H_SCROLL);
		}
		return tabFolder;
	}

	protected File getFile() {
		IFile ifile = ((IFileEditorInput) getEditor().getEditorInput())
				.getFile();
		String fileName = Common.getIfileLocalPath(ifile);
		return new File(fileName);
	}

	protected void initConnection() throws Exception {

		IProject project = ((IFileEditorInput) getEditor().getEditorInput())
				.getFile().getProject();

		uncertainEngine = UncertainProject.getUncertainEngine(project);
		conn = UncertainDataBase.getDBConnection(uncertainEngine);
		conn.setAutoCommit(false);

		IObjectRegistry reg = uncertainEngine.getObjectRegistry();
		DatabaseServiceFactory svcFactory = (DatabaseServiceFactory) reg
				.getInstanceOfType(DatabaseServiceFactory.class);

		BusinessModelServiceContext bc = createContext(uncertainEngine, conn);
		CompositeMap context = bc.getObjectContext();

		CompositeMap bm_model = svcFactory.getModelFactory()
				.getCompositeLoader().loadByFullFilePath(
						getFile().getAbsolutePath());
		service = svcFactory.getModelService(bm_model, context);
	}

	private BusinessModelServiceContext createContext(
			UncertainEngine uncertainEngine, Connection connection) {
		Configuration rootConfig = uncertainEngine.createConfig();
		rootConfig.addParticipant(this);
		CompositeMap context = new CompositeMap("root");
		BusinessModelServiceContext bc = (BusinessModelServiceContext) DynamicObject
				.cast(context, BusinessModelServiceContext.class);
		bc.setConfig(rootConfig);
		bc.setConnection(connection);
		LoggerProvider lp = LoggerProvider.createInstance(Level.FINE,
				System.out);
		LoggingContext.setLoggerProvider(context, lp);
		SqlServiceContext sc = SqlServiceContext
				.createSqlServiceContext(context);
		sc.setTrace(true);
		return bc;
	}

	private ActionContributionItem createActionContributionItem(IAction action) {
		ActionContributionItem aci = new ActionContributionItem(action);
		aci.setMode(ActionContributionItem.MODE_FORCE_TEXT);
		return aci;
	}

	public void refresh(ResultSet resultSet, int resultCount) {
		if (resultSet != null) {
			try {
				creatTableViewer(viewForm, resultSet);
				sashForm.layout();
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		} else if (resultCount != 0) {
			if (tableViewer != null && tableViewer.getTable() != null) {
				tableViewer.getTable().setVisible(false);
				sashForm.layout();
			}
			String sql = getSql().split(" ")[0];
			int message = CustomDialog.showConfirmDialogBox(null,
					"Are you sure you want to " + sql + " " + resultCount
							+ " records?");
			try {
				if (message == SWT.OK) {
					conn.commit();
				} else {
					conn.rollback();
				}
			} catch (SQLException e) {
				CustomDialog.showExceptionMessageBox(e);
			}
		}
	}

	public void refresh(String data) throws Exception {
		if (uncertainEngine == null || !isModify()) {
			return;
		}
		IObjectRegistry reg = uncertainEngine.getObjectRegistry();
		DatabaseServiceFactory svcFactory = (DatabaseServiceFactory) reg
				.getInstanceOfType(DatabaseServiceFactory.class);

		BusinessModelServiceContext bc = createContext(uncertainEngine, conn);
		CompositeMap context = bc.getObjectContext();

		// CompositeMap bm_model =
		// svcFactory.getModelFactory().getCompositeLoader().loadByFullFilePath(getFile().getAbsolutePath());
		CompositeMap bm_model = svcFactory.getModelFactory()
				.getCompositeLoader().loadFromString(data);
		service = svcFactory.getModelService(bm_model, context);

		for (int i = 0; i < tabs.length; i++) {
			StyledText st = (StyledText) mTabFolder.getItem(i).getControl();
			st.setText(service.getSql(tabs[i]).toString());
		}

		if (tableViewer != null && tableViewer.getTable() != null) {
			tableViewer.getTable().setVisible(false);
			sashForm.layout();
		}
		setModify(false);
	}

	private void creatTableViewer(Composite parent, ResultSet resultSet)
			throws SQLException {
		TableColumn[] tableColumns = tableViewer.getTable().getColumns();
		tableViewer.getTable().setVisible(false);
		for (int i = 0; i < tableColumns.length; i++) {
			tableColumns[i].dispose();
		}
		tableViewer.getTable().setVisible(true);
		String[] ColumnProperties = createColumnProperties(resultSet);
		tableViewer.setColumnProperties(ColumnProperties);
		CellEditor[] editors = new CellEditor[ColumnProperties.length];
		for (int i = 0; i < ColumnProperties.length; i++) {
			TextCellEditor tce = new TextCellEditor(tableViewer.getTable());
			Text text = (Text) tce.getControl();
			text.setEditable(false);
			editors[i] = tce;
		}
		tableViewer.setCellEditors(editors);

		createTableColumn(ColumnProperties, tableViewer.getTable());
		CompositeMap input = getInput(resultSet, ColumnProperties);
		tableViewer.setInput(input);
		tableViewer.setLabelProvider(new PlainCompositeMapLabelProvider(
				ColumnProperties));

	}

	private CompositeMap getInput(ResultSet tableRet, String[] ColumnProperties)
			throws SQLException {
		CompositeMap input = new CompositeMap();
		while (tableRet.next()) {
			CompositeMap element = new CompositeMap();
			for (int i = 1; i < ColumnProperties.length; i++) {
				element.put(ColumnProperties[i], tableRet
						.getObject(ColumnProperties[i]));
			}
			input.addChild(element);
		}
		return input;
	}

	private String[] createColumnProperties(ResultSet resultSet)
			throws SQLException {
		ResultSetMetaData resultSetMetaData = resultSet.getMetaData();

		String[] column_index = new String[resultSetMetaData.getColumnCount() + 1];
		column_index[0] = "sequence";
		for (int i = 1; i <= resultSetMetaData.getColumnCount(); i++) {
			column_index[i] = resultSetMetaData.getColumnName(i);
		}
		return column_index;
	}

	private void createTableColumn(String[] ColumnProperties, Table table) {
		String seq_imagePath = LocaleMessage.getString("property.icon");
		Image idp = Activator.getImageDescriptor(seq_imagePath).createImage();
		TableColumn seq_column = new TableColumn(table, SWT.LEFT);
		seq_column.setText(LocaleMessage.getString("sequence"));
		seq_column.setImage(idp);
		seq_column.pack();
		for (int i = 1; i < ColumnProperties.length; i++) {
			TableColumn column = new TableColumn(table, SWT.LEFT);
			column.setText(ColumnProperties[i]);
			column.setImage(idp);
			column.pack();
		}
	}

	public Connection getConnection() {
		return conn;
	}

	public String getSql() {
		StyledText st = (StyledText) mTabFolder.getSelection().getControl();
		return st.getText();
	}

	public boolean isModify() {
		return modify;
	}

	public void setModify(boolean modify) {
		this.modify = modify;
	}
}