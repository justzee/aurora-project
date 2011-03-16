package uncertain.ide.eclipse.bm.wizard.db;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.eclipse.jface.dialogs.IDialogPage;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import aurora.ide.AuroraConstant;

import uncertain.composite.CompositeMap;
import uncertain.ide.eclipse.bm.BMUtil;
import uncertain.ide.eclipse.celleditor.CellInfo;
import uncertain.ide.eclipse.celleditor.ICellEditor;
import uncertain.ide.eclipse.celleditor.StringTextCellEditor;
import uncertain.ide.eclipse.editor.widgets.GridViewer;
import uncertain.ide.eclipse.editor.widgets.core.IGridViewer;
import uncertain.ide.help.ApplicationException;
import uncertain.ide.help.CustomDialog;
import uncertain.ide.help.LocaleMessage;

/**
 * The "New" wizard page allows setting the container for the new file as well
 * as the file name. The page will only accept file name without the extension
 * OR with the extension that matches the expected one (bm).
 */

public class BMTablePage extends WizardPage {
	private Text containerText;
	private BMFromDBWizard wizard;
	private String tableName;
	private String tableRemarks;
	private final String[] columnNames = { "TABLE_NAME", "REMARKS", "TABLE_TYPE","TABLE_SCHEM","TABLE_CAT"};
	private final int REMARKS_INDEX=1;
	private final String[] columnTitles = { "表名", "描述", "类型","模式" , "编目"};
	DatabaseMetaData dbMetaData;
	GridViewer gridViewer;

	public BMTablePage(ISelection selection, BMFromDBWizard bmWizard) {
		super("wizardPage");
		setTitle(LocaleMessage.getString("bussiness.model.editor.file"));
		setDescription(LocaleMessage.getString("bm.wizard.desc"));
		this.wizard = bmWizard;
	}

	/**
	 * @see IDialogPage#createControl(Composite)
	 */
	public void createControl(Composite parent) {
		CompositeMap input = null;

		try {
			Connection dbConnection = wizard.getConnection();
			dbMetaData = dbConnection.getMetaData();
			input = getInput(dbMetaData, "%");
		} catch (Exception e) {
			CustomDialog.showExceptionMessageBox(e);
		}
		gridViewer = new GridViewer(columnNames,
				IGridViewer.filterBar | IGridViewer.NoToolBar|IGridViewer.isOnlyUpdate);
		try {
			gridViewer.setFilterColumn("TABLE_NAME");
			gridViewer.setColumnTitles(columnTitles);
			gridViewer.createViewer(parent);
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
			return;
		}
		gridViewer
				.addSelectionChangedListener(new ISelectionChangedListener() {

					public void selectionChanged(SelectionChangedEvent event) {
						if (getTableName() == null)
							setPageComplete(false);
						else
							setPageComplete(true);
						try {
							wizard.refresh();
						} catch (ApplicationException e) {
							CustomDialog.showErrorMessageBox(e);
							return;
						}

					}
				});
		setControl(gridViewer.getControl());
		setPageComplete(false);

	}

	public String getContainerName() {
		return containerText.getText();
	}

	public DatabaseMetaData getDBMetaData() {
		return dbMetaData;
	}

	public CompositeMap getPrimaryKeys() throws SQLException {
		CompositeMap primaryKeyArray = new CompositeMap(BMUtil.BMPrefix,AuroraConstant.BMUri, "primary-key");
		String tableName = getTableName();
		if (tableName == null)
			return primaryKeyArray;
		ResultSet tableRet = dbMetaData.getPrimaryKeys(null, dbMetaData
				.getUserName(), tableName);
		while (tableRet.next()) {
			CompositeMap field = new CompositeMap(BMUtil.BMPrefix,AuroraConstant.BMUri, "pk-field");
			field.put("name", tableRet.getString("COLUMN_NAME").toLowerCase());
			primaryKeyArray.addChild(field);
		}
		return primaryKeyArray;
	}

	public String getTableName() {
		CompositeMap record = gridViewer.getSelection();
		if (record == null) {
			return null;
		}
		tableName = record.getString("TABLE_NAME");
		return tableName;
	}
	public String getTableRemarks() {
		CompositeMap record = gridViewer.getSelection();
		if (record == null) {
			return null;
		}
		tableRemarks = record.getString("REMARKS");
		return tableRemarks;
	}

	private CompositeMap getInput(DatabaseMetaData DBMetaData,
			String tableNamePattern) throws SQLException {
		CompositeMap input = new CompositeMap();
		ResultSet tableRet = DBMetaData.getTables(null, DBMetaData
				.getUserName(), tableNamePattern, new String[] { "TABLE",
				"VIEW" });
		while (tableRet.next()) {
			int seq = 0;
			CompositeMap element = new CompositeMap();
			element.put(columnNames[seq], tableRet
					.getString(columnNames[seq++]));
			element.put(columnNames[seq], tableRet
					.getString(columnNames[seq++]));
			element.put(columnNames[seq], tableRet
					.getString(columnNames[seq++]));
			element.put(columnNames[seq], tableRet
					.getString(columnNames[seq++]));
			element.put(columnNames[seq], tableRet
					.getString(columnNames[seq++]));
			input.addChild(element);
		}
		return input;
	}
}