package test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.Modifier.ModifierKeyword;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;

public class ASTtest {
	public static void main(String[] args) throws Exception {
		//		helloWorld();
		test();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void test() throws Exception {

		ASTParser parser = ASTParser.newParser(AST.JLS4);
		String cs = getSource();
		parser.setKind(ASTParser.K_EXPRESSION);
		parser.setSource("a[0]	=new Set<String>()".toCharArray());
		Expression result =  (Expression)parser.createAST(null);
		System.out.println(result);
	}

	public static String getSource() {
		StringBuilder sb = new StringBuilder();
		try {
			File f = new File("D:\\Workspaces\\WEB\\sqlj2\\src\\test\\aaa.java");
			FileInputStream fis = new FileInputStream(f);
			byte[] b = new byte[(int) f.length()];
			fis.read(b);
			fis.close();
			sb.append(new String(b, "UTF-8"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sb.toString();
	}

	public static void helloWorld() {
		AST ast = AST.newAST(AST.JLS4);
		CompilationUnit compilationUnit = ast.newCompilationUnit();
		// 创建类
		TypeDeclaration programClass = ast.newTypeDeclaration();
		programClass.setName(ast.newSimpleName("HelloWorld"));
		programClass.modifiers().add(
				ast.newModifier(ModifierKeyword.PUBLIC_KEYWORD));
		compilationUnit.types().add(programClass);
		// 创建包
		PackageDeclaration packageDeclaration = ast.newPackageDeclaration();
		packageDeclaration.setName(ast.newName("com.dream"));
		compilationUnit.setPackage(packageDeclaration);
		MethodDeclaration main = ast.newMethodDeclaration();
		main.setName(ast.newSimpleName("main"));
		main.modifiers().add(
				ast.newModifier(Modifier.ModifierKeyword.PUBLIC_KEYWORD));
		main.modifiers().add(ast.newModifier(ModifierKeyword.STATIC_KEYWORD));
		main.setReturnType2(ast.newPrimitiveType(PrimitiveType.VOID));
		programClass.bodyDeclarations().add(main);
		Block mainBlock = ast.newBlock();
		main.setBody(mainBlock);
		// 给main方法定义String[]参数
		SingleVariableDeclaration mainParameter = ast
				.newSingleVariableDeclaration();
		mainParameter.setName(ast.newSimpleName("arg"));
		mainParameter.setType(ast.newArrayType(ast.newSimpleType(ast
				.newName("String"))));
		main.parameters().add(mainParameter);
		MethodInvocation println = ast.newMethodInvocation();
		println.setName(ast.newSimpleName("prinln"));
		// 生成String类型的常量
		StringLiteral s = ast.newStringLiteral();
		s.setLiteralValue("Hello World");
		println.arguments().add(s);
		println.setExpression(ast.newName("System.out"));
		mainBlock.statements().add(ast.newExpressionStatement(println));
		System.out.println(compilationUnit.toString());
	}
}
