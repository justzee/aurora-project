package aurora.ide.meta.gef.editors.models;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;

public class Dataset extends AuroraComponent {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4619018857153616914L;
	// model
	private String bmPath;
	private List<AuroraComponent> binds = new ArrayList<AuroraComponent>();
	private QueryDataSet queryDataSet;
	private boolean autoQuery;
	private int pageSize;
	private boolean selectable;
	private String queryUrl;

	public void addBind(AuroraComponent c) {
		if (!binds.contains(c)) {
			binds.add(c);
			c.setBindTarget(this);
		}
	}


	public String getBmPath() {
		return bmPath;
	}


	public void setBmPath(String bmPath) {
		this.bmPath = bmPath;
	}


	public List<AuroraComponent> getBinds() {
		return binds;
	}

	public void setBinds(List<AuroraComponent> binds) {
		this.binds = binds;
	}

	public QueryDataSet getQueryDataSet() {
		return queryDataSet;
	}

	public void setQueryDataSet(QueryDataSet queryDataSet) {
		this.queryDataSet = queryDataSet;
	}

	public boolean isAutoQuery() {
		return autoQuery;
	}

	public void setAutoQuery(boolean autoQuery) {
		this.autoQuery = autoQuery;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public boolean isSelectable() {
		return selectable;
	}

	public void setSelectable(boolean selectable) {
		this.selectable = selectable;
	}

	public String getQueryUrl() {
		return queryUrl;
	}

	public void setQueryUrl(String queryUrl) {
		this.queryUrl = queryUrl;
	}

	private void readObject(ObjectInputStream in) throws IOException,
			ClassNotFoundException {
		in.defaultReadObject();

	}

	public void removeBind(AuroraComponent auroraComponent) {
		binds.remove(auroraComponent);
	}

}
