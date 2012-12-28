package aurora.ide.helpers;

public class StringUtil {
	public static boolean isBlank(Object s) {
		return null == s || "".equals(s);
	}
}
