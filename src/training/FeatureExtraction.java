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
import java.util.List;

public class FeatureExtraction {
	public static List<String[]> LABEL_DCOIDS_LIST = new ArrayList<String[]>();
	public static HashMap<String, String> DOCID_VECTOR_MAP = new HashMap<String, String>();
	public static HashMap<String, String> TOKEN_MAP = new HashMap<String, String>();
	public static String FEATURE = "";

	static {
		try {
			File LabelVectorFile = new File(PATHS.VECTOR_PATH);
			BufferedReader vReader = new BufferedReader(new InputStreamReader(
					new FileInputStream(LabelVectorFile), "UTF-8"));
			String line = vReader.readLine();
			while (line != null) {
				if ("".equals(line.trim())) {
					line = vReader.readLine();
					continue;
				}
				String[] DocIdVectorArr = line.split("\t");
				DOCID_VECTOR_MAP.put(DocIdVectorArr[0], DocIdVectorArr[1]);
				line = vReader.readLine();
			}
			vReader.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		try {
			File LabelDocIdsFile = new File(PATHS.LABEL_SET_DOCIDS_PATH);
			BufferedReader dReader = new BufferedReader(new InputStreamReader(
					new FileInputStream(LabelDocIdsFile), "UTF-8"));
			String line = dReader.readLine();
			while (line != null) {
				if ("".equals(line.trim())) {
					line = dReader.readLine();
					continue;
				}
				String[] labelDocIdsArr = line.split("\t");
				LABEL_DCOIDS_LIST.add(labelDocIdsArr);
				line = dReader.readLine();
			}
			dReader.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		try {
			File TokenSetFile = new File(PATHS.TOKEN_SET_PATH);
			BufferedReader tReader = new BufferedReader(new InputStreamReader(
					new FileInputStream(TokenSetFile), "UTF-8"));
			String line = tReader.readLine();
			while (line != null) {
				if ("".equals(line.trim())) {
					line = tReader.readLine();
					continue;
				}
				String[] tokenArr = line.split("\t");
				TOKEN_MAP.put(tokenArr[0], tokenArr[2]);
				line = tReader.readLine();
			}
			tReader.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	public static void main(String[] args) {
		System.out.println("Running...");
		for (int i = 0; i < LABEL_DCOIDS_LIST.size(); i++) {
			String[] docIdArr = LABEL_DCOIDS_LIST.get(i)[1].split(",");
			FEATURE = "P," + DOCID_VECTOR_MAP.get(docIdArr[0]);// ȡ������Ӵ����㷨����Ե���һ���ַ������ԼӸ�P,��־�ϲ�һ����һ���ַ����õ���Li
			for (int j = 1; j < docIdArr.length; j++) {
				String[] fArr = FEATURE.split(",");
				String tmp = "P," + DOCID_VECTOR_MAP.get(docIdArr[j]);
				String[] coArr = tmp.split(",");
				LCSAlgorithm(fArr, coArr);
			}

			String tokenFEATURE = "";
			// ��Label Setд���ļ�
			try {
				BufferedWriter writer = new BufferedWriter(new FileWriter(
						new File(PATHS.FEATURE_PATH), true));
				if (FEATURE.length() > 2)// ȥ��"P,"
					FEATURE = FEATURE.substring(2);

				String[] feArr = FEATURE.split(",");
				for (int j = 0; j < feArr.length; j++) {
					String tmp = TOKEN_MAP.get(feArr[j]);
					if (tmp != null)
						tokenFEATURE += tmp + ",";
				}

				writer.write(LABEL_DCOIDS_LIST.get(i)[0] + "\t"+tokenFEATURE);
				writer.newLine();
				
//				writer.write(LABEL_DCOIDS_LIST.get(i)[0] + ":");
//				writer.newLine();
//				writer.write(tokenFEATURE);
//				writer.newLine();
//				writer.write(FEATURE);
				writer.newLine();
				writer.flush();
				writer.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			System.out.println(LABEL_DCOIDS_LIST.get(i)[0] + ":\n" + tokenFEATURE
					+ "\n" + FEATURE+ "\n");
		}
		System.out.println("Completed.");
	}

	// ������Ӵ�
	public static void LCSAlgorithm(String[] x, String[] y) {
		FEATURE = "P,";
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
			FEATURE += x[i] + ",";
		} else if (b[i][j] == 0) {
			Display(b, x, i - 1, j);
		} else if (b[i][j] == -1) {
			Display(b, x, i, j - 1);
		}
	}
}
