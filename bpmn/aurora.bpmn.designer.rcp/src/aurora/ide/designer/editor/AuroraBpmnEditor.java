package aurora.ide.designer.editor;

import org.eclipse.bpmn2.modeler.ui.editor.BPMN2MultiPageEditor;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.resource.Resource;


public class AuroraBpmnEditor extends BPMN2MultiPageEditor{

	public static final String ID = "aurora.ide.workflow.bpmn.graphiti.ui.editor.AuroraBpmnEditor";
	
	
	public AuroraBpmnEditor(){
		
	}
//private class TM extends MultiPageEditorPart{
//	
//}


	@Override
	public void doSave(IProgressMonitor monitor) {
		super.doSave(monitor);
		Resource resource = this.getDesignEditor().getResource();
		resource.getURI();
		System.out.println(resource);
		//update to db
	}


	@Override
	public void doSaveAs() {
		super.doSaveAs();
	}
}
