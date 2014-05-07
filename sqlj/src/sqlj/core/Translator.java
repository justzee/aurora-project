package sqlj.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.HashMap;

import sqlj.ast.AstTransform;
import sqlj.parser.SqljParser;

public class Translator {

	static HashMap<String, String> param = new HashMap<String, String>();
	static String src;

	public static void main(String[] args) throws Exception {
		if (args.length == 0) {
			printHelp();
			return;
		}
		prepareParameter(args);
		compile();
	}

	private static void compile() throws Exception {
		if (src == null) {
			throw new FileNotFoundException("source file is not specified");
		}
		File f = new File(src);
		FileInputStream fis = new FileInputStream(f);
		byte[] b = new byte[(int) f.length()];
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

	private static String getPara(String key, String default_) {
		String v = param.get(key);
		if (v == null) {
			return default_;
		}
		return v;
	}

	private static void prepareParameter(String[] args) {
		for (int i = 0; i < args.length; i += 2) {
			String on = args[i];
			if (on.startsWith("-") && i + 1 < args.length) {
				param.put(on.substring(1), args[i + 1]);
			} else if (i + 1 == args.length) {
				src = args[i];
			} else {
				throw new IllegalArgumentException(on);
			}
		}
	}

	private static void printHelp() {
		System.out.println("--help--");
		System.out.println("-e gb2312 -d ../bin login.sqlj");
		System.out.println("-e\tencoding");
		System.out.println("-d\toutput directory");
		System.out.println("-src\tkeep java src");
	}

}
