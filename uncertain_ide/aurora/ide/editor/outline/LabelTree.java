package aurora.ide.editor.outline;

import java.util.ArrayList;
import java.util.List;

public class LabelTree<T> {
	private LabelTree<T> parent;
	private String id = "0";
	private T data;
	private List<LabelTree<T>> children = new ArrayList<LabelTree<T>>();

	public LabelTree(T data) {
		this.data = data;
	}

	public int getChildrenCount() {
		return children.size();
	}

	public LabelTree<T> getParent() {
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

	public List<LabelTree<T>> getChildren() {
		return children;
	}

	public void add(T data) {
		LabelTree<T> lt = new LabelTree<T>(data);
		add(lt);
	}

	public void add(int index, T data) {
		LabelTree<T> lt = new LabelTree<T>(data);
		add(index, lt);
	}

	public void add(LabelTree<T> lt) {
		lt.id = this.id + "|" + children.size();
		lt.parent = this;
		children.add(lt);
	}

	public void add(int index, LabelTree<T> lt) {
		lt.id = this.id + "|" + index;
		lt.parent = this;
		children.add(index, lt);
		for (int i = index; i < children.size(); i++) {
			children.get(i).id = this.id + "|" + i;
		}
	}

	public void removeAll() {
		children = new ArrayList<LabelTree<T>>();
	}

	public LabelTree<T> remove(LabelTree<T> lt) {
		LabelTree<T> t = null;
		for (int i = 0; i < children.size(); i++) {
			if (lt.id.equals(children.get(i).id) && lt.data.equals(children.get(i).data)) {
				t = remove(i);
			}
		}
		return t;
	}

	public LabelTree<T> remove(int index) {
		LabelTree<T> t = null;
		if (index < children.size() && index >= 0) {
			t = children.remove(index);
			for (int j = index + 1; j <= children.size(); j++) {
				children.get(j - 1).id = this.id + "|" + (j - 1);
			}
		}
		return t;
	}

	public LabelTree<T> getChild(int index) {
		if (index < 0 || index >= children.size()) {
			return null;
		}
		return children.get(index);
	}

	@SuppressWarnings("unchecked")
	public boolean equals(Object t) {
		if (!(t instanceof LabelTree)) {
			return false;
		} else if (((LabelTree<T>) t).id.equals(id)) {
			return true;
		}
		return false;
	}

	public String toString() {
		return data.toString();
	}
}
