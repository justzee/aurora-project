package aurora.ide.meta.gef.editors.models;

import org.eclipse.draw2d.geometry.Dimension;

public class Navbar extends Container {

	static final long serialVersionUID = 1;
	
	public static final String COMPLEX = "complex";
	

	public static final String SIMPLE = "simple";
	
	
	public Navbar() {
		this.setSize(new Dimension(1, 25));
	}

	@Override
	public String getType() {
		
		return super.getType();
	}

	@Override
	//  navBarType = "complex" 或者 "simple";
	public void setType(String type) {
		super.setType(type);
	}

	/**
	 * 
	 * 不允许增加child，外观使用图片显示
	 * 
	 * */
	public boolean isResponsibleChild(AuroraComponent component) {
		return false;
	}

}
