/**
 * 
 */
package uncertain.ide.eclipse.action;

import uncertain.composite.CompositeMap;
import uncertain.schema.editor.AttributeValue;

public interface IPropertyCategory extends IViewer{
	public  CompositeMap getInput();
	public  AttributeValue getFocusData();
	public boolean IsCategory();
	public void setIsCategory(boolean isCategory);
}
