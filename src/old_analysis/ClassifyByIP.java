package old_analysis;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ClassifyByIP {
	public static String ALERT_LOG_PATH = "C:/Documents and Settings/js4/����/AlertLog/AlertLog.txt";
	public static String SORT_LOG_PATH = "C:/Documents and Settings/js4/����/AlertLog/AlertLogSortByIP.txt";

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		List<List<String[]>> list = new ArrayList<List<String[]>>();

		try {
			BufferedReader wReader = new BufferedReader(new InputStreamReader(
					new FileInputStream(ALERT_LOG_PATH), "UTF-8"));
			String line = null;
			while ((line = wReader.readLine()) != null) {
				boolean ifExist = false;
				if ("".equals(line.trim())) {
					continue;
				}
				String[] logArr = line.split("\t");

				for (int i = 0; i < list.size(); i++) {
					if (logArr[1].equals(list.get(i).get(0)[1])) {
						list.get(i).add(logArr);
						ifExist = true;
					}
				}

				if (!ifExist) {
					List<String[]> tmpList = new ArrayList<String[]>();
					tmpList.add(logArr);
					list.add(tmpList);
				}
			}
			wReader.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File(
					SORT_LOG_PATH), true));
			for (int i = 0; i < list.size(); i++) {
				for (int j = 0; j < list.get(i).size(); j++) {
					writer.write(list.get(i).get(j)[0] + "\t"
							+ list.get(i).get(j)[1] + "\t"
							+ list.get(i).get(j)[2]);
					writer.newLine();
					writer.flush();
				}
				writer.newLine();
				writer.newLine();
			}
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
