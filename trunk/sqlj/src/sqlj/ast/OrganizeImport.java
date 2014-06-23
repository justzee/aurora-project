package sqlj.ast;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ImportDeclaration;

import sqlj.core.IContext;
import sqlj.exception.ParserException;

public class OrganizeImport {
	private static HashMap<String, Boolean> required = new HashMap<String, Boolean>();
	static {
		required.put(ResultSet.class.getPackage().getName(), true);
		required.put(IContext.class.getPackage().getName(), true);
		required.put(ParserException.class.getPackage().getName(), true);
		required.put(java.util.Map.class.getName(), false);
	}
	private List<ImportDeclaration> importList;
	private AST ast;

	public OrganizeImport(CompilationUnit result) {
		this.importList = result.imports();
		this.ast = result.getAST();
	}

	public void organize() {
		Set<String> sets = new HashSet<String>(required.keySet());
		for (ImportDeclaration id : importList) {
			sets.remove(id.getName());
		}
		for (String i : sets) {
			ImportDeclaration id = ast.newImportDeclaration();
			id.setName(ast.newName(i));
			id.setOnDemand(required.get(i));
			importList.add(id);
		}
	}

}
