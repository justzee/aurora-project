/*
 * Created on 2009-7-3
 */
package uncertain.ide.eclipse.editor;

import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.graphics.Image;

import uncertain.composite.CompositeMap;
import uncertain.schema.ComplexType;
import uncertain.schema.Element;
import uncertain.schema.ISchemaManager;

public class CompositeMapLabelProvider extends BaseLabelProvider implements ILabelProvider {
    
    /**
     * @param schemaManager
     */
    public CompositeMapLabelProvider(ISchemaManager schemaManager) {
        super();
        mSchemaManager = schemaManager;
    }

    ISchemaManager      mSchemaManager;
    


    public Image getImage(Object element){
        return null;
    }

    /**
     * Returns the text for the label of the given element.
     *
     * @param obj the element for which to provide the label text
     * @return the text string used to label the element, or <code>null</code>
     *   if there is no text label for the given object
     */
    public String getText(Object obj){
        CompositeMap map = (CompositeMap)obj;
        String raw_name = map.getRawName();
        if(mSchemaManager!=null){
            Element em = mSchemaManager.getElement(map.getQName());
            if(em!=null){
                if(em.isArray())
                    return "[]" + raw_name;
            }
        }
        return raw_name;
    }

    public ISchemaManager getSchemaManager() {
        return mSchemaManager;
    }

    public void setSchemaManager(ISchemaManager schemaManager) {
        mSchemaManager = schemaManager;
    }
}
