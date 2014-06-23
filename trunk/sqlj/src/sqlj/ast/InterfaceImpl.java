package sqlj.ast;

import static sqlj.ast.ASTNodeUtil.*;

import java.lang.reflect.Method;
import java.util.List;

import org.eclipse.jdt.core.dom.*;

import sqlj.core.IContext;
import sqlj.core.IProcedure;

public class InterfaceImpl {
	public static final String CTX_NAME = "__sqlj_context__";

	private TypeDeclaration typeDec;
	private AST ast;

	public InterfaceImpl(TypeDeclaration td) {
		this.typeDec=td;
		this.ast=td.getAST();
	}
	
	public void addDefaultImpl() {
		//add necessary implements
		typeDec.superInterfaceTypes().add(ASTNodeUtil.newSimpleType(ast, IProcedure.class.getName()));
		//create necessary fields
		
		List<BodyDeclaration> bodys = typeDec.bodyDeclarations();

		createField(typeDec, IContext.class, CTX_NAME, false);

		for (Method m : IProcedure.class.getMethods()) {
			String mName = m.getName();

			MethodDeclaration md = createFromMethod(ast, m);
			//md.setBody(ast.newBlock());

			if ("__init__".equals(mName)) {
				md.setBody(createSimpleAssignBlock(ast, CTX_NAME, "args0"));
			} else if ("__finallize__".equals(mName)) {
				md.setBody(createFinallieMethodBody());
			} else if ("getContext".equals(mName)) {
				md.setBody(createSimpleReturnBlock(ast, CTX_NAME));
			}
			bodys.add(md);
		}
	}

	private void createField(TypeDeclaration td, Class type, String name,
			boolean init) {
		AST ast = td.getAST();
		FieldDeclaration fd = ast
				.newFieldDeclaration(newVariableDeclarationFragment(ast, name,
						null));
		fd.setType(newSimpleType(ast, type.getName()));
		td.bodyDeclarations().add(fd);
	}
	
	private Block createFinallieMethodBody() {
		return ast.newBlock();
	}

	public static MethodDeclaration createFromMethod(AST ast, Method m) {
		MethodDeclaration md = ast.newMethodDeclaration();
		md.setName(ast.newSimpleName(m.getName()));
		if (m.getReturnType() != void.class)
			md.setReturnType2(ast.newSimpleType(ast.newName(m.getReturnType()
					.getName())));
		md.modifiers().addAll(ast.newModifiers(Modifier.PUBLIC));
		for (Class c : m.getExceptionTypes()) {
			md.thrownExceptions().add(ast.newName(c.getName()));
		}
		int i_ = 0;
		for (Class c : m.getParameterTypes()) {
			md.parameters().add(
					ASTNodeUtil.newSingleVariableDeclaration(ast,
							ASTNodeUtil.newSimpleType(ast, c.getName()), "args"
									+ i_, null));
		}
		return md;
	}

	public static Block createSimpleAssignBlock(AST ast, String varName,
			String valueName) {
		Block b = ast.newBlock();
		Assignment assi = ast.newAssignment();
		assi.setLeftHandSide(ast.newName(varName));
		if (valueName == null)
			assi.setRightHandSide(ast.newNullLiteral());
		else
			assi.setRightHandSide(ast.newName(valueName));
		b.statements().add(ast.newExpressionStatement(assi));
		return b;
	}

	public static Block createSimpleReturnBlock(AST ast, String varName) {
		Block b = ast.newBlock();
		ReturnStatement rs = ast.newReturnStatement();
		if (varName == null)
			rs.setExpression(ast.newNullLiteral());
		else
			rs.setExpression(ast.newName(varName));
		b.statements().add(rs);
		return b;
	}
}
