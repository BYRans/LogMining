package old_training;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import training.COMMON_PATH;

public class Old2_Tagging {

	public static void main(String[] args) throws ParseException {
		System.out.println("Tagging Running..."+new Date());
		long startTime = System.currentTimeMillis();
		COMMON_PATH.DELETE_FILE(COMMON_PATH.LABEL_DOCIDS_PATH);// 写入Label
																// docIds文件前先删除原文件
		COMMON_PATH.DELETE_FILE(COMMON_PATH.LABEL_VECTOR_PATH);// 写入Label
																// Vector文件前先删除原文件

		List<String> vectorList = new ArrayList<String>();
		try {
			System.out.println("Reading Vector.txt..."+new Date());
			File vectorFile = new File(COMMON_PATH.VECTOR_PATH);
			BufferedReader vReader = new BufferedReader(new InputStreamReader(
					new FileInputStream(vectorFile), "UTF-8"));
			String vLine = "";
			int lineCount = 0;
			String tmpDocIds = "";
			while ((vLine = vReader.readLine()) != null) {
				if ("".equals(vLine.trim()))
					continue;
				vectorList.add(vLine);
			}
			vReader.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		System.out.println("MergeSorting..."+new Date());
		mergeSort(vectorList, 0, 1);

		ArrayList<ArrayList<String>> finalVectorList = new ArrayList<ArrayList<String>>();
		ArrayList<String> tmpList = new ArrayList<String>();
		int lastLenth = vectorList.get(0).length();
		String tmpDocIds = "";
		HashMap<String, String> labelVectorMap = new HashMap<String, String>();

		int kCount = 0;
		int vCount = 0;

		int percent = (int)(vectorList.size()*0.005);
		long percentStartTime = System.currentTimeMillis();
		
		System.out.println("Tagging..."+new Date());
		for (int i = 0; i < vectorList.size(); i++) {
			if (vectorList.get(i).length() != lastLenth) {
				lastLenth = vectorList.get(i).length();
				// 把该长度的Label Vector写入文件
				try {
					BufferedWriter LVWriter = new BufferedWriter(
							new FileWriter(new File(
									COMMON_PATH.LABEL_VECTOR_PATH), true));
					for (Map.Entry<String, String> ic : labelVectorMap
							.entrySet()) {
						LVWriter.write("L" + (kCount++) + "\t" + ic.getKey());
						LVWriter.newLine();
						LVWriter.flush();
					}
					LVWriter.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
				// 把该长度的Label docIds写入文件
				try {
					BufferedWriter DLWriter = new BufferedWriter(
							new FileWriter(new File(
									COMMON_PATH.LABEL_DOCIDS_PATH), true));
					for (Map.Entry<String, String> ic : labelVectorMap
							.entrySet()) {
						DLWriter.write("L" + (vCount++) + "\t" + ic.getValue());
						DLWriter.newLine();

					}
					DLWriter.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
				tmpDocIds = "";
				labelVectorMap = new HashMap<String, String>();
			}

			String[] lineArr = vectorList.get(i).split("\t");

			String vectorStr = "";
			if (lineArr.length > 1)
				vectorStr = lineArr[1];
			else {
				System.out
						.println("wrong data docId(lineCount): " + lineArr[0]);
			}

			if (labelVectorMap.containsKey(vectorStr)) {
				tmpDocIds = labelVectorMap.get(vectorStr) + "," + lineArr[0];
				labelVectorMap.put(vectorStr, tmpDocIds);
			} else {
				labelVectorMap.put(vectorStr, lineArr[0]);
			}
			
			if(i%percent==0){
				System.out.println("Tagged:"+(double)i/vectorList.size()*100+"%");
				System.out.println("process 0.5%("+percent+"records)，use "+(System.currentTimeMillis() - percentStartTime)/1000+"S\n");
				percentStartTime = System.currentTimeMillis();
			}
		
		}

		// 把最后一组同一长度的Label Vector写入文件
		try {
			BufferedWriter LVWriter = new BufferedWriter(new FileWriter(
					new File(COMMON_PATH.LABEL_VECTOR_PATH), true));
			for (Map.Entry<String, String> ic : labelVectorMap.entrySet()) {
				LVWriter.write("L" + (kCount++) + "\t" + ic.getKey());
				LVWriter.newLine();
				LVWriter.flush();
			}
			LVWriter.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 把最后一组同一长度的Label docIds写入文件
		try {
			BufferedWriter DLWriter = new BufferedWriter(new FileWriter(
					new File(COMMON_PATH.LABEL_DOCIDS_PATH), true));
			for (Map.Entry<String, String> ic : labelVectorMap.entrySet()) {
				DLWriter.write("L" + (vCount++) + "\t" + ic.getValue());
				DLWriter.newLine();

			}
			DLWriter.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Completed."+new Date()+"\n\n");
	}

	private static void merge(List<String> list, int s, int m, int t)
			throws ParseException {
		List<String> tmpList = new ArrayList<String>();
		int i = s, j = m;
		while (i < m && j <= t) {
			if (list.get(i).length() <= list.get(j).length()) {
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
	 *            每次归并的有序集合的长度
	 * @throws ParseException
	 **/
	public static void mergeSort(List<String> list, int s, int len)
			throws ParseException {
		int size = list.size();
		int mid = size / (len << 1);
		int c = size & ((len << 1) - 1);
		// -------归并到只剩一个有序集合的时候结束算法-------//
		if (mid == 0)
			return;
		// ------进行一趟归并排序-------//
		for (int i = 0; i < mid; ++i) {
			s = i * 2 * len;
			merge(list, s, s + len, (len << 1) + s - 1);
		}
		// -------将剩下的数和倒数一个有序集合归并-------//
		if (c != 0)
			merge(list, size - c - 2 * len, size - c, size - 1);
		// -------递归执行下一趟归并排序------//
		mergeSort(list, 0, 2 * len);
	}
}
