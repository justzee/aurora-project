package test;

import sqlj.exception.ParserException;
import sqlj.parser.ParameterParser;
import sqlj.parser.ParsedSql;

public class TestParameterParser {
	public static void main(String[] args) throws ParserException {
		ParameterParser parser = new ParameterParser(
				"select a.name into ${@ out } name from ${!\na}\n where user_name like ${ pattern};")  ;
		ParsedSql ps = parser.parse();
		System.out.println(ps);
		ps.printParas();
	}
}
