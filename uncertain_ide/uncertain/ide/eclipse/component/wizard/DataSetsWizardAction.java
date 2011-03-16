package uncertain.ide.eclipse.component.wizard;

import uncertain.composite.CompositeMap;
import uncertain.composite.QualifiedName;
import uncertain.ide.eclipse.editor.core.IViewer;
import uncertain.ide.eclipse.node.action.ActionListener;
import uncertain.ide.eclipse.node.action.ActionInfo;
import uncertain.ide.help.LocaleMessage;
import aurora.ide.AuroraConstant;

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
