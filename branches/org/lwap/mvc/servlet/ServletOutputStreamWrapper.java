/*
 * SerlvetOutputStreamWrapper.java
 *
 * Created on 2002年1月13日, 下午4:02
 */

package org.lwap.mvc.servlet;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletOutputStream;

/**
 *
 * @author  Administrator
 * @version 
 */
public class ServletOutputStreamWrapper extends ServletOutputStream {
    
    OutputStream stream;

    /** Creates new SerlvetOutputStreamWrapper */
    public ServletOutputStreamWrapper(OutputStream strm) {
        stream = strm;
    }

    public void write(int param) throws java.io.IOException {
        stream.write(param);
    }
    
    public void write(byte[] b) throws IOException {
        stream.write(b);
    }
    
    public void write( byte[] b, int off, int len) throws IOException{
        stream.write(b,off,len);
    }
    
    public void flush() throws IOException {
        stream.flush();
    }
    
    public void close() throws IOException {
        stream.close();
    }
    
}
