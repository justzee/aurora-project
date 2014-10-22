package aurora.ide.designer.diagram;

import org.eclipse.graphiti.tb.IToolBehaviorProvider;

public class AuroraBpmn2DiagramTypeProvider extends
		org.eclipse.bpmn2.modeler.ui.diagram.Bpmn2DiagramTypeProvider {
	public AuroraBpmn2DiagramTypeProvider() {
		super();
	}

	public IToolBehaviorProvider[] getAvailableToolBehaviorProviders() {
		return new IToolBehaviorProvider[] { new AuroraBpmn2ToolBehaviorProvider(
				this) };
	}

}
