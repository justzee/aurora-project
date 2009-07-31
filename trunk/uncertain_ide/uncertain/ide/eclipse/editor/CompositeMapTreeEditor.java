/*
 * Created on 2009-7-23
 */
package uncertain.ide.eclipse.editor;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import uncertain.composite.CompositeLoader;
import uncertain.composite.CompositeMap;
import uncertain.ide.AbstractCompositeMapEditor;
import uncertain.schema.ISchemaManager;
import uncertain.schema.SchemaManager;

public class CompositeMapTreeEditor extends AbstractCompositeMapEditor {
    
    /**
     * @param schemaManager
     */
    public CompositeMapTreeEditor(ISchemaManager schemaManager) {
        super();
        mSchemaManager = schemaManager;
        mPropertySheetEditor = new PropertySheetEditor(mSchemaManager);
    }

    Composite               mParent;
    Composite               mContent;
    TreeViewer              mTreeViewer;
    PropertySheetEditor     mPropertySheetEditor;
    ISchemaManager          mSchemaManager;
    CompositeMap            mCurrentSelection;
    
    public class ElementSelectionListener implements ISelectionChangedListener {

        public void selectionChanged(SelectionChangedEvent event) {
            TreeSelection selection = (TreeSelection)event.getSelection();
            CompositeMap data = (CompositeMap)selection.getFirstElement();
            changeSelection(data);
        }

    }

    
    public void changeSelection( CompositeMap newSelection ){
        mCurrentSelection = newSelection;
        mPropertySheetEditor.setData(mCurrentSelection);
    }
    
    protected void createTreeEditor( ){
        mTreeViewer = new TreeViewer(mContent);
        mTreeViewer.setLabelProvider(new CompositeMapLabelProvider());
        mTreeViewer.setContentProvider(new CompositeTreeContentProvider());
        mTreeViewer.addSelectionChangedListener( new ElementSelectionListener() );
        mTreeViewer.getTree().setSize(200, 400);
    }
    
    public void createControls( Composite parent ){        
        mParent = parent;
        mContent = new Composite( mParent, SWT.NONE );
        mContent.setLayout( new FillLayout() );        
        createTreeEditor();
        mPropertySheetEditor.createEditor(mContent);
    }

    public void setData(CompositeMap data) {
        super.setData(data);
        mTreeViewer.setInput(data);
    }
    
    


    public static void main(String[] args) 
        throws Exception
    {
        Display display = new Display ();
        Shell shell = new Shell(display);
        shell.setLayout(new FillLayout());
        
        SchemaManager sm = new SchemaManager();
        sm.loadSchemaByClassPath("aurora.testcase.ui.config.components", "sxsd");
        
        CompositeLoader loader = new CompositeLoader();
        CompositeMap data = loader.loadFromClassPath("uncertain.testcase.schema.screen_test");

        CompositeMapTreeEditor editor = new CompositeMapTreeEditor(sm);
        editor.createControls(shell);
        editor.setData(data);
        
        shell.open ();
        
        while (!shell.isDisposed ()) {
            if (!display.readAndDispatch ()) display.sleep ();
        }
        
        display.dispose ();
    }    

}
