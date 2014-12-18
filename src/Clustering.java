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
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Clustering {
	public static String LabelVectorPath = "C:/Users/Administrator/Desktop/LogMining/LabelVector.txt";
	public static String LabelSetPath = "C:/Users/Administrator/Desktop/LogMining/LabelSet.txt";
	public static double Similarity = 0.75;
	public static List<String[]> labelVectorList = new ArrayList<String[]>();
	static {
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
	}

	public static void main(String[] args) {
		System.out.println("Running...");
		HashMap<String, HashSet> labelSetMap = new HashMap<String, HashSet>();
		HashSet<String> vectorSet = null;

		for (int i = 0; i < labelVectorList.size(); i++) {
			String[] LVArr = labelVectorList.get(i);
			HashSet<String> arrSet = new HashSet<String>();
			for (int j = 1; j < LVArr.length; j++)
				// 数组第一个值是Label，故i=1
				arrSet.add(LVArr[j]);
			
			Iterator iter = labelSetMap.keySet().iterator();  
			boolean isMatch = false;
			while (iter.hasNext()) {
				String key = (String)iter.next(); 
				vectorSet = (HashSet<String>)labelSetMap.get(key);
				boolean similar = labelCompare(vectorSet, arrSet);
				if (similar) {
//					for (String str : arrSet) {//是否合并相似的label
//						vectorSet.add(str);
//					}
					labelSetMap.put(key, vectorSet);
					isMatch = true;
					break;
				} 
			}
			if(!isMatch)
				labelSetMap.put(LVArr[0], arrSet);
		}

		// 把Label Set写入文件
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File(
					LabelSetPath), true));
			
			Iterator iter = labelSetMap.entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry entry = (Map.Entry) iter.next();
				String key = (String) entry.getKey();
				HashSet<String> val = (HashSet<String>) entry.getValue();
				writer.write(key + "\t");
				for (String str : val) {
					writer.write(str+",");
				}
				writer.newLine();
			}
			writer.flush();
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Completed.");
	}

	public static boolean labelCompare(HashSet<String> set,
			HashSet<String> arrSet) {
		boolean similar = false;
		int count = 0;
		for (String str : arrSet) {
			if (set.contains(str))
				count++;
		}
		if (((double)count / set.size()) >= Similarity
				&& ((double)count / arrSet.size()) >= Similarity)
			similar = true;
		return similar;
	}

}
