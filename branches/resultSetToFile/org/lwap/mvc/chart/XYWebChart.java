package org.lwap.mvc.chart;

import java.io.PrintWriter;
import java.util.Iterator;

import javax.servlet.http.HttpSession;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.entity.StandardEntityCollection;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.servlet.ServletUtilities;
import org.jfree.chart.urls.StandardXYURLGenerator;
import org.jfree.data.XYSeries;
import org.jfree.data.XYSeriesCollection;

import uncertain.composite.CompositeMap;


/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author cloudscape
 * @version 1.0
 */

public class XYWebChart extends AbstractWebChart {

  /* implements function  */
   public String generateChart(CompositeMap model, String url ,HttpSession session, PrintWriter pw) {
     String fileName = null;
     CompositeMap child = null;
     CompositeMap grandChild = null;
     boolean is_legend = true;
     boolean is_url = false;
     if (yTitle == null) yTitle = "YTITLE";
     XYSeriesCollection dataset = new XYSeriesCollection();
     if (url != null) is_url = true;

     // collect data
     try {
         Iterator i = model.getChildIterator();
         Iterator i1 = model.getChildIterator();
         child = (CompositeMap) i.next();
         Iterator ichild = child.getChildIterator();
         if (ichild == null) {
             dataset.addSeries(getSeries(model,yTitle,param1,data));
         }else{
             while (i1.hasNext()){
                 child = (CompositeMap) i1.next();
                 dataset.addSeries(getSeries(child,child.getString("NAME"),param1,data));
             }
         }


     // generate chart
       JFreeChart chart = ChartFactory.createXYLineChart(title,xTitle,yTitle,dataset,PlotOrientation.VERTICAL,is_legend,true,is_url);
       XYPlot plot = (XYPlot)chart.getPlot();
       if (is_url)
           plot.getRenderer().setURLGenerator(new StandardXYURLGenerator(
           url, SQL_XPARAMETER, SQL_YPARAMETER));

       //  Write the chart image to the temporary directory
       ChartRenderingInfo info = new ChartRenderingInfo(new
           StandardEntityCollection());
       fileName = ServletUtilities.saveChartAsPNG(chart, length, width, info, session);

       //  Write the image map to the PrintWriter
       ChartUtilities.writeImageMap(pw, fileName, info);
       pw.flush();
     }
     catch (NoDataException e) {
       System.out.println(e.toString());
       fileName = "public_nodata_500x300.png";
     }
     catch (Exception e) {
       System.out.println("Exception - " + e.toString());
       e.printStackTrace(System.out);
       fileName = "public_error_500x300.png";
     }
       return fileName;
   }

   static XYSeries getSeries(CompositeMap model,String  yTitle,String xParam,String yParam) throws NoDataException{
       XYSeries series = new XYSeries(yTitle);
       if (model == null)  throw new NoDataException();
       Iterator i = model.getChildIterator();
       while (i.hasNext()){
           CompositeMap child = (CompositeMap)i.next();
           series.add(child.getLong(xParam),child.getLong(yParam));
       }
       return series;
   }

}
