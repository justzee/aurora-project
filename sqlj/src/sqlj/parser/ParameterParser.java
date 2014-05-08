package sqlj.parser;

import sqlj.exception.ParserException;
import sqlj.util.CharStack;

public class ParameterParser extends Parser {
	public static final String FLAG = "${";

	public ParameterParser(String source) {
		super(source);
	}

	public void setSource(String source) {
		this.source = source;
		this.len = source.length();
	}

	public ParsedSql parse() throws ParserException {
		ParsedSql ps = new ParsedSql();
		StringBuilder sb = new StringBuilder(len);
		int index = -1;
		int lastFlagIndex = -1;
		CharStack cs = new CharStack();
		while ((index = source.indexOf(FLAG, lastFlagIndex)) != -1) {
			sb.append(source.substring(lastFlagIndex + 1, index));
			int i = index + FLAG.length();
			char c = source.charAt(i);
			if (c == '!' || c == '@') {
				if (c == '!') {
					ps.addFragment(sb.toString());
					sb.delete(0, sb.length());
				} else
					sb.append('?');
				cs.clear();
				cs.push('{');
				int i_ = findMatch(cs, source, i + 1);
				Parameter p = new Parameter(c == '@' ? Parameter.OUT
						: Parameter.NONE, source.substring(i + 1, i_).trim());
				ps.addPara(p);
				lastFlagIndex = i_;
			} else {
				sb.append('?');
				cs.clear();
				cs.push('{');
				int i_ = findMatch(cs, source, i);
				Parameter p = new Parameter(Parameter.IN, source.substring(i,
						i_).trim());
				ps.addPara(p);
				lastFlagIndex = i_;
			}
		}
		sb.append(source.substring(lastFlagIndex + 1));
		ps.addFragment(sb.toString());
		return ps;
	}
}
