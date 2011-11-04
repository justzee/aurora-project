package aurora.ide.javascript;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.mozilla.javascript.Parser;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.AstRoot;
import org.mozilla.javascript.ast.NodeVisitor;
import org.mozilla.javascript.ast.StringLiteral;

import uncertain.composite.CompositeMap;

public class Javascript4Rhino {
	private String source;
	private AstRoot cu;
	private CompositeMap map;

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public CompositeMap getMap() {
		return map;
	}

	public void setMap(CompositeMap map) {
		this.map = map;
	}

	public Javascript4Rhino(CompositeMap map) {
		this.source = map.getText();
		this.map = map;

	}

	public AstRoot createAST(IProgressMonitor monitor) {
		Parser p = new Parser();
		AstRoot parse = p.parse(source, "Aurora", 1);
		return parse;
	}

	public List<StringLiteral> getStringLiteralNodes(IProgressMonitor monitor) {
		final List<StringLiteral> nodes = new ArrayList<StringLiteral>();
		AstRoot cu = getJavaScriptUnit(monitor);
		if (cu == null)
			return nodes;
		cu.visitAll(new NodeVisitor() {

			public boolean visit(AstNode node) {
				if (node instanceof StringLiteral) {
					// String value = ((StringLiteral) node).getValue();
					nodes.add((StringLiteral) node);
				}
				return true;
			}
		});
		return nodes;
	}

	public AstRoot getJavaScriptUnit(IProgressMonitor monitor) {
		if (cu == null) {
			try {
				cu = createAST(monitor);
			} catch (Exception e) {

			}
		}
		return cu;
	}

	public String getLiteralValue(StringLiteral sl) {
		return sl.getValue();
	}

}
