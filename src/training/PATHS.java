package training;

/**
 * ����ʽ������PATHS,�洢�������ļ�·��
 * */
public class PATHS {

	/** �洢 ԭʼsyslog �ļ���·�� */
	public final static String RAW_LOG_FILE_PATH = "C:/Users/Administrator/Desktop/LogMining/Analyze/";

	/** �洢  syslog��Lucene �ļ�·�� */
	public final static String LUCENE_PATH = "C:/Users/Administrator/Desktop/LogMining/luceneFile/";

	/** �洢  ���зִ� �ļ�·�� */
	public final static String AllTOKEN_SET_PATH = "C:/Users/Administrator/Desktop/LogMining/AllTokenSet.txt";

	/**�洢  �ִʿ� �ļ�·�� */
	public final static String TOKEN_SET_PATH = "C:/Users/Administrator/Desktop/LogMining/TokenSet.txt";

	/** �洢  syslog��Message���������� �ļ�·�� */
	public final static String VECTOR_PATH = "C:/Users/Administrator/Desktop/LogMining/Vector.txt";
	
	/**�洢  FPTree �ļ�·�� */
	public final static String FPTREE_PATH = "C:/Users/Administrator/Desktop/FPTree.txt";
	
	/**�洢  ����ʱ��ǵ�Message������ �ļ�·�� */
	public final static String LABEL_VECTOR_PATH = "C:/Users/Administrator/Desktop/LogMining/LabelVector.txt";
	
	/**�洢  ѵ��syslog��ʱ����ԭʼ���� �ļ�·�� */
	public final static String LABEL_RAW_DATA_PATH = "C:/Users/Administrator/Desktop/LogMining/LabelRawData.txt";
	
	/** �洢  ��ʱ��ǩ���ñ�ǩ������docIds �ļ�·�� */
	public final static String LABEL_DOCIDS_PATH = "C:/Users/Administrator/Desktop/LogMining/LabelDocIds.txt";
	
	/** �洢 ���շ������ �ļ�·�� */
	public final static String LABEL_SET_PATH = "C:/Users/Administrator/Desktop/LogMining/LabelSet.txt";
	
	/** �洢 syslogʱ��������� �ļ�·�� */
	public final static String TIMESTAMP_LABEL_PATH = "C:/Users/Administrator/Desktop/LogMining/TimeStampLabel.txt";
	
	/** �洢  ���ձ�ǩ��ǩ���ñ�ǩ������docIds �ļ�·�� */
	public final static String LABEL_SET_DOCIDS_PATH = "C:/Users/Administrator/Desktop/LogMining/LabelSetDocIds.txt";
	
	/** �洢 ���� �ļ�·�� */
	public final static String FEATURE_PATH = "C:/Users/Administrator/Desktop/LogMining/Feature/Feature.txt";

	private static final PATHS single = new PATHS();

	// ��̬��������
	public static PATHS getInstance() {
		return single;
	}
}
