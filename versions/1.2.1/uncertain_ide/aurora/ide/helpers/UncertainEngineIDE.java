package aurora.ide.helpers;

import java.io.File;
import java.io.IOException;

import aurora.ide.AuroraPlugin;
import uncertain.core.UncertainEngine;

public class UncertainEngineIDE extends UncertainEngine {

	private String excludePackage;

	public UncertainEngineIDE() {
		super();
		loadBuiltinPackage();
	}
	public UncertainEngineIDE(String excludePackage) {
		super();
		this.excludePackage = excludePackage;
		loadBuiltinPackage();
	}
	public UncertainEngineIDE(File config_dir) {
		this(config_dir,null);
	}
	public UncertainEngineIDE(File config_dir, String excludePackage) {
		super();
		setConfigDirectory(config_dir);
		this.excludePackage = excludePackage;
		loadBuiltinPackage();
	}

	protected void bootstrap() {
		try {
			super.bootstrap();
		} catch (Exception e) {
			LogUtil.getInstance().logWarning(AuroraPlugin.PLUGIN_ID, e);
		}
	}

	protected void loadBuiltinPackage() {
		try {
			if ("uncertain_builtin_package".equals(excludePackage))
				return;
			else if ("aurora_builtin_package".equals(excludePackage)) {
				getPackageManager().loadPackgeDirectory(
						AuroraResourceUtil.getClassPathFile("uncertain_builtin_package").getCanonicalPath());
				return;
			}
			getPackageManager().loadPackgeDirectory(
					AuroraResourceUtil.getClassPathFile("uncertain_builtin_package").getCanonicalPath());
			getPackageManager().loadPackgeDirectory(
					AuroraResourceUtil.getClassPathFile("aurora_builtin_package").getCanonicalPath());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
