package aurora.ide.meta.gef.editors.models;

import java.io.IOException;
import java.io.ObjectInputStream;

import org.eclipse.ui.views.properties.IPropertyDescriptor;

import aurora.ide.meta.gef.editors.property.BooleanPropertyDescriptor;
import aurora.ide.meta.gef.editors.property.StringPropertyDescriptor;

public class Dataset extends AuroraComponent {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4619018857153616914L;
	// model a.b.c形式
	private String model="";

	private boolean autoQuery;
	// private String queryUrl;
	// 自动生成
	private String id="";
	// 是否只是QueryDS,如果true，生成的代码将不设置model
	private boolean isUse4Query;
	// 是否使用父的BM
	private boolean isUseParentBM = true;
	
	
	public static final String AUTO_QUERY ="autoQuery";
	public static final String MODEL ="model";
	public static final String ID ="id";
	
	

	private static final IPropertyDescriptor[] pds = new IPropertyDescriptor[] {
			new StringPropertyDescriptor(AUTO_QUERY, "autoQuery"),
			new StringPropertyDescriptor(MODEL, "model"),
			new StringPropertyDescriptor(ID, "id")};

	public Dataset() {
		// 暂时不显示
		// this.setSize(new Dimension(50, 20));
		this.setType("dataSet");
	}

	@Override
	public IPropertyDescriptor[] getPropertyDescriptors() {
		
		return pds;
	}

	@Override
	public Object getPropertyValue(Object propName) {
		if(AUTO_QUERY.equals(propName)){
			return this.isAutoQuery();
		}
		if(MODEL.equals(propName)){
			return this.getModelPKG();
		}
		if(ID.equals(propName)){
			return this.getId();
		}
		return null;
	}

	private Object getModelPKG() {
		return "a.b.c";
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
