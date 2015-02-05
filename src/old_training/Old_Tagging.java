package old_training;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class Old_Tagging {

	public static void main(String[] args) {
		System.out.println("Tagging Running..."+new Date());
//		List<String> LABEL_VECTOR_LIST = new ArrayList<String>();
//		List<String> docIdList = new ArrayList<String>();
		HashMap<String,String> labelVectorMap = new HashMap<String,String>();
		try {
			File vectorFile = new File(COMMON_PATH.VECTOR_PATH);
			BufferedReader vReader = new BufferedReader(new InputStreamReader(
					new FileInputStream(vectorFile), "UTF-8"));
			String vLine = vReader.readLine();
			int lineCount = 0;
			String tmpDocIds = "";
			while (vLine != null) {
				if ("".equals(vLine.trim())) {
					vLine = vReader.readLine();
					continue;
				}
				boolean isExist = false;
				String[] lineArr = vLine.split("\t");
				String vectorStr = "";
				if (lineArr.length > 1)
					vectorStr = lineArr[1];
				else {
					System.out.println("wrong data docId(lineCount): "
							+ lineArr[0]);
				}

				tmpDocIds = labelVectorMap.get(vectorStr)+lineArr[0] + ",";
				labelVectorMap.put(vectorStr,tmpDocIds);
				
//				for (int i = 0; i < LABEL_VECTOR_LIST.size(); i++) {
//					if (vectorStr.equals(LABEL_VECTOR_LIST.get(i))) {// 如果两个向量完全匹配，则判定为同一Label
//						isExist = true;
//						String docIds = docIdList.get(i);
//						docIds += lineArr[0] + ",";
//						docIdList.set(i, docIds);
//						break;
//					}
//				}
//				if (!isExist) {
//					LABEL_VECTOR_LIST.add(vectorStr);
//					docIdList.add(lineCount + ",");
//				}
				vLine = vReader.readLine();
				lineCount++;
				
				if(lineCount%100000==0)
					System.out.println("Tagging:"+(double)lineCount/COMMON_PATH.VECTOR_COUNT*100+"%");
				
			}
			vReader.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		System.out.println("Writing Label docIds...");
		
		// 把Label docIds写入文件
		COMMON_PATH.DELETE_FILE(COMMON_PATH.LABEL_DOCIDS_PATH);//写入Label docIds文件前先删除原文件
		try {
			BufferedWriter DLWriter = new BufferedWriter(new FileWriter(
					new File(COMMON_PATH.LABEL_DOCIDS_PATH), true));
			
			int vCount = 0;
			for(Map.Entry<String,String> ic:labelVectorMap.entrySet()){
				DLWriter.write("L" + (vCount++) + "\t" + ic.getValue());
				DLWriter.newLine();
				
			}
			
//			for (int i = 0; i < docIdList.size(); i++) {
//				DLWriter.write("L" + i + "\t" + docIdList.get(i));
//				DLWriter.newLine();
//			}
			DLWriter.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("Writing Label Vector...");
		
		// 把Label Vector写入文件
		COMMON_PATH.DELETE_FILE(COMMON_PATH.LABEL_VECTOR_PATH);//写入Label Vector文件前先删除原文件
		try {
			BufferedWriter LVWriter = new BufferedWriter(new FileWriter(
					new File(COMMON_PATH.LABEL_VECTOR_PATH), true));
			
			int kCount = 0;
			for(Map.Entry<String,String> ic:labelVectorMap.entrySet()){
				LVWriter.write("L" + (kCount++) + "\t" + ic.getKey());
				LVWriter.newLine();
				LVWriter.flush();
				
			}
			
//			for (int i = 0; i < LABEL_VECTOR_LIST.size(); i++) {
//				LVWriter.write("L" + i + "\t" + LABEL_VECTOR_LIST.get(i));
//				LVWriter.newLine();
//				LVWriter.flush();
//			}
			LVWriter.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 把临时分类情况写入文件，临时label下所有日志Message域，为效率，暂不存储
		// Directory directory = null;
		// IndexReader reader = null;
		// try {
		// directory = FSDirectory.open(new File(COMMON_PATH.LUCENE_PATH));
		// reader = IndexReader.open(directory);
		// Document document = null;
		// try {
		// BufferedWriter LRWriter = new BufferedWriter(new FileWriter(
		// new File(COMMON_PATH.LABEL_RAW_DATA_PATH), true));
		// for (int i = 0; i < docIdList.size(); i++) {
		// String[] docArr = docIdList.get(i).split(",");
		// LRWriter.write("==============L" + i + "=============");
		// LRWriter.newLine();
		// for (int j = 0; j < docArr.length; j++) {
		// document = reader.document(Integer.valueOf(docArr[j]));
		// LRWriter.write("MESSAGE:" + document.get("message")+" <-- SOURCE:" +
		// document.get("source"));
		// LRWriter.newLine();
		// LRWriter.newLine();
		// }
		// }
		// LRWriter.close();
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// } catch (Exception e) {
		// e.printStackTrace();
		// }

		System.out.println("Completed."+new Date()+"\n\n");
	}
}
