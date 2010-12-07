package uncertain.ide.eclipse.editor.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import uncertain.ide.LocaleMessage;

public class CustomDialog {

	public static void showMessageBox(int style, String title, String message) {
		message = getLocalMessage(message);
		Shell shell = Display.getCurrent().getActiveShell();
		MessageBox messageBox = new MessageBox(shell, style);
		messageBox.setText(title);
		messageBox.setMessage(message);
		messageBox.open();
	}

	public static void showWarningMessageBox(String message) {
		showWarningMessageBox(null,message);
	}
	
	public static void showWarningMessageBox(String title, String message) {
		message = getLocalMessage(message);
		Shell shell = Display.getCurrent().getActiveShell();
		MessageBox messageBox = new MessageBox(shell, SWT.ICON_WARNING | SWT.OK
				| SWT.APPLICATION_MODAL);
		if (title == null)
			title = getLocalMessage("messagebox.warning");
		message = LocaleMessage.getString(message);
		messageBox.setText(title);
		messageBox.setMessage(message);
		messageBox.open();
	}

	public static void showErrorMessageBox(String message) {
		showErrorMessageBox(null,message);
	}
	public static void showErrorMessageBox(Throwable e) {
		showErrorMessageBox(getExceptionMessage(e));
	}
	public static void showErrorMessageBox(String title, String message) {
		message = getLocalMessage(message);
		Shell shell = Display.getCurrent().getActiveShell();
		MessageBox messageBox = new MessageBox(shell, SWT.ICON_ERROR | SWT.OK
				| SWT.APPLICATION_MODAL);
		if (title == null)
			title = getLocalMessage("messagebox.error");
		messageBox.setText(title);
		messageBox.setMessage(message);
		messageBox.open();
	}

	public static void showExceptionMessageBox(String title, Throwable e) {
		Shell shell = Display.getCurrent().getActiveShell();
		MessageBox messageBox = new MessageBox(shell, SWT.ICON_ERROR | SWT.OK
				| SWT.APPLICATION_MODAL);
		if (title == null)
			title = getLocalMessage("messagebox.error");
		messageBox.setText(title);
		String message = CustomDialog.getExceptionMessage(e);
		message = message == null?"":message;
		message = message+System.getProperty("line.separator")+LocaleMessage.getString("more.detail.in.logViewer");
		messageBox.setMessage(message);
		messageBox.open();
		try {
			 PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView("org.eclipse.pde.runtime.LogView");
		} catch (PartInitException e1) {
			e1.printStackTrace();
		}
		throw new RuntimeException(e);
	}

	public static void showExceptionMessageBox(Throwable e) {
		showExceptionMessageBox(null,e);
	}

	public static String getExceptionMessage(Throwable e) {
		String message = null;
		if (e.getCause() != null) {
			message = e.getCause().getLocalizedMessage();

		} else if (e.getLocalizedMessage() != null) {
			message = e.getLocalizedMessage();
		}
		return message;
	}
	public static int showConfirmDialogBox(String message){
		return showConfirmDialogBox(null, message);
	}
	
	public static int showConfirmDialogBox(String title, String message) {
		message = getLocalMessage(message);
		Shell shell = Display.getCurrent().getActiveShell();
		MessageBox messageBox = new MessageBox(shell, SWT.ICON_QUESTION
				| SWT.OK | SWT.CANCEL | SWT.APPLICATION_MODAL);
		if (title == null)
			title = getLocalMessage("messagebox.question");
		messageBox.setText(title);
		messageBox.setMessage(message);
		int buttonID = messageBox.open();
		return buttonID;
	}
	private static String getLocalMessage( String message){
		return LocaleMessage.getString(message);
	}

}
