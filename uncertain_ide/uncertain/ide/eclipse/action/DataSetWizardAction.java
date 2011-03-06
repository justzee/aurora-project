package uncertain.ide.eclipse.action;

import uncertain.composite.CompositeMap;
import uncertain.composite.QualifiedName;
import uncertain.ide.eclipse.editor.core.IViewer;
import uncertain.ide.help.LocaleMessage;
import aurora.ide.AuroraConstant;

public class DataSetWizardAction implements IWizardAction{

	public ActionListener[] createActions(ActionProperties actionProperties) {
		ActionListener[] actions = new ActionListener[2];
		IViewer viewer = actionProperties.getViewer();
		CompositeMap parent = actionProperties.getParent();
		QualifiedName  gridQN = new QualifiedName(AuroraConstant.ApplicationUri,"grid");
		
		actions[0] =new CreateGridFromDataSetAction(viewer, parent,gridQN,ActionListener.DefaultImage);
		actions[0].setText(LocaleMessage.getString("create.grid.from.dataset"));
		
		QualifiedName  formQN = new QualifiedName(AuroraConstant.ApplicationUri,"form");
		actions[1] = new CreateFormFromDataSetAction(viewer, parent,formQN,ActionListener.DefaultImage);
		actions[1].setText(LocaleMessage.getString("create.form.from.dataset"));
		return actions;
	}

}
