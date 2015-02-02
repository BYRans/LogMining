package training;

import java.io.File;

/**
 * 饿汉式单例类COMMON_PATH,存储各步骤文件路径
 * */
public class COMMON_PATH {

	/** 存储 原始syslog 文件夹路径 */
	public final static String RAW_LOG_FILE_PATH = "/home/iie/LogMining/RawLog";

	/** 存储 syslog的Lucene 文件路径 */
	public final static String LUCENE_PATH = "/home/iie/LogMining/LuceneFile";

	/** 存储 带有最终标签域的syslog的Lucene 文件路径 */
	public final static String LABELED_LUCENE_PATH = "/home/iie/LogMining/LabeledLuceneFile/";

	/** 存储 所有分词 文件路径 */
	public final static String AllTOKEN_SET_PATH = "/home/iie/LogMining/AllTokenSet.txt";

	/** 存储 分词库 文件路径 */
	public final static String TOKEN_SET_PATH = "/home/iie/LogMining/TokenSet.txt";

	/** 存储 syslog的Message域向量集合 文件路径 */
	public final static String VECTOR_PATH = "/home/iie/LogMining/Vector.txt";

	/** 存储 FPTree 文件路径 */
	public final static String FPTREE_PATH = "/home/iie/LogMining/LogMerge/FPTree.txt";

	/** 存储 带临时标记的Message域向量 文件路径 */
	public final static String LABEL_VECTOR_PATH = "/home/iie/LogMining/LabelVector.txt";

	/** 存储 训练syslog临时分类原始数据 文件路径 */
	public final static String LABEL_RAW_DATA_PATH = "/home/iie/LogMining/LabelRawData.txt";

	/** 存储 临时标签及该标签包含的docIds 文件路径 */
	public final static String LABEL_DOCIDS_PATH = "/home/iie/LogMining/LabelDocIds.txt";

	/** 存储 最终分类情况 文件路径 */
	public final static String LABEL_SET_PATH = "/home/iie/LogMining/LabelSet.txt";

	/** 存储 syslog时间戳及类别的 文件路径 */
	public final static String TIMESTAMP_LABEL_PATH = "/home/iie/LogMining/TimeStampLabel.txt";

	/** 存储 最终标签标签及该标签包含的docIds 文件路径 */
	public final static String LABEL_SET_DOCIDS_PATH = "/home/iie/LogMining/LabelSetDocIds.txt";

	/** 存储 特征 文件路径 */
	public final static String FEATURE_PATH = "/home/iie/LogMining/Feature/Feature.txt";

	/** 告警日志 文件路径 */
	public final static String WARNING_LOG_PATH = "/home/iie/LogMining/LogMerge/WarningLog/";

	/** 存储 合并后日志 文件路径 */
	public final static String MERGE_LOG_PATH = "/home/iie/LogMining/LogMerge/MergeLog/";

	/** 存储 移除无关syslog的label 文件路径 */
	public final static String REMOVED_LABEL_PATH = "/home/iie/LogMining/LogMerge/RemoveLabel.txt";

	/** 存储 频繁项集文件 的文件 路径 */
	public final static String FREQUENT_ITEM_SETS_PATH = "/home/iie/LogMining/LogMerge/";

	/** 存储 syslog、告警日志特征 文件路径 */
	public final static String FEATURE_FOLDER_PATH = "/home/iie/LogMining/Feature/";

	/** 存储 程序运行统计数据 文件路径 */
	public final static String STATISTICS_PATH = "/home/iie/LogMining/STATISTICS.txt";
	
	/** 存储 Vector Lucene 文件路径 */
	public final static String VECTOR_LUCENE_PATH = "/home/iie/LogMining/VectorLucene/";
	
	/** 存储 ip列表 文件路径 */
	public final static String IP_LIST_PATH = "/home/iie/LogMining/LogMerge/IPList";
	

	public static int VECTOR_COUNT = 40506707;
	public static void SET_VECTOR_COUNT(int count){
		COMMON_PATH.VECTOR_COUNT = count;
	}
	
	private static final COMMON_PATH single = new COMMON_PATH();

	// 静态工厂方法
	public static COMMON_PATH getInstance() {
		return single;
	}

	/**
	 * 初始化文件件。 如果文件夹不存在，则新建文件夹；如果文件夹已存在，则删除路径下所有文件，并保留文件夹及子文件夹
	 * 
	 * @param sPath
	 *            被初始化目录的文件路径
	 * @return 目录初始化成功返回true，否则返回false
	 */
	public static boolean INIT_DIR(String sPath) {
		// 如果sPath不以文件分隔符结尾，自动添加文件分隔符
		if (!sPath.endsWith(File.separator)) {
			sPath = sPath + File.separator;
		}
		File dirFile = new File(sPath);
		// 如果dir对应的文件不存在，或者不是一个目录，则退出
		if (!dirFile.exists() || !dirFile.isDirectory()) {
			dirFile.mkdir();
			return true;
		}
		boolean flag = true;
		// 删除文件夹下的所有文件(包括子目录)
		File[] files = dirFile.listFiles();
		for (int i = 0; i < files.length; i++) {
			if (files[i].isFile()) {// 删除子文件
				flag = DELETE_FILE(files[i].getAbsolutePath());
				if (!flag)
					break;
			} else {// 删除子目录
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
	 * 删除单个文件
	 * 
	 * @param sPath
	 *            被删除文件的文件名
	 * @return
	 * @return 单个文件删除成功返回true，否则返回false
	 */
	public static boolean DELETE_FILE(String sPath) {
		boolean flag = false;
		File file = new File(sPath);
		// 路径为文件且不为空则进行删除
		if (file.isFile() && file.exists()) {
			file.delete();
			flag = true;
		}
		return flag;
	}
}
