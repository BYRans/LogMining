package old_training;
public class reg {

	public static void main(String[] args) throws Exception {
		String record = "servicename;3;8.8.8.8;2014-12-22 09:00:48;node6116;snmpd[7018]: Connection from UDP: [8.8.8.8]:52329";
		String[] recordArr = record.split(";| ");
		String regTimeDay = "^(2014-[0-1]?[0-9]-[0-3]?[0-9])$";				
		String regTime = "^([0-9][0-9]:[0-9][0-9]:[0-9][0-9])$";				
		String regSegment = "node\\d+";
		String regNumber = "(?!^[kmgb]*$)^([0-9kmgb.])*$";
//		if (recordArr[3].matches(regTimeDay)) 
//			System.out.println("day yes");
//		if (recordArr[4].matches(regTime)) 
//			System.out.println("sec yes");
//		if (recordArr[5].matches(regSegment)) 
//			System.out.println("node yes");
//		

		String t = "mb";
		if (t.matches(regNumber)) 
			System.out.println("yes");
		
	}
}