package uncertain.ide.wizards;

import java.io.File;

import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class ProjectWizardPage extends WizardPage{
	
	Text locationPathField;
	String customLocationFieldValue;
    Label locationLabel;
    Button browseButton;
    private String initialLocationFieldValue;
    private static String WZ_TITLE = "Aurora Make Project";
	private static String WZ_DESCRIPTION = "Create a New Aurora Project using 'make' to build it";
	public ProjectWizardPage() {
		super("Aurora Wizard Page");
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
        fileGroup.setText("Seclcet the file");
        
        locationLabel = new Label(fileGroup, SWT.NONE);
        locationLabel.setText("Path");


        // project location entry field
        locationPathField = new Text(fileGroup, SWT.BORDER);
        GridData data = new GridData(GridData.FILL_HORIZONTAL);
        //data.widthHint = SIZING_TEXT_FIELD_WIDTH;
        locationPathField.setLayoutData(data);
  
        browseButton = new Button(fileGroup, SWT.PUSH);
        browseButton.setText("Browse");
        browseButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
                handleLocationBrowseButton();
            }
        });
        
        setButtonLayoutData(browseButton);
        
        if (initialLocationFieldValue == null)
            locationPathField.setText(Platform.getLocation().toOSString());
        else
            locationPathField.setText(initialLocationFieldValue);
        
        setControl(composite);
        
   }
	void handleLocationBrowseButton() {
        FileDialog dialog = new FileDialog(locationPathField
                .getShell());

        String fileName = getFileocationFieldValue();
        if (!fileName.equals("")) { //$NON-NLS-1$
            File path = new File(fileName);
            if (path.exists())
                dialog.setFilterPath(new Path(fileName).toOSString());
        }

        String selectedFile = dialog.open();
        if (selectedFile != null) {
            customLocationFieldValue = selectedFile;
            locationPathField.setText(customLocationFieldValue);
        }
    }
	private String getFileocationFieldValue() {
        if (locationPathField == null)
            return ""; //$NON-NLS-1$

        return locationPathField.getText().trim();
    }

}
