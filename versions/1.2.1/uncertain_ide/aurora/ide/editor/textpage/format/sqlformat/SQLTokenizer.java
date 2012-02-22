package aurora.ide.editor.textpage.format.sqlformat;

import java.util.Iterator;

public class SQLTokenizer implements Iterator<Object> {

	private SQLScanner scanner;

	SQLFormatRule rule;

	private TokenList tokenList;
	private Iterator<?> it;

	private boolean pushedBack = false;
	private Object token;

	public SQLTokenizer(String sql, SQLFormatRule rule) {
		this.scanner = new SQLScanner(sql);
		this.rule = rule;
		this.tokenList = new TokenList();

		parse();

		optimize();

		it = this.tokenList.iterator();
	}

	public boolean hasNext() {
		if (this.pushedBack)
			return true;

		return it.hasNext();
	}

	public Object next() {
		if (this.pushedBack) {
			this.pushedBack = false;
			return this.token;
		}
		if (it.hasNext()) {
			this.token = it.next();
			return this.token;
		} else {
			return null;
		}
	}

	public void pushBack() {
		if (token != null) {
			pushedBack = true;
		}
	}

	private void parse() {
		Token token = new Token("", 0, 0, 0);
		token.setType(TokenUtil.TYPE_BEGIN_SQL);
		this.tokenList.add(token);
		int x = 0;
		int y = 0;
		int beforeType = TokenUtil.TYPE_UNKNOWN;
		int depthParen = 0;

		for (; scanner.hasNext();) {
			int skipCount = scanner.skipSpaceTab();
			if (!scanner.hasNext())
				return;

			x += skipCount;
			int incY = 0;

			StringBuffer sb = new StringBuffer();

			int index = scanner.getCurrent();
			int type = TokenUtil.TYPE_UNKNOWN;
			int subType = 0;

			char c = scanner.peek();
			if (c == '\"') {

				do {
					sb.append(scanner.next());
					c = scanner.peek();
				} while (c != '\"' && scanner.hasNext());

				if (scanner.hasNext())
					sb.append(scanner.next());
				type = TokenUtil.TYPE_NAME;

			} else if (c == '\'') {

				sb.append(scanner.next());
				c = scanner.peek();

				do {
					if (scanner.isPeekEquals("''")) {
						sb.append(scanner.next());
						sb.append(scanner.next());
						c = scanner.peek();

					} else if (c == '\'' && scanner.peek(1) != '\'') {
						break;

					} else {
						sb.append(scanner.next());
						c = scanner.peek();
					}

				} while ((c != '\'' && scanner.hasNext()) || (scanner.isPeekEquals("''")));

				if (scanner.hasNext())
					sb.append(scanner.next());
				type = TokenUtil.TYPE_VALUE;
				subType = TokenUtil.SUBTYPE_VALUE_STRING;

			} else if (scanner.isPeekEquals("--")) {

				sb.append(scanner.next());
				c = scanner.peek();

				do {
					sb.append(scanner.next());
					c = scanner.peek();
				} while (scanner.hasNext() && !scanner.isPeekEquals(TokenUtil.NEW_LINES));

				type = TokenUtil.TYPE_COMMENT;
				subType = TokenUtil.SUBTYPE_COMMENT_SINGLE;

			} else if (scanner.isPeekEquals("/*")) {

				sb.append(scanner.next());
				c = scanner.peek();

				do {
					sb.append(scanner.next());
					c = scanner.peek();

					if (scanner.isPeekEquals(TokenUtil.NEW_LINES)) {
						if (scanner.isPeekEquals("\r\n")) {
							sb.append(scanner.next());
						}
						incY++;
					}

				} while (!scanner.isPeekEquals("*/") && scanner.hasNext());
				if (scanner.hasNext()) {
					sb.append(scanner.next());
					sb.append(scanner.next());
				}
				type = TokenUtil.TYPE_COMMENT;
				subType = TokenUtil.SUBTYPE_COMMENT_MULTI;

			} else if (scanner.isPeekNextEqualsEx("(*)")) {
				sb.append("(*)");
				type = TokenUtil.TYPE_SYMBOL;

			} else if (scanner.isPeekNextEqualsEx("(+)")) {
				sb.append("(+)");
				type = TokenUtil.TYPE_SYMBOL;

			} else if (scanner.isPeekEquals(TokenUtil.NEW_LINES)) {

				if (beforeType == TokenUtil.TYPE_NEW_LINE || beforeType == TokenUtil.TYPE_EMPTY_LINE) {
					type = TokenUtil.TYPE_EMPTY_LINE;
				} else {
					type = TokenUtil.TYPE_NEW_LINE;
				}
				index = scanner.getCurrent() - skipCount;

				if (scanner.isPeekEquals("\r\n")) {
					sb.append(scanner.next());
				}
				sb.append(scanner.next());
				incY++;

			} else if (Character.isDigit(c) || ((c == '.' || c == '+' || c == '-') && Character.isDigit(scanner.peek(1)))) {

				do {
					sb.append(c);
					scanner.next();
					c = scanner.peek();
				} while (TokenUtil.isNumberChar(c));
				type = TokenUtil.TYPE_VALUE;
				subType = TokenUtil.SUBTYPE_VALUE_NUMERIC;

			} else if (TokenUtil.isOperatorChar(c)) {

				String str = scanner.getPeekNextEqualsExString(TokenUtil.OPERATOR);

				if (str != null) {
					sb.append(str);
					if ("(".equals(str)) {
						depthParen++;
					}

				} else {
					sb.append(scanner.next());
				}
				type = TokenUtil.TYPE_OPERATOR;

			} else if (TokenUtil.isBindVariable(c) && !scanner.isPeekEqualsEx("::")) {
				sb.append(scanner.next());
				type = TokenUtil.TYPE_VALUE;
				subType = TokenUtil.SUBTYPE_VALUE_BIND;

			} else if (TokenUtil.isSymbolChar(c)) {
				String str = scanner.getPeekNextEqualsExString(TokenUtil.SYMBOL);

				if (str != null) {
					sb.append(str);
					if ("(".equals(str)) {
						depthParen++;
					}

				} else {
					sb.append(scanner.next());
				}

				type = TokenUtil.TYPE_SYMBOL;

			} else if (TokenUtil.isNameChar(c)) {
				do {
					sb.append(c);
					scanner.next();
					c = scanner.peek();
				} while (TokenUtil.isNameChar(c) && c != -1);

				String upper = sb.toString().toUpperCase();

				if (rule.isKeyword(upper)) {
					if (TokenUtil.isSpecialValue(upper)) {
						type = TokenUtil.TYPE_VALUE;
					} else {
						type = TokenUtil.TYPE_KEYWORD;
					}

				} else {
					type = TokenUtil.TYPE_NAME;
				}

			} else {
				sb.append(scanner.next());
			}

			String original = scanner.substring(index);
			token = new Token(original, x, y, index);

			switch (type) {
			case TokenUtil.TYPE_KEYWORD:
				token.setCustom(sb.toString());

				if (this.rule.isDataTypes(token.getUpper())) {
					subType = TokenUtil.SUBTYPE_KEYWORD_DATATYPE;

				} else if (this.rule.isFunctions(token.getUpper())) {
					subType = TokenUtil.SUBTYPE_KEYWORD_FUNCTION;
				}
				break;

			case TokenUtil.TYPE_NEW_LINE:
			case TokenUtil.TYPE_EMPTY_LINE:
				token.setCustom(StringUtil.leftTrim(token.getOriginal(), TokenUtil.WORD_SEPARATE));
				x = 0;
				break;

			default:
				token.setCustom(sb.toString());
				break;
			}

			token.setType(type);
			token.setSubType(subType);
			token.setDepthParen(depthParen);

			this.tokenList.add(token);

			beforeType = type;

			x += original.length();
			y += incY;

			if (")".equals(sb.toString())) {
				depthParen--;
			}
		}

		token = new Token("", x, y, scanner.getLength());
		token.setType(TokenUtil.TYPE_END_SQL);
		this.tokenList.add(token);
	}

	private void optimize() {

		for (int i = 0; i < tokenList.size(); i++) {
			Token current = tokenList.getToken(i);
			int next1Index = tokenList.getNextValidTokenIndex(i, 1);
			int next2Index = tokenList.getNextValidTokenIndex(i, 2);
			Token next1 = tokenList.getToken(next1Index);
			Token next2 = tokenList.getToken(next2Index);

			if (current == null || next1 == null)
				continue;

			int currentType = current.getType();
			String currentUpper = current.getUpper();
			int next1Type = next1.getType();
			String next1Upper = next1.getUpper();
			int next2Type = (next2 == null) ? TokenUtil.TYPE_UNKNOWN : next2.getType();
			String next2Upper = (next2 == null) ? null : next2.getUpper();

			if (next2 != null && (currentType == TokenUtil.TYPE_KEYWORD || currentType == TokenUtil.TYPE_NAME || currentType == TokenUtil.TYPE_VALUE) && (".".equals(next1Upper)) && (currentType == TokenUtil.TYPE_KEYWORD || next2Type == TokenUtil.TYPE_NAME || next2Type == TokenUtil.TYPE_VALUE || "*".equals(next2Upper))) {
				current.setType(TokenUtil.TYPE_NAME);
				current.setOriginal(scanner.getSql().substring(current.getIndex(), next2.getIndex() + next2.getOriginalLength()));
				current.setCustom(current.getCustom() + "." + next2.getCustom());
				tokenList.removeToken(i + 1, next2Index);
				i--;

			} else if (".".equals(currentUpper) && next1.getSubType() == TokenUtil.SUBTYPE_VALUE_NUMERIC) {
				current.setType(TokenUtil.TYPE_VALUE);
				current.setSubType(TokenUtil.SUBTYPE_VALUE_NUMERIC);
				current.setOriginal(scanner.getSql().substring(current.getIndex(), next1.getIndex() + next1.getOriginalLength()));
				current.setCustom("." + next1.getCustom());
				tokenList.removeToken(i + 1, next1Index);

			} else if (("N".equals(currentUpper) || "Q".equals(currentUpper) || "NQ".equals(currentUpper)) && next1.getSubType() == TokenUtil.SUBTYPE_VALUE_STRING) {
				current.setType(TokenUtil.TYPE_VALUE);
				current.setSubType(TokenUtil.SUBTYPE_VALUE_STRING);
				current.setOriginal(scanner.getSql().substring(current.getIndex(), next1.getIndex() + next1.getOriginalLength()));
				current.setCustom(current.getCustom() + next1.getCustom());
				tokenList.removeToken(i + 1, next1Index);
				i -= 2;

			} else if ((currentType == TokenUtil.TYPE_KEYWORD || currentType == TokenUtil.TYPE_NAME || currentType == TokenUtil.TYPE_VALUE) && (".".equals(next1Upper))) {
				current.setType(TokenUtil.TYPE_NAME);
				current.setOriginal(scanner.getSql().substring(current.getIndex(), next1.getIndex() + next1.getOriginalLength()));
				current.setCustom(current.getCustom() + ".");
				tokenList.removeToken(i + 1, next1Index);

			} else if (current.getSubType() == TokenUtil.SUBTYPE_VALUE_BIND && ":".equals(currentUpper) && next1Type == TokenUtil.TYPE_NAME) {
				current.setOriginal(scanner.getSql().substring(current.getIndex(), next1.getIndex() + next1.getOriginalLength()));
				current.setCustom(":" + next1.getCustom());
				tokenList.removeToken(i + 1, next1Index);

			} else {
				setSqlSeparator(current, i);
			}

		}
		for (int i = 0; i < tokenList.size() - 1; i++) {
			Token current = tokenList.getToken(i);
			int next1Index = tokenList.getNextValidTokenIndex(i, 1);
			Token next1 = tokenList.getToken(next1Index);

			if (current.getType() != TokenUtil.TYPE_KEYWORD)
				continue;

			if (next1 == null || next1.getType() != TokenUtil.TYPE_KEYWORD)
				continue;

			int next2Index = tokenList.getNextValidTokenIndex(i, 2);
			int next3Index = tokenList.getNextValidTokenIndex(i, 3);
			Token next2 = tokenList.getToken(next2Index);
			Token next3 = tokenList.getToken(next3Index);

			if (next3 != null && next3.getType() == TokenUtil.TYPE_KEYWORD && next2 != null && next2.getType() == TokenUtil.TYPE_KEYWORD) {
				StringBuffer sb4 = new StringBuffer();
				sb4.append(current.getCustom()).append(' ');
				sb4.append(next1.getCustom()).append(' ');
				sb4.append(next2.getCustom()).append(' ');
				sb4.append(next3.getCustom());

				if (TokenUtil.isMultiKeyword(sb4.toString().toUpperCase())) {
					current.setOriginal(scanner.getSql().substring(current.getIndex(), next3.getIndex() + next3.getOriginalLength()));
					current.setCustom(sb4.toString());
					tokenList.set(i, current);
					tokenList.removeToken(i + 1, next3Index);
					continue;
				}
			}

			if (next2 != null && next2.getType() == TokenUtil.TYPE_KEYWORD) {
				StringBuffer sb3 = new StringBuffer();
				sb3.append(current.getCustom()).append(' ');
				sb3.append(next1.getCustom()).append(' ');
				sb3.append(next2.getCustom());

				if (TokenUtil.isMultiKeyword(sb3.toString().toUpperCase())) {
					current.setOriginal(scanner.getSql().substring(current.getIndex(), next2.getIndex() + next2.getOriginalLength()));
					current.setCustom(sb3.toString());
					tokenList.set(i, current);
					tokenList.removeToken(i + 1, next2Index);
					continue;
				}
			}

			StringBuffer sb2 = new StringBuffer();
			sb2.append(current.getCustom()).append(' ');
			sb2.append(next1.getCustom());

			if (TokenUtil.isMultiKeyword(sb2.toString().toUpperCase())) {
				current.setOriginal(scanner.getSql().substring(current.getIndex(), next1.getIndex() + next1.getOriginalLength()));
				current.setCustom(sb2.toString());
				tokenList.set(i, current);
				tokenList.removeToken(i + 1, next1Index);
			}
		}

		int size = tokenList.size();
		for (int i = 0; i < size; i++) {
			Token token = this.tokenList.getToken(i);
			if (token.getType() != TokenUtil.TYPE_SYMBOL)
				continue;

			if ("(".equals(token.getUpper())) {
				setInParenInfo(i);
			}
		}
	}

	private void setSqlSeparator(Token token, int index) {
		String upper = token.getUpper();
		if (upper.length() != 1 || !TokenUtil.isSqlSeparate(upper.charAt(0)))
			return;

		int len = tokenList.size();
		for (int i = index; i < len; i++) {
			Token current = this.tokenList.getToken(i);
			int type = current.getType();
			upper = current.getUpper();

			switch (type) {
			case TokenUtil.TYPE_NAME:
			case TokenUtil.TYPE_VALUE:
			case TokenUtil.TYPE_SQL_SEPARATE:
				return;

			case TokenUtil.TYPE_SYMBOL:
				if ("(".equals(upper))
					continue;

				if (TokenUtil.isSqlSeparate(upper.charAt(0)))
					break;
				return;

			case TokenUtil.TYPE_OPERATOR:
				if (TokenUtil.isSqlSeparate(upper.charAt(0)))
					continue;
				return;

			case TokenUtil.TYPE_KEYWORD:
				if (TokenUtil.isBeginSqlKeyword(upper))
					break;
				return;

			case TokenUtil.TYPE_END_SQL:
				break;

			default:
				continue;
			}
			break;
		}

		token.setType(TokenUtil.TYPE_SQL_SEPARATE);

		Token beforeToken = this.tokenList.getToken(index - 1);
		if (beforeToken != null) {
			int beforeType = beforeToken.getType();

			switch (beforeType) {
			case TokenUtil.TYPE_NEW_LINE:
				token.setOriginal(beforeToken.getOriginal() + token.getOriginal());
				token.setCustom(rule.getOutNewLineCodeStr() + token.getCustom());
				token.setIndex(beforeToken.getIndex());
				token.setX(beforeToken.getX());
				token.setY(beforeToken.getY());
				this.tokenList.remove(index - 1);
				index--;
				break;

			case TokenUtil.TYPE_BEGIN_SQL:
			case TokenUtil.TYPE_SQL_SEPARATE:
				token.setCustom(token.getCustom());
				break;

			default:
				token.setCustom(rule.getOutNewLineCodeStr() + token.getCustom());
				break;
			}
		}

		Token nextToken = this.tokenList.getToken(index + 1);
		if (nextToken != null) {
			int nextType = nextToken.getType();
			switch (nextType) {
			case TokenUtil.TYPE_NEW_LINE:
				token.setOriginal(token.getOriginal() + nextToken.getOriginal());
				token.setCustom(token.getCustom() + rule.getOutNewLineCodeStr());
				this.tokenList.remove(index + 1);

			case TokenUtil.TYPE_END_SQL:
				break;

			default:
				token.setOriginal(token.getOriginal());
				token.setCustom(token.getCustom() + rule.getOutNewLineCodeStr());
				break;
			}
		}
	}

	private void setInParenInfo(int startPos) {
		int deep = 1;
		int elementLength = 0;
		boolean valueOnly = true;
		int size = tokenList.size();
		Token parentTokenInParen = tokenList.getParentTokenInParen(startPos);

		Token nextToken = this.tokenList.getToken(startPos - 1);
		if (nextToken != null && !")".equals(nextToken.getUpper())) {
			elementLength++;
		}

		for (int i = startPos + 1; i < size; i++) {
			Token current = this.tokenList.getToken(i);
			int type = current.getType();
			String upper = current.getUpper();

			if ("(".equals(upper)) {
				deep++;
				current.setElementIndexInParen(0);

			} else if (")".equals(upper)) {
				deep--;
				current.setElementIndexInParen(0);

				if (deep == 0) {
					for (int j = startPos; j <= i; j++) {
						current = this.tokenList.getToken(j);
						current.setElementLengthInParen(elementLength);
						current.setValueOnlyInParen(valueOnly);
						current.setParentTokenInParen(parentTokenInParen);
					}
					return;
				}

			} else {
				current.setElementIndexInParen(elementLength);

				switch (type) {
				case TokenUtil.TYPE_KEYWORD:
					valueOnly = false;
					break;

				case TokenUtil.TYPE_NAME:
				case TokenUtil.TYPE_VALUE:
				case TokenUtil.TYPE_OPERATOR:
					break;

				case TokenUtil.TYPE_SYMBOL:
					if (deep == 1 && ",".equals(upper)) {
						current.setElementIndexInParen(elementLength);
						elementLength++;

					} else if ("(".equals(upper)) {
						valueOnly = false;
					}
					break;

				default:
					break;
				}
			}
		}
	}

	public void remove() {

	}
}
