package uncertain.ide.eclipse.component.wizard;

import uncertain.composite.CompositeMap;
import uncertain.composite.QualifiedName;
import uncertain.ide.eclipse.editor.core.IViewer;
import uncertain.ide.eclipse.node.action.ActionListener;
import uncertain.ide.eclipse.node.action.ActionInfo;
import uncertain.ide.help.LocaleMessage;
import aurora.ide.AuroraConstant;

public class DataSetWizardAction implements IWizardAction{

	public ActionListener[] createActions(ActionInfo actionInfo) {
		ActionListener[] actions = new ActionListener[2];
		IViewer viewer = actionInfo.getViewer();
		CompositeMap currentNode = actionInfo.getCurrentNode();
		QualifiedName  gridQN = new QualifiedName(AuroraConstant.ApplicationUri,"grid");
		
		actions[0] =new CreateGridFromDataSetAction(viewer, currentNode,gridQN,ActionListener.DefaultImage);
		actions[0].setText(LocaleMessage.getString("create.grid.from.dataset"));
		
		QualifiedName  formQN = new QualifiedName(AuroraConstant.ApplicationUri,"form");
		actions[1] = new CreateFormFromDataSetAction(viewer, currentNode,formQN,ActionListener.DefaultImage);
		actions[1].setText(LocaleMessage.getString("create.form.from.dataset"));
		return actions;
	}

}
