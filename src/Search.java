import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


//import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.queryparser.classic.QueryParser;

public class Search {

	public static void main(String[] args) throws Exception {

		String lucenePath = "C:/Users/Administrator/Desktop/luceneFile/";
		String queryString = "exception";
		String field = "message";
		int hits = 10;
		search(lucenePath, queryString, field, hits);
	}

	public static void search(String lucenePath, String queryString, String field, int hits) {

		Directory directory = null;
		IndexReader reader = null;
		try {

			directory = FSDirectory.open(new File(lucenePath));
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
					Document document = searcher.doc(sd.doc);
					System.out.print(document.get("timeStamp")+" ");
					System.out.print(document.get("processID")+" ");
					System.out.print(document.get("level")+" ");
					System.out.print(document.get("source")+" ");
					System.out.println(document.get("message")+" ");
					System.out.println("******************");
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
