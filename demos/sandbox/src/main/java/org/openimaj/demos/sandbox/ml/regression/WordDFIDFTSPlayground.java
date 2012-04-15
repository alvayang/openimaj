package org.openimaj.demos.sandbox.ml.regression;

import java.io.File;
import java.io.IOException;

import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.openimaj.io.Cache;
import org.openimaj.io.IOUtils;
import org.openimaj.ml.timeseries.IncompatibleTimeSeriesException;
import org.openimaj.ml.timeseries.SynchronisedTimeSeriesCollection;
import org.openimaj.ml.timeseries.TimeSeriesCollection;
import org.openimaj.ml.timeseries.processor.IntervalSummationProcessor;
import org.openimaj.ml.timeseries.processor.LinearRegressionProcessor;
import org.openimaj.ml.timeseries.processor.MovingAverageProcessor;
import org.openimaj.ml.timeseries.processor.WindowedLinearRegressionAggregator;
import org.openimaj.ml.timeseries.processor.WindowedLinearRegressionProcessor;
import org.openimaj.ml.timeseries.processor.interpolation.util.TimeSpanUtils;
import org.openimaj.ml.timeseries.series.DoubleSynchronisedTimeSeriesCollection;
import org.openimaj.ml.timeseries.series.DoubleTimeSeries;
import org.openimaj.ml.timeseries.series.DoubleTimeSeriesCollection;
import org.openimaj.twitter.finance.YahooFinanceData;
import org.openimaj.util.pair.IndependentPair;
import org.terrier.utility.ArrayUtils;

/**
 * @author ss
 *
 */
public class WordDFIDFTSPlayground {
	/**
	 * @param args
	 * @throws IOException 
	 * @throws IncompatibleTimeSeriesException 
	 */
	public static void main(String[] args) throws IOException, IncompatibleTimeSeriesException {
		TSCollection coll = new TSCollection();
		DoubleTimeSeriesCollection AAPLwords = IOUtils.read(new File("/Users/ss/Development/data/TrendMiner/sheffield/2010/AAPL.specific.ts"), DoubleTimeSeriesCollection.class);
		DateTimeFormatter f = DateTimeFormat.forPattern("YYYY MM dd");
		DateTime begin = f.parseDateTime("2010 01 01");
		DateTime end = f.parseDateTime("2010 12 31");
		long gap = 24 * 60 * 60 * 1000;
		long[] times = TimeSpanUtils.getTime(begin.getMillis(), end.getMillis(), gap);
		AAPLwords = AAPLwords.processInternal(new IntervalSummationProcessor<double[],Double, DoubleTimeSeries>(times));
		timeSeriesToChart(AAPLwords, coll);
		displayTimeSeries(coll,"AAPL words DFIDF", "date","dfidf sum");
		YahooFinanceData data = new YahooFinanceData("AAPL",begin,end);
		data = Cache.load(data);
		coll = new TSCollection();
		timeSeriesToChart("AAPL",data.seriesByName("High"), coll);
		displayTimeSeries(coll,"AAPL High", "date","price");
	}

	private static void displayTimeSeries(TSCollection dataset, String name, String xname, String yname) {
		JFreeChart chart = ChartFactory.createTimeSeriesChart(name,xname,yname, dataset, true, false, false);
		ChartPanel panel = new ChartPanel(chart);
		panel.setFillZoomRectangle(true);
		JFrame j = new JFrame();
		j.setContentPane(panel);
		j.pack();
		j.setVisible(true);
		j.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	private static void timeSeriesToChart(TimeSeriesCollection<?,?,?,DoubleTimeSeries> dstsc, TSCollection coll,String ... append) {
		for (String seriesName: dstsc.getNames()) {
			DoubleTimeSeries series = dstsc.series(seriesName);
			TimeSeries ret = new TimeSeries(seriesName + ArrayUtils.join(append, "-"));
			for (IndependentPair<Long, Double> pair : series) {
				DateTime dt = new DateTime(pair.firstObject());
				Day d = new Day(dt.getDayOfMonth(), dt.getMonthOfYear(), dt.getYear());
				ret.add(d,pair.secondObject());
			}
			coll.addSeries(ret);
		}
	}
	private static void timeSeriesToChart(String name, DoubleTimeSeries highseries, TSCollection coll) {
		TimeSeries ret = new TimeSeries(name);
		for (IndependentPair<Long, Double> pair : highseries) {
			DateTime dt = new DateTime(pair.firstObject());
			Day d = new Day(dt.getDayOfMonth(), dt.getMonthOfYear(), dt.getYear());
			ret.add(d,pair.secondObject());
		}
		coll.addSeries(ret);
	}
}
