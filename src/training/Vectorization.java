package training;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DocsAndPositionsEnum;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.Version;

public class Vectorization {
	
	public static HashMap<String, String> TOKEN_SET_MAP = new HashMap<String, String>();

	static {
		try {
			File termSetFile = new File(COMMON_PATH.TOKEN_SET_PATH);
			BufferedReader br = new BufferedReader(new InputStreamReader(
					new FileInputStream(termSetFile), "UTF-8"));
			String curLine = br.readLine();
			while (curLine != null) {
				if ("".equals(curLine.trim())) {
					curLine = br.readLine();
					continue;
				}
				String[] termArr = curLine.split("\t");
				if (termArr.length >= 3)
					TOKEN_SET_MAP.put(termArr[2], termArr[0]);
				curLine = br.readLine();
			}
			br.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	@SuppressWarnings("deprecation")
	public static void main(String[] args) {
		System.out.println("Vectorization Running...");
		Directory directory = null;
		IndexReader reader = null;
		try {
			directory = FSDirectory.open(new File(COMMON_PATH.LUCENE_PATH));
			reader = IndexReader.open(directory);

			IndexWriterConfig iwc = new IndexWriterConfig(
					Version.LUCENE_4_10_2, new StandardAnalyzer());
			iwc.setUseCompoundFile(false);

			int docCount = reader.maxDoc();
			COMMON_PATH.DELETE_FILE(COMMON_PATH.VECTOR_PATH);//写入VECTOR文件前先删除原文件
			for (int i = 0; i < docCount; i++) {
				// Document document = reader.document(i);
				Terms termVector = reader.getTermVector(i, "message");
				if (termVector != null && termVector.size() > 0) {
					TermsEnum termsEnum = termVector.iterator(null); // 取该field的terms
					BytesRef term = null;
					HashMap<Integer, String> posAndTerMap = new HashMap<Integer, String>();// 把terms及偏移量存到hashMap中,<position,term>.
					while ((term = termsEnum.next()) != null) {// 迭代term
						DocsAndPositionsEnum positionEnum = termsEnum
								.docsAndPositions(null, null);// 该term在该document下的偏移量？？哪有该document的标示？
						positionEnum.nextDoc();
						int freq = positionEnum.freq();// 该term在该文档出现多少次，出现几次就有几个偏移量。
						for (int j = 0; j < freq; j++) {
							int position = positionEnum.nextPosition();
							posAndTerMap.put(position, term.utf8ToString());
						}
					}
					Object[] key_arr = posAndTerMap.keySet().toArray();// 按position排序
					Arrays.sort(key_arr);
					@SuppressWarnings("unused")
					String docContent = "";
					String docVectorContent = "";
					for (Object key : key_arr) {
						String value = posAndTerMap.get(key);
						docContent += value + " ";
						String token = TOKEN_SET_MAP.get(value);
						if (token != null)
							docVectorContent += TOKEN_SET_MAP.get(value) + ",";
					}

					try {
						BufferedWriter writer = new BufferedWriter(
								new FileWriter(new File(COMMON_PATH.VECTOR_PATH), true));
						// writer.write(docContent);
						// writer.newLine();

						writer.write(i + "\t" + docVectorContent);

						writer.newLine();
						writer.newLine();
						writer.flush();
						writer.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
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
		System.out.println("Completed.");
	}
}
