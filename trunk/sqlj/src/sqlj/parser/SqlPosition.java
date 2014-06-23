package sqlj.parser;

import sqlj.core.ParsedSource;
import sqlj.core.SqljBlock;

public abstract class SqlPosition {
	public static final String METHOD_SQL_EXECUTE = "__sqlj_execute";
	public static final String METHOD_SQL_EXECUTE_FOR = "__sqlj_execute_for";
	public static final String METHOD_SQL_EXECUTE_PARA = "__sqlj_execute_para";
	protected SqljBlock sqljBlock;
	protected String source;

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public void setSqljBlock(SqljBlock sqljb) {
		this.sqljBlock = sqljb;
	}

	public SqljBlock getSqljBlock() {
		return sqljBlock;
	}

	protected abstract int getAbsoluteStart();

	public int translate(int preIdx, ParsedSource session) throws Exception {
		StringBuilder out = session.getBuffer();
		out.append(source.substring(preIdx, getAbsoluteStart()));
		//		out.write(sqljBlock.toJavaSource(session));
		out.append(String.format("%s(%d,%s)", METHOD_SQL_EXECUTE,
				sqljBlock.getId(), session.genId("rs")));
		return translate2(preIdx, session);
	}

	protected int translate2(int preIdx, ParsedSource session)
			throws Exception {
		return 0;
	};

}
