/*
 * Created on 2007-4-18
 */
package org.lwap.mvc.chart;

import java.awt.Color;
import java.awt.Paint;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import javax.servlet.http.HttpSession;

import org.jfree.chart.ChartColor;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.entity.StandardEntityCollection;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.XYLineAndShapeRenderer;
import org.jfree.chart.servlet.ServletUtilities;
import org.jfree.data.AbstractIntervalXYDataset;
import org.jfree.data.XYDataset;
import org.jfree.data.XYSeriesCollection;
import org.jfree.data.time.TimeSeriesCollection;

import uncertain.composite.CompositeMap;

public class JFreeXYChart {
    
   
    HashMap seriesMap = new HashMap();
    
    public static final Paint[] DEFAULT_COLORS = ChartColor.createDefaultPaintArray();
    
    public String   ChartTitle;
    public String   XField;
    public String   YField;
    public String   SeriesField;
    public String   XTitle;
    public String   YTitle;
    public String   YTitle2;
    public boolean  IsLegend = true;
    public boolean  IsToolTips = true;
    public boolean  IsUrl = false;
    public boolean  IsHorizontal = true; 
    public String   Url;
    public String   UrlXParam = "series";
    public String   UrlYParam = "item";
    public Axis[]   ChartAxises = null;
    
    public int      Width = 200;
    public int      Height = 200;
    
    protected String   xFieldFormat = "yyyy-MM-dd";    
    protected XYDataset dataset;
    protected boolean   isXFieldDateType = false;
    protected SimpleDateFormat  date_format;

    protected void buildSeries(){
        Iterator it = seriesMap.values().iterator();
        while(it.hasNext()){
            Series s = (Series)it.next();
            if(isXFieldDateType){
                s.createTimeDataset();
            }
            else 
                s.createNumberDataset();
        }
    }
    
    public static Color getColor(int id){
        if(id<DEFAULT_COLORS.length)
            return (Color)DEFAULT_COLORS[id];
        else
            return (Color)DEFAULT_COLORS[id % DEFAULT_COLORS.length];
    }
    
    public void    setXFieldType(Class type){
        if(Date.class.isAssignableFrom(type))
            isXFieldDateType = true;
        else if (Number.class.isAssignableFrom(type))
            isXFieldDateType = false;
        else
            throw new IllegalArgumentException("Chart XFieldType can't be of type "+type.getName());
    
    }
    
    public Class getXFieldType(){
        return isXFieldDateType ? Date.class: Number.class;
    }
    
    
    public void setXFieldFormat(String format){
        xFieldFormat = format;
        date_format = new SimpleDateFormat(format);
    }
    
    public String getXFieldFormat() {
        return xFieldFormat;
    }
    
    public void addChartSeries(Series[] sa) {
        for(int i=0; i<sa.length; i++)
            seriesMap.put(sa[i].Name, sa[i]);
    }
    
    public void bindModel(CompositeMap model){
        Iterator it = seriesMap.values().iterator();
        while(it.hasNext()){
            Series s = (Series)it.next();
            s.clear();
        }
        Iterator mit = model.getChildIterator();
        if(mit==null) return;
        while(mit.hasNext()){
            CompositeMap record = (CompositeMap)mit.next();
            String series_name = record.getString(SeriesField);
            Object xValue = record.getString(XField);
            Object yValue = record.getString(YField);
            if(xValue instanceof String){
                if(isXFieldDateType){
                    if(date_format == null) date_format = new SimpleDateFormat();
                    try{
                        xValue = date_format.parse((String)xValue);
                    }catch(ParseException ex){
                        //throw new IllegalArgumentException("Can't get date value from model",ex);
                        ex.printStackTrace();
                    }
                }else{
                    xValue = new Double((String)xValue);
                }
            }
            if(yValue instanceof String) yValue = new Double((String)yValue);
            Series series = (Series)seriesMap.get(series_name);
            if(series==null) throw new IllegalArgumentException("Series name '"+series_name+"' not found");
            series.addData(xValue, yValue);
        }
    }
    
    public AbstractIntervalXYDataset createXYDataset(){
        if(isXFieldDateType) 
            return new TimeSeriesCollection(); 
        else 
            return new  XYSeriesCollection();
    }
    
    public JFreeChart createChart(XYDataset dataset){
        JFreeChart chart = null;
        if(isXFieldDateType){
            chart =  ChartFactory.createTimeSeriesChart(
                ChartTitle, 
                XTitle, 
                YTitle,
                dataset,//((Series)sa[0]).getDataset(), 
                IsLegend, 
                IsToolTips, 
                IsUrl
            ); 
        }
        else{
            chart =  ChartFactory.createXYLineChart(
                    ChartTitle, 
                    XTitle, 
                    YTitle,
                    dataset, 
                    IsHorizontal?PlotOrientation.HORIZONTAL:PlotOrientation.VERTICAL,
                    IsLegend, 
                    IsToolTips, 
                    IsUrl                    
                    );
        }    
        return chart;
    }

    public String generateChart(
            CompositeMap model, 
            HttpSession session, 
            PrintWriter pw
            ) 
    throws java.io.IOException {
        buildSeries();
        bindModel(model);
        Object[] sa = seriesMap.values().toArray();
        JFreeChart chart = null;
        //AbstractIntervalXYDataset collection1 = createXYDataset();
        //AbstractIntervalXYDataset collection2 = null;
        
        NumberAxis axis2 = null;        
        XYPlot plot = null;
        
        chart = createChart(null);
        plot = chart.getXYPlot();
        
        for(int id=0; id<sa.length; id++){
            Series series = (Series)sa[id];
            if(series.DualAxis){                
                if(axis2==null){
                    axis2 = new NumberAxis(YTitle2);
                    axis2.setAutoRangeIncludesZero(false);
                    plot.setRangeAxis(1, axis2);
                }                
                plot.setDataset(id, series.getDataset());
                plot.mapDatasetToRangeAxis(id, 1);
            }else{
                //series.addTo(collection1);
                plot.setDataset(id,series.getDataset());
            }
            // set series color
            XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
            renderer.setSeriesPaint(id, id==0?Color.BLACK:getColor(id));
            renderer.setToolTipGenerator(StandardXYToolTipGenerator.getTimeSeriesInstance());
            plot.setRenderer(id,renderer);
            
        }

        chart.setBackgroundPaint(java.awt.Color.WHITE);
        
        if( date_format!=null && isXFieldDateType){
            DateAxis axis = (DateAxis) plot.getDomainAxis();
            axis.setDateFormatOverride(date_format);
        }
        
        // Set axis range and tick unit
        if(ChartAxises != null){
            for(int i=0; i<ChartAxises.length; i++){
                NumberAxis nax = (NumberAxis)plot.getRangeAxis(i);
                if(ChartAxises[i].TickUnit!=null)
                    nax.setTickUnit( new NumberTickUnit(ChartAxises[i].TickUnit.doubleValue()));
                if(ChartAxises[i].RangeFrom!=null && ChartAxises[i].RangeTo!=null){
                    nax.setRange(ChartAxises[i].RangeFrom.doubleValue(), ChartAxises[i].RangeTo.doubleValue());
                }
                else if(ChartAxises[i].RangeFrom!=null)
                    nax.setAutoRangeMinimumSize(ChartAxises[i].RangeFrom.doubleValue());
            }
        }
        
        //  Write the chart image to the temporary directory
        ChartRenderingInfo info = new ChartRenderingInfo(new StandardEntityCollection());
        String fileName = ServletUtilities.saveChartAsPNG( chart, Width, Height, info, session);

        //  Write the image map to the PrintWriter
        ChartUtilities.writeImageMap(pw, fileName, info);
        pw.flush();
        
        return fileName;
    }

}
