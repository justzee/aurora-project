package aurora.ide.meta.gef.editors.models;

import org.eclipse.draw2d.geometry.Dimension;

public class Input extends AuroraComponent {

	public static final String TEXT = "text";
	public static final String Combo = "combo";
	public static final String LOV = "lov";
	public static final String CAL = "cal";

	public Input() {
		this.setSize(new Dimension(120, 20));
		this.setType(TEXT);
	}

}
