package AprioriEnd;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ResultAnalyze {

	public static void main(String[] args) {
		String Path = "C:/Users/zys/Desktop/score/9-24/selectResult/";
		File file = new File(Path);
		File[] tempList = file.listFiles();

		for (int m = 0; m < tempList.length; m++) {
			String common = tempList[m].getName();
			System.out.println(common);
			String resultPath1 = Path + common;
			String resultPath2 = "C:/Users/zys/Desktop/score/9-24/selectResultV/"
					+ common;
			List<String> record = new ArrayList<String>();
			List<List<String>> typeGroupRecord = new ArrayList<List<String>>();
			List<String> groupCount = new ArrayList<String>();
			List<List<String>> leixing = new ArrayList<List<String>>();
			Integer totalCount1 = 0;
			Integer totalCount2 = 0;
			try {
				BufferedReader wReader = new BufferedReader(
						new InputStreamReader(new FileInputStream(resultPath1),
								"UTF-8"));
				String line = null;
				while ((line = wReader.readLine()) != null) {
					if ("".equals(line.trim())) {
						continue;
					}
					record.add(line);
				}
				wReader.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}

			// 找出所有类型
			Map<List<String>, Integer> typePair = new HashMap<List<String>, Integer>();
			List<String> satisfiedEqualIPEvent = new ArrayList<String>();
			for (int i = 0; i < record.size(); i++) {
				List<String> type = new ArrayList<String>();
				List<String> ip = new ArrayList<String>();
				String[] logArr = record.get(i).split("\t");
				for (int j = 0; j < logArr.length - 1; j++) {
					String[] sublogArr = logArr[j].split("_");
					type.add(sublogArr[1]);
					ip.add(sublogArr[0]);

				}
				typePair.put(type, 0);
				
			}

			for (java.util.Map.Entry<List<String>, Integer> entry : typePair
					.entrySet()) {
				List<Integer> num1 = new ArrayList<Integer>();
				List<Integer> num2 = new ArrayList<Integer>();
				totalCount1 = 0;
				totalCount2 = 0;
				
				satisfiedEqualIPEvent = resolvePair(entry.getKey(), record);
				if (satisfiedEqualIPEvent.size() != 0) {
					for (String tempStr : satisfiedEqualIPEvent) {
						String[] str = tempStr.split("\t");
						String[] num = str[str.length - 1].split("/");
						num1.add(Integer.parseInt(num[0]));
						num2.add(Integer.parseInt(num[1]));
					}

					if (num1.size() > 0) {
						leixing.add(entry.getKey());
						for (int k = 0; k < num1.size(); k++) {
							totalCount1 = totalCount1 + num1.get(k);
							totalCount2 = totalCount2 + num2.get(k);
						}

						String totalStr = String.valueOf(totalCount1) + "/"
								+ String.valueOf(totalCount2);
						groupCount.add(totalStr);
						typeGroupRecord.add(satisfiedEqualIPEvent);
					}
				}
			}

			try {
				BufferedWriter writer = new BufferedWriter(new FileWriter(
						new File(resultPath2), false));
				for (int i = 0; i < typeGroupRecord.size(); i++) {
					for(String str:leixing.get(i)){
						writer.write(str+"\t");
					}
					writer.write(groupCount.get(i));
					writer.newLine();
					writer.flush();
				}
				writer.close();
			} catch (Exception e) {
				e.printStackTrace();
			}

			System.out.println("end..........");

		}
	}

	public static List<String> resolvePair(List<String> type,
			List<String> record) {
		Boolean ipEqualFlag = false;
		Boolean typeEqualFlag = true;
		List<String> satisfiedEqualIPEvent = new ArrayList<String>();
		for (int i = 0; i < record.size(); i++) {
			ipEqualFlag = false;
			typeEqualFlag = true;
			String[] logArr = record.get(i).split("\t");
			List<String> singleType = new ArrayList<String>();
			List<String> ip = new ArrayList<String>();
			for (int j = 0; j < logArr.length - 1; j++) {
				String[] sublosArr = logArr[j].split("_");
				ip.add(sublosArr[0]);
				singleType.add(sublosArr[1]);
				
			}
			Set s = new HashSet(ip);
			if (s.size() > 1) {
				ipEqualFlag = false;
			} else {
				ipEqualFlag = true;
			}
			if (type.size() == singleType.size()) {
				for (int k = 0; k < type.size(); k++) {
					if (type.get(k).equals(singleType.get(k)) == false) {
						ipEqualFlag = false;
						break;
					}
				}
				if (ipEqualFlag == true && typeEqualFlag == true) {
					satisfiedEqualIPEvent.add(record.get(i));
				}
			}

		}
		return satisfiedEqualIPEvent;
	}
}
