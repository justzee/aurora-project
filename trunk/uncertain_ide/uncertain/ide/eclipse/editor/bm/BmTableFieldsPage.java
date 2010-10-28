package uncertain.ide.eclipse.editor.bm;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.eclipse.jface.dialogs.IDialogPage;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import uncertain.composite.CompositeMap;
import uncertain.datatype.DataType;
import uncertain.datatype.DataTypeRegistry;
import uncertain.ide.LocaleMessage;
import uncertain.ide.eclipse.editor.widgets.CustomDialog;
import uncertain.ide.eclipse.editor.widgets.GridViewer;
import uncertain.ide.eclipse.editor.widgets.IGridViewer;

/**
 * The "New" wizard page allows setting the container for the new file as well
 * as the file name. The page will only accept file name without the extension
 * OR with the extension that matches the expected one (bm).
 */

public class BmTableFieldsPage extends WizardPage {
	private Text containerText;
	BmNewWizard wizard;
	/**
	 * Constructor for SampleNewWizardPage.
	 * 
	 * @param pageName
	 */
	private final String[] columnProperties = {"COLUMN_NAME",
			"TYPE_NAME", "COLUMN_SIZE", "IS_NULLABLE", "REMARKS" };
	DatabaseMetaData dbMetaData;

	CompositeMap fields = new CompositeMap();
	GridViewer filterCompoment;

	public BmTableFieldsPage(ISelection selection, BmNewWizard bmWizard) {
		super("wizardPage");
		setTitle(LocaleMessage.getString("bussiness.model.editor.file"));
		setDescription(LocaleMessage.getString("bm.wizard.desc"));
		this.wizard = bmWizard;
	}

	/**
	 * @see IDialogPage#createControl(Composite)
	 */
	public void createControl(Composite parent) {
		dbMetaData = wizard.getDBMetaData();

		CompositeMap input = null;
		try {
			input = getInput(dbMetaData, "%");
		} catch (SQLException e) {
			CustomDialog.showExceptionMessageBox(e);
		}
		filterCompoment = new GridViewer(columnProperties, IGridViewer.isMulti
				| IGridViewer.isAllChecked);
		filterCompoment.setData(input);
		Composite container = filterCompoment.createViewer(parent);
		setControl(container);
		setPageComplete(true);
	}

	public String getContainerName() {
		return containerText.getText();
	}

	public CompositeMap getSelectedFields() {
		CompositeMap fieldsArray = new CompositeMap(BmNewWizard.bm_pre,
				BmNewWizard.bm_uri, "fields");
		Object[] elements = filterCompoment.getCheckedElements();
		for (int j = 0; j < elements.length; j++) {
			CompositeMap record = (CompositeMap) elements[j];
			CompositeMap field = new CompositeMap(BmNewWizard.bm_pre,
					BmNewWizard.bm_uri, "field");
			field.put("name", record.getString("COLUMN_NAME").toLowerCase());
			field.put("physicalName", record.getString("COLUMN_NAME"));
//			String required = record.getString("IS_NULLABLE").equals("YES") ? "false"
//					: "true";
//			field.put("required", required);
			String dataType = record.getString("TYPE_NAME");
			field.put("databaseType", dataType);
			Integer db_data_type = record.getInt("DATA_TYPE");
			DataTypeRegistry dtr = DataTypeRegistry.getInstance();
			DataType dt = dtr.getType(db_data_type.intValue());
			if (dt == null) {
				CustomDialog.showErrorMessageBox(null, LocaleMessage.getString("database.datatype")
						+ db_data_type + LocaleMessage.getString("is.not.registried"));
			} else
				field.put("datatype", dt.getJavaType().getName());

			fieldsArray.addChild(field);
		}
		return fieldsArray;
	}

	public void refresh() {
		CompositeMap input = null;
		try {
			input = getInput(dbMetaData, "%");
		} catch (SQLException e) {
			CustomDialog.showExceptionMessageBox(e);
		}
		filterCompoment.setData(input);
	}

	private CompositeMap getInput(DatabaseMetaData DBMetaData,
			String tableNamePattern) throws SQLException {
		CompositeMap input = new CompositeMap();
		ResultSet tableRet = DBMetaData.getColumns(null, DBMetaData
				.getUserName(), wizard.getTableName(), "%");
		while (tableRet.next()) {
			CompositeMap element = new CompositeMap();
			element.put(columnProperties[0], tableRet
					.getString(columnProperties[0]));
			element.put(columnProperties[1], tableRet
					.getString(columnProperties[1]));
			element.put(columnProperties[2], new Integer(tableRet
					.getInt(columnProperties[2])));
			element.put(columnProperties[3], tableRet
					.getString(columnProperties[3]));
			element.put(columnProperties[4], tableRet
					.getString(columnProperties[4]));
			element.put("DATA_TYPE", new Integer(tableRet.getInt("DATA_TYPE")));
			input.addChild(element);
		}
		return input;
	}
}