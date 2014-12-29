package training;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Structured {
	public static String FILE_PATH = "C:/Users/Administrator/Desktop/LogMining/Analyze/";
	public static String LUCENE_PATH = "C:/Users/Administrator/Desktop/LogMining/luceneFile/";
	
	public static void main(String[] args) throws Exception {
		System.out.println("Running...");
		IndexWriter writer = null;
		try {

			Directory directory = FSDirectory.open(new File(LUCENE_PATH));
			IndexWriterConfig iwc = new IndexWriterConfig(
					Version.LUCENE_4_10_2, new StandardAnalyzer());
			iwc.setUseCompoundFile(false);
			writer = new IndexWriter(directory, iwc);
			Document document = null;
			List<String> list = new ArrayList<String>();
			File f = new File(FILE_PATH);
			File[] fileList = f.listFiles();

			for (File file : fileList) {
				list = getContent(file);
				for (int i = 0; i < list.size(); i++) {
					String[] recordArr = list.get(i).split(" |,|: ");
					document = new Document();
					document.add(new TextField("id",i+"", Field.Store.YES));
					document.add(new TextField("timeStamp", recordArr[0] + " "
							+ recordArr[1], Field.Store.YES));
					document.add(new TextField("processID", recordArr[2],
							Field.Store.YES));
					document.add(new TextField("level", recordArr[3],
							Field.Store.YES));
					document.add(new TextField("source", recordArr[4],
							Field.Store.YES));
					String message = "";
					for (int j = 5; j < recordArr.length; j++)
						message += recordArr[j] + " ";
					document.add(new Field("message", message,
							Field.Store.YES, Field.Index.ANALYZED,Field.TermVector.WITH_POSITIONS_OFFSETS));
					writer.addDocument(document);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (CorruptIndexException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			System.out.println("Completed.");
		}
	}

	//读日志文件
	public static List<String> getContent(File file) throws Exception {
		Pattern p = Pattern
				.compile(
						"^(\\d{1,4}[-|\\/|年|\\.]\\d{1,2}[-|\\/|月|\\.]\\d{1,2}([日|号])?(\\s)*(\\d{1,2}([点|时])?((:)?\\d{1,2}(分)?((:)?\\d{1,2}(秒)?)?)?)?(\\s)*(PM|AM)?)",
						Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);

		BufferedReader br = new BufferedReader(new InputStreamReader(
				new FileInputStream(file), "UTF-8"));
		String tempLine = "";
		String curLine = br.readLine();
		String nextLine = "";
		List<String> contents = new ArrayList<String>();
		while (curLine != null) {
			nextLine = br.readLine();
			if (nextLine != null) {// 如果当前不是最后一行
				Matcher matcher = p.matcher(nextLine);
				if (matcher.find() && matcher.groupCount() >= 1) {// 如果下一行符合日期格式
					if (!"".equals(tempLine)) {// 如果tempLine不空则存tempLine,空则存curLine
						tempLine += curLine;
						curLine = tempLine;
						tempLine = "";
					}
					contents.add(curLine);
				} else {
					tempLine += curLine;
				}
			} else {
				if (!"".equals(tempLine)) {// 如果tempLine不空则存tempLine,空则存curLine
					tempLine += curLine;
					curLine = tempLine;
					tempLine = "";
				}
				contents.add(curLine);
			}
			curLine = nextLine;
		}
		br.close();
		return contents;
	}
}