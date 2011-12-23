package aurora.ide.meta.gef.editors.wizard;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import aurora.ide.editor.widgets.core.IUpdateMessageDialog;
import aurora.ide.helpers.LocaleMessage;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.jface.layout.TreeColumnLayout;
import org.eclipse.swt.layout.GridData;

public class TemplateWizardPage extends WizardPage implements
		IUpdateMessageDialog {
	private TreeViewer treeViewer;

	public TemplateWizardPage() {
		super("aurora.wizard.Page");
		setTitle("模版选择");
		setDescription("模版选择");
	}

	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		setControl(composite);
		composite.setLayout(new GridLayout(1, false));
		treeViewer = new TreeViewer(composite, SWT.BORDER);
		Tree tree = treeViewer.getTree();
		tree.setHeaderVisible(true);
		tree.setLinesVisible(true);
		GridData gd_tree = new GridData(GridData.FILL_BOTH);
		tree.setLayoutData(gd_tree);
	}

	public void updateStatus(String message) {
		setErrorMessage(message);
		setPageComplete(message == null);
	}
}
