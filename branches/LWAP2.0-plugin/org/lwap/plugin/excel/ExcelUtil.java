package org.lwap.plugin.excel;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import javax.servlet.http.HttpServletResponse;

public class ExcelUtil {
	public static void printOutExcel(File file, HttpServletResponse response)
			throws Exception {
		if (file != null && response != null) {
			OutputStream os = null;
			FileInputStream is = null;
			ReadableByteChannel rbc = null;
			WritableByteChannel wbc = null;
			int Buffer_size = 500 * 1024;
			String filetype = (file.getName()).substring(file.getName()
					.lastIndexOf("."));
			String mimetype = null;
			if (".xls".equalsIgnoreCase(filetype)) {
				mimetype = "application/vnd.ms-excel";
			} else if (".xlsx".equalsIgnoreCase(filetype)) {
				mimetype = "application/vnd.openxmlformats-officedocument.spreadsheetml.worksheet+xml";
			} else {
				return;
			}
			response.setContentType(mimetype);
			response.setContentLength(new Long(file.length()).intValue());
			response.addHeader("Content-Disposition", "attachment; filename="
					+ toUtf8String(file.getName()));
			try {
				os = response.getOutputStream();
				is = new FileInputStream(file);
				rbc = Channels.newChannel(is);
				wbc = Channels.newChannel(os);
				ByteBuffer buf = ByteBuffer.allocate(Buffer_size);
				while ((rbc.read(buf)) > 0) {
					buf.position(0);
					wbc.write(buf);
					buf.clear();
					os.flush();
				}
			} finally {
				if (rbc != null){
					try {
						rbc.close();
					} catch (Exception e) {
						throw new Exception(e);
					}
				}
				if (is != null){
					try {
						is.close();
					} catch (Exception e) {
						throw new Exception(e);
					}
				}
				if (os != null){
					try {
						os.close();
					} catch (Exception e) {
						throw new Exception(e);
					}
				}
			}
		}
	}

	public static String toUtf8String(String s) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if ((c >= 0) && (c <= 255)) {
				sb.append(c);
			} else {
				byte[] b;
				try {
					b = Character.toString(c).getBytes("utf-8");
				} catch (Exception ex) {
					System.out.println(ex);
					b = new byte[0];
				}
				for (int j = 0; j < b.length; j++) {
					int k = b[j];
					if (k < 0) {
						k += 256;
					}
					sb.append("%" + Integer.toHexString(k).toUpperCase());
				}
			}
		}
		return sb.toString();
	}
}
