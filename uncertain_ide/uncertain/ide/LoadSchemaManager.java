package uncertain.ide;

import java.net.URL;

import org.eclipse.core.runtime.FileLocator;

import uncertain.ide.eclipse.editor.widgets.CustomDialog;
import uncertain.ide.eclipse.preferencepages.SxsdDirPreferencePage;
import uncertain.pkg.PackageManager;
import uncertain.schema.SchemaManager;

public class LoadSchemaManager {

	private static SchemaManager schemaManager;

	public static boolean refeshSchemaManager(String[] sxsdPaths) {
		PackageManager pkgManager = new PackageManager();
		try {
			loadBuildinSchema(pkgManager);
			if (sxsdPaths != null){
				for (int i = 0; i < sxsdPaths.length; i++) {
					pkgManager.loadPackgeDirectory(sxsdPaths[i]);
				}
			}
			LoadSchemaManager.schemaManager = new SchemaManager();
			String pkg_name = SchemaManager.class.getPackage().getName();
			String schema_name = pkg_name + ".SchemaForSchema";
			LoadSchemaManager.schemaManager
					.loadSchemaFromClassPath(schema_name);
			LoadSchemaManager.schemaManager.addAll(pkgManager
					.getSchemaManager());
		} catch (Exception e) {
			CustomDialog.showErrorMessageBox(CustomDialog.getExceptionMessage(e));
			return false;
		}
		return true;
	}

	public static SchemaManager refeshSchemaManager() {

		PackageManager pkgManager = new PackageManager();
		try {
			loadBuildinSchema(pkgManager);
			String[] sxsdPaths = SxsdDirPreferencePage.getSxsdPaths();
			if (sxsdPaths != null){
				for (int i = 0; i < sxsdPaths.length; i++) {
					pkgManager.loadPackgeDirectory(sxsdPaths[i]);
				}
			}
			LoadSchemaManager.schemaManager = new SchemaManager();
			String pkg_name = SchemaManager.class.getPackage().getName();
			String schema_name = pkg_name + ".SchemaForSchema";
			LoadSchemaManager.schemaManager
					.loadSchemaFromClassPath(schema_name);
			LoadSchemaManager.schemaManager.addAll(pkgManager
					.getSchemaManager());
		} catch (Exception e) {
			CustomDialog.showErrorMessageBox(CustomDialog.getExceptionMessage(e));
		}
		return LoadSchemaManager.schemaManager;
	}

	static void showSxsdDirHint() {
		CustomDialog.showWarningMessageBox(null, LocaleMessage
				.getString("undefined.sxsd.dir"));
	}

	public static SchemaManager getSchemaManager() {
		if (LoadSchemaManager.schemaManager != null)
			return LoadSchemaManager.schemaManager;
		PackageManager pkgManager = new PackageManager();
		try {
			loadBuildinSchema(pkgManager);
			String[] sxsdPaths = SxsdDirPreferencePage.getSxsdPaths();
			if (sxsdPaths != null){
				for (int i = 0; i < sxsdPaths.length; i++) {
					pkgManager.loadPackgeDirectory(sxsdPaths[i]);
				}
			}
		} catch (Throwable e) {
			CustomDialog.showErrorMessageBox(CustomDialog.getExceptionMessage(e));
		}

		LoadSchemaManager.schemaManager = SchemaManager.getDefaultInstance();
		LoadSchemaManager.schemaManager.addAll(pkgManager.getSchemaManager());
		return LoadSchemaManager.schemaManager;
	}
	public static void  loadBuildinSchema(PackageManager pkgManager) throws Exception {
//		URL url = FileLocator.toFileURL(Activator.getDefault().getBundle().getResource("aurora_builtin_package/"));
//		pkgManager.loadPackgeDirectory(url.getFile());
		String[] packages = new String[]{"aurora_builtin_package/aurora.base/","aurora_builtin_package/aurora.database/","aurora_builtin_package/aurora.presentation/"}; 
		for (int i = 0; i < packages.length; i++) {
			String packageName = packages[i];
			URL url = FileLocator.toFileURL(Activator.getDefault().getBundle().getResource(packageName));
			pkgManager.loadPackage(url.getFile());
		}
	}
}
