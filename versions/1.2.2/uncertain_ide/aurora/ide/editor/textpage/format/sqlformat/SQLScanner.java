package aurora.ide.editor.textpage.format.sqlformat;

public class SQLScanner {
	private String sql;

	private int length;

	private int current;

	public SQLScanner(String sql) {
		this.sql = sql;
		this.length = (sql == null) ? 0 : sql.length();
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public int getLength() {
		return length;
	}

	public int getCurrent() {
		return current;
	}

	public boolean hasNext() {
		return (sql != null) && (current < length);
	}

	public boolean hasNext(int i) {
		return (sql != null) && (current + i < length);
	}

	public char next() {
		if (!hasNext()) {
			return (char) -1;
		}
		return sql.charAt(current++);
	}

	public char peek() {
		if (!hasNext()) {
			return (char) -1;
		}
		return sql.charAt(current);
	}

	public char peek(int i) {
		if (!hasNext(i)) {
			return (char) -1;
		}
		return sql.charAt(current + i);
	}

	public int skipSpaceTab() {
		int count = 0;
		char c = peek();
		while (c == ' ' || c == '\t') {
			next();
			c = peek();
			count++;
		}

		return count;
	}

	public boolean isPeekEquals(String str) {
		if (this.sql == null && str == null)
			return true;

		if (this.length > 0 && str == null)
			return false;

		int len = str.length();
		if (this.length - current < len)
			return false;

		for (int i = 0; i < len; i++) {
			if (peek(i) != str.charAt(i))
				return false;
		}
		return true;
	}

	public boolean isPeekEquals(String[] strs) {
		if (strs == null || strs.length == 0)
			return false;

		for (int i = strs.length - 1; i >= 0; i--) {
			if (isPeekEquals(strs[i]))
				return true;
		}
		return false;
	}

	public boolean isPeekEqualsEx(String str) {
		if (this.sql == null && str == null)
			return true;

		if (this.length > 0 && str == null)
			return false;

		int len = str.length();
		if (this.length - current < len)
			return false;

		int pos = 0;
		for (int i = 0; i < len; i++) {
			pos = skipTabSpaceNewLine(pos);
			if (peek(pos) != str.charAt(i))
				return false;
			pos++;
		}
		return true;
	}

	public boolean isPeekNextEqualsEx(String str) {
		if (this.sql == null && str == null)
			return true;

		if (this.length > 0 && str == null)
			return false;

		int len = str.length();
		if (this.length - current < len)
			return false;

		int pos = 0;
		for (int i = 0; i < len; i++) {
			pos = skipTabSpaceNewLine(pos);
			if (peek(pos) != str.charAt(i))
				return false;
			pos++;
		}

		current += pos;
		return true;
	}

	public String getPeekNextEqualsExString(String[] strs) {
		if (this.sql == null && strs == null)
			return null;

		if (this.length > 0 && strs == null)
			return null;

		for (int i = strs.length - 1; i >= 0; i--) {
			String str = strs[i];
			int len = str.length();
			if (this.length - current < len)
				continue;

			boolean isFind = true;
			int pos = 0;
			for (int j = 0; j < len; j++) {
				pos = skipTabSpaceNewLine(pos);
				if (peek(pos) != str.charAt(j)) {
					isFind = false;
					break;
				}
				pos++;
			}

			if (!isFind)
				continue;

			current += pos;
			return strs[i];
		}

		return null;
	}

	private int skipTabSpaceNewLine(int start) {
		int pos = start;
		char c = peek(pos);
		while (TokenUtil.isWordSeparate(c) || TokenUtil.isNewLineChar(c))
			c = peek(++pos);

		return pos;
	}

	public String substring(int beginIndex) {
		int len = current - beginIndex;
		if (len < 0)
			return null;
		return this.sql.substring(beginIndex, current);
	}
}
