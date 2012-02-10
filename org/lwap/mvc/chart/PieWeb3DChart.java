package org.lwap.mvc.chart;

import java.io.PrintWriter;
import java.util.Iterator;

import javax.servlet.http.HttpSession;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.entity.StandardEntityCollection;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.servlet.ServletUtilities;
import org.jfree.chart.urls.StandardPieURLGenerator;
import org.jfree.data.DefaultPieDataset;

import uncertain.composite.CompositeMap;


/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author CloudScape
 * @version 1.0
 */

public class PieWeb3DChart extends AbstractWebChart {

  public String generateChart(CompositeMap model, String url ,HttpSession session, PrintWriter pw) {
    String fileName = null;
    CompositeMap child = null;
    boolean is_legend = true;
    boolean is_url = false;
    DefaultPieDataset dataset = new DefaultPieDataset();
    if (url != null) is_url = true;
    try {
      Iterator i = model.getChildIterator();
      while (i.hasNext()) {
        child = (CompositeMap) i.next();
        dataset.setValue(child.getString(param1) == null ? WebChart.SQL_XPARAMETER:child.getString(param1),child.getLong(data));
      }
      if (dataset == null) throw new NoDataException();

      JFreeChart chart = ChartFactory.createPieChart3D(title,dataset,is_legend,true,is_url);
      PiePlot plot = (PiePlot)chart.getPlot();
      if (is_url)
          plot.setURLGenerator(new StandardPieURLGenerator(
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


}
