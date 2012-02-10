/*
 * Created on 2007-4-18
 */
package org.lwap.mvc.chart;
import java.util.Date;

import org.jfree.data.AbstractIntervalXYDataset;
import org.jfree.data.XYDataset;
import org.jfree.data.XYSeries;
import org.jfree.data.XYSeriesCollection;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

public class Series {
    
    public String   Name;
    public boolean  DualAxis = false;
    public String   Color;
    
    protected       XYDataset   dataset;
    protected       TimeSeries  timeSeries;
    protected       XYSeries    xySeries;
    
    public XYDataset getDataset(){
        return dataset;
    }    
    
    public XYDataset createTimeDataset(){
        timeSeries = new TimeSeries(Name, Day.class);
        TimeSeriesCollection tc = new TimeSeriesCollection();
        tc.addSeries(timeSeries);
        dataset = tc;
        return dataset;
    }
    
    public XYDataset createNumberDataset(){
        xySeries = new XYSeries(Name);
        XYSeriesCollection xysc = new XYSeriesCollection(xySeries);
        dataset = xysc;
        return dataset;
    }
    
    public void add(Date date, Number value){       
        timeSeries.add(new Day(date), value.doubleValue()); 
    }
    
    public void add(Number x, Number y){
        xySeries.add(x,y);
    }
    
    public void addData(Object x, Object y){
        try{
            if(x==null||y==null) return;
            if(timeSeries!=null) {   
                add((Date)x, (Number)y);
            }
            else if (xySeries!=null) {
                add((Number)x, (Number)y);
            }
        }catch(ClassCastException ex){
            ex.printStackTrace();
        }
    }
    
    public void clear(){
        if(timeSeries!=null) timeSeries.clear();
        else if(xySeries!=null) xySeries.clear();
    }
    
    public org.jfree.data.Series getSeries(){
        return timeSeries==null? (org.jfree.data.Series)xySeries: timeSeries;
    }
    
    public void addTo(AbstractIntervalXYDataset ds){
        if(ds instanceof XYSeriesCollection && xySeries!=null)
            ((XYSeriesCollection)ds).addSeries(xySeries);
        else if(ds instanceof TimeSeriesCollection && timeSeries!=null)
            ((TimeSeriesCollection)ds).addSeries(timeSeries);
        else
            throw new IllegalArgumentException("series '"+Name+"' can't be added to "+ds.getClass());        
    }
    
}
