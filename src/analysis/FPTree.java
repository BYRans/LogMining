package analysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import training.COMMON_PATH;

public class FPTree  {
	public static int WINDOWN_SIZE = 120;// ���ڴ�С������Ϊ��λ
	public static int STEP_SIZE = 30;// ������С������Ϊ��λ
	public static SimpleDateFormat DATE_TEMPLATE = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");

	private int minSup; // ��С֧�ֶ�

	public int getMinSup() {
		return minSup;
	}

	public void setMinSup(int minSup) {
		this.minSup = minSup;
	}

	public static void main(String[] args) throws IOException, Exception {
		FPTree fptree = new FPTree();
		List<List<String>> transRecords = fptree.readFile(COMMON_PATH.MERGE_LOG_PATH); // ��һ�����
		// List<List<String>> transRecords = fptree.readTransData(); //�ڶ������
		// fptree.setMinSup((int) (transRecords.size() * 0.25));
		fptree.setMinSup(0);
		ArrayList<TreeNode> F1 = fptree.buildF1Items(transRecords);
		fptree.printF1(F1);
		TreeNode treeroot = fptree.buildFPTree(transRecords, F1);
		treeroot.print();// ��ӡFPTree
		// fptree.printFPTree(treeroot);
		// Map<List<String>, Integer> patterns = fptree.findFP(treeroot, F1);
		// System.out.println("size of F1 = " + F1.size());
		// long endTime = System.currentTimeMillis();
		// System.out.println("����ʱ��" + (endTime - startTime) + "ms");
		// fptree.printFreqPatterns(patterns, transFile, F1);
	}

	/**
	 * 1.���������¼
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
			System.out.println("��ȡ�������ݿ�ʧ�ܡ�");
			System.exit(-2);
		}
		return records;
	}

	/**
	 * ��ȡ�������ݿ�
	 * 
	 * @param fileDir
	 *            �����ļ�Ŀ¼
	 * @return List<String> �������������
	 * @throws ParseException
	 * @throws IOException
	 */
	@SuppressWarnings("resource")
	private List<List<String>> readFile(String fileDir) throws ParseException {
		List<List<String>> records = new ArrayList<List<String>>();
		List<String[]> dataList = new ArrayList<String[]>();
		try {
			FileReader fr = new FileReader(new File(fileDir));
			BufferedReader br = new BufferedReader(fr);
			String line = null;
			while ((line = br.readLine()) != null) {
				if (line.trim() != "") {
					String[] dataArr = line.split("\t");
					dataList.add(dataArr);
				}
			}
		} catch (IOException e) {
			System.out.println("��ȡ�ļ�ʧ�ܡ�");
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
		cal.add(Calendar.MINUTE, WINDOWN_SIZE);// ���ô������ʱ��
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
				for (String[] item : tmpDataList) {
					record.add(item[1]);
				}
				records.add(record);
			}

			cal.setTime(minDate);
			cal.add(Calendar.MINUTE, STEP_SIZE);// ���������Сʱ���»�һ������
			minDate = cal.getTime();
			cal.setTime(maxDate);
			cal.add(Calendar.MINUTE, STEP_SIZE);
			maxDate = cal.getTime();
		}

		return records;
	}

	/**
	 * 2.����Ƶ��1�
	 * 
	 * @param transRecords
	 * @return
	 */
	public ArrayList<TreeNode> buildF1Items(List<List<String>> transRecords) {
		ArrayList<TreeNode> F1 = null;
		if (transRecords.size() > 0) {
			F1 = new ArrayList<TreeNode>();
			Map<String, TreeNode> map = new HashMap<String, TreeNode>();
			// �����������ݿ��и����֧�ֶ�
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
			// ��֧�ֶȴ��ڣ�����ڣ�minSup������뵽F1��
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
	 * 3.����FP-Tree
	 * 
	 * @param transRecords
	 * @param F1
	 * @return
	 */
	public TreeNode buildFPTree(List<List<String>> transRecords,
			ArrayList<TreeNode> F1) {
		TreeNode root = new TreeNode(); // �������ĸ��ڵ�
		for (List<String> transRecord : transRecords) {
			LinkedList<String> record = sortByF1(transRecord, F1);
			TreeNode subTreeRoot = root;
			TreeNode tmpRoot = null;
			if (root.getChildren() != null) {
				while (!record.isEmpty()
						&& (tmpRoot = subTreeRoot.findChild(record.peek())) != null) {// peek��ȡ��Ԫ��
					tmpRoot.countIncrement(1);
					subTreeRoot = tmpRoot;// ��α���
					record.poll();// pollɾ����Ԫ��
				}
			}
			addNodes(subTreeRoot, record, F1);
		}
		return root;
	}

	/**
	 * 3.1���������ݿ��е�һ����¼����F1��Ƶ��1����е�˳������
	 * 
	 * @param transRecord
	 * @param F1
	 * @return
	 */
	public LinkedList<String> sortByF1(List<String> transRecord,
			ArrayList<TreeNode> F1) {
		Map<String, Integer> map = new HashMap<String, Integer>();
		for (String item : transRecord) {
			// ����F1�Ѿ��ǰ��������еģ�
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
				// ��������
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
	 * 3.2 �����ɸ��ڵ���Ϊָ���ڵ�ĺ����������
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

	/**
	 * 4. ��FPTree���ҵ����е�Ƶ��ģʽ
	 * 
	 * @param root
	 * @param F1
	 * @return
	 */
	public Map<List<String>, Integer> findFP(TreeNode root,
			ArrayList<TreeNode> F1) {
		Map<List<String>, Integer> fp = new HashMap<List<String>, Integer>();

		Iterator<TreeNode> iter = F1.iterator();
		while (iter.hasNext()) {
			TreeNode curr = iter.next();
			// Ѱ��cur������ģʽ��CPB������transRecords��
			List<List<String>> transRecords = new LinkedList<List<String>>();
			TreeNode backnode = curr.getNextHomonym();
			while (backnode != null) {
				int counter = backnode.getCount();
				List<String> prenodes = new ArrayList<String>();
				TreeNode parent = backnode;
				// ����backnode�����Ƚڵ㣬�ŵ�prenodes��
				while ((parent = parent.getParent()).getName() != null) {
					prenodes.add(parent.getName());
				}
				while (counter-- > 0) {
					transRecords.add(prenodes);
				}
				backnode = backnode.getNextHomonym();
			}

			// ��������Ƶ��1�
			ArrayList<TreeNode> subF1 = buildF1Items(transRecords);
			// ��������ģʽ���ľֲ�FP-tree
			TreeNode subRoot = buildFPTree(transRecords, subF1);

			// ������FP-Tree��Ѱ��Ƶ��ģʽ
			if (subRoot != null) {
				Map<List<String>, Integer> prePatterns = findPrePattern(subRoot);
				if (prePatterns != null) {
					Set<Entry<List<String>, Integer>> ss = prePatterns
							.entrySet();
					for (Entry<List<String>, Integer> entry : ss) {
						entry.getKey().add(curr.getName());
						fp.put(entry.getKey(), entry.getValue());
					}
				}
			}
		}

		return fp;
	}

	/**
	 * 4.1 ��һ��FP-Tree���ҵ����е�ǰ׺ģʽ
	 * 
	 * @param root
	 * @return
	 */
	public Map<List<String>, Integer> findPrePattern(TreeNode root) {
		Map<List<String>, Integer> patterns = null;
		List<TreeNode> children = root.getChildren();
		if (children != null) {
			patterns = new HashMap<List<String>, Integer>();
			for (TreeNode child : children) {
				// �ҵ���childΪ���ڵ�������е����г�·������ν��·��ָ�����������κ�·������·����
				LinkedList<LinkedList<TreeNode>> paths = buildPaths(child);
				if (paths != null) {
					for (List<TreeNode> path : paths) {
						Map<List<String>, Integer> backPatterns = combination(path);
						Set<Entry<List<String>, Integer>> entryset = backPatterns
								.entrySet();
						for (Entry<List<String>, Integer> entry : entryset) {
							List<String> key = entry.getKey();
							int c1 = entry.getValue();
							int c0 = 0;
							if (patterns.containsKey(key)) {
								c0 = patterns.get(key).byteValue();
							}
							patterns.put(key, c0 + c1);
						}
					}
				}
			}
		}

		// ���˵���ЩС��MinSup��ģʽ
		Map<List<String>, Integer> rect = null;
		if (patterns != null) {
			rect = new HashMap<List<String>, Integer>();
			Set<Entry<List<String>, Integer>> ss = patterns.entrySet();
			for (Entry<List<String>, Integer> entry : ss) {
				if (entry.getValue() >= minSup) {
					rect.put(entry.getKey(), entry.getValue());
				}
			}
		}
		return rect;
	}

	/**
	 * 4.1.1 �ҵ���ָ���ڵ㣨root�������пɴ�Ҷ�ӽڵ��·��
	 * 
	 * @param stack
	 * @param root
	 */
	public LinkedList<LinkedList<TreeNode>> buildPaths(TreeNode root) {
		LinkedList<LinkedList<TreeNode>> paths = null;
		if (root != null) {
			paths = new LinkedList<LinkedList<TreeNode>>();
			List<TreeNode> children = root.getChildren();
			if (children != null) {
				// �ڴ����Ϸ��뵥��·��ʱ���Էֲ�ڵĽڵ㣬��countҲҪ�ֵ�����·����ȥ
				// ����FP-Tree�Ƕ�֦�����
				if (children.size() > 1) {
					for (TreeNode child : children) {
						int count = child.getCount();
						LinkedList<LinkedList<TreeNode>> ll = buildPaths(child);
						for (LinkedList<TreeNode> lp : ll) {
							TreeNode prenode = new TreeNode(root.getName());
							prenode.setCount(count);
							lp.addFirst(prenode);
							paths.add(lp);
						}
					}
				}
				// ����FP-Tree�ǵ�֦�����
				else {
					for (TreeNode child : children) {
						LinkedList<LinkedList<TreeNode>> ll = buildPaths(child);
						for (LinkedList<TreeNode> lp : ll) {
							lp.addFirst(root);
							paths.add(lp);
						}
					}
				}
			} else {
				LinkedList<TreeNode> lp = new LinkedList<TreeNode>();
				lp.add(root);
				paths.add(lp);
			}
		}
		return paths;
	}

	/**
	 * 4.1.2
	 * ����·��path������Ԫ�ص�������ϣ�������ÿһ����ϵ�count--��ʵ������������һ��Ԫ�ص�count����Ϊ���ǵ�����㷨��֤������
	 * ����path��)�������Ԫ�س��ֵ����˳�򲻱�
	 * 
	 * @param path
	 * @return
	 */
	public Map<List<String>, Integer> combination(List<TreeNode> path) {
		if (path.size() > 0) {
			// ��path���Ƴ��׽ڵ�
			TreeNode start = path.remove(0);
			// �׽ڵ��Լ����Գ�Ϊһ����ϣ�����rect��
			Map<List<String>, Integer> rect = new HashMap<List<String>, Integer>();
			List<String> li = new ArrayList<String>();
			li.add(start.getName());
			rect.put(li, start.getCount());

			Map<List<String>, Integer> postCombination = combination(path);
			if (postCombination != null) {
				Set<Entry<List<String>, Integer>> set = postCombination
						.entrySet();
				for (Entry<List<String>, Integer> entry : set) {
					// ���׽ڵ�֮��Ԫ�ص�������Ϸ���rect��
					rect.put(entry.getKey(), entry.getValue());
					// �׽ڵ㲢�����Ԫ�صĸ�����Ϸ���rect��
					List<String> ll = new ArrayList<String>();
					ll.addAll(entry.getKey());
					ll.add(start.getName());
					rect.put(ll, entry.getValue());
				}
			}

			return rect;
		} else {
			return null;
		}
	}

	/**
	 * ���Ƶ��1�
	 * 
	 * @param F1
	 */
	public void printF1(List<TreeNode> F1) {
		System.out.println("F-1 set: ");
		for (TreeNode item : F1) {
			System.out.print(item.getName() + ":" + item.getCount() + "\t");
		}
		System.out.println();
		System.out.println();
	}

	/**
	 * ��ӡFP-Tree
	 * 
	 * @param root
	 */
	public void printFPTree(TreeNode root) {
		printNode(root);
		List<TreeNode> children = root.getChildren();
		if (children != null && children.size() > 0) {
			for (TreeNode child : children) {
				printFPTree(child);
			}
		}
	}

	/**
	 * ��ӡ���ϵ����ڵ����Ϣ
	 * 
	 * @param node
	 */
	public void printNode(TreeNode node) {
		if (node.getName() != null) {
			System.out.print("Name:" + node.getName() + "\tCount:"
					+ node.getCount() + "\tParent:"
					+ node.getParent().getName());
			if (node.getNextHomonym() != null)
				System.out.print("\tNextHomonym:"
						+ node.getNextHomonym().getName());
			System.out.print("\tChildren:");
			node.printChildrenName();
			System.out.println();
		} else {
			System.out.println("FPTreeRoot");
		}
	}

	/**
	 * ��ӡ�����ҵ�������Ƶ��ģʽ��
	 * 
	 * @param patterns
	 * @param transFile
	 * @param f1
	 * @throws IOException
	 */
	@SuppressWarnings("resource")
	public void printFreqPatterns(Map<List<String>, Integer> patterns,
			String transFile, ArrayList<TreeNode> f1) throws IOException {
		System.out.println();
		System.out.println("MinSupport=" + this.getMinSup());
		System.out.println("Total number of Frequent Patterns is :"
				+ patterns.size());
		System.out
				.println("Frequent Patterns and their Support are written to file");
		String shortFileName = transFile.split("/")[6];
		FileWriter FPResFile = new FileWriter(
				new File(
						"C:/Users/Administrator/Desktop/"
								+ shortFileName.substring(0,
										shortFileName.indexOf("."))
								+ "_fp_minSup" + this.getMinSup() + "_size"
								+ patterns.size() + ".txt"));
		FPResFile.append("MinSupport=" + this.getMinSup() + "\r\n");
		int total = patterns.size() + f1.size();
		FPResFile.append("Total number of Frequent Patterns is :" + total
				+ "\r\n");
		FPResFile.append("Frequent Patterns and their Support\r\n");
		// �����Ƶ��һ�����֧�ֶ�
		FPResFile.append("=======������ 1==========\r\n");
		for (TreeNode tn : f1) {
			FPResFile.append(tn.getCount() + "\t" + tn.getName() + "\r\n");
		}
		Set<Entry<List<String>, Integer>> ss = patterns.entrySet();
		for (Entry<List<String>, Integer> entry : ss) {
			List<String> list = entry.getKey();
			FPResFile.append(entry.getValue() + "\t");
			for (String item : list) {
				FPResFile.append(item + " ");
			}
			FPResFile.append("\r\n");
			FPResFile.flush();
		}
	}

}
