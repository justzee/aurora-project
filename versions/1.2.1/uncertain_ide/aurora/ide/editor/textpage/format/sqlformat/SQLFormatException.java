package aurora.ide.editor.textpage.format.sqlformat;

import java.io.IOException;

@SuppressWarnings("serial")
public class SQLFormatException extends IOException {

	public SQLFormatException() {
		super();
	}

	public SQLFormatException(String arg) {
		super(arg);
	}
}
