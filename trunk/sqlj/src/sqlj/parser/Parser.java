package sqlj.parser;

import sqlj.exception.ParserException;
import sqlj.util.CharStack;

public abstract class Parser {
	public static final String ITERATOR = "iterator";
	public static final String[] MODIFIERS = { "public", "private",
			"protected", "static", "final" };
	protected String source;
	protected int len;

	protected String lastWord;

	public Parser(String source) {
		super();
		setSource(source);
	}

	public void setSource(String source) {
		this.source = source;
		this.len = source.length();
	}

	public abstract Object parse() throws ParserException, Exception;

	/**
	 * when this method is called,it means a none-whitespace character is
	 * needed.
	 * 
	 * @param startIdx
	 * @return
	 * @throws ParserException
	 */
	protected int skipWhitespace(int startIdx) throws ParserException {
		for (; startIdx < len; startIdx++) {
			if (!Character.isWhitespace(source.charAt(startIdx)))
				return startIdx;
		}
		throw new ParserException("Unexpected end of source.");
	}

	protected int skipJavaIdPart(int startIdx) {
		for (; startIdx < len; startIdx++) {
			if (!Character.isJavaIdentifierPart(source.charAt(startIdx)))
				return startIdx;
		}
		return startIdx;
	}

	protected int skipJavaWord(int startIdx) throws ParserException {
		int i1 = skipWhitespace(startIdx);
		if (!Character.isJavaIdentifierStart(source.charAt(i1)))
			throw new ParserException(source, i1, "$");
		int i2 = skipJavaIdPart(i1);
		if (i2 == i1)
			throw new ParserException(source, i2, "_");
		lastWord = source.substring(i1, i2);
		int i3 = skipWhitespace(i2);
		return i3;
	}

	protected int skip(int startIdx, char c) throws ParserException {
		startIdx = skipWhitespace(startIdx);
		if (source.charAt(startIdx) == c)
			return startIdx;
		throw new ParserException(source, startIdx, "" + c);
	}

	protected int findMatch(CharStack cs, String source, int startIdx) {
		int i_ = startIdx;
		char tc;
		char expected = 0;
		while (!cs.isEmpty()) {
			tc = source.charAt(i_++);
			if (expected != 0) {
				if (tc == '\\') {
					i_++;
					continue;
				}
				if (expected != tc)
					continue;
			}
			if (tc == '(' || tc == '{')
				cs.push(tc);
			else if (tc == ')') {
				if (cs.peek() == '(')
					cs.pop();
				else
					cs.push(tc);
			} else if (tc == '}') {
				if (cs.peek() == '{')
					cs.pop();
				else
					cs.push(tc);
			} else if (tc == '\'') {
				if (cs.peek() == '\'') {
					cs.pop();
					expected = 0;
				} else {
					cs.push(tc);
					expected = '\'';
				}
			} else if (tc == '"') {
				if (cs.peek() == '"') {
					cs.pop();
					expected = 0;
				} else {
					cs.push(tc);
					expected = '"';
				}
			}
		}
		return i_ - 1;
	}
}
