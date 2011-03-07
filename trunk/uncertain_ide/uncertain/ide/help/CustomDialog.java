package uncertain.ide.help;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import uncertain.ide.eclipse.project.propertypage.ProjectPropertyPage;


public class CustomDialog {

	public static void showMessageBox(int style, String title, String message) {
		message = getLocalMessage(message);
		Shell shell = getShell();
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
		Shell shell = getShell();
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
	public static void showErrorMessageBox(final Throwable e) {
		Display.getCurrent().asyncExec(new Runnable() {
			public void run() {
				showErrorMessageBox(getExceptionMessage(e));
			}
		});
	}
	public static void showErrorMessageBox(String title, String message) {
		message = getLocalMessage(message);
		Shell shell = getShell();
		MessageBox messageBox = new MessageBox(shell, SWT.ICON_ERROR | SWT.OK
				| SWT.APPLICATION_MODAL);
		if (title == null)
			title = getLocalMessage("messagebox.error");
		messageBox.setText(title);
		messageBox.setMessage(message);
		messageBox.open();
	}

	public static void showExceptionMessageBox(String title, Throwable e) {
		Shell shell = getShell();
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
//		throw new RuntimeException(e);
	}

	public static void showExceptionMessageBox(Throwable e) {
		showExceptionMessageBox(null,e);
		
	}

	public static String getExceptionMessage(Throwable e) {
		String causeMessage = "";
		String tipMessage = "系统提示："+AuroraResourceUtil.LineSeparator;
		if(e == null)
			return null;
		if(e instanceof ApplicationException){
			tipMessage = tipMessage+getMessage(e)+AuroraResourceUtil.LineSeparator;
		}
		else{
			causeMessage = causeMessage+getMessage(e)+AuroraResourceUtil.LineSeparator;
		}
		Throwable parent = e;
		Throwable child;
		while((child=parent.getCause())!=null){
			if(child instanceof ApplicationException){
				tipMessage = tipMessage+getMessage(child)+AuroraResourceUtil.LineSeparator;
			}else{
				//just the root causeMessage is enough.
				causeMessage = getMessage(child)+AuroraResourceUtil.LineSeparator;
			}
			parent = child;
		}
		String message = causeMessage+tipMessage;
		if(ProjectPropertyPage.isDebugMode(AuroraResourceUtil.getIProjectFromSelection())){
			Throwable full = new SystemException(e);
			LogUtil.getInstance().logError(null, full);
		}
		return message;
	}
	private static String getMessage(Throwable e){
		if(e instanceof ApplicationException){
			return e.getLocalizedMessage();
		}else{
			return e.toString();
		}
	}
	public static void printStackTrace(Throwable e){
		StackTraceElement elements[] = e.getStackTrace();
		for (int i=0, n=elements.length; i<n; i++) {
		  System.err.println(elements[i].getFileName() + ":" + 
		    elements[i].getLineNumber() + " ==> " +
		    elements[i].getMethodName()+"()");
		}
		if(e.getCause()!=null){
			printStackTrace(e.getCause());
		}
	}
	public static Throwable getRootCause(Throwable e){
		Throwable cause = e.getCause();
		if(cause == null)
			return e;
		return getRootCause(cause);	
	}
	public static int showConfirmDialogBox(String message){
		return showConfirmDialogBox(null, message);
	}
	
	public static int showConfirmDialogBox(String title, String message) {
		message = getLocalMessage(message);
		Shell shell = getShell();
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
	private static Shell getShell(){
		Shell shell = Display.getCurrent().getActiveShell();
		if(shell == null){
			shell = new Shell(Display.getCurrent());
		}
		return shell;
	}

}
