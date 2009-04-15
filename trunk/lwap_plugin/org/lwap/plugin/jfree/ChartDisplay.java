/*
 * Created on 2009-3-24
 */
package org.lwap.plugin.jfree;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.jfree.chart.servlet.ChartDeleter;
import org.jfree.chart.servlet.DisplayChart;
import org.jfree.chart.servlet.ServletUtilities;

public class ChartDisplay extends DisplayChart {


    public void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(true);
        String filename = request.getParameter("filename");
        if(filename == null)
            throw new ServletException("Parameter 'filename' must be supplied");
        filename = ServletUtilities.searchReplace(filename, "..", "");
        File file = new File(System.getProperty("java.io.tmpdir"), filename);
        if(!file.exists())
            throw new ServletException("File '" + file.getAbsolutePath() + "' does not exist");
        boolean isChartInUserList = true;
        ChartDeleter chartDeleter = (ChartDeleter)session.getAttribute("JFreeChart_Deleter");
        if(chartDeleter != null)
            isChartInUserList = chartDeleter.isChartAvailable(filename);
        boolean isChartPublic = false;
        if(filename.length() >= 6 && filename.substring(0, 6).equals("public"))
            isChartPublic = true;
        if(isChartInUserList || isChartPublic)
            ServletUtilities.sendTempFile(file, response);
        else
            throw new ServletException("Chart image not found");
    } 
    

}
