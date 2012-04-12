package aurora.ide.editor.outline;

import java.util.ArrayList;
import java.util.List;

public class OutlineTree<T> {
	private OutlineTree<T> parent;
	private String id = "0";
	private T data;
	private List<OutlineTree<T>> children = new ArrayList<OutlineTree<T>>();

	public OutlineTree(T data) {
		this.data = data;
	}

	public int getChildrenCount() {
		return children.size();
	}

	public OutlineTree<T> getParent() {
		return parent;
	}

	public String getId() {
		return id;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public List<OutlineTree<T>> getChildren() {
		return children;
	}

	public void add(T data) {
		OutlineTree<T> lt = new OutlineTree<T>(data);
		add(lt);
	}

	public void add(int index, T data) {
		OutlineTree<T> lt = new OutlineTree<T>(data);
		add(index, lt);
	}

	public void add(OutlineTree<T> lt) {
		lt.id = this.id + "|" + children.size();
		lt.parent = this;
		children.add(lt);
	}

	public void add(int index, OutlineTree<T> lt) {
		lt.id = this.id + "|" + index;
		lt.parent = this;
		children.add(index, lt);
		for (int i = index; i < children.size(); i++) {
			children.get(i).id = this.id + "|" + i;
		}
	}

	public void removeAll() {
		children = new ArrayList<OutlineTree<T>>();
	}

	public OutlineTree<T> remove(OutlineTree<T> lt) {
		OutlineTree<T> t = null;
		for (int i = 0; i < children.size(); i++) {
			if (lt.id.equals(children.get(i).id) && lt.data.equals(children.get(i).data)) {
				t = remove(i);
			}
		}
		return t;
	}

	public OutlineTree<T> remove(int index) {
		OutlineTree<T> t = null;
		if (index < children.size() && index >= 0) {
			t = children.remove(index);
			for (int j = index + 1; j <= children.size(); j++) {
				children.get(j - 1).id = this.id + "|" + (j - 1);
			}
		}
		return t;
	}

	public OutlineTree<T> getChild(int index) {
		if (index < 0 || index >= children.size()) {
			return null;
		}
		return children.get(index);
	}

	@SuppressWarnings("unchecked")
	public boolean equals(Object t) {
		if (!(t instanceof OutlineTree)) {
			return false;
		} else if (((OutlineTree<T>) t).id.equals(id)) {
			return true;
		}
		return false;
	}

	public OutlineTree<T> findChild(String id) {
		if (!id.matches("\\d+(\\|\\d+)*")) {
			return null;
		}
		String[] layout = id.split("\\|");
		OutlineTree<T> root = getRoot();
		for (int i=1;i<layout.length;i++) {
			String s=layout[i];
			if (!"".equals(s.trim())) {
				Integer n = new Integer(s);
				root = root.getChild(n);
			}
		}
		if (null == root) {
			return null;
		} else {
			return root;
		}
	}

	public OutlineTree<T> getRoot() {
		return getRoot(this);
	}

	private OutlineTree<T> getRoot(OutlineTree<T> lt) {
		if (null == lt) {
			return null;
		} else if (null == lt.getParent()) {
			return lt;
		}
		return getRoot(lt.getParent());
	}

	public String toString() {
		return data.toString();
	}
}
