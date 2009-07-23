/*
 * Created on 2009-7-3
 */
package uncertain.ide.eclipse.editor;

import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import uncertain.composite.CompositeMap;

public class CompositeTreeContentProvider implements ITreeContentProvider {

    public Object[] getChildren(Object parentElement) {
        if(parentElement==null) return null;
        CompositeMap map = (CompositeMap)parentElement;
        List childs = map.getChilds();
        if(childs==null)
            return null;
        else
            return childs.toArray();
    }

    public Object getParent(Object element) {
        if( element==null) return null;
        CompositeMap map = (CompositeMap)element;
        return map.getParent();
    }

    public boolean hasChildren(Object element) {
        if( element==null) return false;
        CompositeMap map = (CompositeMap)element;
        List childs = map.getChilds();
        return childs != null;
    }

    public Object[] getElements(Object inputElement) {
        if( inputElement==null) return null;
        CompositeMap map = (CompositeMap)inputElement;
        List childs = map.getChilds();
        if(childs==null)
            return null;
        else
            return childs.toArray();
    }

    public void dispose() {
        
    }

    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        
    }

}
