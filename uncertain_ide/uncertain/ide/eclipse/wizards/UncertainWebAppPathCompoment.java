package uncertain.ide.eclipse.wizards;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
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
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;

import uncertain.ide.LocaleMessage;
import uncertain.ide.eclipse.editor.bm.UncertainDataBase;
import uncertain.ide.eclipse.editor.widgets.CustomDialog;

public class UncertainWebAppPathCompoment {
	
	private UpdateMessageDialog dialog;
	private Text uncertainProDirText;
	private Button uncertainProDirButton;
	private String proDirPath;
	private boolean fromLocal = false;
	public UncertainWebAppPathCompoment(UpdateMessageDialog dialog){
		this.dialog = dialog;
	}

	public Composite createControl(Composite parent) {
		
		Composite composite =  new Composite(parent, SWT.NULL);
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		composite.setLayoutData(gridData);
		GridLayout gl = new GridLayout();
		gl.numColumns = 1;
		composite.setLayout(gl);
		
        Group fileGroup = new Group(composite, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        fileGroup.setLayout(layout);
        fileGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        fileGroup.setText("Uncertain Web Application Home Path");


        // project location entry field
        uncertainProDirText = new Text(fileGroup, SWT.BORDER);
        GridData data = new GridData(GridData.FILL_HORIZONTAL);
        uncertainProDirText.setLayoutData(data);
        uncertainProDirText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				dialogChanged();
			}
		});
        uncertainProDirText.setEditable(false);
        
        uncertainProDirButton = new Button(fileGroup, SWT.PUSH);
        uncertainProDirButton.setData(data);
        uncertainProDirButton.setText("From Exist Projects");
        uncertainProDirButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
            	handleBrowse();
            }
        });
        
//        uncertainProDirButton = new Button(fileGroup, SWT.PUSH);
//        uncertainProDirButton.setData(data);
//        uncertainProDirButton.setText("From Local Path");
//        uncertainProDirButton.addSelectionListener(new SelectionAdapter() {
//            public void widgetSelected(SelectionEvent event) {
//                handleLocationBrowseButton();
//            }
//        });
        
        return composite;
       
   }
	void handleLocationBrowseButton() {
		DirectoryDialog dialog = new DirectoryDialog (new Shell(), SWT.NONE);
		dialog.setMessage (LocaleMessage.getString("Please select Uncertain Project Home"));
		dialog.setText (LocaleMessage.getString("Dialog"));
		String result = dialog.open ();
        if (result != null) {
            uncertainProDirText.setText(result);
        }
    }
	private void handleBrowse() {
		fromLocal = false;
		ContainerSelectionDialog dialog = new ContainerSelectionDialog(
				new Shell(), ResourcesPlugin.getWorkspace().getRoot(), false,
				"Select Uncertain Project Home");
		if (dialog.open() == ContainerSelectionDialog.OK) {
			Object[] result = dialog.getResult();
			if (result.length == 1) {
				uncertainProDirText.setText(((Path) result[0]).toString());
			}
		}
	}
	private void dialogChanged(){
		if(uncertainProDirText.getText() == null){
			updateStatus("Uncertain Project directory must be specified");
			return;
		}
		if(uncertainProDirText.getText() != null){
			try {
				String projectPath = getFullPathOfUncertainProDir();
				UncertainDataBase.getDBConnection(projectPath);
				updateStatus(null);
				proDirPath = uncertainProDirText.getText();
			} catch (Exception e) {
				updateStatus("This path is not valid!");
				CustomDialog.showExceptionMessageBox(e);
				return;
			}
		}
	}
	private void updateStatus(String message){
		dialog.updateStatus(message);
	}
	public String getUncertainProDir(){
		return proDirPath;
	}
	private String getFullPathOfUncertainProDir(){
		if(uncertainProDirText.getText()==null ||uncertainProDirText.getText().equals(""))
			return null;
		if(fromLocal)
			return uncertainProDirText.getText();
		return ResourcesPlugin.getWorkspace().getRoot().getLocation().toOSString()+uncertainProDirText.getText();
	}
}
