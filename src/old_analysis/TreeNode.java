package old_analysis;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import training.COMMON_PATH;

public class TreeNode implements Comparable<TreeNode> {

	private String name; // 节点名称
	private int count; // 计数
	private TreeNode parent; // 父节点
	private List<TreeNode> children; // 子节点
	private TreeNode nextHomonym; // 下一个同名节点

	public TreeNode(String name, List<TreeNode> children) {
		this.name = name;
		this.children = children;
	}

	public void print() {
		// for (int i = 0; i < children.size(); i++) {
		print("", true);
		// }
	}

	private void print(String prefix, boolean isTail) {
		System.out.println(prefix + (isTail ? "└── " : "├── ") + name + " :"
				+ count);
		FileWriter FPResFile;
		try {
			
			FPResFile = new FileWriter(new File(COMMON_PATH.FPTREE_PATH
					),true);
			FPResFile.append(prefix + (isTail ? "└── " : "├── ") + name + " :"
					+ count + "\r\n");
			FPResFile.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (children == null)
			return;
		for (int i = 0; i < children.size() - 1; i++) {
			children.get(i).print(prefix + (isTail ? "    " : "│   "), false);
		}
		if (children.size() > 0) {
			children.get(children.size() - 1).print(
					prefix + (isTail ? "    " : "│   "), true);
		}
	}

	public TreeNode() {

	}

	public TreeNode(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public TreeNode getParent() {
		return parent;
	}

	public void setParent(TreeNode parent) {
		this.parent = parent;
	}

	public List<TreeNode> getChildren() {
		return children;
	}

	public void addChild(TreeNode child) {
		if (this.getChildren() == null) {
			List<TreeNode> list = new ArrayList<TreeNode>();
			list.add(child);
			this.setChildren(list);
		} else {
			this.getChildren().add(child);
		}
	}

	public TreeNode findChild(String name) {
		List<TreeNode> children = this.getChildren();
		if (children != null) {
			for (TreeNode child : children) {
				if (child.getName().equals(name)) {
					return child;
				}
			}
		}
		return null;
	}

	public void setChildren(List<TreeNode> children) {
		this.children = children;
	}

	public void printChildrenName() {
		List<TreeNode> children = this.getChildren();
		if (children != null) {
			for (TreeNode child : children) {
				System.out.print(child.getName() + " ");
			}
		} else {
			System.out.print("null");
		}
	}

	public TreeNode getNextHomonym() {
		return nextHomonym;
	}

	public void setNextHomonym(TreeNode nextHomonym) {
		this.nextHomonym = nextHomonym;
	}

	public void countIncrement(int n) {
		this.count += n;
	}

	@Override
	public int compareTo(TreeNode arg0) {
		// TODO Auto-generated method stub
		int count0 = arg0.getCount();
		// 跟默认的比较大小相反，导致调用Arrays.sort()时是按降序排列
		return count0 - this.count;
	}
}
