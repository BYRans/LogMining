import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class Tagging {
	public static String VectorPath = "C:/Users/Administrator/Desktop/LogMining/Vector.txt";
	public static String LabelVectorPath = "C:/Users/Administrator/Desktop/LogMining/LabelVector.txt";
	public static String LabelRawDataPath = "C:/Users/Administrator/Desktop/LogMining/LabelRawData.txt";
	public static String LucenePath = "C:/Users/Administrator/Desktop/LogMining/luceneFile/";

	public static void main(String[] args) {
		System.out.println("Running...");
		List<String> labelVectorList = new ArrayList<String>();
		List<String> docIdList = new ArrayList<String>();
		try {
			File vectorFile = new File(VectorPath);
			BufferedReader vReader = new BufferedReader(new InputStreamReader(
					new FileInputStream(vectorFile), "UTF-8"));
			String vLine = vReader.readLine();
			int lineCount = 0;
			while (vLine != null) {
				if ("".equals(vLine.trim())) {
					vLine = vReader.readLine();
					continue;
				}
				boolean isExist = false;
				for (int i = 0; i < labelVectorList.size(); i++) {
					if (vLine.equals(labelVectorList.get(i))) {// 如果两个向量完全匹配，则判定为同一Label
						isExist = true;
						String docIds = docIdList.get(i);
						docIds += lineCount + ",";
						docIdList.set(i, docIds);
						break;
					}
				}
				if (!isExist) {
					labelVectorList.add(vLine);
					docIdList.add(lineCount + ",");
				}
				vLine = vReader.readLine();
				lineCount++;
			}
			vReader.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		// 把Label Vector写入文件
		try {
			BufferedWriter LVWriter = new BufferedWriter(new FileWriter(new File(
					LabelVectorPath), true));
			for (int i = 0; i < labelVectorList.size(); i++) {
				LVWriter.write("L" + i + "\t" + labelVectorList.get(i));
				LVWriter.newLine();
			}
			LVWriter.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		Directory directory = null;
		IndexReader reader = null;
		try {
			directory = FSDirectory.open(new File(LucenePath));
			reader = IndexReader.open(directory);
			Document document = null;

			try {
				BufferedWriter LRWriter = new BufferedWriter(new FileWriter(
						new File(LabelRawDataPath), true));
				for (int i = 0; i < docIdList.size(); i++) {
					String[] docArr = docIdList.get(i).split(",");
					LRWriter.write("==============L" + i + "=============");
					LRWriter.newLine();
					for (int j = 0; j < docArr.length; j++) {
						document = reader.document(Integer.valueOf(docArr[j]));
						LRWriter.write(document.get("message"));
						LRWriter.newLine();
						LRWriter.newLine();
					}
				}
				LRWriter.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("Completed.");
	}
}
