package training;

/**
 * 饿汉式单例类PATHS,存储各步骤文件路径
 * */
public class PATHS {

	/** 存储 原始syslog 文件夹路径 */
	public final static String RAW_LOG_FILE_PATH = "C:/Users/Administrator/Desktop/LogMining/Analyze/";

	/** 存储  syslog的Lucene 文件路径 */
	public final static String LUCENE_PATH = "C:/Users/Administrator/Desktop/LogMining/luceneFile/";

	/** 存储  所有分词 文件路径 */
	public final static String AllTOKEN_SET_PATH = "C:/Users/Administrator/Desktop/LogMining/AllTokenSet.txt";

	/**存储  分词库 文件路径 */
	public final static String TOKEN_SET_PATH = "C:/Users/Administrator/Desktop/LogMining/TokenSet.txt";

	/** 存储  syslog的Message域向量集合 文件路径 */
	public final static String VECTOR_PATH = "C:/Users/Administrator/Desktop/LogMining/Vector.txt";
	
	/**存储  FPTree 文件路径 */
	public final static String FPTREE_PATH = "C:/Users/Administrator/Desktop/FPTree.txt";
	
	/**存储  带临时标记的Message域向量 文件路径 */
	public final static String LABEL_VECTOR_PATH = "C:/Users/Administrator/Desktop/LogMining/LabelVector.txt";
	
	/**存储  训练syslog临时分类原始数据 文件路径 */
	public final static String LABEL_RAW_DATA_PATH = "C:/Users/Administrator/Desktop/LogMining/LabelRawData.txt";
	
	/** 存储  临时标签及该标签包含的docIds 文件路径 */
	public final static String LABEL_DOCIDS_PATH = "C:/Users/Administrator/Desktop/LogMining/LabelDocIds.txt";
	
	/** 存储 最终分类情况 文件路径 */
	public final static String LABEL_SET_PATH = "C:/Users/Administrator/Desktop/LogMining/LabelSet.txt";
	
	/** 存储 syslog时间戳及类别的 文件路径 */
	public final static String TIMESTAMP_LABEL_PATH = "C:/Users/Administrator/Desktop/LogMining/TimeStampLabel.txt";
	
	/** 存储  最终标签标签及该标签包含的docIds 文件路径 */
	public final static String LABEL_SET_DOCIDS_PATH = "C:/Users/Administrator/Desktop/LogMining/LabelSetDocIds.txt";
	
	/** 存储 特征 文件路径 */
	public final static String FEATURE_PATH = "C:/Users/Administrator/Desktop/LogMining/Feature/Feature.txt";

	private static final PATHS single = new PATHS();

	// 静态工厂方法
	public static PATHS getInstance() {
		return single;
	}
}
