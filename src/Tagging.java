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

public class Tagging {
	public static String VectorPath = "C:/Users/Administrator/Desktop/LogMining/VectorTest.txt";
	public static String LabelVectorPath = "C:/Users/Administrator/Desktop/LogMining/LabelVector.txt";

	public static void main(String[] args) {
		List<String> labelVectorList = new ArrayList<String>();
		try {
			File vectorFile = new File(VectorPath);
			BufferedReader vReader = new BufferedReader(new InputStreamReader(
					new FileInputStream(vectorFile), "UTF-8"));
			String vLine = vReader.readLine();
			while (vLine != null) {
				if ("".equals(vLine.trim())) {
					vLine = vReader.readLine();
					continue;
				}
				boolean isExist = false;
				for (int i = 0; i < labelVectorList.size(); i++) {
					if (vLine.equals(labelVectorList.get(i))) {// 如果两个向量完全匹配，则判定为同一Label
						isExist = true;
						break;
					}
				}
				if (!isExist) {
					labelVectorList.add(vLine);
				}
				vLine = vReader.readLine();
			}
			vReader.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		// 把Label Vector写入文件
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File(
					LabelVectorPath), true));
			for (int i = 0; i < labelVectorList.size(); i++) {
				writer.write("L" + i + "\t" + labelVectorList.get(i));
				writer.newLine();
			}
			writer.flush();
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
