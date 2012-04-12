package aurora.ide.editor.textpage.format.sqlformat;

import java.util.Arrays;

public class SQLFormatRule implements ISQLFormatRule {

	private int convertKeyword = CONVERT_STRING_UPPERCASE;

	private int convertName = CONVERT_STRING_NONE;

	private String indentString = "  ";

	private int outNewLineCode = NEWLINE_CODE_SYSTEM;

	private String outNewLineCodeStr = System.getProperty("line.separator");

	private char outNewLineEnd = System.getProperty("line.separator").charAt(System.getProperty("line.separator").length() - 1);

	private int outSqlSeparator = SQL_SEPARATOR_SLASH;

	private char outSqlSeparatorChar = '/';

	private boolean newLineBeforeComma = true;

	private boolean newLineBeforeAndOr = true;

	private boolean newLineDataTypeParen = false;

	private boolean newLineFunctionParen = false;

	private boolean decodeSpecialFormat = true;

	private boolean inSpecialFormat = true;

	private boolean betweenSpecialFormat = false;

	private boolean removeComment = false;

	private boolean removeEmptyLine = false;

	private boolean indentEmptyLine = false;

	private boolean wordBreak = false;

	private int width = 80;

	private String[] functions = TokenUtil.KEYWORD_FUNCTION;

	private String[] dataTypes = TokenUtil.KEYWORD_DATATYPE;
	
	private int indent=4;

	public SQLFormatRule() {
	}

	public int getConvertKeyword() {
		return convertKeyword;
	}

	public void setConvertKeyword(int convertKeyword) {
		this.convertKeyword = convertKeyword;
	}

	public int getConvertName() {
		return convertName;
	}

	public void setConvertName(int convertName) {
		this.convertName = convertName;
	}

	public String getIndentString() {
		return indentString;
	}

	public void setIndentString(String indentString) {
		this.indentString = (indentString == null) ? "" : indentString;
	}

	public boolean isNewLineBeforeComma() {
		return newLineBeforeComma;
	}

	public void setNewLineBeforeComma(boolean newLineBeforeComma) {
		this.newLineBeforeComma = newLineBeforeComma;
	}

	public boolean isNewLineDataTypeParen() {
		return newLineDataTypeParen;
	}

	public void setNewLineDataTypeParen(boolean newLineDataTypeParen) {
		this.newLineDataTypeParen = newLineDataTypeParen;
	}

	public boolean isNewLineFunctionParen() {
		return newLineFunctionParen;
	}

	public void setNewLineFunctionParen(boolean newLineFunctionParen) {
		this.newLineFunctionParen = newLineFunctionParen;
	}

	public boolean isRemoveComment() {
		return removeComment;
	}

	public void setRemoveComment(boolean removeComment) {
		this.removeComment = removeComment;
	}

	public boolean isRemoveEmptyLine() {
		return removeEmptyLine;
	}

	public void setRemoveEmptyLine(boolean removeEmptyLine) {
		this.removeEmptyLine = removeEmptyLine;
	}

	public boolean isIndentEmptyLine() {
		return indentEmptyLine;
	}

	public void setIndentEmptyLine(boolean indentEmptyLine) {
		this.indentEmptyLine = indentEmptyLine;
	}

	public boolean isWordBreak() {
		return wordBreak;
	}

	public void setWordBreak(boolean wordBreak) {
		this.wordBreak = wordBreak;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public String getOutNewLineCodeStr() {
		return outNewLineCodeStr;
	}

	public char getOutNewLineEnd() {
		return outNewLineEnd;
	}

	public char getOutSqlSeparatorChar() {
		return outSqlSeparatorChar;
	}

	public boolean isNewLineBeforeAndOr() {
		return newLineBeforeAndOr;
	}

	public void setNewLineBeforeAndOr(boolean newLineBeforeAndOr) {
		this.newLineBeforeAndOr = newLineBeforeAndOr;
	}

	public String[] getFunctions() {
		return functions;
	}

	public String[] getDataTypes() {
		return dataTypes;
	}

	public boolean isDecodeSpecialFormat() {
		return decodeSpecialFormat;
	}

	public void setDecodeSpecialFormat(boolean decodeSpecialFormat) {
		this.decodeSpecialFormat = decodeSpecialFormat;
	}

	public boolean isInSpecialFormat() {
		return inSpecialFormat;
	}

	public void setInSpecialFormat(boolean inSpecialFormat) {
		this.inSpecialFormat = inSpecialFormat;
	}

	public boolean isBetweenSpecialFormat() {
		return betweenSpecialFormat;
	}

	public void setBetweenSpecialFormat(boolean betweenSpecialFormat) {
		this.betweenSpecialFormat = betweenSpecialFormat;
	}

	public int getOutSqlSeparator() {
		return outSqlSeparator;
	}

	public void setOutSqlSeparator(int outSqlSeparator) {
		this.outSqlSeparator = outSqlSeparator;

		switch (this.outSqlSeparator) {
		case SQL_SEPARATOR_NONE:
			break;

		case SQL_SEPARATOR_SLASH:
			this.outSqlSeparatorChar = '/';
			break;

		case SQL_SEPARATOR_SEMICOLON:
			this.outSqlSeparatorChar = ';';
			break;

		default:
			break;
		}
	}

	public void setFunctions(String[] functions) {
		functions = StringUtil.toUpperCase(functions);
		this.functions = functions;
		Arrays.sort(this.functions);
	}

	public void setDataTypes(String[] dataTypes) {
		dataTypes = StringUtil.toUpperCase(dataTypes);
		this.dataTypes = dataTypes;
		Arrays.sort(this.dataTypes);
	}

	public void addFunctions(String[] functions) {
		functions = StringUtil.toUpperCase(functions);
		this.functions = (String[]) ArrayUtil.add(this.functions, functions, new String[0]);
		Arrays.sort(this.functions);
	}

	public void addDataTypes(String[] dataTypes) {
		dataTypes = StringUtil.toUpperCase(dataTypes);
		this.dataTypes = (String[]) ArrayUtil.add(this.dataTypes, dataTypes, new String[0]);
		Arrays.sort(this.dataTypes);
	}

	public void subtractFunctions(String[] functions) {
		functions = StringUtil.toUpperCase(functions);
		this.functions = (String[]) ArrayUtil.subtract(this.functions, functions, new String[0]);
	}

	public void subtractDataTypes(String[] dataTypes) {
		dataTypes = StringUtil.toUpperCase(dataTypes);
		this.dataTypes = (String[]) ArrayUtil.subtract(this.dataTypes, dataTypes, new String[0]);
	}

	public int getOutNewLineCode() {
		return outNewLineCode;
	}

	public void setOutNewLineCode(int outNewLineCode) {
		this.outNewLineCode = outNewLineCode;

		switch (outNewLineCode) {
		case NEWLINE_CODE_SYSTEM:
			this.outNewLineCodeStr = System.getProperty("line.separator");
			break;

		case NEWLINE_CODE_CRLF:
		case NEWLINE_CODE_CR:
		case NEWLINE_CODE_LF:
			this.outNewLineCodeStr = TokenUtil.NEW_LINES[outNewLineCode - 1];
			break;
		}

		this.outNewLineEnd = this.outNewLineCodeStr.charAt(this.outNewLineCodeStr.length() - 1);
	}

	public boolean isKeyword(String str) {
		return (Arrays.binarySearch(TokenUtil.KEYWORD, str) >= 0) || (Arrays.binarySearch(this.functions, str) >= 0) || (Arrays.binarySearch(this.dataTypes, str) >= 0);
	}

	public boolean isFunctions(String str) {
		return (Arrays.binarySearch(this.functions, str) >= 0);
	}

	public boolean isDataTypes(String str) {
		return (Arrays.binarySearch(this.dataTypes, str) >= 0);
	}

	public boolean isName(String str) {
		boolean b = isKeyword(str);
		b |= TokenUtil.isSymbol(str);
		b |= TokenUtil.isValue(str);
		b |= TokenUtil.isComment(str);
		b |= TokenUtil.isSqlSeparate(str.charAt(0));
		return !b;
	}

	public int getIndent() {
		return indent;
	}

	public void setIndent(int indent) {
		this.indent = indent;
	}
}
