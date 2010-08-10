package uncertain.ide.eclipse.editor.bm;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.eclipse.jface.dialogs.IDialogPage;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TableViewer;
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
import uncertain.datatype.DataType;
import uncertain.datatype.DataTypeRegistry;
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

public class BmTableFieldsPage extends WizardPage {
	private TableViewer tableViewer;

	private Text containerText;

	private Text fileText;

	Text uncetainText;
	BmNewWizard wizard;
	/**
	 * Constructor for SampleNewWizardPage.
	 * 
	 * @param pageName
	 */
	private final String[] ColumnProperties = { "Sequence", "COLUMN_NAME",
			"TYPE_NAME", "COLUMN_SIZE", "IS_NULLABLE", "REMARKS" };
	private final String[] ColumnText = { LocaleMessage.getString("sequence"),
			"COLUMN_NAME", "TYPE_NAME", "COLUMN_SIZE", "IS_NULLABLE", "REMARKS" };
	DatabaseMetaData m_DBMetaData;

	CompositeMap fields = new CompositeMap();

	private CheckboxTableViewer ctv;
	private PlainCompositeMapLabelProvider labelProvider;

	public BmTableFieldsPage(ISelection selection, BmNewWizard bmWizard) {
		super("wizardPage");
		setTitle("Uncetain bm Editor File");
		setDescription("This wizard creates a new file with *.bm extension that can be opened by a multi-page editor.");
		this.wizard = bmWizard;
	}

	/**
	 * @see IDialogPage#createControl(Composite)
	 */
	public void createControl(Composite parent) {
		m_DBMetaData = wizard.getDBMetaData();

		CompositeMap input = null;
		try {
			input = getInput(m_DBMetaData, "%");
		} catch (SQLException e) {
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

		tableViewer = new TableViewer(container, SWT.MULTI | SWT.CHECK
				| SWT.FULL_SELECTION | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);

		Table table = tableViewer.getTable();
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		// table.setBounds(97, 79, 373, 154);
		gd = new GridData(GridData.FILL_BOTH);
		gd.heightHint = tableViewer.getTable().getItemHeight() * 15;
		tableViewer.getTable().setLayoutData(gd);
		tableViewer.setContentProvider(new PlainCompositeMapContentProvider());
		labelProvider = new PlainCompositeMapLabelProvider(
				ColumnProperties);
		tableViewer.setLabelProvider(labelProvider);

		tableViewer.setColumnProperties(ColumnProperties);
		createTableColumn(ColumnProperties);
		final BmPageFilter filter = new BmPageFilter("COLUMN_NAME");
		tableViewer.addFilter(filter);
		tableViewer.setInput(input);

		ctv = new CheckboxTableViewer(tableViewer.getTable());
		ctv.setAllChecked(true);

		filterText.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				String filterChars = filterText.getText();
				filterChars = filterChars.toUpperCase();
				filter.setFilterString(filterChars);
				labelProvider.refresh();
				tableViewer.refresh();
				// tableViewer.getTable().getItem(1).set

			}
		});
		setControl(container);
		setPageComplete(true);
	}

	private CompositeMap getInput(DatabaseMetaData DBMetaData,
			String tableNamePattern) throws SQLException {
		CompositeMap input = new CompositeMap();
		ResultSet tableRet = DBMetaData.getColumns(null, DBMetaData
				.getUserName(), wizard.getTableName(), "%");
		// ResultSetMetaData rsMetaData = tableRet.getMetaData();
		// for(int i=1;i<=rsMetaData.getColumnCount();i++){
		// System.out.println(rsMetaData.getColumnName(i)+":"+rsMetaData.getColumnTypeName(i));
		// }

		while (tableRet.next()) {
			CompositeMap element = new CompositeMap();
			element.put(ColumnProperties[1], tableRet
					.getString(ColumnProperties[1]));
			element.put(ColumnProperties[2], tableRet
					.getString(ColumnProperties[2]));
			element.put(ColumnProperties[3], new Integer(tableRet
					.getInt(ColumnProperties[3])));
			element.put(ColumnProperties[4], tableRet
					.getString(ColumnProperties[4]));
			/*
			 * short dataType = tableRet.getShort(ColumnProperties[4]); String
			 * javaDataTyper=
			 * java.sql.Types.class.getFields()[dataType].getName();
			 * 
			 * element.put(ColumnProperties[4], javaDataTyper);
			 */
			element.put(ColumnProperties[5], tableRet
					.getString(ColumnProperties[5]));
			element.put("DATA_TYPE", new Integer(tableRet.getInt("DATA_TYPE")));
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

	public String getUncertainProjectDir() {
		return uncetainText.getText();
	}

	public CompositeMap getSelectedFields() {
		CompositeMap fieldsArray = new CompositeMap(BmNewWizard.bm_pre,
				BmNewWizard.bm_uri, "fields");
		Object[] elements = ctv.getCheckedElements();
		for (int j = 0; j < elements.length; j++) {
			CompositeMap record = (CompositeMap) elements[j];
			CompositeMap field = new CompositeMap(BmNewWizard.bm_pre,
					BmNewWizard.bm_uri, "field");
			field.put("name", record.getString("COLUMN_NAME").toLowerCase());
			field.put("physicalName", record.getString("COLUMN_NAME"));
			String required = record.getString("IS_NULLABLE").equals("YES") ? "false"
					: "true";
			field.put("required", required);
			String dataType = record.getString("TYPE_NAME");
			field.put("databaseType", dataType);
			Integer db_data_type = record.getInt("DATA_TYPE");
			DataTypeRegistry dtr = DataTypeRegistry.getInstance();
			DataType dt = dtr.getType(db_data_type.intValue());
			if (dt == null) {
				CustomDialog.showErrorMessageBox(null, "dataBase dataType "
						+ db_data_type + " is not registried!");
			} else
				field.put("datatype", dt.getJavaType().getName());

			fieldsArray.addChild(field);
		}
		return fieldsArray;
	}

	public void refresh() {
		CompositeMap input = null;
		try {
			input = getInput(m_DBMetaData, "%");
			labelProvider.refresh();
		} catch (SQLException e) {
			CustomDialog.showExceptionMessageBox(e);
		}
		tableViewer.setInput(input);
		ctv.setAllChecked(true);
	}
}