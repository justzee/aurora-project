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
import java.util.List;

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
import org.jfree.chart.renderer.AbstractXYItemRenderer;
import org.jfree.chart.renderer.XYItemRenderer;
import org.jfree.chart.renderer.XYLineAndShapeRenderer;
import org.jfree.chart.servlet.ServletUtilities;
import org.jfree.data.AbstractIntervalXYDataset;
import org.jfree.data.AbstractSeriesDataset;
import org.jfree.data.XYDataset;
import org.jfree.data.XYSeriesCollection;
import org.jfree.data.time.TimeSeriesCollection;

import uncertain.composite.CompositeMap;
import uncertain.composite.TextParser;

public class JFreeXYChart {

	HashMap seriesMap = new HashMap();

	public static final Paint[] DEFAULT_COLORS = ChartColor
			.createDefaultPaintArray();

	public String ChartType;
	public String SeriesConfig;
	public String ChartTitle;
	public String XField;
	public String YField;
	public String SeriesField;
	public String XTitle;
	public String YTitle;
	public String YTitle2;
	public boolean IsLegend = true;
	public boolean IsToolTips = true;
	public boolean IsUrl = false;
	public boolean IsHorizontal = false;
	public String Url;
	public String UrlXParam = "series";
	public String UrlYParam = "item";
	public Axis[] ChartAxises = null;

	public int Width = 200;
	public int Height = 200;

	protected String xFieldFormat = "yyyy-MM-dd";
	protected XYDataset dataset;
	protected boolean isXFieldDateType = false;
	protected SimpleDateFormat date_format;

	private final String XYLineChart = "XYLineChart";
	private final String TimeSeriesChart = "TimeSeriesChart";
	private final String ScatterPlot = "ScatterPlot";

	protected void buildSeries() {
		Iterator it = seriesMap.values().iterator();
		while (it.hasNext()) {
			Series s = (Series) it.next();
			if (isXFieldDateType) {
				s.createTimeDataset();
			} else
				s.createNumberDataset();
		}
	}

	public static Color getColor(int id) {
		if (id < DEFAULT_COLORS.length)
			return (Color) DEFAULT_COLORS[id];
		else
			return (Color) DEFAULT_COLORS[id % DEFAULT_COLORS.length];
	}

	public void setXFieldType(Class type) {
		if (Date.class.isAssignableFrom(type))
			isXFieldDateType = true;
		else if (Number.class.isAssignableFrom(type))
			isXFieldDateType = false;
		else
			throw new IllegalArgumentException(
					"Chart XFieldType can't be of type " + type.getName());

	}

	public Class getXFieldType() {
		return isXFieldDateType ? Date.class : Number.class;
	}

	public void setXFieldFormat(String format) {
		xFieldFormat = format;
		date_format = new SimpleDateFormat(format);
	}

	public String getXFieldFormat() {
		return xFieldFormat;
	}

	public void addChartSeries(Series[] sa) {
		for (int i = 0; i < sa.length; i++)
			seriesMap.put(sa[i].Name, sa[i]);
	}

	public void bindModel(CompositeMap model) {
		Iterator it = seriesMap.values().iterator();
		while (it.hasNext()) {
			Series s = (Series) it.next();
			s.clear();
		}
		Iterator mit = model.getChildIterator();
		if (mit == null)
			return;
		while (mit.hasNext()) {
			CompositeMap record = (CompositeMap) mit.next();
			String series_name = record.getString(SeriesField);
			Object xValue = record.getString(XField);
			Object yValue = record.getString(YField);
			if (xValue instanceof String) {
				if (isXFieldDateType) {
					if (date_format == null)
						date_format = new SimpleDateFormat();
					try {
						xValue = date_format.parse((String) xValue);
					} catch (ParseException ex) {
						// throw new
						// IllegalArgumentException("Can't get date value from model",ex);
						ex.printStackTrace();
					}
				} else {
					xValue = new Double((String) xValue);
				}
			}
			if (yValue instanceof String)
				yValue = new Double((String) yValue);
			Series series = (Series) seriesMap.get(series_name);
			if (series == null)
				throw new IllegalArgumentException("Series name '"
						+ series_name + "' not found");
			if (xValue instanceof Date)
				series.add((Date) xValue, (Number) yValue);
			else
				series.add((Number) xValue, (Number) yValue);

		}
	}

	public AbstractIntervalXYDataset createXYDataset() {
		if (isXFieldDateType)
			return new TimeSeriesCollection();
		else
			return new XYSeriesCollection();
	}

	public JFreeChart createChart(XYDataset dataset) {
		JFreeChart chart = null;
		if (ChartType == null || ChartType.equals("")) {
			if (isXFieldDateType) {
				chart = ChartFactory.createTimeSeriesChart(ChartTitle, XTitle,
						YTitle, dataset,// ((Series)sa[0]).getDataset(),
						IsLegend, IsToolTips, IsUrl);
			} else {
				chart = ChartFactory.createXYLineChart(ChartTitle, XTitle,
						YTitle, dataset,
						IsHorizontal ? PlotOrientation.HORIZONTAL
								: PlotOrientation.VERTICAL, IsLegend,
						IsToolTips, IsUrl);
			}
		} else {
			if (ChartType.equals(TimeSeriesChart)) {
				chart = ChartFactory.createTimeSeriesChart(ChartTitle, XTitle,
						YTitle, dataset,// ((Series)sa[0]).getDataset(),
						IsLegend, IsToolTips, IsUrl);
			} else if (ChartType.equals(XYLineChart)) {
				chart = ChartFactory.createXYLineChart(ChartTitle, XTitle,
						YTitle, dataset,
						IsHorizontal ? PlotOrientation.HORIZONTAL
								: PlotOrientation.VERTICAL, IsLegend,
						IsToolTips, IsUrl);
			} else if (ChartType.equals(ScatterPlot)) {
				chart = ChartFactory.createScatterPlot(ChartTitle, XTitle,
						YTitle, dataset,
						IsHorizontal ? PlotOrientation.HORIZONTAL
								: PlotOrientation.VERTICAL, IsLegend,
						IsToolTips, IsUrl);
			}
		}
		return chart;
	}

	public String generateChart(CompositeMap model, HttpSession session,
			PrintWriter pw) throws java.io.IOException {
		
		
		CompositeMap rootModel = model.getParent();

		if(SeriesConfig != null){
			seriesMap.clear();
			CompositeMap cm_SeriesConfig = null;
			String t = TextParser.parse(SeriesConfig, rootModel);
			cm_SeriesConfig = (CompositeMap) rootModel.getObject(t);
			
			List list = cm_SeriesConfig.getChilds();
			Series series = null;
			for (int i = 0; i < list.size(); i++) {
				CompositeMap cm = (CompositeMap) list.get(i);
				series = new Series();
				Object o = cm.get("NAME");
				String name = o == null ? "" : o.toString();
				series.Name=name;
				String str_dualAxis = o == null ? "false" : o.toString();
				boolean bl_dualAxis = str_dualAxis.equals("true")?true:false;
				series.DualAxis=bl_dualAxis;
				seriesMap.put(name,series);
			}
		}
		
		buildSeries();
		bindModel(model);
		Object[] sa = seriesMap.values().toArray();
		JFreeChart chart = null;
		// AbstractIntervalXYDataset collection1 = createXYDataset();
		// AbstractIntervalXYDataset collection2 = null;

		NumberAxis axis2 = null;
		XYPlot plot = null;

		chart = createChart(null);
		plot = chart.getXYPlot();
		TimeSeriesCollection tc = new TimeSeriesCollection();
		XYSeriesCollection xysc = new XYSeriesCollection();
		for (int id = 0; id < sa.length; id++) {
			Series series = (Series) sa[id];
			if (series.DualAxis) {
				if (axis2 == null) {
					axis2 = new NumberAxis(YTitle2);
					axis2.setAutoRangeIncludesZero(false);
					plot.setRangeAxis(1, axis2);
				}
				// plot.setDataset(id, series.getDataset());
				plot.mapDatasetToRangeAxis(id, 1);
			}
			// else{
			// // series.addTo(collection1);
			// plot.setDataset(id,series.getDataset());
			// }
			if (isXFieldDateType)
				tc.addSeries(series.timeSeries);
			else
				xysc.addSeries(series.xySeries);
			// }
			// set series color
			// XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
			XYDataset dataset = xysc;
			if (isXFieldDateType)
				dataset = tc;
			plot.setDataset(dataset);
			// XYItemRenderer localXYLineAndShapeRenderer = (XYItemRenderer
			// )plot.getRenderer();
			// set series color
			AbstractXYItemRenderer renderer=null;
			if(ChartType !=null && ChartType.equals(ScatterPlot))
				renderer =(AbstractXYItemRenderer) plot.getRenderer();
			else	
				renderer = new XYLineAndShapeRenderer();
			renderer.setSeriesPaint(id, id == 0 ? Color.BLACK : getColor(id));
			renderer.setToolTipGenerator(StandardXYToolTipGenerator
					.getTimeSeriesInstance());
			plot.setRenderer(id, renderer);

		}

		chart.setBackgroundPaint(java.awt.Color.WHITE);

		if (date_format != null && isXFieldDateType) {
			DateAxis axis = (DateAxis) plot.getDomainAxis();
			axis.setDateFormatOverride(date_format);
		}

		// Set axis range and tick unit
		if (ChartAxises != null) {
			for (int i = 0; i < ChartAxises.length; i++) {
				NumberAxis nax = (NumberAxis) plot.getRangeAxis(i);
				if(nax == null)
					continue;
				if (ChartAxises[i].TickUnit != null)
					nax.setTickUnit(new NumberTickUnit(ChartAxises[i].TickUnit
							.doubleValue()));
				if (ChartAxises[i].RangeFrom != null
						&& ChartAxises[i].RangeTo != null) {
					nax.setRange(ChartAxises[i].RangeFrom.doubleValue(),
							ChartAxises[i].RangeTo.doubleValue());
				} else if (ChartAxises[i].RangeFrom != null)
					nax.setAutoRangeMinimumSize(ChartAxises[i].RangeFrom
							.doubleValue());
			}
		}

		// Write the chart image to the temporary directory
		ChartRenderingInfo info = new ChartRenderingInfo(
				new StandardEntityCollection());
		String fileName = ServletUtilities.saveChartAsPNG(chart, Width, Height,
				info, session);

		// Write the image map to the PrintWriter
		ChartUtilities.writeImageMap(pw, fileName, info);
		pw.flush();

		return fileName;
	}

}
