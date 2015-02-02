package old_analysis;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Classify {
	public static String ALERT_LOG_PATH = "C:/Documents and Settings/js4/����/20150109/";
	public static String MERGE_LOG_PATH = "C:/Documents and Settings/js4/����/AlertLog/AlertLog.txt";
	public static HashSet<String> set = new HashSet<String>();
	
	
	public static void main(String[] args) throws ParseException {
		System.out.println("running...");
		List<String> timeLabelList = new ArrayList<String>();
		// ����alert log
		try {
			File f = new File(ALERT_LOG_PATH);
			File[] fileList = f.listFiles();
			for (File file : fileList) {
				BufferedReader wReader = new BufferedReader(
						new InputStreamReader(new FileInputStream(file),
								"UTF-8"));
				String line = null;
				while ((line = wReader.readLine()) != null) {
					if ("".equals(line.trim())) {
						continue;
					}
					String[] logArr = line.split(",");
					timeLabelList.add(logArr[3].replace("/", "-").replace("\"",
							"")
							+ "\t"
							+ logArr[2].replace("/", "-").replace("\"", "")
							+ "\t" + logArr[13].replace("\"", ""));
					set.add(logArr[13].replace("\"", ""));
				}
				wReader.close();
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		for (String str : set) {
			System.out.println(str);
		}
		
		System.out.println("Completed.");
	}

}
