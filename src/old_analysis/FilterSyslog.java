package old_analysis;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class FilterSyslog {
	public static String RAW_SYSLOG_PATH = "C:/Documents and Settings/js4/����/503_1/";
	public static String SYSLOG_PATH = "D:/syslog/";

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		List<String> list = new ArrayList<String>();
		int fCount = 0;
		try {
			File f = new File(RAW_SYSLOG_PATH);
			File[] fileList = f.listFiles();
			for (File file : fileList) {
				System.out.println(++fCount);
				BufferedReader wReader = new BufferedReader(
						new InputStreamReader(new FileInputStream(file),
								"UTF-8"));
				String line = null;
				while ((line = wReader.readLine()) != null) {
					if ("".equals(line.trim())) {
						continue;
					}

					if ("10.52.10.7".equals(line.split(";")[2])) {
						BufferedWriter writer = new BufferedWriter(
								new FileWriter(new File(SYSLOG_PATH + "7.txt"),
										true));
						writer.write(line);
						writer.newLine();
						writer.close();
					} else if ("10.52.10.77".equals(line.split(";")[2])) {
						BufferedWriter writer = new BufferedWriter(
								new FileWriter(new File(SYSLOG_PATH + "77.txt"),
										true));
						writer.write(line);
						writer.newLine();
						writer.close();
					} else if ("10.52.10.78".equals(line.split(";")[2])) {
						BufferedWriter writer = new BufferedWriter(
								new FileWriter(new File(SYSLOG_PATH + "78.txt"),
										true));
						writer.write(line);
						writer.newLine();
						writer.close();
					} else if ("10.52.10.79".equals(line.split(";")[2])) {
						BufferedWriter writer = new BufferedWriter(
								new FileWriter(new File(SYSLOG_PATH + "79.txt"),
										true));
						writer.write(line);
						writer.newLine();
						writer.close();
					}
				}
				wReader.close();
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}

	}
}
