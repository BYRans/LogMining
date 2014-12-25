import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FeatureExtraction {
	public static String LabelSetDocIdsPath = "C:/Users/Administrator/Desktop/LogMining/LabelSetDocIds.txt";
	public static String VectorPath = "C:/Users/Administrator/Desktop/LogMining/Vector.txt";
	public static String FeaturePath = "C:/Users/Administrator/Desktop/LogMining/Feature.txt";
	public static String TokenSetPath = "C:/Users/Administrator/Desktop/LogMining/TokenSet.txt";
	public static List<String[]> LabelDocIdsList = new ArrayList<String[]>();
	public static HashMap<String, String> DocIdVectorMap = new HashMap<String, String>();
	public static HashMap<String, String> TokenMap = new HashMap<String, String>();
	public static String feature = "";

	static {
		try {
			File LabelVectorFile = new File(VectorPath);
			BufferedReader vReader = new BufferedReader(new InputStreamReader(
					new FileInputStream(LabelVectorFile), "UTF-8"));
			String line = vReader.readLine();
			while (line != null) {
				if ("".equals(line.trim())) {
					line = vReader.readLine();
					continue;
				}
				String[] DocIdVectorArr = line.split("\t");
				DocIdVectorMap.put(DocIdVectorArr[0], DocIdVectorArr[1]);
				line = vReader.readLine();
			}
			vReader.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		try {
			File LabelDocIdsFile = new File(LabelSetDocIdsPath);
			BufferedReader dReader = new BufferedReader(new InputStreamReader(
					new FileInputStream(LabelDocIdsFile), "UTF-8"));
			String line = dReader.readLine();
			while (line != null) {
				if ("".equals(line.trim())) {
					line = dReader.readLine();
					continue;
				}
				String[] labelDocIdsArr = line.split("\t");
				LabelDocIdsList.add(labelDocIdsArr);
				line = dReader.readLine();
			}
			dReader.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		try {
			File TokenSetFile = new File(TokenSetPath);
			BufferedReader tReader = new BufferedReader(new InputStreamReader(
					new FileInputStream(TokenSetFile), "UTF-8"));
			String line = tReader.readLine();
			while (line != null) {
				if ("".equals(line.trim())) {
					line = tReader.readLine();
					continue;
				}
				String[] tokenArr = line.split("\t");
				TokenMap.put(tokenArr[0], tokenArr[2]);
				line = tReader.readLine();
			}
			tReader.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	public static void main(String[] args) {
		System.out.println("Running...");
		for (int i = 0; i < LabelDocIdsList.size(); i++) {
			String[] docIdArr = LabelDocIdsList.get(i)[1].split(",");
			feature = "P," + DocIdVectorMap.get(docIdArr[0]);// 取最长公共子串的算法会忽略掉第一个字符，所以加个P,日志合并一步第一个字符设置的是Li
			for (int j = 1; j < docIdArr.length; j++) {
				String[] fArr = feature.split(",");
				String temp = "P," + DocIdVectorMap.get(docIdArr[j]);
				String[] coArr = temp.split(",");
				LCSAlgorithm(fArr, coArr);
			}

			String tokenFeature = "";
			// 把Label Set写入文件
			try {
				BufferedWriter writer = new BufferedWriter(new FileWriter(
						new File(FeaturePath), true));
				if (feature.length() > 2)// 去掉"P,"
					feature = feature.substring(2);

				String[] feArr = feature.split(",");
				for (int j = 0; j < feArr.length; j++) {
					String temp = TokenMap.get(feArr[j]);
					if (temp != null)
						tokenFeature += temp + ",";
				}

				writer.write(LabelDocIdsList.get(i)[0] + ":");
				writer.newLine();
				writer.write(tokenFeature);
				writer.newLine();
				writer.write(feature);
				writer.newLine();
				writer.newLine();
				writer.flush();
				writer.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			System.out.println(LabelDocIdsList.get(i)[0] + ":\n" + tokenFeature
					+ "\n" + feature+ "\n");
		}
		System.out.println("Completed.");
	}

	// 最长公共子串
	public static void LCSAlgorithm(String[] x, String[] y) {
		feature = "P,";
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
			feature += x[i] + ",";
		} else if (b[i][j] == 0) {
			Display(b, x, i - 1, j);
		} else if (b[i][j] == -1) {
			Display(b, x, i, j - 1);
		}
	}
}
