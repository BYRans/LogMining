package old_training;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

//import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DocsEnum;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.queryparser.classic.ParseException;
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

public class RMNoiseWord {
	public static Integer TERM_FREQUENT = 2;// 鍒ゅ畾浣庨璇嶉槞鍊�

	public static int Hits = 1000;
	public static String QueryString = "790,928,707,843,349,1018,001";
	public static String Field = "vector";

	public static void main(String[] args) throws Exception {
		System.out.println("Running...");
		// buildTokenSet();
		search("C:/Documents and Settings/js4/桌面/testLucene/", QueryString,
				Field, Hits);// 鏌ヨ鍔熻兘
	}

	@SuppressWarnings("deprecation")
	public static void buildTokenSet() {
		Directory directory = null;
		IndexReader reader = null;
		try {
			directory = FSDirectory.open(new File(COMMON_PATH.LUCENE_PATH));
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
							new File(COMMON_PATH.AllTOKEN_SET_PATH), true));
					writer.write(termCount + "\t" + term);
					writer.newLine();
					writer.flush();
					writer.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (termCount > TERM_FREQUENT) {
					try {
						BufferedWriter writer = new BufferedWriter(
								new FileWriter(new File(
										COMMON_PATH.TOKEN_SET_PATH), true));
						writer.write(++termID + "\t" + termCount + "\t" + term);
						writer.newLine();
						writer.flush();
						writer.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}

			System.out.println("璇嶉闃欏�:" + TERM_FREQUENT + "\n" + "鎬诲垎璇嶆暟:"
					+ msgTerm.size() + "\n" + "鍘婚櫎骞叉壈璇嶆暟:"
					+ (msgTerm.size() - termID) + "\n" + "鏈夋晥璇嶆暟:" + termID
					+ "\n" + "骞叉壈璇嶆暟/鎬诲垎璇嶆暟 = "
					+ ((float) (msgTerm.size() - termID) / msgTerm.size()));
			try {
				BufferedWriter writer = new BufferedWriter(new FileWriter(
						new File(COMMON_PATH.AllTOKEN_SET_PATH), true));
				writer.write("****************************");
				writer.newLine();
				writer.write("璇嶉闃欏�:" + TERM_FREQUENT);
				writer.newLine();
				writer.write("鎬诲垎璇嶆暟:" + msgTerm.size());
				writer.newLine();
				writer.write("鍘婚櫎骞叉壈璇嶆暟:" + (msgTerm.size() - termID));
				writer.newLine();
				writer.write("鏈夋晥璇嶆暟锛" + termID);
				writer.newLine();
				writer.write("骞叉壈璇嶆暟/鎬诲垎璇嶆暟 = "
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

	@SuppressWarnings("deprecation")
	public static void search(String FILE_PATH, String queryString,
			String field, int hits) throws ParseException {
		Directory directory = null;
		IndexReader reader = null;
		try {
			directory = FSDirectory.open(new File(FILE_PATH));
			reader = IndexReader.open(directory);
			IndexSearcher searcher = new IndexSearcher(reader);
			Term term1 = new Term("vector", "790,928,707,843,349,1018,001");
			Term term2 = new Term("label", "Label_0");
			Term term3 = new Term("timeStampDay", "2014-11-18");
			TermQuery query1 = new TermQuery(term1);
			TermQuery query2 = new TermQuery(term2);
			QueryParser parser = new QueryParser("timeStampDay",
					new StandardAnalyzer());
			Query query3 = parser.parse(queryString);

			BooleanQuery booleanQuery = new BooleanQuery();
			booleanQuery.add(query1, Occur.MUST);
			// booleanQuery.add(query2, Occur.MUST);
			// booleanQuery.add(query3, Occur.MUST);
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
				System.out.println("docid:"+docCount[i]);
				
				System.out.print(searcher.doc(docCount[i]).get("docId") + "\t");
				System.out.println(searcher.doc(docCount[i]).get("vector"));
				// System.out.println(searcher.doc(docCount[i]).get("timeStampDay"));
//				 System.out.println("sd.doc " + sd.doc);

				i++;
			}
			
			
			System.out.println("*********");
			Document doc = searcher.doc(1);
			System.out.print(doc.get("docId") + "\t");
			System.out.println(doc.get("vector"));
			
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
