package training;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;




//import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DocsEnum;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.queryparser.classic.QueryParser;

public class CopyOfRMNoiseWord {
	public static String LUCENE_PATH = "C:/Users/Administrator/Desktop/LogMining/LabeledLuceneFile/";
	public static String TermSetPath = "C:/Users/Administrator/Desktop/LogMining/TokenSet.txt";
	public static String AllTOKEN_SET_PATH = "C:/Users/Administrator/Desktop/LogMining/AllTokenSet.txt";
	public static Integer TERM_FREQUENT = 2;// 判定低频词阙值

	public static int Hits = 10;
	public static String QueryString = "2014-11-18";
	public static String Field = "timeStampDay";

	public static void main(String[] args) throws Exception {
		System.out.println("Running...");
//		buildTokenSet();
		 search(LUCENE_PATH, QueryString, Field, Hits);//查询功能
	}

	@SuppressWarnings("deprecation")
	public static void search(String FILE_PATH, String queryString,
			String field, int hits) {
		Directory directory = null;
		IndexReader reader = null;
		try {
			directory = FSDirectory.open(new File(FILE_PATH));
			reader = IndexReader.open(directory);
			IndexSearcher searcher = new IndexSearcher(reader);
			QueryParser parser = new QueryParser(field, new StandardAnalyzer());
			Query query;
			try {
				query = parser.parse(queryString);
				TopDocs tds = searcher.search(query, hits);
				ScoreDoc[] sds = tds.scoreDocs;
				System.out.println(tds.totalHits + " total matching documents");
				for (int j = 0; j < sds.length; j++) {
					System.out.println(sds[j]);

				}
				int[] docCount = new int[hits];
				int i = 0;
				for (ScoreDoc sd : sds) {
					docCount[i] = sd.doc;
					i++;
					System.out.println("sd.doc " + sd.doc);
					// Document document = searcher.doc(sd.doc);
					// System.out.println(document.get("message")+" ");
				}
				List<Integer> list = new ArrayList<Integer>();

				for (int j = 0; j < docCount.length; j++) {
					list.add(docCount[j]);
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}