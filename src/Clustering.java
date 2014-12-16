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
	public static double Similarity = 0.5;
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
				String[] labelVectorArr = line.substring(0, line.length() - 1)
						.split("\t|,");
				labelVectorList.add(labelVectorArr);
				line = vReader.readLine();
			}
			vReader.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	public static void main(String[] args) {
		HashMap<String, HashSet> labelSetMap = new HashMap<String, HashSet>();
		HashSet<String> vectorSet = null;

		String label = labelVectorList.get(0)[0];
		for (int i = 1; i < labelVectorList.get(0).length; i++) {// 第一条数据
			vectorSet = new HashSet<String>();
			vectorSet.add(labelVectorList.get(0)[i]);
		}
		labelSetMap.put(label, vectorSet);

		for (int i = 1; i < labelVectorList.size(); i++) {
			System.out.println("i-->"+i);
			String[] LVArr = labelVectorList.get(i);
			HashSet<String> arrSet = new HashSet<String>();
			for (int j = 1; j < LVArr.length; j++)
				// 数组第一个值是Label，故i=1
				arrSet.add(LVArr[j]);

			
			Iterator iter = labelSetMap.keySet().iterator();  
			boolean isMatch = false;
			while (iter.hasNext()) {
				String key = (String)iter.next(); 
				HashSet<String> val = (HashSet<String>)labelSetMap.get(key);
				boolean similar = labelCompare(val, arrSet);
				System.out.println(similar);
				if (similar) {
					for (String str : arrSet) {
						val.add(str);
					}
					labelSetMap.put(key, val);
					isMatch = true;
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

	}

	public static boolean labelCompare(HashSet<String> set,
			HashSet<String> arrSet) {
		boolean similar = false;
		int count = 0;
		for (String str : arrSet) {
			System.out.println(str);
			if (set.contains(str))
				count++;
		}
		
		System.out.println(count / set.size());
		System.out.println(count / arrSet.size());
		if (count / set.size() >= Similarity
				&& count / arrSet.size() >= Similarity)
			similar = true;
		return similar;
	}

}
