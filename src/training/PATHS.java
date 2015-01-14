package training;

/**
 * ����ʽ������PATHS,�洢�������ļ�·��
 * */
public class PATHS {
	
	/** �洢 ԭʼsyslog �ļ���·�� */
	public final static String RAW_LOG_FILE_PATH = "/home/pgxc/LogMining/Analyze/";

	/** �洢  syslog��Lucene �ļ�·�� */
	public final static String LUCENE_PATH = "/home/pgxc/LogMining/luceneFile/";

	/** �洢  ���зִ� �ļ�·�� */
	public final static String AllTOKEN_SET_PATH = "/home/pgxc/LogMining/AllTokenSet.txt";

	/**�洢  �ִʿ� �ļ�·�� */
	public final static String TOKEN_SET_PATH = "/home/pgxc/LogMining/TokenSet.txt";

	/** �洢  syslog��Message���������� �ļ�·�� */
	public final static String VECTOR_PATH = "/home/pgxc/LogMining/Vector.txt";
	
	/**�洢  FPTree �ļ�·�� */
	public final static String FPTREE_PATH = "/home/pgxc/LogMining/LogMerge/FPTree.txt";
	
	/**�洢  ����ʱ��ǵ�Message������ �ļ�·�� */
	public final static String LABEL_VECTOR_PATH = "/home/pgxc/LogMining/LabelVector.txt";
	
	/**�洢  ѵ��syslog��ʱ����ԭʼ���� �ļ�·�� */
	public final static String LABEL_RAW_DATA_PATH = "/home/pgxc/LogMining/LabelRawData.txt";
	
	/** �洢  ��ʱ��ǩ���ñ�ǩ������docIds �ļ�·�� */
	public final static String LABEL_DOCIDS_PATH = "/home/pgxc/LogMining/LabelDocIds.txt";
	
	/** �洢 ���շ������ �ļ�·�� */
	public final static String LABEL_SET_PATH = "/home/pgxc/LogMining/LabelSet.txt";
	
	/** �洢 syslogʱ��������� �ļ�·�� */
	public final static String TIMESTAMP_LABEL_PATH = "/home/pgxc/LogMining/TimeStampLabel.txt";
	
	/** �洢  ���ձ�ǩ��ǩ���ñ�ǩ������docIds �ļ�·�� */
	public final static String LABEL_SET_DOCIDS_PATH = "/home/pgxc/LogMining/LabelSetDocIds.txt";
	
	/** �洢 ���� �ļ�·�� */
	public final static String FEATURE_PATH = "/home/pgxc/LogMining/Feature/Feature.txt";
	
	/** �澯��־  �ļ�·�� */
	public static String WARNING_LOG_PATH = "/home/pgxc/LogMining/LogMerge/WarningLog.txt";
	
	/**�洢 �ϲ�����־  �ļ�·�� */
	public static String MERGE_LOG_PATH = "/home/pgxc/LogMining/LogMerge/MergeLog.txt";
	
	/**�洢 �Ƴ��޹�syslog��label �ļ�·�� */
	public static String REMOVED_LABEL_PATH = "/home/pgxc/LogMining/LogMerge/RemoveLabel.txt";
	
	/**�洢 Ƶ����ļ� ���ļ� ·�� */
	public static String FREQUENT_ITEM_SETS_PATH = "/home/pgxc/LogMining/LogMerge/";
	
	/**�洢 syslog���澯��־���� �ļ�·�� */
	public static String FEATURE_FOLDER_PATH = "/home/pgxc/LogMining/Feature/";
	
	private static final PATHS single = new PATHS();

	// ��̬��������
	public static PATHS getInstance() {
		return single;
	}
}
