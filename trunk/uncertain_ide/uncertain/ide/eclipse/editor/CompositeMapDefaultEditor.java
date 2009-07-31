/*
 * Created on 2009-7-30
 */
package uncertain.ide.eclipse.editor;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.part.MultiPageEditorPart;

public class CompositeMapDefaultEditor extends MultiPageEditorPart implements
        IResourceChangeListener {
    
    protected   TextEditor  mTextEditor;

    public CompositeMapDefaultEditor() {
            super();
            ResourcesPlugin.getWorkspace().addResourceChangeListener(this);
    }
    
    protected void createTextEditorPage()        
    {
        try{
            mTextEditor = new TextEditor();
            int index = addPage(mTextEditor, getEditorInput());
        } catch (PartInitException e) {
            ErrorDialog.openError(
                    getSite().getShell(),
                    "Error creating nested text editor",
                    null,
                    e.getStatus());
            }
    }

    protected void createPages() {
        // TODO Auto-generated method stub

    }

    public void doSave(IProgressMonitor monitor) {
        // TODO Auto-generated method stub

    }

    public void doSaveAs() {
        // TODO Auto-generated method stub

    }

    public boolean isSaveAsAllowed() {
        // TODO Auto-generated method stub
        return false;
    }

    public void resourceChanged(IResourceChangeEvent event) {
        // TODO Auto-generated method stub

    }   
    
    public void dispose() {
        ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
        super.dispose();
    }
    
    

}
