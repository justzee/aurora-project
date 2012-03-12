package aurora.application.features;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.Enumeration;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.lwap.action.JspForward;

public class HttpForward extends HttpServlet {
	public static String KEY_ADDRESS = "address";

	protected void service(HttpServletRequest request,
			HttpServletResponse response) {
		try {
			writeResponse(response, getHttpUrl(request));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public String getHttpUrl(HttpServletRequest request) {
		String address = super.getInitParameter(KEY_ADDRESS), paramName;
		String[] paramValues;
		StringBuffer params = new StringBuffer();
		int i, length;
		boolean isFirst = true;
		Enumeration enumn = request.getParameterNames();
		while (enumn.hasMoreElements()) {
			paramName = (String) enumn.nextElement();
			paramValues = request.getParameterValues(paramName);
			for (i = 0, length = paramValues.length; i < length; i++) {
				if (isFirst) {
					params.append("?");
					isFirst = false;
				} else {
					params.append("&");
				}
				params.append(paramName);
				params.append("=");
				params.append(paramValues[i].replace(' ', '+'));
			}
		}
		String paramAttri = (String) request
				.getAttribute(JspForward.KEY_PARAM_ATTRI);
		if (paramAttri != null && !"".equals(paramAttri)) {
			if (isFirst) {
				params.append("?");
				isFirst = false;
			} else {
				params.append("&");
			}
			params.append(paramAttri.replace(' ', '+'));
		}
		System.out
				.println("===============" + params + "=====================");
		return address + params;
	}

	// public void writeResponse(HttpServletResponse httpResponse, String url)
	// throws Exception{
	//
	// // httpClient4.1.2
	// DefaultHttpClient httpclient = new DefaultHttpClient();
	// OutputStream os = null;
	// InputStream is = null;
	// try {
	// HttpGet httpget = new HttpGet(url);
	// HttpResponse response = httpclient.execute(httpget);
	// HttpEntity entity = response.getEntity();
	// if (entity != null) {
	// os = httpResponse.getOutputStream();
	// is = entity.getContent();
	// int Buffer_size = 50 * 1024;
	// byte buf[] = new byte[Buffer_size];
	// int len;
	// while ((len = is.read(buf)) > 0)
	// os.write(buf, 0, len);
	// }
	// } finally {
	// if (is != null) {
	// try {
	// is.close();
	// } catch (Exception ignore) {
	// }
	// }
	//
	// if (os != null) {
	// try {
	// os.close();
	// } catch (Exception ignore) {
	// }
	// }
	// httpclient.getConnectionManager().shutdown();
	// }
	// }

	public void writeResponse(HttpServletResponse response, String url)
			throws Exception {
		OutputStream os = null;
		InputStream is = null;
		HttpURLConnection connection = null;
		int Buffer_size = 50 * 1024;
		try {
			URL postUrl = new URL(url);
			connection = (HttpURLConnection) postUrl.openConnection();
			connection.setReadTimeout(0);
			connection.connect();
			response.setContentType(connection.getContentType());
			response.setContentLength(connection.getContentLength());
			response.addHeader("Content-Disposition",
					connection.getHeaderField("Content-Disposition"));
			os = response.getOutputStream();
			try {
				is = connection.getInputStream();
			} catch (Exception e) {
				is = connection.getErrorStream();
			}
			byte buf[] = new byte[Buffer_size];
			int len;
			while ((len = is.read(buf)) > 0)
				os.write(buf, 0, len);
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			if (os != null) {
				try {
					os.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			if (connection != null) {
				connection.disconnect();
			}
		}
	}
}
