package analysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.Map.Entry;
import java.util.Set;

import training.COMMON_PATH;

public class FPGrowth {
	public static int WINDOWN_SIZE = 120;// 窗口大小，分钟为单位
	public static int STEP_SIZE = 30;// 步长大小，分钟为单位
	public static SimpleDateFormat DATE_TEMPLATE = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");
	public static Stack<TreeNode> STACK = new Stack<TreeNode>();
	public static HashSet<String> WARNING_LABEL_SET = new HashSet<String>();
	private int minSup; // 最小支持度

	public int getMinSup() {
		return minSup;
	}

	public void setMinSup(int minSup) {
		this.minSup = minSup;
	}

	public static void main(String[] args) throws IOException, Exception {
		
		initWarningLabelSet();
		File f = new File(COMMON_PATH.MERGE_LOG_PATH);
		File[] fileList = f.listFiles();
		for (File file : fileList) {
			FPGrowth fptree = new FPGrowth();
			List<List<String>> transRecords = fptree.readFile(file);
			fptree.setMinSup(0);
			ArrayList<TreeNode> F1 = fptree.buildF1Items(transRecords);
			TreeNode treeroot = fptree.buildFPTree(transRecords, F1);
			printTree(treeroot, "", true, file.getName().split("_")[1]);// 打印FPTree树
			printTreePaths(treeroot);// 打印FPTree根节点到叶节点路径
		}
	}

	public static void initWarningLabelSet() {

		try {
			File file = new File(COMMON_PATH.WARNING_LOG_LABEL_LIST_PATH);
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					new FileInputStream(file), "UTF-8"));
			String line = null;
			while ((line = reader.readLine()) != null) {
				if ("".equals(line.trim())) {
					continue;
				}
				WARNING_LABEL_SET.add(line.trim());
			}
			reader.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

	}

	/**
	 * 打印多叉树根节点到叶子节点路径
	 * 
	 * @param node
	 *            树根节点
	 */
	public static void printTreePaths(TreeNode node) {
		if (node != null) {
			STACK.add(node);
			if (node.getChildren() == null || node.getChildren().size() == 0) {
				for (TreeNode n : STACK) {
					System.out.print(n.getName() + " ");
				}
				System.out.print("\n");
				STACK.pop();
				return;
			}
			for (int i = 0; i < node.getChildren().size(); i++) {
				printTreePaths(node.getChildren().get(i));
			}
			STACK.pop();
		}
	}

	/**
	 * 打印多叉树，并把打印出的树写入文件。
	 * 
	 * @param treeNode
	 *            根节点
	 * @param prefix
	 *            输入""
	 * @param isTail
	 *            输入 true
	 * @param saveFileName
	 *            输入保存树的文件名，文件在COMMON_PATH.FPTREE_PATH文件夹下。
	 * */
	public static void printTree(TreeNode treeNode, String prefix,
			boolean isTail, String saveFileName) {
		System.out.println(prefix + (isTail ? "└── " : "├── ")
				+ treeNode.getName() + " :" + treeNode.getCount());
		FileWriter FPResFile;
		try {
			FPResFile = new FileWriter(new File(COMMON_PATH.FPTREE_PATH
					+ saveFileName), true);
			FPResFile.append(prefix + (isTail ? "└── " : "├── ")
					+ treeNode.getName() + " :" + treeNode.getCount() + "\r\n");
			FPResFile.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (treeNode.getChildren() == null)
			return;
		for (int i = 0; i < treeNode.getChildren().size() - 1; i++) {
			printTree(treeNode.getChildren().get(i), prefix
					+ (isTail ? "    " : "│   "), false, saveFileName);
		}
		if (treeNode.getChildren().size() > 0) {
			printTree(
					treeNode.getChildren().get(
							treeNode.getChildren().size() - 1), prefix
							+ (isTail ? "    " : "│   "), true, saveFileName);
		}
	}

	/**
	 * 读入事务记录
	 * 
	 * @param filenames
	 * @return
	 */
	public List<List<String>> readTransData(String filename) {
		List<List<String>> records = new LinkedList<List<String>>();
		List<String> record;
		try {
			FileReader fr = new FileReader(new File(filename));
			@SuppressWarnings("resource")
			BufferedReader br = new BufferedReader(fr);
			String line = null;
			while ((line = br.readLine()) != null) {
				if (line.trim() != "") {
					record = new LinkedList<String>();
					String[] items = line.split(" ");
					for (String item : items) {
						record.add(item);
					}
					records.add(record);
				}
			}
		} catch (IOException e) {
			System.out.println("读取事务数据库失败。");
			System.exit(-2);
		}
		return records;
	}

	/**
	 * 读取事务数据库
	 * 
	 * @param fileDir
	 *            事务文件目录
	 * @return List<String> 保存事务的容器
	 * @throws ParseException
	 * @throws IOException
	 */
	@SuppressWarnings("resource")
	private List<List<String>> readFile(File file) throws ParseException {
		List<List<String>> records = new ArrayList<List<String>>();
		List<String[]> dataList = new ArrayList<String[]>();
		try {
			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);
			String line = null;
			while ((line = br.readLine()) != null) {
				if (!"".equals(line.trim())) {
					String[] dataArr = line.split("\t");
					dataList.add(dataArr);
				}
			}
		} catch (IOException e) {
			System.out.println("读取文件失败。");
			System.exit(-2);
		}

		if (dataList.size() == 0)
			return records;
		String tmpDate = (dataList.get(0))[0];
		Date minDate = DATE_TEMPLATE.parse(tmpDate);
		tmpDate = (dataList.get(dataList.size() - 1))[0];
		Date endDate = DATE_TEMPLATE.parse(tmpDate);
		Calendar cal = Calendar.getInstance();
		cal.setTime(minDate);
		cal.add(Calendar.MINUTE, WINDOWN_SIZE);// 设置窗口最大时间
		Date maxDate = cal.getTime();
		while (minDate.getTime() < endDate.getTime()) {
			List<String[]> tmpDataList = new ArrayList<String[]>();
			for (int i = 0; i < dataList.size(); i++) {
				tmpDate = (dataList.get(i))[0];
				Date timeStamp = DATE_TEMPLATE.parse(tmpDate);
				if (timeStamp.getTime() >= minDate.getTime()
						&& timeStamp.getTime() < maxDate.getTime()) {
					tmpDataList.add(dataList.get(i));
				}
			}

			List<String> record = new ArrayList<String>();
			if (tmpDataList.size() > 0) {
				boolean ifExistWarn = false;
				for (String[] item : tmpDataList) {
					record.add(item[1]);
					if (WARNING_LABEL_SET.contains(item[1]))// 如果该事务项集存在告警日志，则存入事务数据集
						ifExistWarn = true;
				}
				if (ifExistWarn)
					records.add(record);
			}

			cal.setTime(minDate);
			cal.add(Calendar.MINUTE, STEP_SIZE);// 窗口最大最小时间下滑一个步长
			minDate = cal.getTime();
			cal.setTime(maxDate);
			cal.add(Calendar.MINUTE, STEP_SIZE);
			maxDate = cal.getTime();
		}

		return records;
	}

	/**
	 * 构造频繁1项集
	 * 
	 * @param transRecords
	 * @return
	 */
	public ArrayList<TreeNode> buildF1Items(List<List<String>> transRecords) {
		ArrayList<TreeNode> F1 = null;
		if (transRecords.size() > 0) {
			F1 = new ArrayList<TreeNode>();
			Map<String, TreeNode> map = new HashMap<String, TreeNode>();
			// 计算事务数据库中各项的支持度
			for (List<String> record : transRecords) {
				for (String item : record) {
					if (!map.keySet().contains(item)) {
						TreeNode node = new TreeNode(item);
						node.setCount(1);
						map.put(item, node);
					} else {
						map.get(item).countIncrement(1);
					}
				}
			}
			// 把支持度大于（或等于）minSup的项加入到F1中
			Set<String> names = map.keySet();
			for (String name : names) {
				TreeNode tnode = map.get(name);
				if (tnode.getCount() >= minSup) {
					F1.add(tnode);
				}
			}
			Collections.sort(F1);
			return F1;
		} else {
			return null;
		}
	}

	/**
	 * 建立FP-Tree
	 * 
	 * @param transRecords
	 * @param F1
	 * @return
	 */
	public TreeNode buildFPTree(List<List<String>> transRecords,
			ArrayList<TreeNode> F1) {
		TreeNode root = new TreeNode(); // 创建树的根节点
		for (List<String> transRecord : transRecords) {
			LinkedList<String> record = sortByF1(transRecord, F1);
			TreeNode subTreeRoot = root;
			TreeNode tmpRoot = null;
			if (root.getChildren() != null) {
				while (!record.isEmpty()
						&& (tmpRoot = subTreeRoot.findChild(record.peek())) != null) {// peek获取首元素
					tmpRoot.countIncrement(1);
					subTreeRoot = tmpRoot;// 层次遍历
					record.poll();// poll删除首元素
				}
			}
			addNodes(subTreeRoot, record, F1);
		}
		return root;
	}

	/**
	 * 把事务数据库中的一条记录按照F1（频繁1项集）中的顺序排序
	 * 
	 * @param transRecord
	 * @param F1
	 * @return
	 */
	public LinkedList<String> sortByF1(List<String> transRecord,
			ArrayList<TreeNode> F1) {
		Map<String, Integer> map = new HashMap<String, Integer>();
		for (String item : transRecord) {
			// 由于F1已经是按降序排列的，
			for (int i = 0; i < F1.size(); i++) {
				TreeNode tnode = F1.get(i);
				if (tnode.getName().equals(item)) {
					map.put(item, i);
				}
			}
		}
		ArrayList<Entry<String, Integer>> al = new ArrayList<Entry<String, Integer>>(
				map.entrySet());
		Collections.sort(al, new Comparator<Map.Entry<String, Integer>>() {
			@Override
			public int compare(Entry<String, Integer> arg0,
					Entry<String, Integer> arg1) {
				// 降序排列
				return arg0.getValue() - arg1.getValue();
			}
		});
		LinkedList<String> rest = new LinkedList<String>();
		for (Entry<String, Integer> entry : al) {
			rest.add(entry.getKey());
		}
		return rest;
	}

	/**
	 * 把若干个节点作为指定节点的后代插入树中
	 * 
	 * @param ancestor
	 * @param record
	 * @param F1
	 */
	public void addNodes(TreeNode ancestor, LinkedList<String> record,
			ArrayList<TreeNode> F1) {
		if (record.size() > 0) {
			while (record.size() > 0) {
				String item = record.poll();
				TreeNode leafnode = new TreeNode(item);
				leafnode.setCount(1);
				leafnode.setParent(ancestor);
				ancestor.addChild(leafnode);

				for (TreeNode f1 : F1) {
					if (f1.getName().equals(item)) {
						while (f1.getNextHomonym() != null) {
							f1 = f1.getNextHomonym();
						}
						f1.setNextHomonym(leafnode);
						break;
					}
				}

				addNodes(leafnode, record, F1);
			}
		}
	}

}
