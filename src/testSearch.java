import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
//import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DocsAndPositionsEnum;
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
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.queryparser.classic.QueryParser;

public class testSearch {
	public static String lucenePath = "C:/Users/Administrator/Desktop/LogMining/luceneFile/";
	public static String termSetPath = "C:/Users/Administrator/Desktop/LogMining/termSet.txt";
	public static String queryString = "exception";
	public static String field = "message";
	public static int hits = 10;

	public static void main(String[] args) throws Exception {
		search(lucenePath, queryString, field, hits);
	}

	public static void search(String lucenePath, String queryString,
			String field, int hits) {
		Directory directory = null;
		IndexReader reader = null;
		try {
			directory = FSDirectory.open(new File(lucenePath));
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
				if (termCount > 2) {
					try {
						BufferedWriter writer = new BufferedWriter(
								new FileWriter(new File(termSetPath), true));
						writer.write(++termID + "\t" + termCount + "\t" + term);
						writer.newLine();
						writer.flush();
						writer.close();

					} catch (Exception e) {

					}
					System.out.println(term + "\t" + termCount);
				}
			}

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
				for (ScoreDoc sd : sds) {
					// Document document = searcher.doc(sd.doc);
					Terms termVector = reader.getTermVector(sd.doc, "message");
					if (termVector != null && termVector.size() > 0) {
						TermsEnum termsEnum = termVector.iterator(null); // 取该field的terms
						BytesRef term = null;
						HashMap<Integer, String> posAndTerMap = new HashMap<Integer, String>();// 把terms及偏移量存到hashMap中,<position,term>.
						int termIndex = 0;
						while ((term = termsEnum.next()) != null) {// 迭代term
							DocsAndPositionsEnum positionEnum = termsEnum
									.docsAndPositions(null, null);// 该term在该document下的偏移量？？哪有该document的标示？
							positionEnum.nextDoc();
							// int position=positionEnum.nextPosition();

							int freq = positionEnum.freq();// 该term在该文档出现多少次，出现几次就有几个偏移量。
							for (int i = 0; i < freq; i++) {
								int position = positionEnum.nextPosition();
								posAndTerMap.put(position, term.utf8ToString());
							}
						}
						Object[] key_arr = posAndTerMap.keySet().toArray();// 按position排序
						Arrays.sort(key_arr);
						for (Object key : key_arr) {
							String value = posAndTerMap.get(key);
							System.out.print(value + " ");
						}
						System.out.println();
					}
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

	public static void displayToken(String str, Analyzer analyzer) {
		try {
			// 将一个字符串创建成Token流
			str = "hello kim,I am dennisit,我是 中国人,my email is dennisit@163.com, and my QQ is 1325103287";
			TokenStream stream = analyzer
					.tokenStream("", new StringReader(str));
			// 保存相应词汇
			CharTermAttribute cta = stream
					.addAttribute(CharTermAttribute.class);
			while (stream.incrementToken()) {
				System.out.print("[" + cta + "]");
			}
			System.out.println();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
