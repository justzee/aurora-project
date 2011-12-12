package aurora.ide.meta.gef.editors.figures;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

public class ColorConstants {
	private static Display dis = Display.getDefault();
	public static final Color WHITE = new Color(dis, 255, 255, 255);
	public static final Color BLACK = new Color(dis, 0, 0, 0);
	public static final Color BORDER = new Color(dis, 186, 186, 186);
	public static final Color VBORDER = new Color(dis, 0, 0, 200);
	public static final Color READONLY = new Color(dis, 219, 219, 219);
	public static final Color REQUIRED = new Color(dis, 255, 249, 194);
	public static final Color TITLETEXT = new Color(dis, 5, 90, 120);

}
