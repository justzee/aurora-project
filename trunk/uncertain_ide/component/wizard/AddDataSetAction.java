package component.wizard;

import ide.AuroraPlugin;
import helpers.LocaleMessage;
import node.action.AddElementAction;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;

import editor.core.IViewer;

import uncertain.composite.CompositeMap;
import uncertain.composite.QualifiedName;

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
		return AuroraPlugin.getImageDescriptor(LocaleMessage.getString("wizard.icon"));
	}
}
