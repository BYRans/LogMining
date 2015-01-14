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

public class RMNoiseWordByRule {

	public static int Hits = 10;
	public static String QueryString = "exception";
	public static String Field = "message";

	public static void main(String[] args) throws Exception {
		System.out.println("RMNoiseWordByRule Running...");
		buildTokenSet();
		// search(LUCENE_PATH, QueryString, Field, Hits);//��ѯ����
	}

	@SuppressWarnings("deprecation")
	public static void buildTokenSet() {
		Directory directory = null;
		IndexReader reader = null;
		try {
			directory = FSDirectory.open(new File(PATHS.LUCENE_PATH));
			reader = IndexReader.open(directory);
			Terms msgTerm = MultiFields.getTerms(reader, "message");
			TermsEnum msgEnum = msgTerm.iterator(null);
			int termID = 0;
			String regNumber = "^[0-9a-fA-F]*$";
			String regIP = "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
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
							new File(PATHS.AllTOKEN_SET_PATH), true));
					writer.write(termCount + "\t" + term);
					writer.newLine();
					writer.flush();
					writer.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				boolean isNumber = term.matches(regNumber);
				boolean isIP = term.matches(regIP);
				
				if ((!isNumber)&&(!isIP)) {
					try {
						BufferedWriter writer = new BufferedWriter(
								new FileWriter(new File(PATHS.TOKEN_SET_PATH), true));
						writer.write(++termID + "\t" + termCount + "\t" + term);
						writer.newLine();
						writer.flush();
						writer.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			
			System.out.println("�ִܷ���:"
					+ msgTerm.size() + "\n" + "ȥ�����Ŵ���:"
					+ (msgTerm.size() - termID) + "\n" + "��Ч����:" + termID
					+ "\n" + "���Ŵ���/�ִܷ��� = "
					+ ((float) (msgTerm.size() - termID) / msgTerm.size()));
			try {
				BufferedWriter writer = new BufferedWriter(new FileWriter(
						new File(PATHS.AllTOKEN_SET_PATH), true));
				writer.write("****************************");
				writer.newLine();
				writer.write("�ִܷ���:" + msgTerm.size());
				writer.newLine();
				writer.write("ȥ�����Ŵ���:" + (msgTerm.size() - termID));
				writer.newLine();
				writer.write("��Ч������" + termID);
				writer.newLine();
				writer.write("���Ŵ���/�ִܷ��� = "
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
