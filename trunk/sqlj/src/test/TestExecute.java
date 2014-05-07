package test;

import sqlj.core.IContextService;

public class TestExecute {
	public static void main(String[] args) throws Exception {
		Object aaa = Class.forName("test.login").newInstance();
		aaa.getClass().getMethod("execute",new Class[] {IContextService.class}).invoke(aaa,new Object[] {null});
	}
}
