package dataFilter;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import training.COMMON_PATH;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class FilterRawLog {

	public static void main(String[] args) throws Exception {
		System.out.println("Structured Running..." + new Date());
		String filterDataPath = "/home/iie/FilteredData/";
		int logCount = 0;// 统计日志总条数

		long startDate = System.currentTimeMillis();

		List<String> list = new ArrayList<String>();
		File f = new File(COMMON_PATH.RAW_LOG_FILE_PATH + "df");
		File[] fileList = f.listFiles();
		double fileTotal = fileList.length;
		double fileCount = 0;
		for (File file : fileList) {
			list = getRealDataContent(file);
			logCount += list.size();
			System.out.println(logCount);
			for (int i = 0; i < list.size(); i++) {
				String[] recordArr = list.get(i).split(";| ");

				String regTimeDay = "^(2014-[0-1]?[0-9]-[0-3]?[0-9])$";
				String regTimeSec = "^([0-9][0-9]:[0-9][0-9]:[0-9][0-9])$";
				String regSegment = "BJLTSH-503-DFA-CL-SEV\\d+";
				if (recordArr.length < 6)
					continue;
				if (!recordArr[3].matches(regTimeDay)) {
					continue;
				}
				if (!recordArr[4].matches(regTimeSec)) {
					continue;
				}
				if (!recordArr[5].matches(regSegment)) {
					continue;
				}

				try {
					BufferedWriter writer = new BufferedWriter(new FileWriter(
							new File(filterDataPath + recordArr[2]), true));
					writer.write(list.get(i));
					writer.newLine();
					writer.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			System.out.println("DataFileter:"
					+ ((++fileCount) / fileTotal * 100) + "%");
		}
		long endDate = System.currentTimeMillis();
		System.out.println(startDate - endDate + "ms");
		System.out.println(logCount);
		System.out.println("Completed." + new Date() + "\n\n");

	}

	// 读日志文件
	public static List<String> getRealDataContent(File file) throws Exception {
		BufferedReader br = new BufferedReader(new InputStreamReader(
				new FileInputStream(file), "GBK"));
		String tempLine = "";
		String curLine = br.readLine();
		String nextLine = "";
		List<String> contents = new ArrayList<String>();
		while (curLine != null) {
			contents.add(curLine);
			curLine = br.readLine();
		}
		br.close();
		return contents;
	}

}