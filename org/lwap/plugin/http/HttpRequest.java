package org.lwap.plugin.http;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;

import uncertain.composite.CompositeLoader;
import uncertain.composite.CompositeMap;
import uncertain.composite.TextParser;
import uncertain.proc.AbstractEntry;
import uncertain.proc.ProcedureRunner;

public class HttpRequest extends AbstractEntry {
	String url;
	String method;
	String host;
	String agreement;
	String returnpath;
	String returnasmap;
	String flag;

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	Parameter[] parameters;
	Head[] heads;

	int port;

	public String getAgreement() {
		return agreement;
	}

	public void setAgreement(String agreement) {
		this.agreement = agreement;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public Head[] getHeads() {
		return heads;
	}

	public void setHeads(Head[] heads) {
		this.heads = heads;
	}

	public Parameter[] getParameters() {
		return parameters;
	}

	public void setParameters(Parameter[] parameters) {
		this.parameters = parameters;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getReturnpath() {
		return returnpath;
	}

	public void setReturnpath(String returnpath) {
		this.returnpath = returnpath;
	}

	public String getReturnasmap() {
		return returnasmap;
	}

	public void setReturnasmap(String returnasmap) {
		this.returnasmap = returnasmap;
	}

	public void run(ProcedureRunner runner) throws Exception {
		HttpClient httpClient = new HttpClient();
		String host = getHost();
		int port = getPort();
		String url = getUrl();
		String agreement = getAgreement();
		httpClient.getHostConfiguration().setHost(host, port, agreement);
		CompositeMap model = runner.getContext().getChild("model");
		String sentflag = TextParser.parse(getFlag(), model);
		if (sentflag.equals("Y")) {

			HttpMethod method = postMethod(url, getParameters(), model);
			httpClient.executeMethod(method);
			String response = method.getResponseBodyAsString();
			CompositeMap returnlist = new CompositeMap(getReturnpath());
			System.out.println(response);
			try {
				CompositeLoader cl = new CompositeLoader();
				CompositeMap cm = cl.loadFromString(response, "GBK");
				
				returnlist.copy(cm);
				
			} catch (Exception e) {
				CompositeMap error = new CompositeMap("error");
				error.put("VALUE", response);
				returnlist.addChild(error);
			}
			model.addChild(returnlist);
			xmlformattrac(returnlist);
			 System.out.println(model.toXML());
			 System.out.println(TextParser.parse("${/model/returnlist/error/@VALUE}",
			 model ));
		}
	}

	private static HttpMethod postMethod(String url, Parameter[] parameters,
			CompositeMap map) throws IOException {
		PostMethod post = new UTF8PostMethod(url);

		NameValuePair[] param = new NameValuePair[parameters.length];
		for (int i = 0, l = parameters.length; i < l; i++) {
			NameValuePair nv = new NameValuePair(parameters[i].getName(),
					TextParser.parse(parameters[i].getValue(), map));
			param[i] = nv;
		}
		post.setRequestBody(param);
		post.releaseConnection();
		return post;

	}

	public static class UTF8PostMethod extends PostMethod {
		public UTF8PostMethod(String url) {
			super(url);
		}

		@Override
		public String getRequestCharSet() {
			// return super.getRequestCharSet();
			return "UTF-8";
		}
	}

	public static void xmlformattrac(CompositeMap map) {
		Iterator it = map.getChildIterator();
		while (it.hasNext()) {
			CompositeMap ct = (CompositeMap) it.next();
			if (ct.getText() != null)
				ct.put("VALUE", ct.getText());
		}
	}

}
