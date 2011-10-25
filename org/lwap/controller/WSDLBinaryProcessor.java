package org.lwap.controller;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;

import javax.servlet.http.HttpServletResponse;

import org.lwap.database.IResultSetProcessor;
import org.lwap.database.datatype.DataTypeManager;
import org.lwap.database.datatype.DatabaseTypeField;

import aurora.database.IResultSetConsumer;
import uncertain.composite.CompositeMap;

public class WSDLBinaryProcessor implements IResultSetConsumer,
		IResultSetProcessor {
	MainService service;
	String path;

	public WSDLBinaryProcessor(CompositeMap context_map, String path) {
		this.service = MainService.getServiceInstance(context_map);
		this.path = path;
	}

	public void processResultSet(ResultSet rs) {
		HttpServletResponse response = service.getResponse();
		response.setContentType("text/xml;charset=utf-8");
		response.setHeader("Cache-Control", "no-cache, must-revalidate");
		CompositeMap result = null;
		PrintWriter out = null;
		try {
			out = response.getWriter();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		out.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		out.write("<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">");
		out.write("<soap:Body>");
		try {
			writeContent(rs, out);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}finally{
			out.write("</soap:Body>");
			out.write("</soap:Envelope>");
			try {
				rs.close();
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
			out.close();
		}		
	}

	void writeContent(ResultSet rs, PrintWriter out) throws Exception {
		out.println("<" + path + ">");
		ResultSetMetaData rs_meta;
		Object value;
		while (rs.next()) {
			out.println("<record>");
			rs_meta = rs.getMetaData();
			for (int i = 1, l = rs_meta.getColumnCount(); i <= l; i++) {
				int columnType = rs_meta.getColumnType(i);
				DatabaseTypeField fld = DataTypeManager.getType(columnType);
				String column = rs_meta.getColumnName(i).toUpperCase();
				out.println("<" + column + ">");
				value = fld.getObject(rs, i);
				switch (columnType) {
				case Types.BLOB:
					if (value != null) {
						out.println(ToBase64((Blob) value));
					} else {
						out.println();
					}
					break;
				default:
					out.println(value==null?"":value);
					break;
				}				
				out.println("</" + column + ">");
			}
			out.println("</record>");
			out.flush();
		}
		out.println("</" + path + ">");
	}

	String ToBase64(Blob value) throws Exception {
		String strBase64 = null;
		ReadableByteChannel rbc = null;
		WritableByteChannel wbc = null;
		InputStream in = null;
		OutputStream baos =null;
		// in.available()返回文件的字节长度
		try {
			baos = new ByteArrayOutputStream();
			in = value.getBinaryStream();
			rbc = Channels.newChannel(in);
			wbc = Channels.newChannel(baos);
			
			ByteBuffer buf = ByteBuffer.allocate(1000);
			int size = -1;
			while ((size = rbc.read(buf)) > 0) {
				buf.position(0);
				wbc.write(buf);
				buf.clear();
				baos.flush();
			}
			byte[] bytes = ((ByteArrayOutputStream)baos).toByteArray();		
			strBase64 = Base64.encodeBase64String(bytes); // 将字节流数组转换为字符串			
		} catch (IOException e) {
			throw e;
		} finally {
			try{
				if (in != null)
					in.close();		
			}catch(Exception e){
				
			}
			try{
				if(baos!=null)
					baos.close();	
			}catch(Exception e){
				
			}
			try{
				if(rbc!=null)
					rbc.close();	
			}catch(Exception e){
				
			}	
			try{
				if(wbc!=null)
					wbc.close();	
			}catch(Exception e){
				
			}
		}
		return strBase64;
	}

	public void begin(String root_name) {
		// TODO Auto-generated method stub

	}

	public void newRow(String row_name) {
		// TODO Auto-generated method stub

	}

	public void loadField(String name, Object value) {
		// TODO Auto-generated method stub

	}

	public void endRow() {
		// TODO Auto-generated method stub

	}

	public void end() {
		// TODO Auto-generated method stub

	}

	public void setRecordCount(long count) {
		// TODO Auto-generated method stub

	}

	public Object getResult() {
		// TODO Auto-generated method stub
		return null;
	}
}
