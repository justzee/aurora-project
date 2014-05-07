package sqlj.util;

public class StringUtil {
	public static String[] split(String str, String split) {
		return null;
	}

	public static String join(Object[] objs, String sep) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < objs.length - 1; i++) {
			sb.append(objs[i]).append(sep);
		}
		sb.append(objs[objs.length - 1]);
		return sb.toString();
	}
}
