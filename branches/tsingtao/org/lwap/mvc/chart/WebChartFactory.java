package org.lwap.mvc.chart;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author CloudScape
 * @version 1.0
 */

public class WebChartFactory {
  private static WebChartFactory instance ;
  private WebChartFactory() {
  }

  public static WebChartFactory getInstance(){
      if (instance == null){
          instance = new WebChartFactory() ;
          return instance;
      }
      else
        return instance;
  }

  public WebChart getWebChart(String type){
      WebChart returnValue = null;
      if (type.equalsIgnoreCase("bar"))
          returnValue = new BarWebChart();
      if (type.equalsIgnoreCase("bar3d"))
          returnValue = new BarWeb3DChart();
      if (type.equalsIgnoreCase("pie"))
          returnValue = new PieWebChart();
      if (type.equalsIgnoreCase("pie3d"))
          returnValue = new PieWeb3DChart();
      if (type.equalsIgnoreCase("xy"))
          returnValue = new XYWebChart();
      if (type.equalsIgnoreCase("timexy"))
          returnValue = new TimeXYWebChart();
/*      

      if (type.equalsIgnoreCase("xyarea"))
          returnValue = new XYAreaWebChart();
*/
      return returnValue;
  }

}