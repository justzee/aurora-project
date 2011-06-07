package component.wizard;

import helpers.AuroraConstant;
import helpers.LocaleMessage;
import node.action.ActionInfo;
import node.action.ActionListener;
import editor.core.IViewer;
import uncertain.composite.CompositeMap;
import uncertain.composite.QualifiedName;

public class DataSetsWizardAction implements IWizardAction{

	public ActionListener[] createActions(ActionInfo actionProperties) {
		ActionListener[] actions = new ActionListener[1];
		IViewer viewer = actionProperties.getViewer();
		CompositeMap parent = actionProperties.getCurrentNode();
		QualifiedName  gridQN = new QualifiedName(AuroraConstant.ApplicationUri,"dataSet");
		actions[0] =new AddDataSetAction(viewer, parent,gridQN,ActionListener.DefaultImage);
		actions[0].setText(LocaleMessage.getString("dataset.wizard"));
		return actions;
	}

}
