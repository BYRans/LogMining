package old_analysis;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;

public class DecreaseRawLog {
	public static String SYSLOG_PATH = "D:/syslog/7.txt";
	public static String SYSLOG_REAL_PATH = "D:/syslog/realSyslog7.txt";
	public static String ALERTLOG_PATH = "D:/syslog/alertlog7.txt";
	public static HashSet<String> set = new HashSet<String>();

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			BufferedReader wReader = new BufferedReader(new InputStreamReader(
					new FileInputStream(ALERTLOG_PATH), "UTF-8"));
			String line = null;
			while ((line = wReader.readLine()) != null) {
				if ("".equals(line.trim())) {
					continue;
				}
				String timeStamp = line.split("\t")[0];
				timeStamp = timeStamp.split(" ")[0];
				set.add(timeStamp);
				System.out.println(timeStamp);
			}
			wReader.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		
		try {
			BufferedReader wReader = new BufferedReader(new InputStreamReader(
					new FileInputStream(SYSLOG_PATH), "UTF-8"));
			String line = null;
			while ((line = wReader.readLine()) != null) {
				if ("".equals(line.trim())) {
					continue;
				}
				String timeStamp = line.split(";")[3];
				timeStamp = timeStamp.split(" ")[0];
				if (set.contains(timeStamp)) {
					try {// ��ͳ�ƽ��д��ͳ�ƻ����ļ�
						BufferedWriter writer = new BufferedWriter(
								new FileWriter(new File(SYSLOG_REAL_PATH), true));
						writer.write(line);
						writer.newLine();
						writer.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			wReader.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

}
