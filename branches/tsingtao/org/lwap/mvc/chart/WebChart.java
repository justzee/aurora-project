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

    /* ͼƬ��*/
    public void setLength(int length);
    /* ͼƬ��*/
    public void setWidth (int width);
    /* X�������*/
    public void setParam1(String param1);
    /*Y�������*/
    public void setParam2(String param2);
    /*������*/
    public void setData(String data);
    /*label*/
    public void setTitle(String title);
    /*categoryAxisLabel*/
    public void setXTitle(String xtitle);
    /*valueAxisLabel*/
    public void setYTitle(String ytitle);
    /*��ǩ������*/
    public void setLabelType(String type);
    /* ��ɶ�Ӧ��ͼƬ  */
    public String generateChart(CompositeMap model,String url,HttpSession session, PrintWriter pw);
    
	public void setLabelFormat(String labelFormat);

	public void setPercentFormat(String percentFormat);

	public void setFontFamily(String fontFamily);

	public void setFontSize(int fontSize);

	public void setFontStyle(int fontStyle);

}