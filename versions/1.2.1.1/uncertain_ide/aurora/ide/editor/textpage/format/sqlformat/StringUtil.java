package aurora.ide.editor.textpage.format.sqlformat;

import java.util.Arrays;

public class StringUtil {

	public static String padLeft(String s, int width, char padChar) {
		if (s == null || s.length() >= width)
			return s;

		StringBuffer sb = new StringBuffer(width);
		int loop = width - s.length();

		for (int i = 0; i < loop; i++)
			sb.append(padChar);
		sb.append(s);

		return sb.toString();
	}

	public static String padRight(String s, int width, char padChar) {
		if (s == null || s.length() >= width)
			return s;

		StringBuffer sb = new StringBuffer(width);
		int loop = width - s.length();

		sb.append(s);
		for (int i = 0; i < loop; i++)
			sb.append(padChar);

		return sb.toString();
	}

	public static String padCenter(String s, int width, char padChar) {
		if (s == null || s.length() >= width)
			return s;

		StringBuffer sb = new StringBuffer(width);
		int loop = (width - s.length()) / 2;

		for (int i = 0; i < loop; i++)
			sb.append(padChar);
		String padString = sb.toString();
		sb.append(s);
		sb.append(padString);
		if (sb.length() != width)
			sb.append(padChar);

		return sb.toString();
	}

	public static String union(String[] strs, char c) {
		if (strs == null)
			return null;

		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < strs.length; i++) {
			sb.append(strs[i]);
			sb.append(c);
		}
		sb.deleteCharAt(sb.length() - 1);

		return sb.toString();
	}

	public static String leftTrim(String str, char[] charArray) {
		if (str == null || charArray == null)
			return str;

		Arrays.sort(charArray);

		int len = str.length();
		for (int i = 0; i < len; i++) {
			char c = str.charAt(i);
			if (0 > Arrays.binarySearch(charArray, c))
				return str.substring(i);
		}
		return str;
	}

	public static String rightTrim(String str, char[] charArray) {
		if (str == null || charArray == null)
			return str;

		Arrays.sort(charArray);

		int len = str.length();
		for (int i = len - 1; i > 0; i--) {
			char c = str.charAt(i);
			if (0 > Arrays.binarySearch(charArray, c))
				return str.substring(0, i);
		}
		return str;
	}

	public static String Trim(String str, char[] charArray) {
		if (str == null || charArray == null)
			return str;

		Arrays.sort(charArray);

		return rightTrim(leftTrim(str, charArray), charArray);
	}

	public static String toCapitalcase(String str) {
		if (str == null || str.length() == 0)
			return str;

		StringBuffer sb = new StringBuffer(str.toLowerCase());
		sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
		return sb.toString();
	}

	public static String[] toUpperCase(String[] strs) {
		if (strs == null || strs.length == 0)
			return strs;

		for (int i = 0; i < strs.length; i++) {
			strs[i] = strs[i].toUpperCase();
		}
		return strs;
	}

	public static String[] toLowerCase(String[] strs) {
		if (strs == null || strs.length == 0)
			return strs;

		for (int i = 0; i < strs.length; i++) {
			strs[i] = strs[i].toLowerCase();
		}
		return strs;
	}
}
