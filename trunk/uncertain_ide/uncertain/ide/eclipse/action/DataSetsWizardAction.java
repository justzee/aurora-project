package uncertain.ide.eclipse.action;

import aurora.ide.AuroraConstant;
import uncertain.composite.CompositeMap;
import uncertain.composite.QualifiedName;
import uncertain.ide.LocaleMessage;
import uncertain.ide.eclipse.editor.IViewer;

public class DataSetsWizardAction implements IWizardAction{

	public ActionListener[] createActions(ActionProperties actionProperties) {
		ActionListener[] actions = new ActionListener[1];
		IViewer viewer = actionProperties.getViewer();
		CompositeMap parent = actionProperties.getParent();
		QualifiedName  gridQN = new QualifiedName(AuroraConstant.ApplicationUri,"dataSet");
		actions[0] =new AddDataSetAction(viewer, parent,gridQN);
		actions[0].setText(LocaleMessage.getString("dataset.wizard"));
		return actions;
	}

}
