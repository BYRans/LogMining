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

public class LogMining {
	public static void main(String[] args) throws Exception {
		IndexWriter writer = null;
		try {
			String filePath = "C:/Users/Administrator/Desktop/Analyze/";
			String lucenePath = "C:/Users/Administrator/Desktop/luceneFile/";
			
			List<String> sw = new LinkedList<String>();
			sw.add("src");
			CharArraySet stopWords = new CharArraySet(sw, true);
			StandardAnalyzer analyzer = new StandardAnalyzer(stopWords);
			
			Directory directory = FSDirectory.open(new File(lucenePath));
			IndexWriterConfig iwc = new IndexWriterConfig(
					Version.LUCENE_4_10_2, analyzer);
			iwc.setUseCompoundFile(false);
			writer = new IndexWriter(directory, iwc);
			Document document = null;
			List<String> list = new ArrayList<String>();
			File f = new File(filePath);
			File[] fileList = f.listFiles();

			for (File file : fileList) {
				list = getContent(file);
				for (int i = 0; i < list.size(); i++) {
					String[] recordArr = list.get(i).split(" |,|: ");
					document = new Document();
					document.add(new TextField("timeStamp", recordArr[0] + " "
							+ recordArr[1], Field.Store.YES));
					System.out.println("timeStamp:" + recordArr[0] + " "
							+ recordArr[1]);

					document.add(new TextField("processID", recordArr[2],
							Field.Store.YES));
					System.out.println("processID:" + recordArr[2]);

					document.add(new TextField("level", recordArr[3],
							Field.Store.YES));
					System.out.println("level:" + recordArr[3]);

					document.add(new TextField("source", recordArr[4],
							Field.Store.YES));
					System.out.println("source:" + recordArr[4]);
					String message = "";
					for (int j = 5; j < recordArr.length; j++)
						message += recordArr[j] + " ";
					document.add(new TextField("message", message,
							Field.Store.YES));
					System.out.println("message:" + message);
					System.out.println("**************************");

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
		}
	}

	// // 2. query
	// String querystr = args.length > 0 ? args[0] : "193398817";
	//
	// // the "title" arg specifies the default field to use
	// // when no field is explicitly specified in the query.
	// Query q = new QueryParser(Version.LUCENE_4_10_2, "isbn", analyzer)
	// .parse(querystr);
	//
	// // 3. search
	// int hitsPerPage = 10;
	// IndexReader reader = DirectoryReader.open(directory);
	// IndexSearcher searcher = new IndexSearcher(reader);
	// TopScoreDocCollector collector = TopScoreDocCollector.create(
	// hitsPerPage, true);
	// searcher.search(q, collector);
	// ScoreDoc[] hits = collector.topDocs().scoreDocs;
	//
	// // 4. display results
	// System.out.println("Found " + hits.length + " hits.");
	// for (int i = 0; i < hits.length; ++i) {
	// int docId = hits[i].doc;
	// Document d = searcher.doc(docId);
	// System.out.println((i + 1) + ". " + d.get("isbn") + "\t"
	// + d.get("title"));
	// }
	// // reader can only be closed when there
	// // is no need to access the documents any more.
	// reader.close();
	// }
	//
	// private static void addDoc(IndexWriter w, String title, String isbn)
	// throws IOException {
	// Document doc = new Document();
	// doc.add(new TextField("title", title, Field.Store.YES));
	//
	// // use a string field for isbn because we don't want it tokenized
	// doc.add(new StringField("isbn", isbn, Field.Store.YES));
	// w.addDocument(doc);
	// }

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