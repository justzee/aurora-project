package uncertain.ide.eclipse.editor.widgets.config;

import uncertain.ide.eclipse.editor.bm.AuroraDataBase;
import uncertain.ide.eclipse.editor.widgets.CustomDialog;
import uncertain.ide.eclipse.editor.widgets.core.IUpdateMessageDialog;
import uncertain.ide.util.LocaleMessage;

public class WebDirPicker extends ProjectDirPicker {

	public WebDirPicker(IUpdateMessageDialog dialog) {
		super(dialog);
	}

	public WebDirPicker() {
		super();
	}

	protected void valid() {
		if (dirText.getText() == null) {
			updateStatus(LocaleMessage.getString("path.must.be.specified"));
			return;
		}
		try {
			String projectPath = getFullPathOfDir();
			AuroraDataBase.getDBConnection(projectPath);
			updateStatus(null);
			dirStr = dirText.getText();
		} catch (Exception e) {
			updateStatus(LocaleMessage.getString("this.path.is.not.valid"));
			CustomDialog.showExceptionMessageBox(e);
			return;
		}
	}
}
