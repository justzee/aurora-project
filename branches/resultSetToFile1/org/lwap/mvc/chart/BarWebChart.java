package org.lwap.mvc.chart;

import java.awt.Font;
import java.io.PrintWriter;
import java.util.Iterator;

import javax.servlet.http.HttpSession;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.Legend;
import org.jfree.chart.StandardLegend;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.StandardEntityCollection;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.servlet.ServletUtilities;
import org.jfree.chart.urls.StandardCategoryURLGenerator;
import org.jfree.data.DefaultCategoryDataset;

import uncertain.composite.CompositeMap;


/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author cloudscape
 * @version 1.0
 */

public class BarWebChart extends AbstractWebChart {

  /* implements function  */
  public String generateChart(CompositeMap model, String url ,HttpSession session, PrintWriter pw) {
    String fileName = null;
    CompositeMap child = null;
    boolean is_legend = true;
    boolean is_url = false;
    DefaultCategoryDataset dataset = new DefaultCategoryDataset();
    if (url != null) is_url = true;
    // collect data
    try {
      Iterator i = model.getChildIterator();
      if(i!=null)
      while (i.hasNext()) {
        child = (CompositeMap) i.next();
        if (child.getString(param1) == null)
             is_legend = false;
        dataset.addValue(child.getLong(data),
                         child.getString(param1) == null ? WebChart.SQL_XPARAMETER:child.getString(param1),
                         child.getString(param2) == null ? WebChart.SQL_YPARAMETER:child.getString(param2));
      }
    if (dataset == null) throw new NoDataException();

    // generate chart
      JFreeChart chart = ChartFactory.createBarChart(title,xTitle,yTitle,dataset,PlotOrientation.VERTICAL,is_legend,true,is_url);
      setChartFont(chart);
      
      CategoryPlot plot = (CategoryPlot)chart.getPlot();
      if (is_url)
          plot.getRenderer().setItemURLGenerator(new StandardCategoryURLGenerator(
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
	private void setChartFont(JFreeChart chart) {

		if (fontFamily == null)
			return;
		Font font = new Font(fontFamily, fontStyle, fontSize);
		// set title font
		if (chart.getTitle() != null && title != null) {
			chart.getTitle().setFont(font);
		}
		// set legend font
		Legend legend = chart.getLegend();
		if (legend != null) {
			StandardLegend standardLegend = (StandardLegend) legend;
			standardLegend.setItemFont(font);
		}
		 CategoryPlot plot = (CategoryPlot)chart.getPlot();
		 CategoryAxis domainAxis = plot.getDomainAxis();//(柱状图的x轄1�7)   
		 domainAxis.setTickLabelFont(font);//设置x轴坐标上的字佄1�7   
		 domainAxis.setLabelFont(font);//设置x轴上的标题的字体     
		 ValueAxis valueAxis = plot.getRangeAxis();//(柱状图的y轄1�7)   
		 valueAxis.setTickLabelFont(font);//设置y轴坐标上的字佄1�7   
		 valueAxis.setLabelFont(font);//设置y轴坐标上的标题的字体  
	}
}