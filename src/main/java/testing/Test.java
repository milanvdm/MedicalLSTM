package testing;

import java.util.ArrayList;
import java.util.List;

public class Test {

	public static void main(String[] args) {
		List<Double> weights = new ArrayList<Double>();

		double i = 10;
		while(i != 0) {
			weights.add(i / 10);
			i--;
		}

		double sum = 0;
		for(double toSum: weights) {
			sum = sum + toSum;
		}

		List<Double> toReturn = new ArrayList<Double>();
		for(double weight: weights) {
			double newWeight = weight / sum;
			toReturn.add(newWeight);
		}

		System.out.println(toReturn.toString());
		
		sum = 0;
		for(double toSum: toReturn) {
			sum = sum + toSum;
		}
		
		System.out.println(sum);
	}

}
