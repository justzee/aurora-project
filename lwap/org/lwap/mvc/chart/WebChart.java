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

public interface WebChart {
    public static String SQL_COUNT = "COUNT";
    public static String SQL_XPARAMETER = "X-AXIS";
    public static String SQL_YPARAMETER = "Y-AXIS";

    /* 图片长*/
    public void setLength(int length);
    /* 图片宽*/
    public void setWidth (int width);
    /* X参数名称*/
    public void setParam1(String param1);
    /*Y参数名称*/
    public void setParam2(String param2);
    /*数据名称*/
    public void setData(String data);
    /*label*/
    public void setTitle(String title);
    /*categoryAxisLabel*/
    public void setXTitle(String xtitle);
    /*valueAxisLabel*/
    public void setYTitle(String ytitle);
    /*标签的类型*/
    public void setLabelType(String type);
    /* 生成对应的图片  */
    public String generateChart(CompositeMap model,String url,HttpSession session, PrintWriter pw);

}