package uncertain.ide.eclipse.wizards;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Composite;

import uncertain.ide.LocaleMessage;

public class ProjectWizardPage extends WizardPage implements UpdateMessageDialog{

	private static String WZ_TITLE = LocaleMessage.getString("aurora.project");
	private static String WZ_DESCRIPTION = LocaleMessage.getString("create.a.new.aurora.project");
	private ProjectDirPicker dirPicker;
	public ProjectWizardPage() {
		super("aurora.wizard.Page");
		setTitle(WZ_TITLE);
		setDescription(WZ_DESCRIPTION);
	}
	public void createControl(Composite parent) {
		dirPicker = new WebDirPicker(this);	
		Composite composite =  dirPicker.createControl(parent);
        setControl(composite);
       
   }
	public void updateStatus(String message) {
		setErrorMessage(message);
		setPageComplete(message == null);
	}
	public String getUncertainProDir(){
		return dirPicker.getDirPath();
	}
}
