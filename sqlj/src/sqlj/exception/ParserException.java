package sqlj.exception;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParserException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6284174097199268758L;

	public ParserException() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ParserException(String message, Throwable cause) {
		super(message, cause);
	}

	public ParserException(String message) {
		super(message);
	}

	/**
	 * report an unexpected character exception
	 * 
	 * @param source
	 * @param idx
	 *            the index of invalid character
	 * @param expected
	 *            the string that contains characters<br>
	 *            $ means java identifier start<br>
	 *            _ means whitespace
	 */
	public ParserException(String source, int idx, String expected) {
		super();
		this.source = source;
		this.idx = idx;
		this.expected = expected;
		calcDetailInfo(source, idx);
	}

	public static final int INVALID_CHAR = 1;
	public static final int UNEXPECTED_END = 2;

	int exception_type;

	String source;
	String expected;
	int line = 1;
	int column;
	int idx;
	char errorChar;
	String errorLine;

	void calcDetailInfo(String source, int idx) {
		this.errorChar = source.charAt(idx);
		Pattern p = Pattern.compile("\\r\\n|\\n|\\r", Pattern.DOTALL);
		Matcher m = p.matcher(source);
		while (m.find() && m.start() < idx) {
			line++;
			column = m.end();
		}
		errorLine = source.substring(column, idx + 1);
		column = idx - column + 1;
	}

	public String getSource() {
		return source;
	}

	public String getExpected() {
		return expected;
	}

	public String getErrorLine() {
		return errorLine;
	}

	public int getLine() {
		return line;
	}

	public int getColumn() {
		return column;
	}

	public char getErrorChar() {
		return errorChar;
	}

	public int getErrorIndex() {
		return idx;
	}

	public String getMessage() {
		if (super.getMessage() != null)
			return super.getMessage();
		return String.format("Unexpected char %s @[%d,%d] when expect %s",
				errorChar, line, column, expected);
	}

}
