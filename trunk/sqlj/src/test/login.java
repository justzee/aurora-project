package test;
import java.sql.*;

import sqlj.core.*;
import sqlj.exception.*;

import java.util.Map;

import aurora.application.util.MD5Util;
import aurora.service.ServiceContext;
import uncertain.composite.CompositeMap;

public class login extends Procedure {
	
	private ServiceContext svcCtx ;
	
	public void execute(IContextService contextServ) throws Exception {
		svcCtx = (ServiceContext)contextServ.getContext();
//		svcCtx.getInstanceOfType(type)
		login("mn1194","111111","ZHS","127.0.0.1");
	}

	private void login(String p_user_name, String p_password, String p_language,
			String p_ip) throws Exception {
		PreparedStatement __sqlj_ps_gen6 = getConnection()
						.prepareStatement(
								"select * from sys_user where user_name = upper(?)");
				__sqlj_ps_gen6.setString(1, p_user_name);
				__sqlj_ps_gen6.execute();
				UPDATE_COUNT = __sqlj_ps_gen6.getUpdateCount();
				ResultSet __sqlj_rs_gen0 = __sqlj_ps_gen6.getResultSet();
		SysUser sys_user=DataTransfer.transfer1(SysUser.class,
			__sqlj_rs_gen0);
		PreparedStatement __sqlj_ps_gen7 = getConnection()
				.prepareStatement(
						"select t.nls_language\r\n\t\t        from sys_languages t, fnd_language_code f\r\n\t\t       where t.language_code = f.language_code\r\n\t\t         and t.language_code = ?\r\n\t\t         and f.installed_flag = 'Y'");
		__sqlj_ps_gen7.setString(1, p_language);
		__sqlj_ps_gen7.execute();
		UPDATE_COUNT = __sqlj_ps_gen7.getUpdateCount();
		ResultSet __sqlj_rs_gen1 = __sqlj_ps_gen7.getResultSet();
		Map nls_lang=DataTransfer.transfer1(Map.class,
			__sqlj_rs_gen1);
	    StringBuilder __sqlj_sql_gen9 = new StringBuilder();
		__sqlj_sql_gen9.append("alter session set nls_language='");
		__sqlj_sql_gen9.append(nls_lang.get("nls_language"));
		__sqlj_sql_gen9.append("'");
		PreparedStatement __sqlj_ps_gen8 = getConnection().prepareStatement(
				__sqlj_sql_gen9.toString());
		__sqlj_ps_gen8.execute();
		UPDATE_COUNT = __sqlj_ps_gen8.getUpdateCount();
		ResultSet __sqlj_rs_gen2 = __sqlj_ps_gen8.getResultSet();
		;
	    validate_user(sys_user);
	    validate_role(sys_user);
	    validate_frozen_flag(sys_user);
	    validate_password(sys_user,p_password);
		int id;
		CallableStatement __sqlj_ps_gen10 = getConnection()
				.prepareCall(
						"begin select user_id into ? from sys_user where user_name='MN1194';end;");
		__sqlj_ps_gen10.registerOutParameter(1, Types.DECIMAL);
		__sqlj_ps_gen10.execute();
		UPDATE_COUNT = __sqlj_ps_gen10.getUpdateCount();
		id = __sqlj_ps_gen10.getInt(1);
		ResultSet __sqlj_rs_gen3 = __sqlj_ps_gen10.getResultSet();
		;
		svcCtx.getParameter().put("id", id);
	}
	
	private void validate_user(SysUser u) throws Exception{
		if(u.start_date.getTime()>System.currentTimeMillis())
			throw UserDefinedException.create("e_user_failure");
		if(u.end_date!=null&&u.end_date.getTime()<System.currentTimeMillis())
			throw UserDefinedException.create("e_user_failure");
	}
	
	private void validate_role(SysUser u) throws Exception{
		PreparedStatement __sqlj_ps_gen11 = getConnection().prepareStatement(
				"select count(1) count from fnd_companies");
		__sqlj_ps_gen11.execute();
		UPDATE_COUNT = __sqlj_ps_gen11.getUpdateCount();
		ResultSet __sqlj_rs_gen4 = __sqlj_ps_gen11.getResultSet();
		Map cc=DataTransfer.transfer1(Map.class,__sqlj_rs_gen4);
		long count = DataTransfer.castLong(cc.get("count"));
		if(count == 0)
			return;
		try {
			PreparedStatement __sqlj_ps_gen12 = getConnection()
					.prepareStatement(
							"select 1\r\n\t\t        from dual\r\n\t\t       where exists\r\n\t\t         (select *\r\n\t                from sys_user_role_groups g\r\n\t               where g.user_id = ?\r\n\t                 and g.start_date <= trunc(sysdate)\r\n\t                 and (g.end_date >= trunc(sysdate) or g.end_date is null))\r\n\t\t\t");
			__sqlj_ps_gen12.setObject(1, u.user_id);
			__sqlj_ps_gen12.execute();
			UPDATE_COUNT = __sqlj_ps_gen12.getUpdateCount();
			ResultSet __sqlj_rs_gen5 = __sqlj_ps_gen12.getResultSet();
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
}