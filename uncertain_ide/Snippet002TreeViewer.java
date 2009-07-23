

import java.util.ArrayList;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import uncertain.composite.CompositeMap;
import uncertain.ide.eclipse.editor.CompositeMapLabelProvider;
import uncertain.ide.eclipse.editor.CompositeTreeContentProvider;

/**
 * A simple TreeViewer to demonstrate usage
 * 
 * @author Tom Schindl <tom.schindl@bestsolution.at>
 *
 */
public class Snippet002TreeViewer {
   

    
    public Snippet002TreeViewer(Shell shell) {
        final TreeViewer v = new TreeViewer(shell);
        v.setLabelProvider(new CompositeMapLabelProvider());
        v.setContentProvider(new CompositeTreeContentProvider());
        v.setInput(createMap());
    }
    
    public CompositeMap createMap(){
        CompositeMap m = new CompositeMap("el", "http://www.uncertain.org/schema", "element");
        CompositeMap attribs = m.createChild("attributes");
        attribs.addChild( new CompositeMap("attribute"));
        return m;
    }

    
    public static void main(String[] args) {
        Display display = new Display ();
        Shell shell = new Shell(display);
        shell.setLayout(new FillLayout());
        new Snippet002TreeViewer(shell);
        shell.open ();
        
        while (!shell.isDisposed ()) {
            if (!display.readAndDispatch ()) display.sleep ();
        }
        
        display.dispose ();
    }
}
