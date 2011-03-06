package uncertain.ide.eclipse.bm.editor;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import uncertain.composite.CompositeMap;
import uncertain.ide.eclipse.editor.widgets.GridViewer;
import uncertain.ide.help.ApplicationException;
import uncertain.ide.help.CustomDialog;
import uncertain.ide.help.LocaleMessage;

public class GridDialog extends TitleAreaDialog {

	private GridViewer grid;
	public GridDialog(Shell shell,GridViewer grid) {
		super(shell);
		this.grid = grid;
	}
	protected Control createContents(Composite parent) {
		Control contents = super.createContents(parent);
		contents.setSize(800, 800);
		setTitle(LocaleMessage.getString("grid.dialog"));
		return contents;
	}

	/**
	 * Creates the gray area
	 * 
	 * @param parent
	 *            the parent composite
	 * @return Control
	 */
	protected Control createDialogArea(Composite parent) {
		Composite control = (Composite) super.createDialogArea(parent);
		try {
			grid.createViewer(control);
		} catch (ApplicationException e) {
			CustomDialog.showErrorMessageBox(e);
		}
		return control;
	}

	protected boolean isResizable() {
		return true;
	}

	public CompositeMap getSelected() {
		return grid.getSelection();
	}
}
