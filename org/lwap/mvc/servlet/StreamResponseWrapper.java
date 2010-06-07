/*
 * StreamResponseWrapper.java
 *
 * Created on 2002年1月13日, 下午3:59
 */

package org.lwap.mvc.servlet;

import java.io.OutputStream;
import java.io.PrintWriter;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

/**
 *
 * @author  Administrator
 * @version 
 */
public class StreamResponseWrapper extends HttpServletResponseWrapper {
    
    ServletOutputStreamWrapper wrapper;

    /** Creates new StreamResponseWrapper */
    public StreamResponseWrapper(HttpServletResponse response ,OutputStream strm) {
        super( response);
        wrapper = new ServletOutputStreamWrapper(strm);
    }
    
    public ServletOutputStream getOutputStream() throws java.io.IOException{
        return wrapper;
    }
    
    public java.io.PrintWriter getWriter() throws java.io.IOException{
        return new PrintWriter(wrapper);
    }
   
  

}
