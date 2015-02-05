package old_training;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.DocsAndPositionsEnum;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;

public class OutputWrongData {
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
		System.out.println("Running...");
		Directory directory = null;
		IndexReader reader = null;
		try {
			directory = FSDirectory.open(new File(COMMON_PATH.LUCENE_PATH));
			reader = IndexReader.open(directory);
			int docCount = reader.maxDoc();
			for (int i = 0; i < docCount; i++) {
				// Document document = reader.document(i);
				Terms termVector = reader.getTermVector(i, "message");
				if (termVector != null && termVector.size() > 0) {
					TermsEnum termsEnum = termVector.iterator(null); // ȡ��field��terms
					BytesRef term = null;
					HashMap<Integer, String> posAndTerMap = new HashMap<Integer, String>();// ��terms��ƫ�����浽hashMap��,<position,term>.
					while ((term = termsEnum.next()) != null) {// ���term
						DocsAndPositionsEnum positionEnum = termsEnum
								.docsAndPositions(null, null);// ��term�ڸ�document�µ�ƫ�����������и�document�ı�ʾ��
						positionEnum.nextDoc();
						int freq = positionEnum.freq();// ��term�ڸ��ĵ����ֶ��ٴΣ����ּ��ξ��м���ƫ������
						for (int j = 0; j < freq; j++) {
							int position = positionEnum.nextPosition();
							posAndTerMap.put(position, term.utf8ToString());
						}
					}
					Object[] key_arr = posAndTerMap.keySet().toArray();// ��position����
					Arrays.sort(key_arr);
					String docContent = "";
					String docVectorContent = "";
					for (Object key : key_arr) {
						String value = posAndTerMap.get(key);
						docContent += value + " ";
						String token = TOKEN_SET_MAP.get(value);
						if (token != null)
							docVectorContent += TOKEN_SET_MAP.get(value) + ",";
					}

					if ("".equals(docVectorContent)) {
						System.out.println(docContent);

						Document document = reader.document(Integer.valueOf(i));
						System.out.println(document.get("serviceName") + " ; "
								+ document.get("netType") + " ; "
								+ document.get("ip") + " ; "
								+ document.get("timeStamp") + " ; "
								+ document.get("segment") + " ; source:"
								+ document.get("source") + " ; message:"
								+ document.get("message"));
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
