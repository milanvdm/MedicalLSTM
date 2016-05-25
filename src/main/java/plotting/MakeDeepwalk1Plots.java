package plotting;

import java.awt.BasicStroke;
import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.LegendItem;
import org.jfree.chart.LegendItemCollection;
import org.jfree.chart.LegendItemSource;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.DatasetRenderingOrder;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.category.LevelRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.RectangleEdge;

public class MakeDeepwalk1Plots {

	public static void main(String[] args) throws FileNotFoundException, IOException {

		List<Deepwalk1> deepwalk1List = new ArrayList<Deepwalk1>();

		String directory = "/media/milan/Data/Thesis/Results/Approaches";

		File dir = new File(directory);
		File[] directoryListing = dir.listFiles();
		if (directoryListing != null) {
			for (File child : directoryListing) {

				String name = child.getName();

				if(name.contains("Deepwalk")) {
					if(name.contains("Cluster1")) {
						Deepwalk1 toAdd = new Deepwalk1();
						toAdd.parse(child);
						deepwalk1List.add(toAdd);
					}
				}

			}

		}


		Map<Deepwalk1, List<Deepwalk1>> combos = new HashMap<Deepwalk1, List<Deepwalk1>>();

		for(Deepwalk1 toCheck: deepwalk1List) {
			if(combos.containsKey(toCheck)) {
				combos.get(toCheck).add(toCheck);
			}
			else {
				combos.put(toCheck, new ArrayList<Deepwalk1>());
				combos.get(toCheck).add(toCheck);
			}
		}
		
		List<Deepwalk1> listToPlot = combos.values().iterator().next(); // PLOT FIRST ONE IN MAP


		final DefaultCategoryDataset averageDS = new DefaultCategoryDataset( );
		final DefaultCategoryDataset minDS = new DefaultCategoryDataset();
		final DefaultCategoryDataset maxDS = new DefaultCategoryDataset();
		final String filler = "";

		List<Integer> parameters = new ArrayList<Integer>();
		Map<Integer, List<Double>> link = new HashMap<Integer, List<Double>>();
		
		for(Deepwalk1 toPlot: listToPlot) {

			double average = toPlot.getTotalAverage();
			int parameter = toPlot.k; 
			double min = toPlot.getMin();
			double max = toPlot.getMax();

			parameters.add(parameter);
			link.put(parameter, new ArrayList<Double>());
			link.get(parameter).add(average);
			link.get(parameter).add(min);
			link.get(parameter).add(max);

		}
		
		Collections.sort(parameters);
		
		int j = 0;
		while(j < parameters.size()) {
			List<Double> toAdds = link.get(parameters.get(j));
			averageDS.addValue( toAdds.get(0), parameters.get(j), filler);
			minDS.addValue( toAdds.get(1), parameters.get(j), filler);
			maxDS.addValue( toAdds.get(2), parameters.get(j), filler);
			j++;
		}
		

		JFreeChart barChart = ChartFactory.createBarChart(
				"TEST",
				null, "Percentage of codes", 
				averageDS,PlotOrientation.VERTICAL, 
				true, true, false);


		CategoryPlot plot = barChart.getCategoryPlot();
		plot.setRangeAxisLocation(AxisLocation.BOTTOM_OR_LEFT);
		barChart.getLegend().setPosition(RectangleEdge.RIGHT);


		CategoryItemRenderer renderer2 = new LevelRenderer();
		plot.setDataset(1, minDS);
		plot.setRenderer(1, renderer2);
		plot.setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD);

		CategoryItemRenderer renderer3 = new LevelRenderer();
		plot.setDataset(2, maxDS);
		plot.setRenderer(2, renderer3);
		plot.setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD);


		renderer2.setSeriesPaint(0, Color.BLACK);
		renderer2.setSeriesStroke(0, new BasicStroke(5.0f));

		renderer2.setSeriesPaint(1, Color.BLACK);
		renderer2.setSeriesStroke(1, new BasicStroke(5.0f));

		renderer2.setSeriesPaint(2, Color.BLACK);
		renderer2.setSeriesStroke(2, new BasicStroke(5.0f));

		renderer3.setSeriesPaint(0, Color.BLACK);
		renderer3.setSeriesStroke(0, new BasicStroke(5.0f));

		renderer3.setSeriesPaint(1, Color.BLACK);
		renderer3.setSeriesStroke(1, new BasicStroke(5.0f));

		renderer3.setSeriesPaint(2, Color.BLACK);
		renderer3.setSeriesStroke(2, new BasicStroke(5.0f));

		LegendItemCollection legendItemsOld = plot.getLegendItems();
		final LegendItemCollection legendItemsNew = new LegendItemCollection();
		for(int i = 0; i< legendItemsOld.getItemCount(); i++){
			if(!(i > 2)){
				legendItemsNew.add(legendItemsOld.get(i));
			}
		}
		
		plot.setFixedLegendItems(legendItemsNew);

		int width = 640; /* Width of the image */
		int height = 480; /* Height of the image */ 
		File BarChart = new File( "test.jpeg" ); 
		ChartUtilities.saveChartAsJPEG( BarChart , barChart , width , height );

	}
}
