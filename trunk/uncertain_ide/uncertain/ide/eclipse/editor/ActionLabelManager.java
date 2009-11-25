/**
 * 
 */
package uncertain.ide.eclipse.editor;

import java.util.HashMap;

import org.eclipse.jface.resource.ImageDescriptor;

import uncertain.ide.Activator;


/**
 * @author linjinxiao
 *
 */
public class ActionLabelManager {

	
	public static final String ELEMENT = "ELEMENT";
	public static final String ADD = "ADD";
	public static final String CATEGORY = "CATEGORY";
	public static final String ASC = "ASC";
	public static final String COPY = "COPY";
	public static final String DELETE = "DELETE";
	public static final String REFRESH = "REFRESH";
	public static final String PASTE = "PASTE";
	
	


	private static HashMap Icons = new HashMap();
	private static HashMap Texts = new HashMap();
	
	private static ActionLabelManager actionLabelManager;
	/**
	 * 
	 */
	private ActionLabelManager() {
		initIcons();
		initTexts();
	}
	private static void initIcons(){
		setIconPath(ELEMENT, "icons/element_obj.gif");
		setIconPath(ADD, "icons/add_obj.gif");
		setIconPath(CATEGORY, "icons/category.gif");
		setIconPath(ASC, "icons/asc.gif");
		setIconPath(COPY, "icons/copy.gif");
		setIconPath(DELETE, "icons/delete_obj.gif");
		setIconPath(REFRESH, "icons/refresh.gif");
		setIconPath(PASTE, "icons/paste.gif");
		
	}
	private static void initTexts(){
		setText(ELEMENT, "ÔªËØ");
		setText(COPY, "¸´ÖÆ");
		setText(PASTE, "Õ³Ìù");
		setText(DELETE, "É¾³ý");
		setText(REFRESH, "Ë¢ÐÂ");
		setText(CATEGORY, "·Ö×é");
		setText(ASC, "ÉýÐò");
	}

	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	public static void setIconPath(String iconId,String path){
		Icons.put(iconId, path);
	}
	public static void setText(String textId,String textDesc){
		Texts.put(textId, textDesc);
	}
	
	public static ImageDescriptor getImageDescriptor(String iconId){
		if(Icons.isEmpty())
			initIcons();
		return Activator.getImageDescriptor(Icons.get(iconId).toString());
	}
	public static String getText(String textId){
		if(Texts.isEmpty())
			initTexts();
		return Texts.get(textId).toString();
	}
	public static ActionLabelManager getInstance(){
		if(actionLabelManager == null){
			actionLabelManager = new ActionLabelManager();
		}
		return actionLabelManager;
	}
	

}
