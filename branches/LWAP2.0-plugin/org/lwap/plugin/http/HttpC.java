package org.lwap.plugin.http;

import java.io.IOException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;


public class HttpC {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		HttpClient httpClient = new HttpClient();
		String host = "";
		String url = "/";
		httpClient.getHostConfiguration().setHost(host, 80, "http");
		HttpMethod method = postMethod(url);
		http://sdk2.entinfo.cn/z_send.aspx?sn=SDK-XXX-010-XXXXX&pwd=888888&mobile=135****4752&content=ÄãºÃ
		httpClient.executeMethod(method);   
		String response = method.getResponseBodyAsString();
		byte[] dataResponseBody =response.getBytes("utf-8");
//		byte[]   dataResponseBody   =   method.getResponseBody(); 
		String   result   =   new   String(dataResponseBody,"gb2312"); 


		 System.out.println(result);  
		 System.out.println(response); 

	} 

	private static HttpMethod postMethod(String url) throws IOException {
		PostMethod post = new PostMethod(url);
//		post.setRequestHeader("Content-type",
//				" application/xml; charset=UTF-8");
		String pass = MD5.getMD5("368059".getBytes());
		NameValuePair[] param = { new NameValuePair("sn", "SDK-SWW-010-00074"),
				new NameValuePair("pwd", pass)  ,
				new NameValuePair("mobile","13524715731"),
				new NameValuePair("content","ÄãºÃ")
				
				};
		post.setRequestBody(param);
		post.releaseConnection();
		return post;
	}
	
}
