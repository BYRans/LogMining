package dataFilter;

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
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.queryparser.classic.QueryParser;

import training.COMMON_PATH;

public class Statistics {
	public static Integer TERM_FREQUENT = 2;// 判定低频词阙值

	public static int Hits = 1000;
	public static String QueryString = "2014-11-19";
	public static String Field = "timeStamp";

	public static void main(String[] args) throws Exception {
		System.out.println("Running...");
		buildTokenSet();
		// search(LUCENE_PATH, QueryString, Field, Hits);// 查询功能
	}

	@SuppressWarnings("deprecation")
	public static void buildTokenSet() {
		Directory directory = null;
		IndexReader reader = null;
		try {
			directory = FSDirectory.open(new File(COMMON_PATH.LUCENE_PATH));
			reader = IndexReader.open(directory);
			Terms msgTerm = MultiFields.getTerms(reader, "ip");
			TermsEnum msgEnum = msgTerm.iterator(null);
			int termID = 0;
			while (msgEnum.next() != null) {
				String term = msgEnum.term().utf8ToString();

				System.out.println(term);

				// DocsEnum termDocs = msgEnum.docs(null, null,
				// DocsEnum.FLAG_FREQS);
				// int termCount = 0;
				// while (termDocs.nextDoc() != DocsEnum.NO_MORE_DOCS) {
				// termCount += termDocs.freq();
				// }
				// try {
				// BufferedWriter writer = new BufferedWriter(new FileWriter(
				// new File(AllTOKEN_SET_PATH), true));
				// writer.write(termCount + "\t" + term);
				// writer.newLine();
				// writer.flush();
				// writer.close();
				// } catch (Exception e) {
				// e.printStackTrace();
				// }
				// if (termCount > TERM_FREQUENT) {
				// try {
				// BufferedWriter writer = new BufferedWriter(
				// new FileWriter(new File(TermSetPath), true));
				// writer.write(++termID + "\t" + termCount + "\t" + term);
				// writer.newLine();
				// writer.flush();
				// writer.close();
				// } catch (Exception e) {
				// e.printStackTrace();
				// }
				// }
			}

			// System.out.println("词频阙值:" + TERM_FREQUENT + "\n" + "总分词数:"
			// + msgTerm.size() + "\n" + "去除干扰词数:"
			// + (msgTerm.size() - termID) + "\n" + "有效词数:" + termID
			// + "\n" + "干扰词数/总分词数 = "
			// + ((float) (msgTerm.size() - termID) / msgTerm.size()));
			// try {
			// BufferedWriter writer = new BufferedWriter(new FileWriter(
			// new File(AllTOKEN_SET_PATH), true));
			// writer.write("****************************");
			// writer.newLine();
			// writer.write("词频阙值:" + TERM_FREQUENT);
			// writer.newLine();
			// writer.write("总分词数:" + msgTerm.size());
			// writer.newLine();
			// writer.write("去除干扰词数:" + (msgTerm.size() - termID));
			// writer.newLine();
			// writer.write("有效词数：" + termID);
			// writer.newLine();
			// writer.write("干扰词数/总分词数 = "
			// + ((float) (msgTerm.size() - termID) / msgTerm.size()));
			// writer.newLine();
			// writer.flush();
			// writer.close();
			// } catch (Exception e) {
			// e.printStackTrace();
			// }
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

	@SuppressWarnings("deprecation")
	public static void search(String FILE_PATH, String queryString,
			String field, int hits) throws ParseException {
		Directory directory = null;
		IndexReader reader = null;
		try {
			directory = FSDirectory.open(new File(FILE_PATH));
			reader = IndexReader.open(directory);
			IndexSearcher searcher = new IndexSearcher(reader);
			Term term1 = new Term("ip", "192.168.8.190");
			Term term2 = new Term("label", "Label_0");
			Term term3 = new Term("timeStampDay", "2014-11-18");
			TermQuery query1 = new TermQuery(term1);
			TermQuery query2 = new TermQuery(term2);
			// TermQuery query3 = new TermQuery(term3);
			String QueryString = "2014-11-18";
			QueryParser parser = new QueryParser("timeStampDay",
					new StandardAnalyzer());
			Query query3 = parser.parse(queryString);

			BooleanQuery booleanQuery = new BooleanQuery();
			booleanQuery.add(query1, Occur.MUST);
			booleanQuery.add(query2, Occur.MUST);
			booleanQuery.add(query3, Occur.MUST);
			// query = parser.parse(queryString);
			TopDocs tds = searcher.search(booleanQuery, hits);
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
				System.out.println(searcher.doc(docCount[i]).get("label"));
				System.out.println(searcher.doc(docCount[i]).get("ip"));
				System.out.println(searcher.doc(docCount[i])
						.get("timeStampDay"));
				System.out.println("sd.doc " + sd.doc);
				// Document document = searcher.doc(sd.doc);
				// System.out.println(document.get("message")+" ");
			}
			List<Integer> list = new ArrayList<Integer>();

			for (int j = 0; j < docCount.length; j++) {
				list.add(docCount[j]);
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
