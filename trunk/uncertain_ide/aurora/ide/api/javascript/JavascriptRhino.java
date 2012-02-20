package aurora.ide.api.javascript;

import org.mozilla.javascript.Parser;
import org.mozilla.javascript.ast.AstRoot;
import org.mozilla.javascript.ast.StringLiteral;

public class JavascriptRhino {
	private String source;

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public JavascriptRhino(String source) {
		this.setSource(source);

	}

	public AstRoot createAST() {
		Parser p = new Parser();
		AstRoot parse = p.parse(source == null ? "" : source, "line", 1);
		return parse;
	}

	public String getLiteralValue(StringLiteral sl) {
		return sl.getValue();
	}

}
