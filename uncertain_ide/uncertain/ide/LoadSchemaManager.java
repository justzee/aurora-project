package uncertain.ide;

import uncertain.ide.eclipse.editor.widgets.CustomDialog;
import uncertain.ide.eclipse.preferencepages.SxsdDirectoryPreferencePage;
import uncertain.pkg.PackageManager;
import uncertain.schema.SchemaManager;

public class LoadSchemaManager {

	public static boolean refeshSchemaManager(String[] sxsdPaths){
		PackageManager pkgManager = new PackageManager();
		try {
			if (sxsdPaths == null || sxsdPaths.length ==0) {
				LoadSchemaManager.showSxsdDirHint();
			}
			for(int i=0;i<sxsdPaths.length;i++){
				pkgManager.loadPackgeDirectory(sxsdPaths[i]);
			}
			LoadSchemaManager.schemaManager = new SchemaManager();
			String pkg_name = SchemaManager.class.getPackage().getName();
			String schema_name = pkg_name + ".SchemaForSchema";
			LoadSchemaManager.schemaManager.loadSchemaFromClassPath(schema_name);
			LoadSchemaManager.schemaManager.addAll(pkgManager.getSchemaManager());
		} catch (Exception e) {
			CustomDialog.showExceptionMessageBox(e);
			return false;
		}
		return true;
	}

	public static SchemaManager refeshSchemaManager() {
	
			PackageManager pkgManager = new PackageManager();
			try {
	//			String sxsdDir = Activator.getDefault().getPreferenceStore()
	//					.getString(SxsdDirectoryPreferencePage.SXSD_DIRECTORY);
	//			if (sxsdDir == null || sxsdDir.equals("")) {
	//				Common.showSxsdDirHint();
	//			}
	//			pkgManager.loadPackgeDirectory(sxsdDir);
				String[] sxsdPaths = SxsdDirectoryPreferencePage.getSxsdPaths();
				if (sxsdPaths == null || sxsdPaths.length ==0) {
					LoadSchemaManager.showSxsdDirHint();
				}
				for(int i=0;i<sxsdPaths.length;i++){
					pkgManager.loadPackgeDirectory(sxsdPaths[i]);
				}
				LoadSchemaManager.schemaManager = new SchemaManager();
				String pkg_name = SchemaManager.class.getPackage().getName();
				String schema_name = pkg_name + ".SchemaForSchema";
				LoadSchemaManager.schemaManager.loadSchemaFromClassPath(schema_name);
				LoadSchemaManager.schemaManager.addAll(pkgManager.getSchemaManager());
			} catch (Exception e) {
				CustomDialog.showExceptionMessageBox(e);
			}
			return LoadSchemaManager.schemaManager;
		}

	static void showSxsdDirHint() {
		CustomDialog.showWarningMessageBox(null, LocaleMessage.getString("undefined.sxsd.dir"));
	}

	public static SchemaManager getSchemaManager() {
			if (LoadSchemaManager.schemaManager != null)
				return LoadSchemaManager.schemaManager;
			PackageManager pkgManager = new PackageManager();
			try {
	//			String sxsdDir = Activator.getDefault().getPreferenceStore()
	//					.getString(SxsdDirectoryPreferencePage.SXSD_DIRECTORY);
	//			if (sxsdDir == null || sxsdDir.equals("")) {
	//				showSxsdDirHint();
	//			}
				String[] sxsdPaths = SxsdDirectoryPreferencePage.getSxsdPaths();
				if (sxsdPaths == null || sxsdPaths.length ==0) {
					showSxsdDirHint();
				}
				for(int i=0;i<sxsdPaths.length;i++){
					pkgManager.loadPackgeDirectory(sxsdPaths[i]);
				}
			} catch (Exception e) {
				CustomDialog.showExceptionMessageBox(e);
			}
	
			LoadSchemaManager.schemaManager = SchemaManager.getDefaultInstance();
			LoadSchemaManager.schemaManager.addAll(pkgManager.getSchemaManager());
			return LoadSchemaManager.schemaManager;
		}

	static SchemaManager schemaManager;

}
