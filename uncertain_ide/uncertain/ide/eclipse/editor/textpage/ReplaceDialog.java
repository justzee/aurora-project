package uncertain.ide.eclipse.editor.textpage;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class ReplaceDialog extends Dialog {
  
  private Text findSText;
  private Text replaceSText;
  private Button check;
  private Button findNext;
  private Button replace;
  private Button replaceAll;
  private UncertainStyledText te;
  
  public ReplaceDialog(Shell parentShell, UncertainStyledText te) {
    super(parentShell);
    setShellStyle(SWT.CLOSE | SWT.MODELESS | SWT.BORDER | SWT.TITLE);
    this.te = te;
  }
  
  protected void configureShell(Shell shell) {
    super.configureShell(shell);
    shell.setText("替换");
  }
  
  protected Control createDialogArea(Composite parent) {
    Composite comp = (Composite)super.createDialogArea(parent);
    GridLayout layout = (GridLayout)comp.getLayout();
    layout.numColumns = 2;
    
    Composite leftTop = new Composite(comp, SWT.NULL);
    GridData data = new GridData(GridData.FILL_HORIZONTAL);
    leftTop.setLayoutData(data);
    RowLayout rowLayout = new RowLayout(SWT.HORIZONTAL);
    rowLayout.wrap = false;
    rowLayout.pack = true;
    leftTop.setLayout(rowLayout);
    
    Label label = new Label(leftTop, SWT.LEFT);
    label.setText("查找内容(&N):");
    
    findSText = new Text(leftTop, SWT.SINGLE | SWT.HORIZONTAL | SWT.BORDER);
    findSText.setLayoutData(new RowData(200, 13));
    findSText.addModifyListener(new ModifyListener() {
      public void modifyText(ModifyEvent event) {
        if (((Text)event.getSource()).getText() != null
            && ((Text)event.getSource()).getText().length() != 0) {
          findNext.setEnabled(true);
          replace.setEnabled(true);
          replaceAll.setEnabled(true);
        } else {
          findNext.setEnabled(false);
          replace.setEnabled(false);
          replaceAll.setEnabled(false);
        }
      }
    });
    
    Composite right = new Composite(comp, SWT.NULL);
    data = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
    data.verticalSpan = 3;
    right.setLayoutData(data);
    FillLayout fillLayout = new FillLayout(SWT.VERTICAL);
    fillLayout.spacing = 6;
    right.setLayout(fillLayout);
    
    findNext = new Button(right, SWT.NULL);
    findNext.setText("查找下一个(&F)");
    findNext.setEnabled(false);
    parent.getShell().setDefaultButton(findNext);
    findNext.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent event) {
        String content = te.getText();
        String findText = findSText.getText();
        te.setFindText(findText);
        te.setDown(true);
        te.setIgnoreCase(!check.getSelection());
        if (!check.getSelection()) {
          content = content.toLowerCase();
          findText = findText.toLowerCase();
        }
        Point position = te.getSelection();
        int i = content.indexOf(findText, position.y);
        if (i != -1) {
          te.setSelection(i, i + findText.length());
          int offset = te.getCaretOffset();
          int linenumber = te.getLineAtOffset(offset);
          te.setLineBackground(0, te.getLineCount(), te.getShell().getDisplay().getSystemColor(SWT.COLOR_WHITE));
          te.setLineBackground(linenumber, 1, te.getLineBackground());
//          te.getStatusLineManager().setMessage((linenumber + 1) + "  row    " + (offset - te.getOffsetAtLine(linenumber) + 1) + "  column");
          te.redraw();
        } else {
          MessageBox dialog = new MessageBox(getShell(), SWT.OK | SWT.ICON_INFORMATION);
          dialog.setText("记事本");
          dialog.setMessage("找不到\"" + findSText.getText() + "\"");
          dialog.open();
        }
      }
    });
    
    replace = new Button(right, SWT.NULL);
    replace.setEnabled(false);
    replace.setText("替换(&R)");
    replace.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent event) {
        Point position = te.getSelection();
        if (position.x == position.y)
          return;
        String replaceText = replaceSText.getText();
        te.insert(replaceText);
        int offset = te.getCaretOffset();
        int linenumber = te.getLineAtOffset(offset);
        te.setLineBackground(0, te.getLineCount(), te.getShell().getDisplay().getSystemColor(SWT.COLOR_WHITE));
        te.setLineBackground(linenumber, 1, te.getLineBackground());
//        te.getStatusLineManager().setMessage((linenumber + 1) + "  row    " + (offset - te.getOffsetAtLine(linenumber) + 1) + "  column");
        te.redraw();
      }
    });
    
    replaceAll = new Button(right, SWT.NULL);
    replaceAll.setText("全部替换(&A)");
    replaceAll.setEnabled(false);
    replaceAll.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent event) {
        String findText = findSText.getText();
        String replaceText = replaceSText.getText();
        String content = te.getText();
        if (check.getSelection()) {
          content = content.replaceAll(findText, replaceText);
        } else {
          Pattern pattern = Pattern.compile(findText, Pattern.CASE_INSENSITIVE);
          Matcher matcher = pattern.matcher(content);
          content = matcher.replaceAll(replaceText);
        }
        te.setText(content);
        te.setLineBackground(0, 1, te.getLineBackground());
//        te.getStatusLineManager().setMessage("1  row    1  column");
      }
    });
    
    Button cancel = new Button(right, SWT.NULL);
    cancel.setText("取消");
    cancel.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent event) {
        close();
      }
    });
    
    Composite leftCenter = new Composite(comp, SWT.NULL);
    data = new GridData(GridData.FILL_HORIZONTAL);
    leftCenter.setLayoutData(data);
    leftCenter.setLayout(rowLayout);
    
    label = new Label(leftCenter, SWT.LEFT);
    label.setText("替换为(&P):    ");
    
    replaceSText = new Text(leftCenter, SWT.SINGLE | SWT.HORIZONTAL | SWT.BORDER);
    replaceSText.setLayoutData(new RowData(200, 13));
    
    check = new Button(comp, SWT.CHECK);
    check.setText("区分大小写(&C)");
    
    return comp;
  }

  protected void createButtonsForButtonBar(Composite parent)
  {
  }
  
  protected Control createButtonBar(Composite parent) {
    return null;
  }
}
