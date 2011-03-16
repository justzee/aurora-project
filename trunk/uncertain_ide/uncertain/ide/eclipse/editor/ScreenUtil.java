package uncertain.ide.eclipse.editor;

import uncertain.composite.CompositeMap;
import aurora.ide.AuroraConstant;

public class ScreenUtil {

	
	public static CompositeMap createScreenTopNode(){
		CompositeMap model = new CompositeMap("a",AuroraConstant.ScreenQN.getNameSpace(),AuroraConstant.ScreenQN.getLocalName());
		return model;
	}
}
