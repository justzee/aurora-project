package aurora.ide.editor.textpage.format.sqlformat;

public class ExceptionHandler {
	public static void handleException(String message, Token token) throws SQLFormatException {
		StringBuffer sb = new StringBuffer();
		sb.append(message);
		sb.append(":\n");
		sb.append(token.toString());
		throw new SQLFormatException(sb.toString());
	}
}
