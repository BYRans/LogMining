package analysis;

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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import training.COMMON_PATH;

public class LogMerge {
	
	public static SimpleDateFormat DATE_TEMPLATE = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");
	public static Set<String> REMOVED_LABEL_SET = new HashSet<String>();

	static {
		try {// removed Label set��ʼ��
			File rmLabelFile = new File(COMMON_PATH.REMOVED_LABEL_PATH);
			BufferedReader fReader = new BufferedReader(new InputStreamReader(
					new FileInputStream(rmLabelFile), "UTF-8"));
			String line = null;
			while ((line = fReader.readLine()) != null) {
				if ("".equals(line.trim()))
					continue;
				REMOVED_LABEL_SET.add(line);
			}
			fReader.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	public static void main(String[] args) throws ParseException {
		System.out.println("running...");
		List<String[]> timeLabelList = new ArrayList<String[]>();

		// ����syslog time+label
		try {
			File syslogFile = new File(COMMON_PATH.TIMESTAMP_LABEL_PATH);
			BufferedReader vReader = new BufferedReader(new InputStreamReader(
					new FileInputStream(syslogFile), "UTF-8"));
			String line = null;
			while ((line = vReader.readLine()) != null) {
				if ("".equals(line.trim())) {
					continue;
				}
				String[] timeLabelArr = line.split("\t");
				if (timeLabelArr.length != 2)
					continue;
				if (REMOVED_LABEL_SET.contains(timeLabelArr[1]))
					continue;
				timeLabelList.add(timeLabelArr);
			}
			vReader.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		// ����warning log
		try {
			File warningLogFile = new File(COMMON_PATH.WARNING_LOG_PATH);
			BufferedReader wReader = new BufferedReader(new InputStreamReader(
					new FileInputStream(warningLogFile), "UTF-8"));
			String line = null;
			while ((line = wReader.readLine()) != null) {
				if ("".equals(line.trim())) {
					continue;
				}
				String[] timeLabelArr = line.split("\t");
				if (timeLabelArr.length != 2)
					continue;
				timeLabelList.add(timeLabelArr);
			}
			wReader.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		mergeSort(timeLabelList, 0, 1);

		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File(
					COMMON_PATH.MERGE_LOG_PATH), true));
			for (int i = 0; i < timeLabelList.size(); i++) {
				writer.write(timeLabelList.get(i)[0] + "\t"
						+ timeLabelList.get(i)[1]);
				writer.newLine();
				writer.flush();
			}
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("Completed.");
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
	 *            ÿ�ι鲢�����򼯺ϵĳ���
	 * @throws ParseException
	 **/
	public static void mergeSort(List<String[]> list, int s, int len)
			throws ParseException {
		int size = list.size();
		int mid = size / (len << 1);
		int c = size & ((len << 1) - 1);
		// -------�鲢��ֻʣһ�����򼯺ϵ�ʱ������㷨-------//
		if (mid == 0)
			return;
		// ------����һ�˹鲢����-------//
		for (int i = 0; i < mid; ++i) {
			s = i * 2 * len;
			merge(list, s, s + len, (len << 1) + s - 1);
		}
		// -------��ʣ�µ����͵���һ�����򼯺Ϲ鲢-------//
		if (c != 0)
			merge(list, size - c - 2 * len, size - c, size - 1);
		// -------�ݹ�ִ����һ�˹鲢����------//
		mergeSort(list, 0, 2 * len);
	}
}
