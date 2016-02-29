package AprioriEnd;

import java.text.ParseException;

public class Run {
	public static void main(String[] args) throws ParseException {
		// TODO Auto-generated method stub
		System.out.println("begin.......");
		DataPreProcessEnd.main(null);
		ApriorilesEnd.main(null);
		SelectRules.main(null);
		ResultAnalyze.main(null);
		Change.main(null);
		System.out.println("end.......");
	}
}
