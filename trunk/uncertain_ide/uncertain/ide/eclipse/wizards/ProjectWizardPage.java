package uncertain.ide.eclipse.wizards;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import uncertain.ide.Common;

public class ProjectWizardPage extends WizardPage{
	
	private Text uncertainProDirText;
	private Label uncertainProDirLabel;
	private Button uncertainProDirButton;
    private static String WZ_TITLE = "uncertain Project";
	private static String WZ_DESCRIPTION = "Create a New uncertain Project ";
	public ProjectWizardPage() {
		super("uncertain Wizard Page");
		setTitle(WZ_TITLE);
		setDescription(WZ_DESCRIPTION);
	}
	public void createControl(Composite parent) {
		
		Composite composite =  new Composite(parent, SWT.NULL);
		GridLayout gl = new GridLayout();
		gl.numColumns = 1;
		composite.setLayout(gl);
		
        Group fileGroup = new Group(composite, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.numColumns = 3;
        fileGroup.setLayout(layout);
        fileGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        fileGroup.setText("uncertain project");
        
        uncertainProDirLabel = new Label(fileGroup, SWT.NONE);
        uncertainProDirLabel.setText("Path");


        // project location entry field
        uncertainProDirText = new Text(fileGroup, SWT.BORDER);
        GridData data = new GridData(GridData.FILL_HORIZONTAL);
        //data.widthHint = SIZING_TEXT_FIELD_WIDTH;
        uncertainProDirText.setLayoutData(data);
        uncertainProDirText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				dialogChanged();
			}
		});
        
        uncertainProDirButton = new Button(fileGroup, SWT.PUSH);
        uncertainProDirButton.setText("Browse");
        uncertainProDirButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
                handleLocationBrowseButton();
            }
        });
        
        setButtonLayoutData(uncertainProDirButton);
        
        setControl(composite);
        setPageComplete(true);
        
   }
	void handleLocationBrowseButton() {
		DirectoryDialog dialog = new DirectoryDialog (getShell(), SWT.NONE);
		dialog.setMessage (Common.getString("Example_string"));
		dialog.setText (Common.getString("Title"));
		String result = dialog.open ();
        if (result != null) {
            uncertainProDirText.setText(result);
        }
    }
	private void dialogChanged(){
		if(uncertainProDirText.getText() == null){
			updateStatus("uncertain project directory must be specified");
			return;
		}
		if(uncertainProDirText.getText() != null){
			try {
				Common.getDBConnection(uncertainProDirText.getText());
			} catch (Exception e) {
				Common.showExceptionMessageBox(null, e);
				updateStatus("The path is not valid!");
				return;
			}
		}
	}
	private void updateStatus(String message) {
		setErrorMessage(message);
		setPageComplete(message == null);
	}
	public String getUncertainProDir(){
		return uncertainProDirText.getText();
	}
}
