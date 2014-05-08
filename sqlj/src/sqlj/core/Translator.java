package sqlj.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.HashMap;

import sqlj.ast.AstTransform;
import sqlj.parser.SqljParser;

public class Translator {

	HashMap<String, String> param = new HashMap<String, String>();
	String src;

	public static void main(String[] args) throws Exception {

		if (args.length == 0) {
			printHelp();
			return;
		}
		Translator trans = new Translator();
		trans.prepareParameter(args);

		trans.compile();
	}

	private void compile() throws Exception {
		if (src == null) {
			throw new FileNotFoundException("source file is not specified");
		}
		File f = new File(src);
		FileInputStream fis = new FileInputStream(f);
		byte[] b = new byte[(int) f.length()];//large file ???
		fis.read(b);
		fis.close();
		String source = new String(b, getPara("e", "UTF-8"));
		SqljParser parser = new SqljParser(source);
		ParsedSource ps = parser.parse();
		AstTransform transform = new AstTransform(ps);
		String javaSrc = transform.tranform();
		String fileName = f.getName();
		int idx = fileName.indexOf('.');
		fileName = fileName.substring(0, idx) + ".java";
		File fo = new File(getPara("d", f.getParent()), fileName);
		FileOutputStream fos = new FileOutputStream(fo);
		fos.write(javaSrc.getBytes("UTF-8"));
		fos.close();
	}

	private String getPara(String key, String default_) {
		String v = param.get(key);
		if (v == null) {
			return default_;
		}
		return v;
	}

	private void prepareParameter(String[] args) {
		String lastParam = null;
		for (int i = 0; i < args.length - 1; i++) {
			String a = args[i];
			if (a.charAt(0) == '-') {
				lastParam = a.substring(1);
				if (lastParam.length() == 0)
					throw new IllegalArgumentException("-");
			} else {
				if (lastParam == null)
					throw new IllegalArgumentException(a);
				param.put(lastParam, a);
			}
		}
		src = args[args.length - 1];
	}

	private static void printHelp() {
		System.out.println("--help--");
		System.out.println("-e gbk -d ../bin login.sqlj");
		System.out.println("-e\tencoding");
		System.out.println("-d\toutput directory");
		System.out.println("-src\tkeep java src");
	}

}
