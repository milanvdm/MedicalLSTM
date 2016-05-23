package plotting;

import java.io.File;
import java.io.IOException;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

public class MapPlot {

	public static void main(String[] args) throws IOException {


		final String first = "0.0";
		final String second = "0.33";
		final String third = "0.5";
		final String fourth = "0.66";
		final String fifth = "0.75";
		final String sixth = "1.0";
		final String percentage = "";

		final DefaultCategoryDataset dataset = new DefaultCategoryDataset( );

		dataset.addValue( 0.17 , first , percentage );
		dataset.addValue( 0.05 , second , percentage );
		dataset.addValue( 0.25 , third , percentage );
		dataset.addValue( 0.12 , fourth , percentage );

		dataset.addValue( 0.01 , fifth , percentage );
		dataset.addValue( 0.41 , sixth , percentage );
		

		JFreeChart barChart = ChartFactory.createBarChart(
				"MAPPING STATISTICS",
				"Percentage of words that match", "Percentage of codes", 
				dataset,PlotOrientation.VERTICAL, 
				true, true, false);

		int width = 640; /* Width of the image */
		int height = 480; /* Height of the image */ 
		File BarChart = new File( "mappingStats.jpeg" ); 
		ChartUtilities.saveChartAsJPEG( BarChart , barChart , width , height );

	}

}
