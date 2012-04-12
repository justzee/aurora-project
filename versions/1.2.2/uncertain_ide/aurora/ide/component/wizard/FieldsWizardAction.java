package aurora.ide.component.wizard;

import aurora.ide.editor.core.IViewer;
import aurora.ide.helpers.AuroraConstant;
import aurora.ide.node.action.ActionInfo;
import aurora.ide.node.action.ActionListener;
import uncertain.composite.CompositeMap;
import uncertain.composite.QualifiedName;

public class FieldsWizardAction implements IWizardAction{

	public ActionListener[] createActions(ActionInfo actionInfo) {
		ActionListener[] actions = new ActionListener[2];
		IViewer viewer = actionInfo.getViewer();
		CompositeMap currentNode = actionInfo.getCurrentNode();
		QualifiedName  childQN = new QualifiedName(AuroraConstant.ApplicationUri,"field");
		
		actions[0] =new CreateLovFromBMAction(viewer, currentNode,childQN,ActionListener.DefaultImage);
		actions[0].setText("创建LOV");
		
		actions[1] =new CreateComboBoxFromBMAction(viewer, currentNode,childQN,ActionListener.DefaultImage);
		actions[1].setText("创建COMBO");
		
		return actions;
	}

}
