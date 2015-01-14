package training;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class LogMergeByLCS {
	public static double SIMILARITY = 0.6;
	public static List<String[]> LABEL_VECTOR_LIST = new ArrayList<String[]>();
	public static HashMap<String, String> LABEL_DOCIDS_MAP = new HashMap<String, String>();
	public static int LCS = 0;
	public static String SIMILAR_LABELS = "";
	public static List<String> LABEL_SET_LIST = new LinkedList<String>();
	public static boolean[] FLAG;

	static {
		// ��Label vector�ļ�
		try {
			File LabelVectorFile = new File(PATHS.LABEL_VECTOR_PATH);
			BufferedReader vReader = new BufferedReader(new InputStreamReader(
					new FileInputStream(LabelVectorFile), "UTF-8"));
			String line = vReader.readLine();
			while (line != null) {
				if ("".equals(line.trim())) {
					line = vReader.readLine();
					continue;
				}
				String[] labelVectorArr = line.split("\t|,");
				LABEL_VECTOR_LIST.add(labelVectorArr);
				line = vReader.readLine();
			}
			vReader.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		// ��label docIds�ļ�
		try {
			File LabelDocIDsFile = new File(PATHS.LABEL_DOCIDS_PATH);
			BufferedReader dReader = new BufferedReader(new InputStreamReader(
					new FileInputStream(LabelDocIDsFile), "UTF-8"));
			String line = dReader.readLine();
			while (line != null) {
				if ("".equals(line.trim())) {
					line = dReader.readLine();
					continue;
				}
				String[] labelDocIdsArr = line.split("\t");
				LABEL_DOCIDS_MAP.put(labelDocIdsArr[0], labelDocIdsArr[1]);
				line = dReader.readLine();
			}
			dReader.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	@SuppressWarnings("deprecation")
	public static void main(String[] args) {
		System.out.println("LogMergeByLCS Running...");
		int[][] LCSArr = new int[LABEL_VECTOR_LIST.size()][LABEL_VECTOR_LIST
				.size()];
		for (int i = 0; i < LABEL_VECTOR_LIST.size(); i++) {
			String[] rowStr = LABEL_VECTOR_LIST.get(i);
			for (int j = 0; j < LABEL_VECTOR_LIST.size(); j++) {
				String[] colStr = LABEL_VECTOR_LIST.get(j);
				LCSAlgorithm(rowStr, colStr);
				double rowStr_lcs = (double) LCS / (rowStr.length - 1);// ��Ϊ�����һ����Label�����㳤��ʱ�Ͳ�����label��
				double colStr_lcs = (double) LCS / (colStr.length - 1);
				if (rowStr_lcs >= SIMILARITY && colStr_lcs >= SIMILARITY) {
					LCSArr[i][j] = 1;
				} else {
					LCSArr[i][j] = 0;
				}
			}
		}
		String[] vertexs = new String[LABEL_VECTOR_LIST.size()];
		for (int i = 0; i < LABEL_VECTOR_LIST.size(); i++) {
			String[] LVArr = LABEL_VECTOR_LIST.get(i);
			vertexs[i] = LVArr[0];
		}
		DFSTraverse(LCSArr.length, vertexs, LCSArr);

		// ��Label Setд���ļ�
		Directory directory = null;
		IndexReader reader = null;
		try {
			directory = FSDirectory.open(new File(PATHS.LUCENE_PATH));
			reader = IndexReader.open(directory);
			Document document = null;

			try {
				BufferedWriter writer = new BufferedWriter(new FileWriter(
						new File(PATHS.LABEL_SET_PATH), true));

				for (int i = 0; i < LABEL_SET_LIST.size(); i++) {
					writer.write("===========Label_" + i + "===============");
					writer.newLine();
					writer.write(LABEL_SET_LIST.get(i));
					writer.newLine();
					writer.newLine();
				}
				writer.write("***************************");
				writer.newLine();
				writer.newLine();

				for (int i = 0; i < LABEL_SET_LIST.size(); i++) {
					String[] labelArr = LABEL_SET_LIST.get(i).split(",");
					writer.write("===========Label_" + i + "===============");
					writer.newLine();
					for (int j = 0; j < labelArr.length; j++) {
						String docIds = LABEL_DOCIDS_MAP.get(labelArr[j]);
						String[] docIdArr = docIds.split(",");
						for (int k = 0; k < docIdArr.length; k++) {
							document = reader.document(Integer
									.valueOf(docIdArr[k]));
							// Message sourceд���ļ�
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

			try {
				BufferedWriter tlWriter = new BufferedWriter(new FileWriter(
						new File(PATHS.TIMESTAMP_LABEL_PATH), true));
				for (int i = 0; i < LABEL_SET_LIST.size(); i++) {
					String[] labelArr = LABEL_SET_LIST.get(i).split(",");
					for (int j = 0; j < labelArr.length; j++) {
						String docIds = LABEL_DOCIDS_MAP.get(labelArr[j]);
						String[] docIdArr = docIds.split(",");
						for (int k = 0; k < docIdArr.length; k++) {
							document = reader.document(Integer
									.valueOf(docIdArr[k]));
							// Message sourceд���ļ�
							tlWriter.write(document.get("timeStamp")+"\t");
							tlWriter.write("Label_" + i);
							tlWriter.newLine();
						}
					}
				}
				tlWriter.flush();
				tlWriter.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		// ��Label docIdsд���ļ�
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File(
					PATHS.LABEL_SET_DOCIDS_PATH), true));

			for (int i = 0; i < LABEL_SET_LIST.size(); i++) {
				String[] labelsArr = LABEL_SET_LIST.get(i).split(",");
				String docIds = "";
				for (int j = 0; j < labelsArr.length; j++) {
					docIds += LABEL_DOCIDS_MAP.get(labelsArr[j]);
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

		// ����labelд��Lucene

		System.out.println("Completed.");
	}

	// ������Ӵ�
	public static void LCSAlgorithm(String[] x, String[] y) {
		LCS = 0;// lcs��ȫ�ֱ���������ÿ�ε���LCS�㷨��Ҫ���³�ʼ��һ��lcs��
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
			LCS++;
		} else if (b[i][j] == 0) {
			Display(b, x, i - 1, j);
		} else if (b[i][j] == -1) {
			Display(b, x, i, j - 1);
		}
	}

	// ͼ����ȱ�������(�ݹ�)
	public static void DFSTraverse(int nodeCount, String[] vertexs,
			int[][] edges) {
		FLAG = new boolean[nodeCount];
		for (int i = 0; i < nodeCount; i++) {
			SIMILAR_LABELS = "";
			if (FLAG[i] == false) {// ��ǰ����û�б�����
				DFS(i, nodeCount, vertexs, edges);
				LABEL_SET_LIST.add(SIMILAR_LABELS);
			}
		}
	}

	// ͼ��������ȵݹ��㷨
	public static void DFS(int i, int nodeCount, String[] vertexs, int[][] edges) {
		FLAG[i] = true;// ��i�����㱻����
		SIMILAR_LABELS += vertexs[i] + ",";
		for (int j = 0; j < nodeCount; j++) {
			if (FLAG[j] == false && edges[i][j] == 1) {
				DFS(j, nodeCount, vertexs, edges);
			}
		}
	}
}
