/**
 * 
 */
package uncertain.ide.eclipse.editor.textpage;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

/**
 * @author linjinxiao
 * 
 */
public class UncertainStyledText extends StyledText {

	public static final int UNDO = 262234;// binding = SWT.MOD1 + 'Z';
	public static final int REDO = 262233;// binding = SWT.MOD1 + 'Y';
	public static final int RESERCH = 262214;// binding = SWT.MOD1 + 'F';
	public static final int GOTOLINE = 262215;// binding = SWT.MOD1 + 'G';
	public static final int REPLACE = 262226;// binding = SWT.MOD1 + 'R';

	UndoManager undoManager;

	private String findText;
	private boolean down;
	private boolean ignoreCase;
	private Color lineBackground;

	public UncertainStyledText(Composite parent, int style) {
		super(parent, style);
		undoManager = new UndoManager(50);
		undoManager.connect(this);
		setKeyBinding(SWT.MOD1 + 'Z', UNDO);
		setKeyBinding(SWT.MOD1 + 'Y', REDO);
		setKeyBinding(SWT.MOD1 + 'F', RESERCH);
		setKeyBinding(SWT.MOD1 + 'G', GOTOLINE);
		setKeyBinding(SWT.MOD1 + 'R', REPLACE);
	}

	/**
	 * @param args
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.swt.custom.StyledText#invokeAction(int)
	 */
	public void invokeAction(int action) {
		switch (action) {
		case UNDO:
			undo();
			break;
		case REDO:
			redo();
			break;
		case RESERCH:
			reserch();
			break;
		case GOTOLINE:
			gotoLine();
			break;
		case REPLACE:
			replace();
			break;	
		default:
			super.invokeAction(action);
		}
	}

	private void undo() {
		if (undoManager != null)
			undoManager.undo();
	}

	private void redo() {
		if (undoManager != null)
			undoManager.redo();
	}

	private void reserch() {
		Shell shell = new Shell();
		FindDialog findDialog = new FindDialog(shell, this);
		findDialog.open();
	}

	private void gotoLine() {
		Shell shell = new Shell();
		TurningDialog turningDialog = new TurningDialog(shell, this);
		turningDialog.open();
	}
	private void replace(){
		Shell shell = new Shell();
        ReplaceDialog replaceDialog = new ReplaceDialog(shell,this);
        replaceDialog.open();
	}

	public void setFindText(String findText) {
		this.findText = findText;
	}

	public void setDown(boolean down) {
		this.down = down;
	}

	public void setIgnoreCase(boolean ignoreCase) {
		this.ignoreCase = ignoreCase;
	}

	public String getFindText() {
		return findText;
	}

	public boolean isIgnoreCase() {
		return ignoreCase;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	public boolean isDown() {
		return down;
	}

	public Color getLineBackground() {
		return lineBackground;
	}

//	public StatusLineManager getStatusLineManager() {
//		return super.getStatusLineManager();
//	}
}
