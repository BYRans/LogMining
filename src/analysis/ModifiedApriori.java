package analysis;

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

import training.PATHS;

public class ModifiedApriori {
	public int minSup;// 最小支持度
	public static List<Set<String>> recordList;// 以List<Set<String>>格式保存,利用Set的有序性
	public static HashMap<String, String> FEATURE_MAP = new HashMap<String, String>();
	public static int WINDOWN_SIZE = 120;// 窗口大小，分钟为单位
	public static int STEP_SIZE = 30;// 步长大小，分钟为单位
	public static SimpleDateFormat DATE_TEMPLATE = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");
	public static int[] THRESHOLD = { 8 };// 出现次数阈值
	public static int ITEMS_COUNT = 0;
	// public static double[] THRESHOLD = { 0.1 };//百分比阈值

	static {
		try {// 读入feature
			File f = new File(PATHS.FEATURE_FOLDER_PATH);
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
		PATHS.FREQUENT_ITEM_SETS_PATH += "fp_threshold";
		recordList = apriori.readFile(PATHS.MERGE_LOG_PATH);
		for (int k = 0; k < THRESHOLD.length; k++) {
			System.out.println(PATHS.MERGE_LOG_PATH + " THRESHOLD: " + THRESHOLD[k]);
			long totalItem = 0;
			long totalTime = 0;
			FileWriter tgFileWriter = new FileWriter(PATHS.FREQUENT_ITEM_SETS_PATH
					+ (THRESHOLD[k] * 100) + ".txt");
			// apriori.setMinSup((int) (recordList.size() *
			// THRESHOLD[k]));//这句是按百分比取
			apriori.setMinSup(THRESHOLD[k]);// 按出现次数
			long startTime = System.currentTimeMillis();
			Map<String, Integer> f1Set = apriori.findFP1Items(recordList);
			long endTime = System.currentTimeMillis();
			totalTime += endTime - startTime;

			// 频繁1项集信息得加入支持度
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
			System.out.println("共用时：" + totalTime + "ms");
			System.out.println("共有" + totalItem + "项频繁模式");
		}
		System.out.println("Completed.");
	}

	/**
	 * 由频繁K-1项集生成频繁K项集
	 * 
	 * @param preMap
	 *            保存频繁K项集的map
	 * @param tgFileWriter
	 *            输出文件句柄
	 * @return int 频繁i项集的数目
	 * @throws IOException
	 */
	private Map<Set<String>, Integer> genNextKItem(
			Map<Set<String>, Integer> preMap) {
		Map<Set<String>, Integer> result = new HashMap<Set<String>, Integer>();
		// 遍历两个k-1项集生成k项集
		List<Set<String>> preSetArray = new ArrayList<Set<String>>();
		for (Map.Entry<Set<String>, Integer> preMapItem : preMap.entrySet()) {
			preSetArray.add(preMapItem.getKey());
		}
		int preSetLength = preSetArray.size();
		for (int i = 0; i < preSetLength - 1; i++) {
			for (int j = i + 1; j < preSetLength; j++) {
				String[] strA1 = preSetArray.get(i).toArray(new String[0]);
				String[] strA2 = preSetArray.get(j).toArray(new String[0]);
				if (isCanLink(strA1, strA2)) { // 判断两个k-1项集是否符合连接成k项集的条件　
					Set<String> set = new TreeSet<String>();
					for (String str : strA1) {
						set.add(str);
					}
					set.add((String) strA2[strA2.length - 1]); // 连接成k项集
					// 判断k项集是否需要剪切掉，如果不需要被cut掉，则加入到k项集列表中
					if (isNeedCut(preMap, set)) {// 由于单调性，必须保证k项集的所有k-1项子集都在preMap中出现，否则就该剪切该k项集
						result.put(set, 0);
					}
				}
			}
		}
		return assertFP(result);// 遍历事物数据库，求支持度，确保为频繁项集
	}

	/**
	 * 检测k项集是否该剪切。由于单调性，必须保证k项集的所有k-1项子集都在preMap中出现，否则就该剪切该k项集
	 * 
	 * @param preMap
	 *            k-1项频繁集map
	 * @param set
	 *            待检测的k项集
	 * @return boolean 是否该剪切
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
	 * 获取k项集set的所有k-1项子集
	 * 
	 * @param set
	 *            频繁k项集
	 * @return List<Set<String>> 所有k-1项子集容器
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
	 * 遍历事物数据库，求支持度，确保为频繁项集
	 * 
	 * @param allKItem
	 *            候选频繁k项集
	 * @return Map<Set<String>, Integer> 支持度大于阈值的频繁项集和支持度map
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
	 * 检测两个频繁K项集是否可以连接，连接条件是只有最后一个项不同
	 * 
	 * @param strA1
	 *            k项集1
	 * @param strA1
	 *            k项集2
	 * @return boolean 是否可以连接
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
	 * 将频繁i项集的内容及支持度输出到文件 格式为 模式:支持度
	 * 
	 * @param f1Map
	 *            保存频繁i项集的容器<i项集 , 支持度>
	 * @param tgFileWriter
	 *            输出文件句柄
	 * @return int 频繁i项集的数目
	 * @throws IOException
	 */
	private int printMap(Map<Set<String>, Integer> f1Map,
			FileWriter tgFileWriter) throws IOException {

		List<Map.Entry<Set<String>, Integer>> infoIds = new ArrayList<Map.Entry<Set<String>, Integer>>(
				f1Map.entrySet());
		// HashMap排序
		Collections.sort(infoIds,
				new Comparator<Map.Entry<Set<String>, Integer>>() {
					public int compare(Map.Entry<Set<String>, Integer> o1,
							Map.Entry<Set<String>, Integer> o2) {
						return (o2.getValue() - o1.getValue());// 按value，o2-o1降序，o1-o2升序
					}
				});

		tgFileWriter.append("\r\n" + "**********项数： " + (++ITEMS_COUNT)
				+ "**********" + "\r\n");
		tgFileWriter.flush();
		for (int i = 0; i < infoIds.size(); i++) {
			Entry<Set<String>, Integer> ent = infoIds.get(i);
//			System.out.println(ent.getValue() + "\t" + ent.getKey());
			tgFileWriter.append(ent.getValue() + "\t");
			for (String p : ent.getKey()) {
				tgFileWriter.append(p + " ");
			}
			
			//把feature写入文件
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
	 * 生成频繁1项集
	 * 
	 * @param fileDir
	 *            事务文件目录
	 * @return Map<String, Integer> 保存频繁1项集的容器<1项集 , 支持度>
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
	 * 读取事务数据库
	 * 
	 * @param fileDir
	 *            事务文件目录
	 * @return List<String> 保存事务的容器
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
			System.out.println("读取文件失败。");
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
		cal.add(Calendar.MINUTE, WINDOWN_SIZE);// 设置窗口最大时间
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
			cal.add(Calendar.MINUTE, STEP_SIZE);// 窗口最大最小时间下滑一个步长
			minDate = cal.getTime();
			cal.setTime(maxDate);
			cal.add(Calendar.MINUTE, STEP_SIZE);
			maxDate = cal.getTime();
		}

		return records;
	}
}
