package aurora.ide.editor.textpage.format.sqlformat;

import java.util.LinkedList;

public class TokenList extends LinkedList<Object> {

	private static final long serialVersionUID = -7668336470690425446L;

	public Token getToken(int index) {
		if (index < 0 || size() - 1 < index)
			return null;
		return (Token) super.get(index);
	}

	public Token getFirstToken() {
		return (Token) super.getFirst();
	}

	public Token getLastToken() {
		return (Token) super.getLast();
	}

	public int getNextValidTokenIndex(int start, int index) {
		start++;
		if (start < 0 || this.size() - 1 < start || index <= 0)
			return -1;

		int count = 0;
		int len = this.size();
		for (int i = start; i < len; i++) {
			Token token = this.getToken(i);
			switch (token.getType()) {
			case TokenUtil.TYPE_NEW_LINE:
				continue;

			default:
				count++;
				if (index <= count)
					return i;
				continue;
			}
		}
		return -1;
	}

	public void removeToken(int start, int end) {
		if (start < 0 || this.size() - 1 < start || end < 0 || this.size() - 1 < end)
			return;

		if (start > end)
			return;

		for (int i = end; i >= start; i--) {
			this.remove(i);
		}
	}

	public Token getParentTokenInParen(int index) {
		if (index - 1 <= 0)
			return null;

		for (int i = index - 1; i >= 0; i--) {
			Token token = (Token) super.get(i);

			switch (token.getType()) {
			case TokenUtil.TYPE_KEYWORD:

				return token;

			case TokenUtil.TYPE_NAME:
				return token;

			case TokenUtil.TYPE_COMMENT:
			case TokenUtil.TYPE_EMPTY_LINE:
			case TokenUtil.TYPE_NEW_LINE:
				continue;

			default:
				return null;
			}
		}

		return null;
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("[TokenList:");
		buffer.append(" modCount: ");
		buffer.append(modCount);
		buffer.append("]");
		return buffer.toString();
	}
}
