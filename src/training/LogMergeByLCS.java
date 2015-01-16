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

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

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
			File LabelVectorFile = new File(COMMON_PATH.LABEL_VECTOR_PATH);
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
			File LabelDocIDsFile = new File(COMMON_PATH.LABEL_DOCIDS_PATH);
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
				LCS = LCSLength(rowStr, colStr);
				double rowStr_lcs = (double) LCS / (rowStr.length - 1);// ��Ϊ�����һ����Label�����㳤��ʱ�Ͳ�����label�����Ҹ��㷨Ҳ����Ե�һ���ַ���
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
		COMMON_PATH.DELETE_FILE(COMMON_PATH.LABEL_SET_PATH);// д��Label_Set�ļ�ǰ��ɾ��ԭ�ļ�

		try {
			directory = FSDirectory.open(new File(COMMON_PATH.LUCENE_PATH));
			reader = IndexReader.open(directory);
			Document document = null;

			try {
				BufferedWriter writer = new BufferedWriter(new FileWriter(
						new File(COMMON_PATH.LABEL_SET_PATH), true));

				for (int i = 0; i < LABEL_SET_LIST.size(); i++) {
					writer.write("===========Label_" + i + "===============");
					writer.newLine();
					writer.write(LABEL_SET_LIST.get(i));
					writer.newLine();
					writer.newLine();
					writer.flush();
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
							writer.flush();
						}
					}

				}
				writer.close();
			} catch (Exception e) {
				e.printStackTrace();
			}

			IndexWriter LLWriter = null;
			try {
				// save as Labeled Lucene File
				COMMON_PATH.INIT_DIR(COMMON_PATH.LABELED_LUCENE_PATH);// ��ʼ��Labeled_Lucene�ļ���
				Directory LLDirectory = FSDirectory.open(new File(
						COMMON_PATH.LABELED_LUCENE_PATH));
				IndexWriterConfig LLiwc = new IndexWriterConfig(
						Version.LUCENE_4_10_2, new StandardAnalyzer());
				LLiwc.setUseCompoundFile(false);
				LLWriter = new IndexWriter(LLDirectory, LLiwc);
				Document LLDocument = null;

				for (int i = 0; i < LABEL_SET_LIST.size(); i++) {
					String[] labelArr = LABEL_SET_LIST.get(i).split(",");
					for (int j = 0; j < labelArr.length; j++) {
						String docIds = LABEL_DOCIDS_MAP.get(labelArr[j]);
						String[] docIdArr = docIds.split(",");
						for (int k = 0; k < docIdArr.length; k++) {
							document = reader.document(Integer
									.valueOf(docIdArr[k]));
							document.add(new Field("label", "Label_" + i,
									Field.Store.YES, Field.Index.NOT_ANALYZED));
							LLWriter.addDocument(document);
						}
					}

				}

			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (LLWriter != null) {
					try {
						LLWriter.close();
					} catch (CorruptIndexException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			// д��TimesTamp_Label�ļ�
			COMMON_PATH.DELETE_FILE(COMMON_PATH.TIMESTAMP_LABEL_PATH);// д��TimesTamp_Label�ļ�ǰ��ɾ��ԭ�ļ�
			try {
				BufferedWriter tlWriter = new BufferedWriter(new FileWriter(
						new File(COMMON_PATH.TIMESTAMP_LABEL_PATH), true));
				for (int i = 0; i < LABEL_SET_LIST.size(); i++) {
					String[] labelArr = LABEL_SET_LIST.get(i).split(",");
					for (int j = 0; j < labelArr.length; j++) {
						String docIds = LABEL_DOCIDS_MAP.get(labelArr[j]);
						String[] docIdArr = docIds.split(",");
						for (int k = 0; k < docIdArr.length; k++) {
							document = reader.document(Integer
									.valueOf(docIdArr[k]));
							// Message sourceд���ļ�
							tlWriter.write(document.get("timeStamp") + "\t");
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
		COMMON_PATH.DELETE_FILE(COMMON_PATH.LABEL_SET_DOCIDS_PATH);// д��Label
																	// docIds�ļ�ǰ��ɾ��ԭ�ļ�
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File(
					COMMON_PATH.LABEL_SET_DOCIDS_PATH), true));

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

	// ����������Ӵ�����
	public static int LCSLength(String[] x, String[] y) {
		// int[][] b = new
		// int[x.length][y.length];//b[][]�洢x��yLCS·��������ֻ��Ҫ���ȣ�����Ҫ�õ�LCS����
		int[][] c = new int[x.length][y.length];// c[][]�洢x��yLCS����
		for (int i = 1; i < x.length; i++) {
			for (int j = 1; j < y.length; j++) {
				if (x[i].equals(y[j])) {
					c[i][j] = c[i - 1][j - 1] + 1;
				} else if (c[i - 1][j] >= c[i][j - 1]) {
					c[i][j] = c[i - 1][j];
				} else {
					c[i][j] = c[i][j - 1];
				}
			}
		}
		return c[x.length - 1][y.length - 1];
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

/*
 * try { COMMON_PATH.INIT_DIR(COMMON_PATH.LABELED_LUCENE_PATH);// ��ʼ��Lucene�ļ���
 * Directory LLDirectory = FSDirectory.open(new File(
 * COMMON_PATH.LABELED_LUCENE_PATH)); IndexWriterConfig LLiwc = new
 * IndexWriterConfig( Version.LUCENE_4_10_2, new StandardAnalyzer());
 * LLiwc.setUseCompoundFile(false); IndexWriter LLWriter = new
 * IndexWriter(LLDirectory, LLiwc); Document LLDocument = null;
 * 
 * String[] recordArr = list.get(i).split(";| "); String regTime =
 * "^([0-9][0-9]:[0-9][0-9]:[0-9][0-9])$";// ��������ʱ����в��죬���������һ�� if
 * (!recordArr[4].matches(regTime)) { logCount--; continue; } if
 * (!recordArr[5].equals("BJLTSH-503-DFA-CL-SEV7")) { logCount--; continue; }
 * document = new Document(); document.add(new TextField("serviceName",
 * recordArr[0], Field.Store.YES)); document.add(new TextField("netType",
 * recordArr[1], Field.Store.YES)); document.add(new Field("ip", recordArr[2],
 * Field.Store.YES,Field.Index.ANALYZED)); document.add(new
 * TextField("timeStamp", recordArr[3] + " " + recordArr[4], Field.Store.YES));
 * document.add(new TextField("segment", recordArr[5], Field.Store.YES)); String
 * source = ""; String message = ""; for (int k = 6; k < recordArr.length; k++)
 * { message += recordArr[k] + " "; } String[] messArr = message.split(":"); if
 * (messArr.length > 1) { source = messArr[0]; message = ""; for (int j = 1; j <
 * messArr.length; j++) message += messArr[j] + " "; } document.add(new
 * TextField("source", source, Field.Store.YES)); document.add(new
 * Field("message", message, Field.Store.YES, Field.Index.ANALYZED,
 * Field.TermVector.WITH_POSITIONS_OFFSETS)); LLWriter.addDocument(document); }
 * catch (IOException e) { e.printStackTrace(); } finally { if (writer != null)
 * { try { writer.close(); } catch (CorruptIndexException e) {
 * e.printStackTrace(); } catch (IOException e) { e.printStackTrace(); } }
 */

