import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

//import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DocsEnum;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.SlowCompositeReaderWrapper;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.DocIdSetIterator;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.queryparser.classic.QueryParser;

public class RMNoiseWord {
	public static String LucenePath = "C:/Users/Administrator/Desktop/LogMining/luceneFile/";
	public static String TermSetPath = "C:/Users/Administrator/Desktop/LogMining/tokenSet.txt";
	public static String AllTokenSetPath = "C:/Users/Administrator/Desktop/LogMining/allTokenSet.txt";
	public static Integer TermFrequent = 2;// 判定低频词阙值

	public static int Hits = 10;
	public static String QueryString = "exception";
	public static String Field = "message";

	public static void main(String[] args) throws Exception {
		System.out.println("Running...");
		buildTokenSet();
		// search(LucenePath, QueryString, Field, Hits);//查询功能
	}

	public static void buildTokenSet() {
		Directory directory = null;
		IndexReader reader = null;
		try {
			directory = FSDirectory.open(new File(LucenePath));
			reader = IndexReader.open(directory);
			Terms msgTerm = MultiFields.getTerms(reader, "message");
			TermsEnum msgEnum = msgTerm.iterator(null);
			int termID = 0;
			while (msgEnum.next() != null) {
				String term = msgEnum.term().utf8ToString();
				DocsEnum termDocs = msgEnum.docs(null, null,
						DocsEnum.FLAG_FREQS);
				int termCount = 0;
				while (termDocs.nextDoc() != DocsEnum.NO_MORE_DOCS) {
					termCount += termDocs.freq();
				}
				try {
					BufferedWriter writer = new BufferedWriter(new FileWriter(
							new File(AllTokenSetPath), true));
					writer.write(termCount + "\t" + term);
					writer.newLine();
					writer.flush();
					writer.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (termCount > TermFrequent) {
					try {
						BufferedWriter writer = new BufferedWriter(
								new FileWriter(new File(TermSetPath), true));
						writer.write(++termID + "\t" + termCount + "\t" + term);
						writer.newLine();
						writer.flush();
						writer.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			
			System.out.println("词频阙值:" + TermFrequent + "\n" + "总分词数:"
					+ msgTerm.size() + "\n" + "去除干扰词数:"
					+ (msgTerm.size() - termID) + "\n" + "有效词数:" + termID
					+ "\n" + "干扰词数/总分词数 = "
					+ ((float) (msgTerm.size() - termID) / msgTerm.size()));
			try {
				BufferedWriter writer = new BufferedWriter(new FileWriter(
						new File(TermSetPath), true));
				writer.write("****************************");
				writer.newLine();
				writer.write("词频阙值:" + TermFrequent);
				writer.newLine();
				writer.write("总分词数:" + msgTerm.size());
				writer.newLine();
				writer.write("去除干扰词数:" + (msgTerm.size() - termID));
				writer.newLine();
				writer.write("有效词数：" + termID);
				writer.newLine();
				writer.write("干扰词数/总分词数 = "
						+ ((float) (msgTerm.size() - termID) / msgTerm.size()));
				writer.newLine();
				writer.flush();
				writer.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			System.out.println("Completed.");
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

	public static void search(String filePath, String queryString,
			String field, int hits) {
		Directory directory = null;
		IndexReader reader = null;
		try {
			directory = FSDirectory.open(new File(filePath));
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
