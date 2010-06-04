package uncertain.ide.eclipse.editor.textpage;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.MessageBox;

public class FindSelectionListener extends SelectionAdapter {
  
  private UncertainStyledText te;
  
  public FindSelectionListener(UncertainStyledText te) {
    this.te = te;
  }
  
  public void widgetSelected(SelectionEvent event) {
    String content = te.getText();
    String findText = te.getFindText();
    if(findText == null) 
      return;
    if (te.isIgnoreCase()) {
      content = content.toLowerCase();
      findText = findText.toLowerCase();
    }
    Point position = te.getSelection();
    if (te.isDown()) {
      int i = content.indexOf(findText, position.y);
      if (i != -1) {
        te.setSelection(i, i + findText.length());
        int offset = te.getCaretOffset();
        int linenumber = te.getLineAtOffset(offset);
        te.setLineBackground(0, te.getLineCount(), te.getShell().getDisplay().getSystemColor(SWT.COLOR_WHITE));
        te.setLineBackground(linenumber, 1, te.getLineBackground());
//        te.getStatusLineManager().setMessage((linenumber + 1) + "  row    " + (offset - te.getOffsetAtLine(linenumber) + 1) + "  column");
        te.redraw();
      } else {
        MessageBox dialog = new MessageBox(te.getShell(),SWT.OK | SWT.ICON_INFORMATION);
        dialog.setText("记事本");
        dialog.setMessage("找不到\"" + te.getFindText() + "\"");
        dialog.open();
      }
    } else {
      content = content.substring(0, position.x);
      int i = content.lastIndexOf(findText);
      if (i != -1) {
        te.setSelection(i, i + findText.length());
        int offset = te.getCaretOffset();
        int linenumber = te.getLineAtOffset(offset);
        te.setLineBackground(0, te.getLineCount(), te.getShell().getDisplay().getSystemColor(SWT.COLOR_WHITE));
        te.setLineBackground(linenumber, 1, te.getLineBackground());
//        te.getStatusLineManager().setMessage((linenumber + 1) + "  row    " + (offset - te.getOffsetAtLine(linenumber) + 1) + "  column");
        te.redraw();
      } else {
        MessageBox dialog = new MessageBox(te.getShell(),SWT.OK | SWT.ICON_INFORMATION);
        dialog.setText("记事本");
        dialog.setMessage("找不到\"" + te.getFindText() + "\"");
        dialog.open();
      }
    }
  }

}
