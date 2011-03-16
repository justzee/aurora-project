package uncertain.ide.eclipse.component.wizard;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;

import uncertain.composite.CompositeMap;
import uncertain.composite.QualifiedName;
import uncertain.ide.Activator;
import uncertain.ide.eclipse.editor.core.IViewer;
import uncertain.ide.eclipse.node.action.AddElementAction;
import uncertain.ide.help.LocaleMessage;

public class AddDataSetAction extends AddElementAction {

	public AddDataSetAction(IViewer viewer, CompositeMap currentNode,QualifiedName childQN, int actionStyle) {
		super(viewer, currentNode, childQN, actionStyle);
	}

	public void run() {
		if (callWizard() == Window.CANCEL)
			return;
		if (viewer != null) {
			viewer.refresh(true);
		}
	}

	private int callWizard() {
		DataSetWizard wizard = new DataSetWizard(currentNode);
		WizardDialog dialog = new WizardDialog(new Shell(), wizard);
		return dialog.open();
	}

	public ImageDescriptor getDefaultImageDescriptor() {
		return Activator.getImageDescriptor(LocaleMessage.getString("wizard.icon"));
	}
}
