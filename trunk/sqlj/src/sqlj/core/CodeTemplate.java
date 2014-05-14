package sqlj.core;

import java.util.ResourceBundle;

public class CodeTemplate {
	private static final String BUNDLE_NAME = CodeTemplate.class.getPackage()
			.getName() + ".code_template";
	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle
			.getBundle(BUNDLE_NAME);

	public static String get(String key) {
		return RESOURCE_BUNDLE.getString(key);
	}
}
