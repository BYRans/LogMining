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

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.TotalHitCountCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import training.COMMON_PATH;

public class LogMerge {

	public static SimpleDateFormat DATE_TEMPLATE = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");
	public static Set<String> REMOVED_LABEL_SET = new HashSet<String>();
	public static List<String[]> TIME_LABEL_LIST = new ArrayList<String[]>();
	public static Set<String> IP_SET = new HashSet<String>();

	public static void main(String[] args) throws ParseException {
		System.out.println("running...");
		long startTime = System.currentTimeMillis();
		COMMON_PATH.INIT_DIR(COMMON_PATH.MERGE_LOG_PATH);// 写入mergeLog文件夹前先初始化文件夹

		// 读入ip set
		readIPs(COMMON_PATH.IP_LIST_PATH);
		int i = 0;
		long percentStartTime = System.currentTimeMillis();
		for (String ip : IP_SET) {
			TIME_LABEL_LIST = new ArrayList<String[]>();
			// removed Label set初始化
			initRemovedLabelSet(COMMON_PATH.REMOVED_LABEL_PATH);
			// 读入syslog time+label
			readSyslog(COMMON_PATH.LABELED_LUCENE_PATH, ip);
			// 读入warning log time+label
			readWarningLog(COMMON_PATH.WARNING_LOG_PATH, ip);
			// 按时间戳归并排序syslog和warning log
			mergeSort(TIME_LABEL_LIST, 0, 1);
			// 写入排序后合并日志
			writeMergeLog(COMMON_PATH.MERGE_LOG_PATH, ip);
			System.out.println("Merged" + (i++) + "/" + IP_SET.size() + "use"
					+ (System.currentTimeMillis() - percentStartTime) / 1000
					+ "S\n");
			percentStartTime = System.currentTimeMillis();
		}
		System.out.println("LogMerge Completed.used "+(System.currentTimeMillis() - startTime)/1000+"S\n");
	}

	// 读入ip列表
	private static void readIPs(String path) {
		try {
			File rmLabelFile = new File(path);
			BufferedReader fReader = new BufferedReader(new InputStreamReader(
					new FileInputStream(rmLabelFile), "UTF-8"));
			String line = null;
			while ((line = fReader.readLine()) != null) {
				if ("".equals(line.trim()))
					continue;
				IP_SET.add(line.trim());
			}
			fReader.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	// removed Label set初始化
	private static void initRemovedLabelSet(String path) {
		try {
			File rmLabelFile = new File(path);
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

	// 读入syslog timeStamp+label
	private static void readSyslog(String path, String ip) {
		Directory directory = null;
		IndexReader reader = null;
		try {
			directory = FSDirectory.open(new File(path));
			reader = IndexReader.open(directory);
			IndexSearcher searcher = new IndexSearcher(reader);
			Term term1 = new Term("ip", ip);
			System.out.println(ip);
			TermQuery query1 = new TermQuery(term1);
			BooleanQuery booleanQuery = new BooleanQuery();
			booleanQuery.add(query1, Occur.MUST);
			TotalHitCountCollector collector = new TotalHitCountCollector();
			searcher.search(booleanQuery, collector);
			System.out.println(collector.getTotalHits());
			int hits = Math.max(1, collector.getTotalHits());
			TopDocs tds = searcher.search(booleanQuery, hits);
			ScoreDoc[] sds = tds.scoreDocs;
			int[] docCount = new int[hits];
			for (int i = 0; i < sds.length; i++) {
				docCount[i] = sds[i].doc;
				Document document = searcher.doc(docCount[i]);
				String[] timeLabelArr = new String[2];
				timeLabelArr[0] = document.get("timeStamp");
				timeLabelArr[1] = document.get("label");
				if (REMOVED_LABEL_SET.contains(timeLabelArr[1]))
					continue;
				TIME_LABEL_LIST.add(timeLabelArr);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	// 读入warning log timeStamp+label
	private static void readWarningLog(String path, String ip) {
		try {
			File warningLogFile = new File(path + ip);
			BufferedReader wReader = new BufferedReader(new InputStreamReader(
					new FileInputStream(warningLogFile), "UTF-8"));
			String line = null;
			while ((line = wReader.readLine()) != null) {

				if ("".equals(line.trim())) {
					continue;
				}
				String[] logArr = line.split(",");
				if (logArr.length < 15)
					continue;
				String[] timeLabelArr = { logArr[3].replace("/", "-"),
						logArr[13] };
				if (timeLabelArr.length != 2)
					continue;
				TIME_LABEL_LIST.add(timeLabelArr);
			}
			wReader.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	// 写入排序后合并日志
	private static void writeMergeLog(String path, String ip) {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File(
					path + "mergeLog_" + ip + ".txt"), true));
			for (int i = 0; i < TIME_LABEL_LIST.size(); i++) {
				writer.write(TIME_LABEL_LIST.get(i)[0] + "\t"
						+ TIME_LABEL_LIST.get(i)[1]);
				writer.newLine();
				writer.flush();
			}
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
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
	 *            每次归并的有序集合的长度
	 * @throws ParseException
	 **/
	public static void mergeSort(List<String[]> list, int s, int len)
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
