package test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;

import sqlj.ast.AstTransform;
import sqlj.core.ParsedSource;
import sqlj.parser.SqljParser;

public class TestTranslator {
	public static void main(String[] args) throws Exception {
		File f = new File("D:\\Workspaces\\WEB\\sqlj\\src\\login.sqlj");
		File fo = new File("D:\\Workspaces\\WEB\\sqlj\\src\\test\\login.java");
		FileInputStream fis = new FileInputStream(f);
		byte[] b = new byte[(int) f.length()];
		fis.read(b);
		fis.close();
		String source = new String(b, "UTF-8");
		SqljParser parser = new SqljParser(source);
		FileWriter fw = new FileWriter(fo);
		ParsedSource ps = parser.parse();
		AstTransform trans = new AstTransform(ps);
		String str = trans.tranform();
		fw.write(str);
		fw.close();

	}
}
