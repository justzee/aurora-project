package uncertain.ide.eclipse.editor.textpage;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class FindDialog extends Dialog {
  
  private UncertainStyledText te;
  private Button findNext;
  private Text text;
  private Button[] radios;
  private Button check;
  
  public FindDialog(Shell parentShell, UncertainStyledText te) {
    super(parentShell);
//    setShellStyle(SWT.CLOSE | SWT.MODELESS | SWT.BORDER | SWT.TITLE);
    this.te = te;
  }
  
  protected void configureShell(Shell shell) {
    super.configureShell(shell);
    shell.setText("查找");
  }

  
  protected Control createDialogArea(Composite parent) {
    Composite comp = (Composite)super.createDialogArea(parent);
    GridLayout layout = (GridLayout)comp.getLayout();
    layout.numColumns = 2;
    
    Composite leftTop = new Composite(comp, SWT.NULL);
    GridData data = new GridData(GridData.FILL_BOTH);
    leftTop.setLayoutData(data);
    leftTop.setLayout(new RowLayout(SWT.HORIZONTAL));
    
    Label label = new Label(leftTop, SWT.LEFT);
    label.setText("查找内容(&N):");
    
    text = new Text(leftTop, SWT.SINGLE | SWT.HORIZONTAL | SWT.BORDER);
    text.setLayoutData(new RowData(180, 13));
    text.addModifyListener(new ModifyListener() {
      public void modifyText(ModifyEvent event) {
        if (((Text)event.getSource()).getText() != null
            && ((Text)event.getSource()).getText().length() != 0)
          findNext.setEnabled(true);
        else
          findNext.setEnabled(false);
      }
    });
    
    Composite right = new Composite(comp, SWT.NULL);
    data = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
    data.verticalSpan = 2;
    right.setLayoutData(data);
    FillLayout fillLayout = new FillLayout(SWT.VERTICAL);
    fillLayout.spacing = 6;
    right.setLayout(fillLayout);
    findNext = new Button(right, SWT.NULL);
    findNext.setText("查找下一个");
    findNext.setEnabled(false);
    parent.getShell().setDefaultButton(findNext);
    findNext.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent event) {
        String content = te.getText();
        String findText = text.getText();
        te.setFindText(findText);
        te.setDown(radios[1].getSelection());
        te.setIgnoreCase(!check.getSelection());
        if (!check.getSelection()) {
          content = content.toLowerCase();
          findText = findText.toLowerCase();
        }
        Point position = te.getSelection();
        if (radios[1].getSelection()) {
          int i = content.indexOf(findText, position.y);
          if (i != -1) {
            te.setSelection(i, i + findText.length());
          } else {
            MessageBox dialog = new MessageBox(getShell(), SWT.OK | SWT.ICON_INFORMATION);
            dialog.setText("记事本");
            dialog.setMessage("找不到\"" + text.getText() + "\"");
            dialog.open();
          }
        } else {
          content = content.substring(0, position.x);
          int i = content.lastIndexOf(findText);
          if (i != -1) {
            te.setSelection(i, i + findText.length());
          } else {
            MessageBox dialog = new MessageBox(getShell(), SWT.OK | SWT.ICON_INFORMATION);
            dialog.setText("记事本");
            dialog.setMessage("找不到\"" + text.getText() + "\"");
            dialog.open();
          }
        }
      }
    });
    
    Button cancel = new Button(right, SWT.NULL);
    cancel.setText("取消");
    cancel.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent event) {
        close();
      }
    });
    
    Composite leftBottom = new Composite(comp, SWT.NULL);
    data = new GridData(GridData.FILL_BOTH);
    data.widthHint = 220;
    leftBottom.setLayoutData(data);
    RowLayout rowLayout = new RowLayout(SWT.HORIZONTAL);
    rowLayout.wrap = false;
    rowLayout.spacing = 10;
    leftBottom.setLayout(rowLayout);
    check = new Button(leftBottom, SWT.CHECK);
    check.setText("区分大小写");
    check.pack();
    
    Group group = new Group(leftBottom, SWT.SHADOW_ETCHED_IN);
    group.setText("方向");
    group.setLayoutData(new RowData(160, 25));
    group.setLayout(new FillLayout(SWT.HORIZONTAL));
    radios = new Button[2];
    radios[0] = new Button(group, SWT.RADIO);
    radios[0].setText("向上(&U)");
    radios[0].pack();
    radios[1] = new Button(group, SWT.RADIO);
    radios[1].setSelection(true);
    radios[1].setText("向下(&D)");
    radios[1].pack();
    
    group.pack();
    return comp;
  }
  
  protected void createButtonsForButtonBar(Composite parent)
  {
  }
  
  protected Control createButtonBar(Composite parent) {
    return null;
  }

}
