package util;

import java.util.Arrays;
import java.util.List;

public class Constants {
	
	public static int MAX_DRUG_LIST_SIZE = 0;
	public static int AMOUNT_DRUG_ATTRIBUTES = 4;

	public static int CONDITION_ID_COLUMN = 0;
	public static int DATE_COLUMN = 1;
	public static int PATIENTNUMBER_COLUMN = 2;
	public static int BIRTH_YEAR_COLUMN = 3;
	public static int GENDER_CONCEPT_COLUMN = 4;
	public static int CONDITION_TYPE_COLUMN = 5;
	public static int CONDITION_TYPE_DESC_COLUMN = 6;
	public static int CONDITION_COLUMN = 7;
	public static int DRUG_LIST_COLUMN = 8;
	public static int TIME_DIFFERENCE_COLUMN = 9;
	public static int SEASON_COLUMN = 10;
	
	
	// "1","09-dec-2003","1","1942","8532","65","Condition Era","35305814","[]"
	public static List<Integer> COLUMS_TO_IGNORE = Arrays.asList(new Integer[]{	CONDITION_ID_COLUMN,
																				DATE_COLUMN,
																				PATIENTNUMBER_COLUMN,
																				CONDITION_TYPE_DESC_COLUMN,
																				DRUG_LIST_COLUMN});
	
	
	public static String INPUT_CSV = "/media/milan/Data/Thesis/Datasets/OSIM/Final/conditions_merged.csv";
	public static String INPUT_CSV_TEST = "/media/milan/Data/Thesis/Datasets/OSIM/Sorted/test_conditions_sorted.csv";
	
	public static String OUTPUT_VOCAB = "/media/milan/Data/Thesis/Results/vocab_count.txt";
	public static String OUTPUT_WORD2VEC = "/media/milan/Data/Thesis/Results/word2vec.ser";
	
	
	
	
	
	public static String INPUT_CSV_EXA = "../OSIM/conditions_merged.csv";
	public static String OUTPUT_VOCAB_EXA = "../Results/vocab_count.txt";
	public static String INPUT_CSV_TEST_EXA = "../OSIM/test_conditions_merged.csv";
	
	public static String OUTPUT_WORD2VEC_EXA = "/media/milan/Data/Thesis/Results/word2vec.ser";
	
	
	
	
}

