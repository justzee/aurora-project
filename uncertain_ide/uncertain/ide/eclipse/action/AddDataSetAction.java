package uncertain.ide.eclipse.action;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;

import uncertain.composite.CompositeMap;
import uncertain.composite.QualifiedName;
import uncertain.ide.Activator;
import uncertain.ide.eclipse.editor.core.IViewer;
import uncertain.ide.util.LocaleMessage;

public class AddDataSetAction extends AddElementAction {

	public AddDataSetAction(IViewer viewer, CompositeMap parentCM,
			String prefix, String uri, String cmName, String text,
			int actionStyle) {
		super(viewer, parentCM, prefix, uri, cmName, text, actionStyle);
	}

	public AddDataSetAction(IViewer viewer, CompositeMap parentCM,
			QualifiedName qName, int actionStyle) {
		super(viewer, parentCM, qName, actionStyle);
	}

	public void run() {
		if (callWizard() == Window.CANCEL)
			return;
		if (viewer != null) {
			viewer.refresh(true);
		}
	}

	private int callWizard() {
		DataSetWizard wizard = new DataSetWizard(parent, prefix, uri, localName);
		WizardDialog dialog = new WizardDialog(new Shell(), wizard);
		return dialog.open();
	}

	public ImageDescriptor getDefaultImageDescriptor() {
		return Activator.getImageDescriptor(LocaleMessage
				.getString("wizard.icon"));
	}
}
