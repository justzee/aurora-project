/*
 * Created on 2010-1-7 下午02:23:24
 * Author: Zhou Fan
 */
package org.lwap.mvc.chart;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.jfree.chart.servlet.ChartDeleter;
import org.jfree.chart.servlet.ServletUtilities;

public class DisplayChartServlet extends HttpServlet {

    public void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        String filename = request.getParameter("filename");
        if (filename == null)
            throw new ServletException("Parameter 'filename' must be supplied");
        filename = ServletUtilities.searchReplace(filename, "..", "");
        File file = new File(System.getProperty("java.io.tmpdir"), filename);
        if (!file.exists())
            throw new ServletException("File '" + file.getAbsolutePath()
                    + "' does not exist");
        boolean isChartInUserList = false;
        if(session!=null){
            ChartDeleter chartDeleter = (ChartDeleter) session
                    .getAttribute("JFreeChart_Deleter");
            if (chartDeleter != null)
                isChartInUserList = chartDeleter.isChartAvailable(filename);
        }
        boolean isChartPublic = false;
        if (filename.length() >= 6 && filename.substring(0, 6).equals("public"))
            isChartPublic = true;
        ServletUtilities.sendTempFile(file, response);
        /*
        if (isChartInUserList || isChartPublic ) {
            ServletUtilities.sendTempFile(file, response);
            
        } else {
            throw new ServletException("Chart image not found");
        }
        */
    }

}
