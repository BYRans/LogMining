package old_analysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.Map.Entry;

import training.COMMON_PATH;

public class ModifiedApriori {
	public int minSup;// ��С֧�ֶ�
	public static List<Set<String>> recordList;// ��List<Set<String>>��ʽ����,����Set��������
	public static HashMap<String, String> FEATURE_MAP = new HashMap<String, String>();
	public static int WINDOWN_SIZE = 120;// ���ڴ�С������Ϊ��λ
	public static int STEP_SIZE = 30;// ������С������Ϊ��λ
	public static SimpleDateFormat DATE_TEMPLATE = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");
	public static int[] THRESHOLD = { 8 };// ���ִ�����ֵ
	public static int ITEMS_COUNT = 0;
	// public static double[] THRESHOLD = { 0.1 };//�ٷֱ���ֵ

	static {
		try {// ����feature
			File f = new File(COMMON_PATH.FEATURE_FOLDER_PATH);
			File[] fileList = f.listFiles();
			for (File file : fileList) {
				BufferedReader fReader = new BufferedReader(
						new InputStreamReader(new FileInputStream(file),
								"UTF-8"));
				String line = null;
				while ((line = fReader.readLine()) != null) {
					if ("".equals(line.trim())) {
						continue;
					}
					String[] tmpArr = line.split("\t");
					if (tmpArr.length != 2)
						continue;
					FEATURE_MAP.put(tmpArr[0], tmpArr[1]);
				}
				fReader.close();
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	public int getMinSup() {
		return minSup;
	}

	public void setMinSup(int minSup) {
		this.minSup = minSup;
	}

	/**
	 * @param args
	 * @throws ParseException
	 */
	public static void main(String[] args) throws IOException, ParseException {
		System.out.println("running...");

		ModifiedApriori apriori = new ModifiedApriori();
		COMMON_PATH.FREQUENT_ITEM_SETS_PATH += "fp_threshold";
		recordList = apriori.readFile(COMMON_PATH.MERGE_LOG_PATH);
		for (int k = 0; k < THRESHOLD.length; k++) {
			System.out.println(COMMON_PATH.MERGE_LOG_PATH + " THRESHOLD: " + THRESHOLD[k]);
			long totalItem = 0;
			long totalTime = 0;
			FileWriter tgFileWriter = new FileWriter(COMMON_PATH.FREQUENT_ITEM_SETS_PATH
					+ (THRESHOLD[k] * 100) + ".txt");
			// apriori.setMinSup((int) (recordList.size() *
			// THRESHOLD[k]));//����ǰ��ٷֱ�ȡ
			apriori.setMinSup(THRESHOLD[k]);// �����ִ���
			long startTime = System.currentTimeMillis();
			Map<String, Integer> f1Set = apriori.findFP1Items(recordList);
			long endTime = System.currentTimeMillis();
			totalTime += endTime - startTime;

			// Ƶ��1���Ϣ�ü���֧�ֶ�
			Map<Set<String>, Integer> f1Map = new HashMap<Set<String>, Integer>();
			for (Map.Entry<String, Integer> f1Item : f1Set.entrySet()) {
				Set<String> fs = new HashSet<String>();
				fs.add(f1Item.getKey());
				f1Map.put(fs, f1Item.getValue());
			}

			totalItem += apriori.printMap(f1Map, tgFileWriter);
			Map<Set<String>, Integer> result = f1Map;
			do {
				System.out.println(result.size());
				startTime = System.currentTimeMillis();
				result = apriori.genNextKItem(result);
				endTime = System.currentTimeMillis();
				totalTime += endTime - startTime;
				totalItem += apriori.printMap(result, tgFileWriter);
				tgFileWriter.flush();
			} while (result.size() != 0);
			tgFileWriter.close();
			System.out.println("����ʱ��" + totalTime + "ms");
			System.out.println("����" + totalItem + "��Ƶ��ģʽ");
		}
		System.out.println("Completed.");
	}

	/**
	 * ��Ƶ��K-1����Ƶ��K�
	 * 
	 * @param preMap
	 *            ����Ƶ��K���map
	 * @param tgFileWriter
	 *            ����ļ����
	 * @return int Ƶ��i�����Ŀ
	 * @throws IOException
	 */
	private Map<Set<String>, Integer> genNextKItem(
			Map<Set<String>, Integer> preMap) {
		Map<Set<String>, Integer> result = new HashMap<Set<String>, Integer>();
		// ��������k-1����k�
		List<Set<String>> preSetArray = new ArrayList<Set<String>>();
		for (Map.Entry<Set<String>, Integer> preMapItem : preMap.entrySet()) {
			preSetArray.add(preMapItem.getKey());
		}
		int preSetLength = preSetArray.size();
		for (int i = 0; i < preSetLength - 1; i++) {
			for (int j = i + 1; j < preSetLength; j++) {
				String[] strA1 = preSetArray.get(i).toArray(new String[0]);
				String[] strA2 = preSetArray.get(j).toArray(new String[0]);
				if (isCanLink(strA1, strA2)) { // �ж�����k-1��Ƿ������ӳ�k���������
					Set<String> set = new TreeSet<String>();
					for (String str : strA1) {
						set.add(str);
					}
					set.add((String) strA2[strA2.length - 1]); // ���ӳ�k�
					// �ж�k��Ƿ���Ҫ���е��������Ҫ��cut��������뵽k��б���
					if (isNeedCut(preMap, set)) {// ���ڵ����ԣ����뱣֤k�������k-1���Ӽ�����preMap�г��֣�����͸ü��и�k�
						result.put(set, 0);
					}
				}
			}
		}
		return assertFP(result);// ����������ݿ⣬��֧�ֶȣ�ȷ��ΪƵ���
	}

	/**
	 * ���k��Ƿ�ü��С����ڵ����ԣ����뱣֤k�������k-1���Ӽ�����preMap�г��֣�����͸ü��и�k�
	 * 
	 * @param preMap
	 *            k-1��Ƶ����map
	 * @param set
	 *            �����k�
	 * @return boolean �Ƿ�ü���
	 * @throws IOException
	 */
	private boolean isNeedCut(Map<Set<String>, Integer> preMap, Set<String> set) {
		boolean FLAG = false;
		List<Set<String>> subSets = getSubSets(set);
		for (Set<String> subSet : subSets) {
			if (subSet.contains("2002")||subSet.contains("2004")||subSet.contains("2008")) {
				FLAG = true;
			}
		}
		return FLAG;
	}

	/**
	 * ��ȡk�set������k-1���Ӽ�
	 * 
	 * @param set
	 *            Ƶ��k�
	 * @return List<Set<String>> ����k-1���Ӽ�����
	 * @throws IOException
	 */
	private List<Set<String>> getSubSets(Set<String> set) {
		String[] setArray = set.toArray(new String[0]);
		List<Set<String>> result = new ArrayList<Set<String>>();
		for (int i = 0; i < setArray.length; i++) {
			Set<String> subSet = new HashSet<String>();
			for (int j = 0; j < setArray.length; j++) {
				if (j != i)
					subSet.add(setArray[j]);
			}
			result.add(subSet);
		}
		return result;
	}

	/**
	 * ����������ݿ⣬��֧�ֶȣ�ȷ��ΪƵ���
	 * 
	 * @param allKItem
	 *            ��ѡƵ��k�
	 * @return Map<Set<String>, Integer> ֧�ֶȴ�����ֵ��Ƶ�����֧�ֶ�map
	 * @throws IOException
	 */
	private Map<Set<String>, Integer> assertFP(
			Map<Set<String>, Integer> allKItem) {
		Map<Set<String>, Integer> result = new HashMap<Set<String>, Integer>();
		for (Set<String> kItem : allKItem.keySet()) {
			for (Set<String> data : recordList) {
				boolean FLAG = true;
				for (String str : kItem) {
					if (!data.contains(str)) {
						FLAG = false;
						break;
					}
				}
				if (FLAG)
					allKItem.put(kItem, allKItem.get(kItem) + 1);
			}
			if (allKItem.get(kItem) >= minSup) {
				result.put(kItem, allKItem.get(kItem));
			}
		}
		return result;
	}

	/**
	 * �������Ƶ��K��Ƿ�������ӣ�����������ֻ�����һ���ͬ
	 * 
	 * @param strA1
	 *            k�1
	 * @param strA1
	 *            k�2
	 * @return boolean �Ƿ��������
	 * @throws IOException
	 */
	private boolean isCanLink(String[] strA1, String[] strA2) {
		boolean FLAG = true;
		if (strA1.length != strA2.length) {
			return false;
		} else {
			for (int i = 0; i < strA1.length - 1; i++) {
				if (!strA1[i].equals(strA2[i])) {
					FLAG = false;
					break;
				}
			}
			if (strA1[strA1.length - 1].equals(strA2[strA1.length - 1])) {
				FLAG = false;
			}
		}
		return FLAG;
	}

	/**
	 * ��Ƶ��i������ݼ�֧�ֶ�������ļ� ��ʽΪ ģʽ:֧�ֶ�
	 * 
	 * @param f1Map
	 *            ����Ƶ��i�������<i� , ֧�ֶ�>
	 * @param tgFileWriter
	 *            ����ļ����
	 * @return int Ƶ��i�����Ŀ
	 * @throws IOException
	 */
	private int printMap(Map<Set<String>, Integer> f1Map,
			FileWriter tgFileWriter) throws IOException {

		List<Map.Entry<Set<String>, Integer>> infoIds = new ArrayList<Map.Entry<Set<String>, Integer>>(
				f1Map.entrySet());
		// HashMap����
		Collections.sort(infoIds,
				new Comparator<Map.Entry<Set<String>, Integer>>() {
					public int compare(Map.Entry<Set<String>, Integer> o1,
							Map.Entry<Set<String>, Integer> o2) {
						return (o2.getValue() - o1.getValue());// ��value��o2-o1����o1-o2����
					}
				});

		tgFileWriter.append("\r\n" + "**********���� " + (++ITEMS_COUNT)
				+ "**********" + "\r\n");
		tgFileWriter.flush();
		for (int i = 0; i < infoIds.size(); i++) {
			Entry<Set<String>, Integer> ent = infoIds.get(i);
//			System.out.println(ent.getValue() + "\t" + ent.getKey());
			tgFileWriter.append(ent.getValue() + "\t");
			for (String p : ent.getKey()) {
				tgFileWriter.append(p + " ");
			}
			
			//��featureд���ļ�
			tgFileWriter.append("\r\n" + "\t");
			for (String p : ent.getKey()) {
				tgFileWriter.append("( " + FEATURE_MAP.get(p) + " ) ");
			}

			tgFileWriter.append("\r\n");
			tgFileWriter.flush();
		}
		return f1Map.size();
	}

	/**
	 * ���Ƶ��1�
	 * 
	 * @param fileDir
	 *            �����ļ�Ŀ¼
	 * @return Map<String, Integer> ����Ƶ��1�������<1� , ֧�ֶ�>
	 * @throws IOException
	 */
	private Map<String, Integer> findFP1Items(List<Set<String>> recordList) {
		Map<String, Integer> result = new HashMap<String, Integer>();
		Map<String, Integer> itemCount = new HashMap<String, Integer>();
		for (Set<String> ds : recordList) {
			for (String d : ds) {
				if (itemCount.containsKey(d)) {
					itemCount.put(d, itemCount.get(d) + 1);
				} else {
					itemCount.put(d, 1);
				}
			}
		}

		for (Map.Entry<String, Integer> ic : itemCount.entrySet()) {
			if (ic.getValue() >= minSup) {
				result.put(ic.getKey(), ic.getValue());
			}
		}
		return result;
	}

	/**
	 * ��ȡ������ݿ�
	 * 
	 * @param fileDir
	 *            �����ļ�Ŀ¼
	 * @return List<String> �������������
	 * @throws ParseException
	 * @throws IOException
	 */
	@SuppressWarnings("resource")
	private List<Set<String>> readFile(String fileDir) throws ParseException {
		List<Set<String>> records = new ArrayList<Set<String>>();
		List<String[]> dataList = new ArrayList<String[]>();
		try {
			FileReader fr = new FileReader(new File(fileDir));
			BufferedReader br = new BufferedReader(fr);
			String line = null;
			while ((line = br.readLine()) != null) {
				if (line.trim() != "") {
					String[] dataArr = line.split("\t");
					dataList.add(dataArr);
				}
			}
		} catch (IOException e) {
			System.out.println("��ȡ�ļ�ʧ�ܡ�");
			System.exit(-2);
		}

		if (dataList.size() == 0)
			return records;
		String tmpDate = (dataList.get(0))[0];
		Date minDate = DATE_TEMPLATE.parse(tmpDate);
		tmpDate = (dataList.get(dataList.size() - 1))[0];
		Date endDate = DATE_TEMPLATE.parse(tmpDate);
		Calendar cal = Calendar.getInstance();
		cal.setTime(minDate);
		cal.add(Calendar.MINUTE, WINDOWN_SIZE);// ���ô������ʱ��
		Date maxDate = cal.getTime();

		while (minDate.getTime() < endDate.getTime()) {
			List<String[]> tmpDataList = new ArrayList<String[]>();
			for (int i = 0; i < dataList.size(); i++) {
				tmpDate = (dataList.get(i))[0];
				Date timeStamp = DATE_TEMPLATE.parse(tmpDate);
				if (timeStamp.getTime() >= minDate.getTime()
						&& timeStamp.getTime() < maxDate.getTime()) {
					tmpDataList.add(dataList.get(i));
				}
			}

			Set<String> record = new HashSet<String>();
			if (tmpDataList.size() > 0) {
				for (String[] item : tmpDataList) {
					record.add(item[1]);
				}
				records.add(record);
			}

			cal.setTime(minDate);
			cal.add(Calendar.MINUTE, STEP_SIZE);// ���������Сʱ���»�һ������
			minDate = cal.getTime();
			cal.setTime(maxDate);
			cal.add(Calendar.MINUTE, STEP_SIZE);
			maxDate = cal.getTime();
		}

		return records;
	}
}
