package uncertain.ide.eclipse.action;

import aurora.ide.AuroraConstant;
import uncertain.composite.CompositeMap;
import uncertain.composite.QualifiedName;
import uncertain.ide.LocaleMessage;
import uncertain.ide.eclipse.editor.IViewer;

public class DataSetWizardAction implements IWizardAction{

	public ActionListener[] createActions(ActionProperties actionProperties) {
		ActionListener[] actions = new ActionListener[2];
		IViewer viewer = actionProperties.getViewer();
		CompositeMap parent = actionProperties.getParent();
		QualifiedName  gridQN = new QualifiedName(AuroraConstant.ApplicationUri,"grid");
		
		actions[0] =new CreateGridFromDataSetAction(viewer, parent,gridQN);
		actions[0].setText(LocaleMessage.getString("create.grid.from.dataset"));
		
		QualifiedName  formQN = new QualifiedName(AuroraConstant.ApplicationUri,"form");
		actions[1] = new CreateFormFromDataSetAction(viewer, parent,formQN);
		actions[1].setText(LocaleMessage.getString("create.form.from.dataset"));
		return actions;
	}

}
