package aurora.ide.editor.textpage.format.sqlformat;

public interface ISQLFormatRule {

	public static final int CONVERT_STRING_NONE = 0;
	public static final int CONVERT_STRING_UPPERCASE = 1;
	public static final int CONVERT_STRING_LOWERCASE = 2;
	public static final int CONVERT_STRING_CAPITALCASE = 3;

	public static final int NEWLINE_CODE_SYSTEM = 0;
	public static final int NEWLINE_CODE_CRLF = 1;
	public static final int NEWLINE_CODE_CR = 2;
	public static final int NEWLINE_CODE_LF = 3;

	public static final int SQL_SEPARATOR_NONE = 0;
	public static final int SQL_SEPARATOR_SLASH = 1;
	public static final int SQL_SEPARATOR_SEMICOLON = 2;

	public static final String[] FUNCTION = { "NVL2", "NVL", "ASCII", "CHR", "CONCAT", "INITCAP", "INSTR", "LENGTH", "LOWER", "UPPER", "RPAD", "LPAD", "LTRIM", "RTRIM", "SUBSTR", "REPLACE", "SOUNDEX", "TRIM", "ABS", "ACOS", "ASIN", "ATAN", "CEIL", "COS", "COSH", "EXP", "FLOOR", "LN", "LOG", "MOD", "POWER", "ROUND", "TRUNC", "SIGN", "SIN", "SIGH", "SQRT", "TAN", "TANH", "TRUNC", "ADD_MONTHS", "LAST_DAY", "MONTHS_BETWEEN", "NEW_TIME", "NEXT_DAY", "SYSDATE", "CHARTOROWID", "CONVERT", "HEXTORAW", "RAWTOHEX", "ROWIDTOCHAR", "TO_CHAR", "TO_DATE", "TO_MULTI_BYTE", "TO_NUMBER", "BFILENAME", "CONVERT", "DUMP", "EMPTY_BLOB", "GREATEST", "LEAST", "UID", "USER", "USEREVN", "AVG", "MAX", "MIN", "STDDEV", "VARIANCE", "GROUP", "HAVING", "ORDER" };

	public abstract int getConvertKeyword();

	public abstract void setConvertKeyword(int convertKeyword);

	public abstract int getConvertName();

	public abstract void setConvertName(int convertName);

	public abstract String getIndentString();

	public abstract void setIndentString(String indentString);

	public abstract boolean isNewLineBeforeComma();

	public abstract void setNewLineBeforeComma(boolean newLineBeforeComma);

	public abstract boolean isNewLineBeforeAndOr();

	public abstract void setNewLineBeforeAndOr(boolean newLineBeforeAndOr);

	public abstract boolean isNewLineDataTypeParen();

	public abstract void setNewLineDataTypeParen(boolean newLineDataTypeParen);

	public abstract boolean isNewLineFunctionParen();

	public abstract void setNewLineFunctionParen(boolean newLineFunctionParen);

	public abstract boolean isDecodeSpecialFormat();

	public abstract void setDecodeSpecialFormat(boolean decodeSpecialFormat);

	public abstract boolean isInSpecialFormat();

	public abstract void setInSpecialFormat(boolean inSpecialFormat);

	public abstract boolean isBetweenSpecialFormat();

	public abstract void setBetweenSpecialFormat(boolean betweenSpecialFormat);

	public abstract boolean isRemoveComment();

	public abstract void setRemoveComment(boolean removeComment);

	public abstract boolean isRemoveEmptyLine();

	public abstract void setRemoveEmptyLine(boolean removeEmptyLine);

	public abstract boolean isIndentEmptyLine();

	public abstract void setIndentEmptyLine(boolean indentEmptyLine);

	public abstract boolean isWordBreak();

	public abstract void setWordBreak(boolean wordBreak);

	public abstract int getWidth();

	public abstract void setWidth(int width);

	public abstract int getOutNewLineCode();

	public abstract void setOutNewLineCode(int outNewLineCode);

	public abstract int getOutSqlSeparator();

	public abstract void setOutSqlSeparator(int outSqlSeparator);

	public abstract String[] getFunctions();

	public abstract void setFunctions(String[] functions);

	public abstract void addFunctions(String[] functions);

	public abstract void subtractFunctions(String[] functions);

	public abstract String[] getDataTypes();

	public abstract void setDataTypes(String[] dataTypes);

	public abstract void addDataTypes(String[] dataTypes);

	public abstract void subtractDataTypes(String[] dataTypes);

}