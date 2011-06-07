package editor;

import helpers.AuroraConstant;
import uncertain.composite.CompositeMap;

public class ScreenUtil {

	
	public static CompositeMap createScreenTopNode(){
		CompositeMap model = new CompositeMap("a",AuroraConstant.ScreenQN.getNameSpace(),AuroraConstant.ScreenQN.getLocalName());
		return model;
	}
}
