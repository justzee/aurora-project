package aurora.ide.editor.textpage.format.sqlformat;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

@SuppressWarnings("rawtypes")
public class ArrayUtil {

	public static Object[] add(Object[] array1, Object obj2) {
		Object[] array2 = { obj2 };
		return add(array1, array2, null);
	}

	public static Object[] add(Object[] array1, Object[] array2) {
		return add(array1, array2, null);
	}

	public static Object[] add(Object[] array1, Object obj2, Object[] conv) {
		Object[] array2 = { obj2 };
		return add(array1, array2, conv);
	}

	@SuppressWarnings("unchecked")
	public static Object[] add(Object[] array1, Object[] array2, Object[] conv) {
		if (array1 != null && array2 == null)
			return array1;
		if (array1 == null && array2 != null)
			return array2;

		List list = new LinkedList(Arrays.asList(array1));

		for (int i = 0; i < array2.length; i++) {
			if (!list.contains(array2[i]) && array2[i] != null)
				list.add(array2[i]);
		}

		if (conv == null)
			return list.toArray();
		return list.toArray(conv);
	}

	public static Object[] subtract(Object[] array1, Object obj2) {
		Object[] array2 = { obj2 };
		return subtract(array1, array2, null);
	}

	public static Object[] subtract(Object[] array1, Object[] array2) {
		return subtract(array1, array2, null);
	}

	public static Object[] subtract(Object[] array1, Object obj2, Object[] conv) {
		Object[] array2 = { obj2 };
		return subtract(array1, array2, conv);
	}

	@SuppressWarnings("unchecked")
	public static Object[] subtract(Object[] array1, Object[] array2, Object[] conv) {
		if (array1 == null || array1.length == 0 || array2 == null || array2.length == 0)
			return array1;

		LinkedList list = new LinkedList(Arrays.asList(array1));

		for (int i = 0; i < array2.length; i++) {
			if (list.contains(array2[i]))
				list.remove(array2[i]);
		}

		if (conv == null)
			return list.toArray();
		return list.toArray(conv);
	}
}
