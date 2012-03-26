package aurora.ide.editor.textpage.format.sqlformat;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PLSQLFormat {
	private StringBuffer sb = new StringBuffer();
	private List<String> sqlBuffer = new ArrayList<String>();
	SQLFormat sqlFormat = new SQLFormat();

	public PLSQLFormat(String sql) {
		sqlFormat.rule = new SQLFormatRule();
		sqlFormat.rule.setNewLineBeforeComma(false);
		sqlFormat.tokenizer = new SQLTokenizer(sql, sqlFormat.rule);
		for (Iterator<?> it = sqlFormat.tokenizer; it.hasNext();) {
			Token token = (Token) it.next();
			if ("".equals(token.getCustom().trim())) {
				continue;
			}
			String s = token.getCustom().trim();
			sqlBuffer.add(s);
		}
	}

	private void addIndent(int indent) {
		for (int i = 0; i < indent * 4; i++) {
			sb.append(" ");
		}
	}

	public String format() throws Exception {
		int indent = 0;
		boolean once = false;
		boolean isBegin = false;
		boolean isFunction = false;
		boolean isException = false;
		boolean isUserFunction = false;
		boolean isDeclare = false;
		int userFunctionLength = 0;
		for (int i = 0; i < sqlBuffer.size(); i++) {
			boolean isEnd = i == sqlBuffer.size() - 1;
			String curWord = sqlBuffer.get(i);
			String nextWord = isEnd ? "" : sqlBuffer.get(i + 1).replaceAll("\r\n|\n", "").toUpperCase();
			String upper = curWord.replaceAll("\r\n|\n", "").toUpperCase();
			if (("BEGIN".equals(upper))) {
				indent = indent <= 0 ? 0 : indent--;
				isBegin = true;
				isDeclare = false;
				isException = false;
				sb.append(upper);
				sb.append("\n");
				indent++;
				addIndent(indent);
			} else if (("DECLARE".equals(upper)) || ("EXCEPTION".equals(upper))) {
				indent = indent <= 0 ? 0 : indent--;
				sb.append(upper);
				sb.append("\n");
				indent++;
				addIndent(indent);
				if ("EXCEPTION".equals(upper)) {
					isException = true;
				} else {
					isDeclare = true;
				}
				isBegin = false;
			} else if (("END".equals(upper))) {
				if (once) {
					once = false;
					indent--;
				}
				sb.append(upper);
				if (!isEnd) {
					String s = sqlBuffer.get(i + 1).toUpperCase();
					if ("IF".equals(s) || "LOOP".equals(s)) {
						sb.append(" ");
					}
				}
			} else if ((";".equals(upper))) {
				sb.append(upper);
				sb.append("\n");
				if ("BEGIN".equals(nextWord) || "EXCEPTION".equals(nextWord) || "END".equals(nextWord) || "ELSE".equals(nextWord) || "ELSIF".equals(nextWord) || "WHEN".equals(nextWord)) {
					indent--;
					if (isException && "END".equals(nextWord)) {
						indent--;
						isException = false;
					}
					if (isBegin && "BEGIN".equals(nextWord)) {
						indent++;
					}
				}
				if (once) {
					once = false;
					indent--;
				}
				addIndent(indent);
			} else if (("SELECT".equals(upper)) || ("UPDATE".equals(upper)) || ("DELETE".equals(upper)) || ("CREATE".equals(upper)) || ("INSERT".equals(upper)) || ("DROP".equals(upper))
					|| ("TRUNCATE".equals(upper)) || ("MERGE".equals(upper)) || ("ALTER".equals(upper)) || ("CREATE OR REPLACE".equals(upper))) {
				StringBuffer temp = new StringBuffer();
				for (; i < sqlBuffer.size(); i++) {
					String s = sqlBuffer.get(i).replaceAll("\r\n|\n", "");
					if (s.equals(";")) {
						break;
					} else if (s.matches("(--.*)")) {
						s += "\n";
					}
					temp.append(s);
					temp.append(" ");
				}
				StringBuffer s = new StringBuffer(sqlFormat.format(temp.toString()));
				String[] ss = s.toString().split("\n");
				sb.append(ss[0] + "\n");
				for (int ii = 1; ii < ss.length - 1; ii++) {
					addIndent(indent);
					sb.append(ss[ii] + "\n");
				}
				addIndent(indent);
				sb.append(ss[ss.length - 1]);
				sb.append(";");
				sb.append("\n");
				if (once) {
					once = false;
					indent--;
				}
				addIndent(indent);
			} else if (("THEN".equals(upper)) || ("ELSE".equals(upper))) {
				sb.append(upper);
				sb.append("\n");
				indent++;
				addIndent(indent);
			} else if ("IS".equals(upper)) {
				once = true;
				sb.append(upper);
				if (!nextWord.equals(";")) {
					sb.append("\n");
					indent++;
					addIndent(indent);
				}
			} else if ("LOOP".equals(upper)) {
				sb.append(upper);
				if (!nextWord.equals(";")) {
					sb.append("\n");
					indent++;
					addIndent(indent);
				}
			} else if ("END".equals(upper)) {
				indent--;
				addIndent(indent);
				sb.append(upper);
			} else if ((",".equals(upper))) {
				if (isFunction) {
					sb.append(curWord);
				} else if (isUserFunction) {
					sb.append(curWord);
					sb.append("\n");
					addIndent(indent);
					for (int ii = 0; ii <= userFunctionLength; ii++) {
						sb.append(" ");
					}
				} else {
					sb.append(curWord);
					sb.append("\n");
					addIndent(indent);
				}
			} else if ((upper.startsWith("--")) || (upper.startsWith("/*") && upper.endsWith("*/"))) {
				if (upper.matches("/\\*.+\\*/")) {
					String[] ss = curWord.split("\n");
					sb.append(ss[0].trim());
					sb.append("\n");
					for (int ii = 1; ii < ss.length; ii++) {
						addIndent(indent);
						sb.append(ss[ii].trim());
						sb.append("\n");
					}
				} else {
					sb.append(curWord);
					sb.append("\n");
				}
				if ("BEGIN".equals(nextWord) || "EXCEPTION".equals(nextWord) || "END".equals(nextWord) || "ELSE".equals(nextWord) || "ELSIF".equals(nextWord) || "WHEN".equals(nextWord)) {
					indent--;
					if (isException && "END".equals(nextWord)) {
						indent--;
						isException = false;
					}
				}
				addIndent(indent);
			} else if (curWord.endsWith(".")) {
				sb.append(curWord);
			} else if (curWord.endsWith("*")) {
				sb.append(curWord);
				sb.append(" ");
			} else if ("(".equals(curWord)) {
				if (isFunction || isUserFunction) {
					sb.append(curWord);
				} else {
					for (int ii = i; ii < sqlBuffer.size(); ii++) {
						String s = sqlBuffer.get(ii);
						if (")".equals(s)) {
							sb.append(curWord);
							break;
						} else if (",".equals(s)) {
							sb.append("\n");
							addIndent(indent);
							sb.append(curWord);
							sb.append("\n");
							indent++;
							addIndent(indent);
							break;
						}
					}
				}
			} else if (")".equals(nextWord)) {
				if (isFunction) {
					sb.append(curWord);
					isFunction = false;
				} else if (isUserFunction) {
					sb.append(curWord);
					isUserFunction = false;
				} else {
					for (int ii = i; ii > 0; ii--) {
						String s = sqlBuffer.get(ii);
						if ("(".equals(s)) {
							sb.append(curWord);
							sb.append(" ");
							break;
						} else if (",".equals(s)) {
							sb.append(curWord);
							sb.append("\n");
							indent--;
							addIndent(indent);
							break;
						}
					}
				}
			} else if ("=".equals(upper) && ">".equals(nextWord)) {
				sb.append(curWord);
			} else if ((!nextWord.matches("%|(\\(.+\\))|;|,|\\(|\\)|\\.")) && (!upper.matches("%|\\(|\\.|:"))) {
				sb.append(curWord);
				sb.append(" ");
			} else {
				sb.append(curWord);
				if ("(".equals(nextWord)) {
					for (String s : ISQLFormatRule.FUNCTION) {
						if (s.equals(upper)) {
							isFunction = true;
							break;
						} else {
							isUserFunction = true;
							userFunctionLength = curWord.length();
						}
					}
				}
			}
		}
		int start = 0;
		start = sb.indexOf("$", start);
		int end = sb.indexOf("}", start);
		while (start != -1) {
			for (int i = start; i < end; i++) {
				if (sb.charAt(i) == ' ') {
					sb.deleteCharAt(i);
					end--;
				}
			}
			start++;
			start = sb.indexOf("$", start);
			end = sb.indexOf("}", start);
		}
		return sb.toString().trim();
	}

}
