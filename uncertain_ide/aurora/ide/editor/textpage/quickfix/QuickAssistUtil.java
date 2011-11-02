package aurora.ide.editor.textpage.quickfix;

import java.util.List;

import uncertain.composite.CompositeMap;
import uncertain.util.resource.Location;

public class QuickAssistUtil {
	/**
	 * 在一个CompositeMap 中查找一个子结点map,这个子节点是最小的包含指定行的map
	 * 
	 * @param rootMap
	 * @param line
	 *            行号 从0开始
	 * @return
	 */
	public static CompositeMap findMap(CompositeMap rootMap, int line) {
		@SuppressWarnings("unchecked")
		List<CompositeMap> childs = rootMap.getChildsNotNull();
		for (CompositeMap map : childs) {
			Location loc = map.getLocationNotNull();
			if (loc.getStartLine() <= line + 1 && loc.getEndLine() >= line + 1)
				return findMap(map, line);
		}
		return rootMap;
	}

	/**
	 * 最小编辑距离
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public static int getEditDistance(char a[], char b[]) {
		int i, j;
		int m = a.length;
		int n = b.length;
		int d[] = new int[n + 1];
		for (i = 0; i <= n; i++)
			d[i] = i;
		for (i = 1; i <= m; i++) {
			int y = i - 1;
			for (j = 1; j <= n; j++) {
				int x = y;
				y = d[j];
				int z = (j > 1 ? d[j - 1] : i);
				int del = (a[i - 1] == b[j - 1] ? 0 : 1);
				d[j] = Math.min(Math.min(x + del, y + 1), z + 1);
			}
		}
		int temp = d[n];
		return temp;
	}

	/**
	 * 计算最小编辑距离(忽略大小写)
	 * 
	 * @param t
	 * @param s
	 * @return
	 */
	public static int getEditDistance(String t, String s) {
		return getEditDistance(t.toLowerCase().toCharArray(), s.toLowerCase()
				.toCharArray());
	}

	/**
	 * 计算一个合适的编辑距离(忽略大小写)
	 * 
	 * @param s1
	 *            错误单词
	 * @param s2
	 *            正确单词
	 * @return -1 表示不认为这两个词之间有明显联系<br/>
	 *         <b>非负值</b> 有意义的编辑距离
	 */
	public static int getApproiateEditDistance(String s1, String s2) {
		int ed = getEditDistance(s1, s2);
		if (1 << ed > s2.length())
			return -1;
		return ed;
	}
}
