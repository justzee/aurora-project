package org.lwap.mvc.chart;

import java.io.PrintWriter;

import javax.servlet.http.HttpSession;

import uncertain.composite.CompositeMap;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author CloudScape
 * @version 1.0
 */

public abstract class AbstractWebChart implements WebChart {

  int length ;
  int width ;
  String param1 ;
  String param2 ;
  String data;
  String labelType;
  String title;
  String xTitle;
  String yTitle;

  public void setLength(int length) {
    this.length = length;
  }
  public void setWidth(int width) {
    this.width = width;
  }
  public void setParam1(String param1) {
    this.param1 = param1;
  }
  public void setParam2(String param2) {
    this.param2 = param2;
  }
  public void setData(String data) {
    this.data = data;
  }
  public void setTitle(String title) {
    this.title = title;
  }
  public void setXTitle(String xtitle) {
    this.xTitle = xtitle;
  }
  public void setYTitle(String ytitle) {
    this.yTitle = ytitle;
  }
  public void setLabelType(String type) {
    this.labelType = type;
  }
  public abstract String generateChart(CompositeMap model, String url, HttpSession session, PrintWriter pw) ;
}