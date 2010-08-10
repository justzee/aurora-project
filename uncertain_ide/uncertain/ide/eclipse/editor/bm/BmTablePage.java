package uncertain.ide.eclipse.editor.bm;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.eclipse.jface.dialogs.IDialogPage;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;

import uncertain.composite.CompositeMap;
import uncertain.ide.Activator;
import uncertain.ide.LocaleMessage;
import uncertain.ide.eclipse.editor.widgets.CustomDialog;
import uncertain.ide.eclipse.editor.widgets.PlainCompositeMapContentProvider;
import uncertain.ide.eclipse.editor.widgets.PlainCompositeMapLabelProvider;

/**
 * The "New" wizard page allows setting the container for the new file as well
 * as the file name. The page will only accept file name without the extension
 * OR with the extension that matches the expected one (bm).
 */

public class BmTablePage extends WizardPage {

	private TableViewer tableViewer;

	private Text containerText;

	private Text fileText;

	private BmNewWizard wizard;

	private String tableName;
	/**
	 * Constructor for SampleNewWizardPage.
	 * 
	 * @param pageName
	 */
	private final String[] ColumnProperties = { "sequence", "TABLE_NAME",
			"TABLE_CAT", "TABLE_SCHEM", "TABLE_TYPE", "REMARKS" };
	private final String[] ColumnText = { LocaleMessage.getString("sequence"),
			"TABLE_NAME", "TABLE_CAT", "TABLE_SCHEM", "TABLE_TYPE", "REMARKS" };
	DatabaseMetaData m_DBMetaData;

	public BmTablePage(ISelection selection, BmNewWizard bmWizard) {
		super("wizardPage");
		setTitle("Uncetain bm Editor File");
		setDescription("This wizard creates a new file with *.bm extension that can be opened by a multi-page editor.");
		this.wizard = bmWizard;
	}

	/**
	 * @see IDialogPage#createControl(Composite)
	 */
	public void createControl(Composite parent) {
		CompositeMap input = null;
		
		try {
			Connection dbConnection = wizard.getConnection();
			m_DBMetaData = dbConnection.getMetaData();
			input = getInput(m_DBMetaData, "%");
		} catch (Exception e) {
			CustomDialog.showExceptionMessageBox(e);
		}
		
		Composite container = new Composite(parent, SWT.NULL);
		GridData gd = new GridData(GridData.FILL_BOTH);
		container.setLayoutData(gd);

		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		container.setLayout(layout);

		Label headerLabel = new Label(container, SWT.NONE);
		headerLabel.setText("Please input the prefix of table:");

		gd = new GridData(GridData.FILL_HORIZONTAL);
		headerLabel.setLayoutData(gd);

		final Text filterText = new Text(container, SWT.SINGLE | SWT.BORDER
				| SWT.SEARCH);
		gd.heightHint = 15;
		filterText.setLayoutData(gd);

		tableViewer = new TableViewer(container, SWT.SINGLE
				| SWT.FULL_SELECTION | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);

		Table table = tableViewer.getTable();
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		// table.setBounds(97, 79, 373, 154);
		gd = new GridData(GridData.FILL_BOTH);
		gd.heightHint = tableViewer.getTable().getItemHeight() * 15;
		tableViewer.getTable().setLayoutData(gd);
		tableViewer.setContentProvider(new PlainCompositeMapContentProvider());
		final PlainCompositeMapLabelProvider  labelProvider = new PlainCompositeMapLabelProvider(
				ColumnProperties); 
		tableViewer.setLabelProvider(labelProvider);
		tableViewer
				.setCellEditors(new TextCellEditor[ColumnProperties.length + 1]);
		tableViewer.setColumnProperties(ColumnProperties);
		createTableColumn(ColumnProperties);
		final BmPageFilter filter = new BmPageFilter("TABLE_NAME");
		tableViewer.addFilter(filter);
		tableViewer.setInput(input);

		tableViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			
			public void selectionChanged(SelectionChangedEvent event) {
				wizard.refresh();
				
			}
		});

		filterText.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				String filterChars = filterText.getText();
				filterChars = filterChars.toUpperCase();
				filter.setFilterString(filterChars);
				labelProvider.refresh();
				tableViewer.refresh();
				tableViewer.getTable().setSelection(0);

			}
		});
		setControl(container);
		
	}

	private CompositeMap getInput(DatabaseMetaData DBMetaData,
			String tableNamePattern) throws SQLException {
		CompositeMap input = new CompositeMap();
		/*
		 * if(tableNamePattern != null && tableNamePattern.equals("%")){ return
		 * input; }
		 */
		ResultSet tableRet = DBMetaData.getTables(null, DBMetaData
				.getUserName(), tableNamePattern, new String[] { "TABLE" });
		while (tableRet.next()) {
			CompositeMap element = new CompositeMap();
			element.put(ColumnProperties[1], tableRet
					.getString(ColumnProperties[1]));
			element.put(ColumnProperties[2], tableRet
					.getString(ColumnProperties[2]));
			element.put(ColumnProperties[3], tableRet
					.getString(ColumnProperties[3]));
			element.put(ColumnProperties[4], tableRet
					.getString(ColumnProperties[4]));
			element.put(ColumnProperties[5], tableRet
					.getString(ColumnProperties[5]));
			input.addChild(element);
		}
		return input;
	}

	private void createTableColumn(String[] ColumnProperties) {
		String seq_imagePath = LocaleMessage.getString("property.icon");
		Image idp = Activator.getImageDescriptor(seq_imagePath).createImage();
		for (int i = 0; i < ColumnProperties.length; i++) {
			TableColumn column = new TableColumn(tableViewer.getTable(),
					SWT.LEFT);
			column.setText(ColumnText[i]);
			column.setImage(idp);
			// column.setWidth(80);
			column.pack();
		}
	}

	public String getContainerName() {
		return containerText.getText();
	}

	public String getFileName() {
		return fileText.getText();
	}

	public String getTableName() {
//		if (tableName == null) {
			IStructuredSelection selection = (IStructuredSelection) tableViewer
					.getSelection();
			CompositeMap record = (CompositeMap) selection.getFirstElement();
			tableName = record.getString("TABLE_NAME");
//			System.out.println("tableName:"+tableName);			
//		}
		return tableName;
	}

	public CompositeMap getPrimaryKeys() throws SQLException {
		CompositeMap primaryKeyArray = new CompositeMap(BmNewWizard.bm_pre,
				BmNewWizard.bm_uri, "primary-key");
		ResultSet tableRet = m_DBMetaData.getPrimaryKeys(null, m_DBMetaData
				.getUserName(), getTableName());
		while (tableRet.next()) {
			CompositeMap field = new CompositeMap(BmNewWizard.bm_pre,
					BmNewWizard.bm_uri, "field");
			field.put("name", tableRet.getString("COLUMN_NAME").toLowerCase());
			primaryKeyArray.addChild(field);
			/*
			 * System.out.println(tableRet.getString("PK_NAME"));
			 * System.out.println(tableRet.getInt("KEY_SEQ"));
			 */
		}
		return primaryKeyArray;
	}

	public DatabaseMetaData getDBMetaData() {
		return m_DBMetaData;
	}
}