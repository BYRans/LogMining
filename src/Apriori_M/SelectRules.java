package AprioriEnd;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class SelectRules {
	
	public static void main(String[] args) {
		System.out.println("running..........");
		String path = "C:/Users/zys/Desktop/score/9-24/result/";
		File file = new File(path);
		File[] tempList = file.listFiles();
		Boolean flag = false;
		for (int j = 0; j < tempList.length; j++) {
			List<String> LABEL_LIST = new ArrayList<String>();
			String common = tempList[j].getName();
			String resultPath = "C:/Users/zys/Desktop/score/9-24/result/"
					+ common;
			String rulesPath = "C:/Users/zys/Desktop/score/9-24/"
					+ common;
			// 读取文件
			System.out.println(resultPath);
			try {
				BufferedReader wReader = new BufferedReader(
						new InputStreamReader(new FileInputStream(resultPath),
								"UTF-8"));
				String line = null;
				while ((line = wReader.readLine()) != null) {
					String[] temp = line.split("\t");
					String IPTest = null;
					flag = false;
					for (int m = 0; m < temp.length - 1; m++) {
						String[] sublogArr = temp[m].split("_");
						if (m == 0) {
							IPTest = sublogArr[0];
						} else {
							if (IPTest.equals(sublogArr[0])) {
								flag = true;
								continue;
							} else {
								flag = false;
								break;
							}
						}
					}
					if (flag == true) {
						LABEL_LIST.add(line);
					}

				}
				wReader.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}

			// 写入文件
			try {
				BufferedWriter writer = new BufferedWriter(new FileWriter(
						new File(rulesPath), false));
				for (String record : LABEL_LIST) {
					writer.write(record);
					System.out.println(record);
					writer.newLine();
					writer.flush();
				}
				writer.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		System.out.println("end..........");
	}
}
