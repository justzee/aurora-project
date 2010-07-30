package uncertain.ide.eclipse.wizards;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Composite;

public class UncertainProjectWizardPage extends WizardPage implements UpdateMessageDialog{

	private static String WZ_TITLE = "Uncertain Project";
	private static String WZ_DESCRIPTION = "Create a New Uncertain Project ";
	private UncertainWebAppPathCompoment webPath;
	public UncertainProjectWizardPage() {
		super("Uncertain Wizard Page");
		setTitle(WZ_TITLE);
		setDescription(WZ_DESCRIPTION);
	}
	public void createControl(Composite parent) {
		webPath = new UncertainWebAppPathCompoment(this);	
		Composite composite =  webPath.createControl(parent);
        setControl(composite);
       
   }
	public void updateStatus(String message) {
		setErrorMessage(message);
		setPageComplete(message == null);
	}
	public String getUncertainProDir(){
		return webPath.getUncertainProDir();
	}
}
