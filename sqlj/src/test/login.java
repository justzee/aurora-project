package test;
import java.sql.*;

import sqlj.core.*;
import sqlj.exception.*;

import java.util.Map;

import aurora.application.util.MD5Util;
import aurora.service.ServiceContext;
import uncertain.composite.CompositeMap;

public class login implements sqlj.core.IProcedure {
	
	private ServiceContext svcCtx ;
	
	public void execute() throws Exception {
		svcCtx = (ServiceContext)getContext();
//		svcCtx.getInstanceOfType(type)
		login("mn1194","11111","ZHS","127.0.0.1");
	}

	private void login(String p_user_name, String p_password, String p_language,
			String p_ip) throws Exception {
		PreparedStatement __sqlj_ps_gen9 = getConnection()
						.prepareStatement(
								"select * from sys_user where user_name = upper(?)");
				__sqlj_ps_gen9.setString(1, p_user_name);
				__sqlj_ps_gen9.execute();
				ResultSet __sqlj_rs_gen0 = __sqlj_ps_gen9.getResultSet();
				__sqlj_rs_list_gen6.add(__sqlj_rs_gen0);
		SysUser sys_user=DataTransfer.transfer1(SysUser.class, __sqlj_rs_gen0);
		PreparedStatement __sqlj_ps_gen10 = getConnection()
				.prepareStatement(
						"select t.nls_language\r\n\t\t        from sys_languages t, fnd_language_code f\r\n\t\t       where t.language_code = f.language_code\r\n\t\t         and t.language_code = ?\r\n\t\t         and f.installed_flag = 'Y'");
		__sqlj_ps_gen10.setString(1, p_language);
		__sqlj_ps_gen10.execute();
		ResultSet __sqlj_rs_gen1 = __sqlj_ps_gen10.getResultSet();
		__sqlj_rs_list_gen6.add(__sqlj_rs_gen1);
		String nls_lang=DataTransfer.transfer1(String.class, __sqlj_rs_gen1);
	    StringBuilder __sqlj_sql_gen12 = new StringBuilder();
		__sqlj_sql_gen12.append("alter session set nls_language='");
		__sqlj_sql_gen12.append(nls_lang);
		__sqlj_sql_gen12.append("'");
		PreparedStatement __sqlj_ps_gen11 = getConnection().prepareStatement(
				__sqlj_sql_gen12.toString());
		__sqlj_ps_gen11.execute();
		ResultSet __sqlj_rs_gen2 = __sqlj_ps_gen11.getResultSet();
		__sqlj_rs_list_gen6.add(__sqlj_rs_gen2);
		validate_user(sys_user);
	    validate_role(sys_user);
	    validate_frozen_flag(sys_user);
	    validate_password(sys_user,p_password);
		PreparedStatement __sqlj_ps_gen13 = getConnection().prepareStatement(
				"select user_id from sys_user where user_name='MN1194'");
		__sqlj_ps_gen13.execute();
		ResultSet __sqlj_rs_gen3 = __sqlj_ps_gen13.getResultSet();
		__sqlj_rs_list_gen6.add(__sqlj_rs_gen3);
		int id=DataTransfer.transfer1(int.class,
		__sqlj_rs_gen3);
		svcCtx.getParameter().put("id", id);
	}
	
	private void validate_user(SysUser u) throws Exception{
		if(u.start_date.getTime()>System.currentTimeMillis())
			throw UserDefinedException.create("e_user_failure");
		if(u.end_date!=null&&u.end_date.getTime()<System.currentTimeMillis())
			throw UserDefinedException.create("e_user_failure");
	}
	
	private void validate_role(SysUser u) throws Exception{
		PreparedStatement __sqlj_ps_gen14 = getConnection().prepareStatement(
				"select count(1) count from fnd_companies");
		__sqlj_ps_gen14.execute();
		ResultSet __sqlj_rs_gen4 = __sqlj_ps_gen14.getResultSet();
		__sqlj_rs_list_gen6.add(__sqlj_rs_gen4);
		int count=DataTransfer.transfer1(int.class, __sqlj_rs_gen4);
		if(count == 0)
			return;
		try {
			PreparedStatement __sqlj_ps_gen15 = getConnection()
					.prepareStatement(
							"select 1\r\n\t\t        from dual\r\n\t\t       where exists\r\n\t\t         (select *\r\n\t                from sys_user_role_groups g\r\n\t               where g.user_id = ?\r\n\t                 and g.start_date <= trunc(sysdate)\r\n\t                 and (g.end_date >= trunc(sysdate) or g.end_date is null))\r\n\t\t\t");
			__sqlj_ps_gen15.setObject(1, u.user_id);
			__sqlj_ps_gen15.execute();
			ResultSet __sqlj_rs_gen5 = __sqlj_ps_gen15.getResultSet();
			__sqlj_rs_list_gen6.add(__sqlj_rs_gen5);
			//just for test no_data_found ,so Object.class is ok
			DataTransfer.transfer1(Object.class,
			__sqlj_rs_gen5);
		}catch(NoDataFoundException e) {
			throw UserDefinedException.create("e_role_null");
		} catch (Exception e1) {
			throw e1;
		}
	}
	
	private void validate_frozen_flag(SysUser u) throws Exception{
		if("Y".equals(u.frozen_flag))
			throw UserDefinedException.create("e_frozen_failure");
	}
	
	private void validate_password(SysUser u,String p_password) throws Exception{
		if(p_password==null)
			throw UserDefinedException.create("e_password_null");
		if(!MD5Util.md5Hex(p_password).equals(u.encrypted_user_password))
			throw UserDefinedException.create("e_password_failure");
		// sys_user_pkg.password_rule_check..
	}
	
	public static class SysUser{
		public long user_id;
		public String user_name;
		public Date start_date;
		public Date end_date;
		public String frozen_flag;
		public String encrypted_user_password;
	}

	java.util.ArrayList<ResultSet> __sqlj_rs_list_gen6 = new java.util.ArrayList<ResultSet>();
	java.sql.Connection __sqlj_conn_gen7;
	java.lang.Object __sqlj_ctx_gen8;

	public java.lang.Object getContext() {
		return __sqlj_ctx_gen8;
	}

	public java.sql.Connection getConnection() {
		return __sqlj_conn_gen7;
	}

	public void cleanUp(){for (ResultSet rs:__sqlj_rs_list_gen6){try {if (rs != null)rs.close();} catch (Exception e){}}}

	public void setConnection(java.sql.Connection args0) {
		__sqlj_conn_gen7 = args0;
	}

	public void setContext(java.lang.Object args0) {
		__sqlj_ctx_gen8 = args0;
	}
}