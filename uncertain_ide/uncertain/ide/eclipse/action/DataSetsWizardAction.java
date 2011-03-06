package uncertain.ide.eclipse.action;

import uncertain.composite.CompositeMap;
import uncertain.composite.QualifiedName;
import uncertain.ide.eclipse.editor.core.IViewer;
import uncertain.ide.help.LocaleMessage;
import aurora.ide.AuroraConstant;

public class DataSetsWizardAction implements IWizardAction{

	public ActionListener[] createActions(ActionProperties actionProperties) {
		ActionListener[] actions = new ActionListener[1];
		IViewer viewer = actionProperties.getViewer();
		CompositeMap parent = actionProperties.getParent();
		QualifiedName  gridQN = new QualifiedName(AuroraConstant.ApplicationUri,"dataSet");
		actions[0] =new AddDataSetAction(viewer, parent,gridQN,ActionListener.DefaultImage);
		actions[0].setText(LocaleMessage.getString("dataset.wizard"));
		return actions;
	}

}
