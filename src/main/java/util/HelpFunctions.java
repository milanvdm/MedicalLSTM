package util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class HelpFunctions {

	public static Map<Double, String> icdDoubles;
    static {
        icdDoubles = new HashMap<Double, String>();
    }

	public static <T> T[] concatAll(T[] first, T[]... rest) {
		int totalLength = first.length;
		for (T[] array : rest) {
			totalLength += array.length;
		}
		T[] result = Arrays.copyOf(first, totalLength);
		int offset = first.length;
		for (T[] array : rest) {
			System.arraycopy(array, 0, result, offset, array.length);
			offset += array.length;
		}
		return result;
	}

	public static double[] parse(String s) {
		String[] strings = s.replace("[", "").replace("]", "").split(", ");
		double result[] = new double[strings.length];
		for (int i = 0; i < result.length; i++) {
			result[i] = Double.parseDouble(strings[i]);
		}
		return result;
	}

	public static double[] ListToPrimitiveDouble(List<Double> doubles) {

		double[] target = new double[doubles.size()];
		for (int i = 0; i < target.length; i++) {

			target[i] = doubles.get(i);   
		}

		return target;
	}



}
