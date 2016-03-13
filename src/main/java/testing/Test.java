package testing;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Test {

	public static void main(String[] args) throws ParseException {
		SimpleDateFormat parserSDF = new SimpleDateFormat("dd-MMM");
		
		Date date = parserSDF.parse("01-jul-2005");
		
		
		System.out.println(decideSeason(date));
	}
	
	private static Double decideSeason(Date date) throws ParseException {
		SimpleDateFormat f = new SimpleDateFormat("MM-dd");
		
		if(date.after(f.parse("04-21")) && date.before(f.parse("06-21"))){
            return 4.0;
        }
        else if(date.after(f.parse("06-20")) && (date.before(f.parse("09-23"))))
        {
            return 1.0;
        }
        else if(date.after(f.parse("09-22")) && date.before(f.parse("12-22")))
        {
            return 2.0;
        }
        else return 3.0;
	}

}
