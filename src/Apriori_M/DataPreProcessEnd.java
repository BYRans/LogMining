package AprioriEnd;

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
import java.util.Date;
import java.util.List;

public class DataPreProcessEnd {
	public static List<String[]> TIME_LABEL_LIST = new ArrayList<String[]>();
	public static List<List<String>> TIME_LABEL_NEW_LIST = new ArrayList<List<String>>();
	public static SimpleDateFormat DATE_TEMPLATE = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");
	public static List<List<String>> TRANSACTIONS = new ArrayList<List<String>>();

	public static void main(String[] args) throws ParseException {
		System.out.println("running......");
		long startTime = System.currentTimeMillis();

		String warningLog = "";
		String faultLog = "";
		MergeFile(warningLog, faultLog);
		mergeSort(TIME_LABEL_LIST, 0, 1); // 灏嗘棩蹇楁寜鐓ф椂闂存埑杩涜鎺掑簭
		writeSortedFile(TIME_LABEL_LIST);
		System.out.println("end......");
		System.out.println("执行耗时:" + (System.currentTimeMillis() - startTime)
				+ "ms");
	}

	private static void MergeFile(String warningLog, String faultLog) {
		try {
			File warningLogFile = new File(warningLog);
			BufferedReader wReader = new BufferedReader(new InputStreamReader(
					new FileInputStream(warningLogFile), "UTF-8"));
			String line = null;
			while ((line = wReader.readLine()) != null) { // read warning file
				if ("".equals(line.trim())) { // 閬囩┖琛岋紝鐣ヨ繃缁х画鎵ц
					continue;
				}
				String[] logArr = line.split("\t");
				if (logArr.length != 4) {
					continue;
				}
				String[] timeLabelArr = { logArr[2],
						logArr[1] + "_" + logArr[3] + "a" };
				if (timeLabelArr.length != 2)
					continue;
				TIME_LABEL_LIST.add(timeLabelArr);
			}
			wReader.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		try {
			BufferedReader wReader = new BufferedReader(new InputStreamReader(
					new FileInputStream(faultLog), "UTF-8"));
			String line = null;
			while ((line = wReader.readLine()) != null) {
				if ("".equals(line.trim())) {
					continue;
				}
				String[] logArr = line.split("\t");
				String[] timeLabelArr = { logArr[1],
						logArr[3] + "_" + logArr[0] + "f" };
				if (timeLabelArr.length != 2)
					continue;
				TIME_LABEL_LIST.add(timeLabelArr);
			}
			wReader.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

	}

	private static void readWarningLog(String path) {

		try {
			File warningLogFile = new File(path);
			BufferedReader wReader = new BufferedReader(new InputStreamReader(
					new FileInputStream(warningLogFile), "UTF-8"));
			String line = null;
			while ((line = wReader.readLine()) != null) {
				if ("".equals(line.trim())) { // 閬囩┖琛岋紝鐣ヨ繃缁х画鎵ц
					continue;
				}

				String[] logArr = line.split("\t");
				if (logArr.length != 4) {
					continue;
				}

				String[] timeLabelArr = { logArr[2],
						logArr[1] + "_" + logArr[3] };
				if (timeLabelArr.length != 2)
					continue;
				TIME_LABEL_LIST.add(timeLabelArr);
			}
			wReader.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		// for(String[] str :TIME_LABEL_LIST){
		// System.out.println(str[1]);
		// }

	}

	private static void merge(List<String[]> list, int s, int m, int t)
			throws ParseException {
		List<String[]> tmpList = new ArrayList<String[]>();
		int i = s, j = m;
		while (i < m && j <= t) {
			Date dateI = DATE_TEMPLATE.parse(list.get(i)[0]);
			Date dateJ = DATE_TEMPLATE.parse(list.get(j)[0]);
			if (dateI.getTime() <= dateJ.getTime()) {
				tmpList.add(list.get(i));
				i++;
			} else {
				tmpList.add(list.get(j));
				j++;
			}
		}
		while (i < m) {
			tmpList.add(list.get(i));
			i++;
		}

		while (j <= t) {
			tmpList.add(list.get(j));
			j++;
		}
		for (int c = 0; c < tmpList.size(); c++) {
			list.set(s + c, tmpList.get(c));
		}
	}

	/**
	 * @param a
	 * @param s
	 * @param len
	 *            婵絽绻戦鑹般亹閹烘垼瀚欓柣銊ュ濠�焦鎯旇箛娑欒偁闁告艾鐗忓▓鎴︽⒐閸喖顔�
	 * @throws ParseException
	 **/
	public static void mergeSort(List<String[]> list, int s, int len)
			throws ParseException {
		int size = list.size();
		int mid = size / (len << 1);
		int c = size & ((len << 1) - 1);
		// -------鐟滅増甯掗懟鐔煎礆閺夊灝娑ч柛鎾櫃缁斿瓨绋夐鍛畳閹兼潙绻樺▔锕傚触閸垺鐣遍柡鍐硾閿熺晫绱掗幘瀛樺皢缂佺姵顨濈涵锟�-----//
		if (mid == 0)
			return;
		// ------閺夆晜绋栭、鎴炵▔閿熺晫顭佺憸鐗堝笒閼荤喖骞掗幒鎴犵-------//
		for (int i = 0; i < mid; ++i) {
			s = i * 2 * len;
			merge(list, s, s + len, (len << 1) + s - 1);
		}
		// -------閻忓繐妫楁晶鎸庣▔鐎ｎ剚鐣遍柡浣规緲閹蜂即宕愰幒鎾存濞戞搫鎷烽柌婊堝嫉婢跺﹦纰嶉梻鍡楁閹氦銇愰幒鎴ｅ珯-------//
		if (c != 0)
			merge(list, size - c - 2 * len, size - c, size - 1);
		// -------闂侇偅甯掔紞濠囧箥瑜戦、鎴炵▔鐎ｂ晝顏遍悺鎺斿枎缂嶅﹪鐛懜闈涚瑩閹艰揪鎷�----//
		mergeSort(list, 0, 2 * len);
	}

	public static void convertFormat(List<String[]> TIME_LABEL_LIST,
			List<List<String>> TIME_LABEL_NEW_LIST) {
		for (String[] str : TIME_LABEL_LIST) {
			List<String> tempList = new ArrayList<String>();
			tempList.add(str[0]);
			tempList.add(str[1]);
			TIME_LABEL_NEW_LIST.add(tempList);
		}
		for (List list1 : TIME_LABEL_NEW_LIST) {
			System.out.println(list1.get(0));
			System.out.println(list1.get(1));
		}
	}

	// 灏嗘寜鏃堕棿鎴虫帓搴忓悗鐨勬枃浠朵繚瀛樺埌鏂囦欢涓�
	public static void writeSortedFile(List<String[]> TIME_LABEL_LIST) {

		String TimeSortedLogFile = "";
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File(
					TimeSortedLogFile), false));
			for (String[] record : TIME_LABEL_LIST) {
				writer.write(record[0] + "\t" + record[1]);
				writer.newLine();
				writer.flush();
			}
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
