package uncertain.ide.eclipse.bm.wizard.db;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.IDialogPage;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import uncertain.composite.CompositeMap;
import uncertain.datatype.DataType;
import uncertain.datatype.DataTypeRegistry;
import uncertain.ide.eclipse.bm.BMUtil;
import uncertain.ide.eclipse.celleditor.CellInfo;
import uncertain.ide.eclipse.celleditor.ICellEditor;
import uncertain.ide.eclipse.celleditor.StringTextCellEditor;
import uncertain.ide.eclipse.editor.widgets.GridViewer;
import uncertain.ide.eclipse.editor.widgets.core.IGridViewer;
import uncertain.ide.help.ApplicationException;
import uncertain.ide.help.CustomDialog;
import uncertain.ide.help.LocaleMessage;
import aurora.ide.AuroraConstant;

/**
 * The "New" wizard page allows setting the container for the new file as well
 * as the file name. The page will only accept file name without the extension
 * OR with the extension that matches the expected one (bm).
 */

public class BMFieldsPage extends WizardPage {
	private Text containerText;
	private BMFromDBWizard wizard;
	private final String[] columnNames = { "COLUMN_NAME", "REMARKS", "IS_NULLABLE", "TYPE_NAME","COLUMN_SIZE" };
	private final String[] columnTitles = { "列名", "描述","可空","类型","大小",};
	private final int REMARKS_INDEX=1;
	private final String[] excluedColumns = { "CREATED_BY", "CREATION_DATE","LAST_UPDATED_BY", "LAST_UPDATE_DATE" };
	private DatabaseMetaData dbMetaData;
	private GridViewer gridViewer;
	public BMFieldsPage(ISelection selection, BMFromDBWizard bmWizard) {
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
			CustomDialog.showErrorMessageBox(e);
			return;
		}
		gridViewer = new GridViewer(columnNames, IGridViewer.isMulti
				| IGridViewer.isAllChecked|IGridViewer.isOnlyUpdate);
		Composite container = null;
		try {
			gridViewer.setColumnTitles(columnTitles);
			container = gridViewer.createViewer(parent);
			CellEditor[] celleditors = new CellEditor[columnNames.length];
			CellInfo cellProperties = new CellInfo(gridViewer, "REMARKS", false);
			ICellEditor cellEditor = new StringTextCellEditor(cellProperties);
			celleditors[REMARKS_INDEX] = cellEditor.getCellEditor();
			cellEditor.init();
			gridViewer.addEditor("REMARKS", cellEditor);
			gridViewer.setCellEditors(celleditors);
			gridViewer.setData(input);
		} catch (ApplicationException e) {
			CustomDialog.showErrorMessageBox(e);
		}
		setControl(container);
		setPageComplete(true);
	}

	public String getContainerName() {
		return containerText.getText();
	}

	public CompositeMap getSelectedFields() throws ApplicationException {
		CompositeMap fieldsArray = new CompositeMap(BMUtil.BMPrefix,
				AuroraConstant.BMUri, "fields");
		Object[] elements = gridViewer.getCheckedElements();
		for (int j = 0; j < elements.length; j++) {
			CompositeMap record = (CompositeMap) elements[j];
			CompositeMap field = new CompositeMap(BMUtil.BMPrefix,
					AuroraConstant.BMUri, "field");
			String fieldName = record.getString("COLUMN_NAME").toLowerCase();
			field.put("name", fieldName);
			field.put("physicalName", record.getString("COLUMN_NAME"));
			String dataType = record.getString("TYPE_NAME");
			field.put("databaseType", dataType);
			Integer db_data_type = record.getInt("DATA_TYPE");
			DataTypeRegistry dtr = DataTypeRegistry.getInstance();
			DataType dt = dtr.getType(db_data_type.intValue());
			if (dt == null) {
				CustomDialog.showErrorMessageBox(null, LocaleMessage
						.getString("database.datatype")
						+ db_data_type
						+ LocaleMessage.getString("is.not.registried"));
			} else
				field.put("datatype", dt.getJavaType().getName());
			String prompt = record.getString("REMARKS");
			String prompt_code = wizard.getTableName().toUpperCase()+"."+fieldName.toUpperCase();
			field.put("prompt", prompt_code);
			if(prompt != null){
				wizard.addPrompt(prompt_code, prompt);
			}
			fieldsArray.addChild(field);
		}
		return fieldsArray;
	}

	public void refresh() throws ApplicationException {
		CompositeMap input = null;
		try {
			input = getInput(dbMetaData, "%");
		} catch (SQLException e) {
			CustomDialog.showExceptionMessageBox(e);
		}
		gridViewer.setData(input);
		gridViewer.refresh(false);
	}

	private CompositeMap getInput(DatabaseMetaData DBMetaData,
			String tableNamePattern) throws SQLException {
		List excluedColumnList = new ArrayList();
		for (int i = 0; i < excluedColumns.length; i++) {
			String column = excluedColumns[i];
			if (column != null) {
				excluedColumnList.add(column);
			}
		}
		CompositeMap input = new CompositeMap();
		String tableName = wizard.getTableName();
		if (tableName == null) {
			setPageComplete(false);
			return input;
		}
		setPageComplete(true);
		ResultSet tableRet = DBMetaData.getColumns(null, DBMetaData
				.getUserName(), tableName, "%");
		while (tableRet.next()) {
			CompositeMap element = new CompositeMap();
			String columnName = tableRet.getString(columnNames[0]);
			if (excluedColumnList.contains(columnName)) {
				continue;
			}
			element.put(columnNames[0], columnName);
			element.put(columnNames[1], tableRet
					.getString(columnNames[1]));
			element.put(columnNames[2], tableRet
					.getString(columnNames[2]));
			element.put(columnNames[3], tableRet
					.getString(columnNames[3]));
			element.put(columnNames[4], new Integer(tableRet
					.getInt(columnNames[4])));
			element.put("DATA_TYPE", new Integer(tableRet.getInt("DATA_TYPE")));
			input.addChild(element);
		}
		return input;
	}
}