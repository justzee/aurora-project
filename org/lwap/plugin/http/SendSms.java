package org.lwap.plugin.http;

import uncertain.composite.CompositeMap;
import uncertain.proc.AbstractEntry;
import uncertain.proc.ProcedureRunner;

public class SendSms extends AbstractEntry {
	private String sn;
	private String pwd;
	private String serviceurl;
	private String mobile;
	private String content;
	private String sendflag;

	public String getSn() {
		return sn;
	}

	public void setSn(String sn) {
		this.sn = sn;
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	public String getServiceurl() {
		return serviceurl;
	}

	public void setServiceurl(String serviceurl) {
		this.serviceurl = serviceurl;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public void run(ProcedureRunner runner) throws Exception {
		CompositeMap context = runner.getContext();
		System.out.println(context.toXML());
		String sn = context.getObject(this.getSn()).toString();
		String pwd = context.getObject(this.getPwd()).toString();
		String serviceUrl = context.getObject(this.getServiceurl()).toString();
		String mobile = context.getObject(this.getMobile()).toString();
		String content = context.getObject(this.getContent()).toString();
		String sendflag = context.getObject(this.getSendflag()).toString();
		System.out.println(sn);
		System.out.println(pwd);
		System.out.println(serviceUrl);
		System.out.println(mobile);
		System.out.println(content);
		System.out.println(sendflag);
		if (sendflag.equals("Y")) {
			Client client = new Client(sn, pwd,serviceUrl);
			if (HaveChinese.hasChinese(content)){
				String rrid = "";
				String result_mt = client.mt(mobile, content, "", "", rrid);
				System.out.println("this is number" + rrid);
			}else 
			if (content.length() <= 140) {
				String rrid = "";
				String result_mt = client.mt(mobile, content, "", "", rrid);
				System.out.println("this is number" + rrid);
			} else {
				for (int i = 0, l = content.length() / 135 + 1; i < l; i++) {
				    int end =(i + 1) * 135>content.length()?content.length():(i + 1) * 135; 
				  
					String scon = content.substring(i * 135, end);
					scon="("+(i+1)+"/"+l+")"+scon;
					
					String rrid = "";
					String result_mt = client.mt(mobile, scon, "", "", rrid);
					System.out.println("this is number" + rrid);
					System.out.println("---------------");
					System.out.println(scon);
				}
			}
		} else {
			System.out.println("no send");
		}

	}

	public String getSendflag() {
		return sendflag;
	}

	public void setSendflag(String sendflag) {
		this.sendflag = sendflag;
	}

}
