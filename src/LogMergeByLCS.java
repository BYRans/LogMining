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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class LogMergeByLCS {
	public static String LabelVectorPath = "C:/Users/Administrator/Desktop/LogMining/LabelVector.txt";
	public static String LabelSetPath = "C:/Users/Administrator/Desktop/LogMining/LabelSet.txt";
	public static double Similarity = 0.75;
	public static List<String[]> labelVectorList = new ArrayList<String[]>();
	public static int lcs = 0;
	public static List<String> LabelSetList = new LinkedList<String>();

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
		int[][] LCSArr = new int[labelVectorList.size()][labelVectorList.size()];
		for (int i = 0; i < labelVectorList.size(); i++) {
			String[] rowStr = labelVectorList.get(i);
			for (int j = 0; j < labelVectorList.size(); j++) {
				String[] colStr = labelVectorList.get(j);
				LCSAlgorithm(rowStr, colStr);
				double rowStr_lcs = (double) lcs/ (rowStr.length - 1);// 因为数组第一个是Label，本应去掉，但没去掉，计算长度时就不计算label。
				double colStr_lcs = (double) lcs/ (colStr.length - 1);
				
				if (rowStr_lcs >= Similarity && colStr_lcs >= Similarity) {
					LCSArr[i][j] = 1;
				} else {
					LCSArr[i][j] = 0;
					System.out.println( lcs +" "+rowStr_lcs + "  " + colStr_lcs);
				}
			}
		}

		int[] flagArr = new int[LCSArr.length];
		for (int i = 0; i < LCSArr.length; i++) {
			if (flagArr[i] == 1)
				continue;
			String similarLabels = "";
			for (int j = 0; j < LCSArr[i].length; j++) {
				if (LCSArr[i][j] == 1) {
					String[] LVArr = labelVectorList.get(j);
					similarLabels += LVArr[0] + ",";
//					System.out.println(similarLabels);
					flagArr[j] = 1;
				}
			}
			LabelSetList.add(similarLabels);
		}

		// 把Label Set写入文件
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File(
					LabelSetPath), true));

			for (int i = 0; i < LabelSetList.size(); i++) {
				writer.write("===========Label_" + i + "===============");
				writer.newLine();
				writer.write(LabelSetList.get(i));
				writer.newLine();
				writer.newLine();
			}
			writer.flush();
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Completed.");
	}

	public static void LCSAlgorithm(String[] x, String[] y) {
		lcs = 0;// lcs是全局变量，所以每次调用LCS算法都要重新初始化一下lcs。
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
			lcs++;
		} else if (b[i][j] == 0) {
			Display(b, x, i - 1, j);
		} else if (b[i][j] == -1) {
			Display(b, x, i, j - 1);
		}
	}
}