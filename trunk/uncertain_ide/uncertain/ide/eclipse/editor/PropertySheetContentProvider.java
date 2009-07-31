/*
 * Created on 2009-7-3
 */
package uncertain.ide.eclipse.editor;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import uncertain.composite.CompositeMap;
import uncertain.schema.ISchemaManager;
import uncertain.schema.editor.AttributeValue;
import uncertain.schema.editor.CompositeMapEditor;

/**
 * For edit CompositeMap in property sheet view
 * ElementContentProvider
 * @author Zhou Fan
 *
 */
public class PropertySheetContentProvider implements IStructuredContentProvider {
    
    /** @todo add attributes cache
    // Map<QName, Attribute[]>

    /**
     * @param schemaManager
     */
    public PropertySheetContentProvider(ISchemaManager schemaManager) {
        super();
        mSchemaManager = schemaManager;
    }

    ISchemaManager      mSchemaManager;
    
    public Object[] getElements(Object inputElement) {
        CompositeMap    map = (CompositeMap)inputElement;
        CompositeMapEditor editor = new CompositeMapEditor(mSchemaManager, map);
        AttributeValue[] avs =  editor.getAttributeList();
        return avs;
    }

    public void dispose() {

    }

    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

    }

}
