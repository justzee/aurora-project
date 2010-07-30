package uncertain.ide.eclipse.editor.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

import uncertain.ide.LocaleMessage;

public class CustomDialog {

	public static void showMessageBox(int style, String title, String message) {
		Shell shell = new Shell();
		MessageBox messageBox = new MessageBox(shell, style);
		messageBox.setText(title);
		messageBox.setMessage(message);
		messageBox.open();
		// MessageDialog.
	}

	public static void showWarningMessageBox(String title, String message) {
		Shell shell = new Shell();
		MessageBox messageBox = new MessageBox(shell, SWT.ICON_WARNING | SWT.OK
				| SWT.APPLICATION_MODAL);
		if (title == null)
			title = LocaleMessage.getString("messagebox.warning");
		messageBox.setText(title);
		messageBox.setMessage(message);
		messageBox.open();
	}

	public static void showErrorMessageBox(String title, String message) {
		Shell shell = new Shell();
		MessageBox messageBox = new MessageBox(shell, SWT.ICON_ERROR | SWT.OK
				| SWT.APPLICATION_MODAL);
		if (title == null)
			title = LocaleMessage.getString("messagebox.error");
		messageBox.setText(title);
		messageBox.setMessage(message);
		messageBox.open();
	}

	public static void showExceptionMessageBox(String title, Exception e) {
		Shell shell = new Shell();
		MessageBox messageBox = new MessageBox(shell, SWT.ICON_ERROR | SWT.OK
				| SWT.APPLICATION_MODAL);
		if (title == null)
			title = LocaleMessage.getString("messagebox.error");
		messageBox.setText(title);
		String message = CustomDialog.getExceptionMessage(e);
		if (message != null) {
			messageBox.setMessage(message);
			messageBox.open();
		}
		throw new RuntimeException(e);
	}

	public static void showExceptionMessageBox(Exception e) {
		Shell shell = new Shell();
		MessageBox messageBox = new MessageBox(shell, SWT.ICON_ERROR | SWT.OK
				| SWT.APPLICATION_MODAL);
		messageBox.setText(LocaleMessage.getString("messagebox.error"));
		String message = getExceptionMessage(e);
		if (message != null) {
			messageBox.setMessage(message);
			messageBox.open();
		}
		throw new RuntimeException(e);
		// System.out.println("showExceptionMessageBox...");
		// Status status = new Status(IStatus.ERROR, "uncertain ide", 0,
		// "More message in error log view.", e);
		// ErrorDialog.openError(null, null, null, status);
		// throw new RuntimeException(e);
	}

	public static String getExceptionMessage(Exception e) {
		String message = null;
		if (e.getCause() != null) {
			message = e.getCause().getLocalizedMessage();

		} else if (e.getLocalizedMessage() != null) {
			message = e.getLocalizedMessage();
		}
		return message;
	}

	public static int showConfirmDialogBox(String title, String message) {
		Shell shell = new Shell();
		MessageBox messageBox = new MessageBox(shell, SWT.ICON_QUESTION
				| SWT.OK | SWT.CANCEL | SWT.APPLICATION_MODAL);
		if (title == null)
			title = LocaleMessage.getString("messagebox.question");
		messageBox.setText(title);
		messageBox.setMessage(message);
		int buttonID = messageBox.open();
		return buttonID;
	}

}
