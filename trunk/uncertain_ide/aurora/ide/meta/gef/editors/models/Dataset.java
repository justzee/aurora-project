package aurora.ide.meta.gef.editors.models;

import java.io.IOException;
import java.io.ObjectInputStream;

public class Dataset extends AuroraComponent {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4619018857153616914L;
	// model a.b.c形式
	private String model;

	private boolean autoQuery;
	// private String queryUrl;
	// 自动生成
	private String id;
	// 是否只是QueryDS,如果true，生成的代码将不设置model
	private boolean isUse4Query;
	// 是否使用父的BM
	private boolean isUseParentBM = true;

	public Dataset() {
		// 暂时不显示
		// this.setSize(new Dimension(50, 20));
		this.setType("dataSet");
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public boolean isAutoQuery() {
		return autoQuery;
	}

	public void setAutoQuery(boolean autoQuery) {
		this.autoQuery = autoQuery;
	}

	private void readObject(ObjectInputStream in) throws IOException,
			ClassNotFoundException {
		in.defaultReadObject();

	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public boolean isUse4Query() {
		return isUse4Query;
	}

	public void setUse4Query(boolean isUse4Query) {
		this.isUse4Query = isUse4Query;
	}

	public boolean isUseParentBM() {
		return isUseParentBM;
	}

	public void setUseParentBM(boolean isUseParentBM) {
		this.isUseParentBM = isUseParentBM;
	}

}
