package aurora.ide.helpers;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import aurora.ide.AuroraPlugin;

public class StatusUtil {
	static String defaultPluginId = AuroraPlugin.PLUGIN_ID;

	/**
	 * create a {@code Status} from a Throwable instance
	 * 
	 * @param thr
	 * @param pluginID
	 *            if {@code null} pased in,then use
	 *            {@code AuroraPlugin.PLUGIN_ID}
	 * @return
	 */
	public static IStatus createStatus(Throwable thr, String pluginID) {
		return new Status(Status.ERROR, getPluginID(pluginID),
				thr.getLocalizedMessage(), thr);
	}

	static String getPluginID(String id) {
		if (id == null)
			return defaultPluginId;
		return id;
	}

	/**
	 * 
	 * @param shell
	 *            can be {@code null}
	 * @param title
	 * @param shortDesc
	 * @param showInCurrentThread
	 *            if dialog can not be opened in current thread,please pased
	 *            {@code false} to prevent
	 *            {@code SWTException: Invalid thread access}
	 * @param status
	 */
	public static void showStatusDialog(final Shell shell, final String title,
			final String shortDesc, boolean showInCurrentThread,
			final IStatus status) {
		if (showInCurrentThread) {
			StatusDialog.openError(shell, title, shortDesc, status);
		} else {
			Display.getDefault().asyncExec(new Runnable() {

				public void run() {
					StatusDialog.openError(shell, title, shortDesc, status);
				}
			});
		}
	}

	/**
	 * @see #createStatus(Throwable, String)
	 * @see #showStatusDialog(Shell, String, String, boolean, IStatus)
	 */
	public static void showExceptionDialog(Shell shell, final String title,
			final String shortDesc, boolean showInCurrentThread, Throwable thr) {
		IStatus detail = createStatus(thr, null);
		showStatusDialog(shell, title, shortDesc, showInCurrentThread, detail);
	}

	/**
	 * create {@code MultiStatus} from {@code IStatus[]}
	 * 
	 * @param pluginID
	 *            if {@code null} pased in,then use
	 *            {@code AuroraPlugin.PLUGIN_ID}
	 * @param msg
	 * @param thr
	 * @param status
	 * @return
	 */
	public static MultiStatus createMultiStatus(String pluginID, String msg,
			Throwable thr, IStatus[] status) {
		return new MultiStatus(getPluginID(pluginID), IStatus.ERROR, status,
				msg, thr);
	}
}
