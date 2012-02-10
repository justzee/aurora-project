package aurora.ide.helpers;

import uncertain.core.UncertainEngine;
import uncertain.pkg.PackageManager;
import uncertain.schema.ISchemaManager;
import aurora.ide.preferencepages.SxsdDirPreferencePage;

public class LoadSchemaManager {

	private static UncertainEngine uncertainEngine;

	public static boolean refeshSchemaManager(String[] sxsdPaths) {
		try {
			uncertainEngine = UncertainEngineUtil.getUncertainEngine();
			PackageManager pkgManager = uncertainEngine.getPackageManager();
			if (sxsdPaths != null) {
				for (int i = 0; i < sxsdPaths.length; i++) {
					pkgManager.loadPackgeDirectory(sxsdPaths[i]);
				}
			}
		} catch (Exception e) {
			DialogUtil.showErrorMessageBox(ExceptionUtil.getExceptionTraceMessage(e));
			return false;
		}
		return true;
	}

	public static ISchemaManager refeshSchemaManager() {
		try {
			String[] sxsdPaths = SxsdDirPreferencePage.getSxsdPaths();
			refeshSchemaManager(sxsdPaths);
		} catch (Exception e) {
			DialogUtil.showErrorMessageBox(ExceptionUtil.getExceptionTraceMessage(e));
		}
		return uncertainEngine.getSchemaManager();
	}

	static void showSxsdDirHint() {
		DialogUtil.showWarningMessageBox(LocaleMessage.getString("undefined.sxsd.dir"));
	}

	public static ISchemaManager getSchemaManager() {
		if (uncertainEngine != null)
			return uncertainEngine.getSchemaManager();
		try {
			uncertainEngine = UncertainEngineUtil.getUncertainEngine();
			PackageManager pkgManager = uncertainEngine.getPackageManager();
			String[] sxsdPaths = SxsdDirPreferencePage.getSxsdPaths();
			if (sxsdPaths != null) {
				for (int i = 0; i < sxsdPaths.length; i++) {
					pkgManager.loadPackgeDirectory(sxsdPaths[i]);
				}
			}
		} catch (Throwable e) {
			DialogUtil.showErrorMessageBox(ExceptionUtil.getExceptionTraceMessage(e));
			throw new RuntimeException(e);
		}
		return uncertainEngine.getSchemaManager();
	}
}
