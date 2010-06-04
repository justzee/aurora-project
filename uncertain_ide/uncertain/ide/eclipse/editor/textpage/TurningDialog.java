package uncertain.ide.eclipse.editor.textpage;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

public class TurningDialog extends Dialog {
  
  private UncertainStyledText te;
  private StyledText lineText;
  
  public TurningDialog(Shell parentShell, UncertainStyledText te) {
    super(parentShell);
    setShellStyle(SWT.CLOSE | SWT.MODELESS | SWT.BORDER | SWT.TITLE);
    this.te = te;
  }
  
  protected Control createDialogArea(Composite parent) {
    Composite comp = (Composite)super.createDialogArea(parent);
    GridLayout layout = (GridLayout)comp.getLayout();
    layout.numColumns = 2;
    layout.verticalSpacing = 4;
    
    lineText = new StyledText(comp, SWT.SINGLE | SWT.HORIZONTAL | SWT.BORDER);
    GridData data = new GridData(GridData.FILL_HORIZONTAL);
    data.verticalSpan = 2;
    data.verticalAlignment = SWT.BEGINNING;
    lineText.setLayoutData(data);
    
    Button ok = new Button(comp, SWT.PUSH);
    ok.setText("     确定     ");
    parent.getShell().setDefaultButton(ok);
    ok.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent event) {
        String linenumber = lineText.getText();
        if (Integer.parseInt(linenumber) > te.getLineCount()) {
          MessageBox dialog = new MessageBox(getShell(), SWT.OK);
          dialog.setText("记事本-跳行");
          dialog.setMessage("行数超过范围");
          dialog.open();
          return;
        }
        StyledText text = te;
        int offset = text.getOffsetAtLine(Integer.parseInt(linenumber) - 1);
        text.setSelection(offset);
        text.setFocus();
      }
    });
    
    Button cancel = new Button(comp, SWT.PUSH);
    cancel.setText("     取消     ");
    cancel.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent event) {
        close();
      }
    });
    
    return comp;
  }
  
  protected void createButtonsForButtonBar(Composite parent)
  {
  }
  
  protected Control createButtonBar(Composite parent) {
    return null;
  }

}
