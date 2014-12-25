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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class LogMergeByLCS {
	public static String LabelVectorPath = "C:/Users/Administrator/Desktop/LogMining/LabelVector.txt";
	public static String LabelSetPath = "C:/Users/Administrator/Desktop/LogMining/LabelSet.txt";
	public static String LabelSetDocIdsPath = "C:/Users/Administrator/Desktop/LogMining/LabelSetDocIds.txt";
	public static String LucenePath = "C:/Documents and Settings/js4/桌面/LogMining/luceneFile/";
	public static String LabelDocIdsPath = "C:/Users/Administrator/Desktop/LogMining/LabelDocIds.txt";
	public static double Similarity = 0.8;
	public static List<String[]> labelVectorList = new ArrayList<String[]>();
	public static HashMap<String, String> labelDocIdsMap = new HashMap<String, String>();
	public static int lcs = 0;
	public static String similarLabels = "";
	public static List<String> LabelSetList = new LinkedList<String>();
	public static boolean[] flag;

	static {
		// 读Label vector文件
		try {
			File LabelVectorFile = new File(LabelVectorPath);
			BufferedReader vReader = new BufferedReader(new InputStreamReader(
					new FileInputStream(LabelVectorFile), "UTF-8"));
			String line = vReader.readLine();
			while (line != null) {
				if ("".equals(line.trim())) {
					line = vReader.readLine();
					continue;
				}
				String[] labelVectorArr = line.split("\t|,");
				labelVectorList.add(labelVectorArr);
				line = vReader.readLine();
			}
			vReader.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		// 读label docIds文件
		try {
			File LabelDocIDsFile = new File(LabelDocIdsPath);
			BufferedReader dReader = new BufferedReader(new InputStreamReader(
					new FileInputStream(LabelDocIDsFile), "UTF-8"));
			String line = dReader.readLine();
			while (line != null) {
				if ("".equals(line.trim())) {
					line = dReader.readLine();
					continue;
				}
				String[] labelDocIdsArr = line.split("\t");
				labelDocIdsMap.put(labelDocIdsArr[0], labelDocIdsArr[1]);
				line = dReader.readLine();
			}
			dReader.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	public static void main(String[] args) {
		System.out.println("Running...");
		int[][] LCSArr = new int[labelVectorList.size()][labelVectorList.size()];
		for (int i = 0; i < labelVectorList.size(); i++) {
			String[] rowStr = labelVectorList.get(i);
			for (int j = 0; j < labelVectorList.size(); j++) {
				String[] colStr = labelVectorList.get(j);
				LCSAlgorithm(rowStr, colStr);
				double rowStr_lcs = (double) lcs / (rowStr.length - 1);// 因为数组第一个是Label，计算长度时就不计算label。
				double colStr_lcs = (double) lcs / (colStr.length - 1);
				if (rowStr_lcs >= Similarity && colStr_lcs >= Similarity) {
					LCSArr[i][j] = 1;
				} else {
					LCSArr[i][j] = 0;
				}
			}
		}
		String[] vertexs = new String[labelVectorList.size()];
		for (int i = 0; i < labelVectorList.size(); i++) {
			String[] LVArr = labelVectorList.get(i);
			vertexs[i] = LVArr[0];
		}
		DFSTraverse(LCSArr.length, vertexs, LCSArr);

		// 把Label Set写入文件
		Directory directory = null;
		IndexReader reader = null;
		try {
			directory = FSDirectory.open(new File(LucenePath));
			reader = IndexReader.open(directory);
			Document document = null;

			try {
				BufferedWriter writer = new BufferedWriter(new FileWriter(
						new File(LabelSetPath), true));

				for (int i = 0; i < LabelSetList.size(); i++) {
					writer.write("===========Label_" + i + "===============");
					writer.newLine();
					writer.write(LabelSetList.get(i));
					writer.newLine();
					writer.newLine();
				}
				writer.write("***************************");
				writer.newLine();
				writer.newLine();

				for (int i = 0; i < LabelSetList.size(); i++) {
					String[] labelArr = LabelSetList.get(i).split(",");
					writer.write("===========Label_" + i + "===============");
					writer.newLine();
					for (int j = 0; j < labelArr.length; j++) {
						String docIds = labelDocIdsMap.get(labelArr[j]);
						String[] docIdArr = docIds.split(",");
						for (int k = 0; k < docIdArr.length; k++) {
							document = reader.document(Integer
									.valueOf(docIdArr[k]));
							writer.write("MESSAGE:" + document.get("message")
									+ " <-- SOURCE:" + document.get("source"));
							writer.newLine();
						}
					}

				}
				writer.flush();
				writer.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 把Label docIds写入文件
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File(
					LabelSetDocIdsPath), true));

			for (int i = 0; i < LabelSetList.size(); i++) {
				String[] labelsArr = LabelSetList.get(i).split(",");
				String docIds = "";
				for (int j = 0; j < labelsArr.length; j++) {
					docIds += labelDocIdsMap.get(labelsArr[j]);
				}
				writer.write("Label_" + i + "\t" + docIds);
				writer.newLine();
				writer.newLine();
			}
			writer.flush();
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Completed.");
	}

	// 最长公共子串
	public static void LCSAlgorithm(String[] x, String[] y) {
		lcs = 0;// lcs是全局变量，所以每次调用LCS算法都要重新初始化一下lcs。
		int[][] b = getLength(x, y);
		Display(b, x, x.length - 1, y.length - 1);
	}

	public static int[][] getLength(String[] x, String[] y) {
		int[][] b = new int[x.length][y.length];
		int[][] c = new int[x.length][y.length];
		for (int i = 1; i < x.length; i++) {
			for (int j = 1; j < y.length; j++) {
				if (x[i].equals(y[j])) {
					c[i][j] = c[i - 1][j - 1] + 1;
					b[i][j] = 1;
				} else if (c[i - 1][j] >= c[i][j - 1]) {
					c[i][j] = c[i - 1][j];
					b[i][j] = 0;
				} else {
					c[i][j] = c[i][j - 1];
					b[i][j] = -1;
				}
			}
		}
		return b;
	}

	public static void Display(int[][] b, String[] x, int i, int j) {
		if (i == 0 || j == 0)
			return;
		if (b[i][j] == 1) {
			Display(b, x, i - 1, j - 1);
			lcs++;
		} else if (b[i][j] == 0) {
			Display(b, x, i - 1, j);
		} else if (b[i][j] == -1) {
			Display(b, x, i, j - 1);
		}
	}

	// 图的深度遍历操作(递归)
	public static void DFSTraverse(int nodeCount, String[] vertexs,
			int[][] edges) {
		flag = new boolean[nodeCount];
		for (int i = 0; i < nodeCount; i++) {
			similarLabels = "";
			if (flag[i] == false) {// 当前顶点没有被访问
				DFS(i, nodeCount, vertexs, edges);
				LabelSetList.add(similarLabels);
			}
		}
	}

	// 图的深度优先递归算法
	public static void DFS(int i, int nodeCount, String[] vertexs, int[][] edges) {
		flag[i] = true;// 第i个顶点被访问
		similarLabels += vertexs[i] + ",";
		for (int j = 0; j < nodeCount; j++) {
			if (flag[j] == false && edges[i][j] == 1) {
				DFS(j, nodeCount, vertexs, edges);
			}
		}
	}
}
