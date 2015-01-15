package training;

import java.io.File;

/**
 * ����ʽ������COMMON_PATH,�洢�������ļ�·��
 * */
public class COMMON_PATH {

	/** �洢 ԭʼsyslog �ļ���·�� */
	public final static String RAW_LOG_FILE_PATH = "C:/Users/Administrator/Desktop/LogMining/RawLog";

	/** �洢 syslog��Lucene �ļ�·�� */
	public final static String LUCENE_PATH = "C:/Users/Administrator/Desktop/testtest";

	/** �洢 �������ձ�ǩ���syslog��Lucene �ļ�·�� */
	public final static String LABELED_LUCENE_PATH = "/home/pgxc/LogMining/LabeledLuceneFile/";

	/** �洢 ���зִ� �ļ�·�� */
	public final static String AllTOKEN_SET_PATH = "/home/pgxc/LogMining/AllTokenSet.txt";

	/** �洢 �ִʿ� �ļ�·�� */
	public final static String TOKEN_SET_PATH = "/home/pgxc/LogMining/TokenSet.txt";

	/** �洢 syslog��Message���������� �ļ�·�� */
	public final static String VECTOR_PATH = "/home/pgxc/LogMining/Vector.txt";

	/** �洢 FPTree �ļ�·�� */
	public final static String FPTREE_PATH = "/home/pgxc/LogMining/LogMerge/FPTree.txt";

	/** �洢 ����ʱ��ǵ�Message������ �ļ�·�� */
	public final static String LABEL_VECTOR_PATH = "/home/pgxc/LogMining/LabelVector.txt";

	/** �洢 ѵ��syslog��ʱ����ԭʼ���� �ļ�·�� */
	public final static String LABEL_RAW_DATA_PATH = "/home/pgxc/LogMining/LabelRawData.txt";

	/** �洢 ��ʱ��ǩ���ñ�ǩ������docIds �ļ�·�� */
	public final static String LABEL_DOCIDS_PATH = "/home/pgxc/LogMining/LabelDocIds.txt";

	/** �洢 ���շ������ �ļ�·�� */
	public final static String LABEL_SET_PATH = "/home/pgxc/LogMining/LabelSet.txt";

	/** �洢 syslogʱ��������� �ļ�·�� */
	public final static String TIMESTAMP_LABEL_PATH = "/home/pgxc/LogMining/TimeStampLabel.txt";

	/** �洢 ���ձ�ǩ��ǩ���ñ�ǩ������docIds �ļ�·�� */
	public final static String LABEL_SET_DOCIDS_PATH = "/home/pgxc/LogMining/LabelSetDocIds.txt";

	/** �洢 ���� �ļ�·�� */
	public final static String FEATURE_PATH = "/home/pgxc/LogMining/Feature/Feature.txt";

	/** �澯��־ �ļ�·�� */
	public static String WARNING_LOG_PATH = "/home/pgxc/LogMining/LogMerge/WarningLog.txt";

	/** �洢 �ϲ�����־ �ļ�·�� */
	public static String MERGE_LOG_PATH = "/home/pgxc/LogMining/LogMerge/MergeLog.txt";

	/** �洢 �Ƴ��޹�syslog��label �ļ�·�� */
	public static String REMOVED_LABEL_PATH = "/home/pgxc/LogMining/LogMerge/RemoveLabel.txt";

	/** �洢 Ƶ����ļ� ���ļ� ·�� */
	public static String FREQUENT_ITEM_SETS_PATH = "/home/pgxc/LogMining/LogMerge/";

	/** �洢 syslog���澯��־���� �ļ�·�� */
	public static String FEATURE_FOLDER_PATH = "/home/pgxc/LogMining/Feature/";

	/** �洢 ��������ͳ������ �ļ�·�� */
	public static String STATISTICS_PATH = "/home/pgxc/LogMining/STATISTICS.txt";

	private static final COMMON_PATH single = new COMMON_PATH();

	// ��̬��������
	public static COMMON_PATH getInstance() {
		return single;
	}

	/**
	 * ��ʼ���ļ����� ����ļ��в����ڣ����½��ļ��У�����ļ����Ѵ��ڣ���ɾ��·���������ļ����������ļ��м����ļ���
	 * 
	 * @param sPath
	 *            ����ʼ��Ŀ¼���ļ�·��
	 * @return Ŀ¼��ʼ���ɹ�����true�����򷵻�false
	 */
	public static boolean INIT_DIR(String sPath) {
		// ���sPath�����ļ��ָ�����β���Զ�����ļ��ָ���
		if (!sPath.endsWith(File.separator)) {
			sPath = sPath + File.separator;
		}
		File dirFile = new File(sPath);
		// ���dir��Ӧ���ļ������ڣ����߲���һ��Ŀ¼�����˳�
		if (!dirFile.exists() || !dirFile.isDirectory()) {
			dirFile.mkdir();
			return true;
		}
		boolean flag = true;
		// ɾ���ļ����µ������ļ�(������Ŀ¼)
		File[] files = dirFile.listFiles();
		for (int i = 0; i < files.length; i++) {
			if (files[i].isFile()) {// ɾ�����ļ�
				flag = DELETE_FILE(files[i].getAbsolutePath());
				if (!flag)
					break;
			} else {// ɾ����Ŀ¼
				flag = INIT_DIR(files[i].getAbsolutePath());
				if (!flag)
					break;
			}
		}
		if (!flag)
			return false;
		return true;
	}

	/**
	 * ɾ�������ļ�
	 * 
	 * @param sPath
	 *            ��ɾ���ļ����ļ���
	 * @return
	 * @return �����ļ�ɾ���ɹ�����true�����򷵻�false
	 */
	public static boolean DELETE_FILE(String sPath) {
		boolean flag = false;
		File file = new File(sPath);
		// ·��Ϊ�ļ��Ҳ�Ϊ�������ɾ��
		if (file.isFile() && file.exists()) {
			file.delete();
			flag = true;
		}
		return flag;
	}
}
