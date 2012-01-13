package aurora.ide.meta.gef.editors.property;

import aurora.ide.AuroraPlugin;
import aurora.ide.meta.gef.editors.models.Renderer;
import aurora.ide.search.core.Util;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.internal.ide.dialogs.OpenResourceDialog;

public class RendererEditDialog extends EditWizard {
	private Renderer renderer;
	private String tmpOpenPath;
	private String tmpLabelText;
	private InnerPage page;

	public RendererEditDialog() {
		super();
		setWindowTitle("renderer");
	}

	@Override
	public void setDialogEdiableObject(DialogEditableObject obj) {
		renderer = (Renderer) obj;
	}

	@Override
	public void addPages() {
		page = new InnerPage("renderer");
		addPage(page);
		super.addPages();
	}

	@Override
	public boolean performFinish() {
		renderer.setOpenPath(tmpOpenPath);
		renderer.setLabelText(tmpLabelText);
		return true;
	}

	private class InnerPage extends WizardPage {

		protected InnerPage(String pageName) {
			super(pageName);
			setTitle("renderer 详细设置");
		}

		public void createControl(final Composite parent) {
			Composite com = new Composite(parent, SWT.NONE);
			com.setLayout(new GridLayout(3, false));
			//
			Label label = new Label(com, SWT.NONE);
			label.setText("显示文本 : ");
			final Text text1 = new Text(com, SWT.BORDER);
			text1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
					1, 1));
			tmpLabelText = renderer.getLabelText();
			if (tmpLabelText == null)
				tmpLabelText = "";

			text1.addModifyListener(new ModifyListener() {

				public void modifyText(ModifyEvent e) {
					tmpLabelText = text1.getText();
				}
			});
			text1.setText(tmpLabelText);
			new Label(com, SWT.NONE);
			label = new Label(com, SWT.NONE);
			label.setText("目标 : ");
			final Text text = new Text(com, SWT.BORDER);
			text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
					1, 1));
			tmpOpenPath = renderer.getOpenPath();
			if (tmpOpenPath == null)
				tmpOpenPath = "";
			text.setText(tmpOpenPath);
			Button btn = new Button(com, SWT.FLAT);
			btn.setText("选择(&O)");
			btn.addSelectionListener(new SelectionListener() {

				public void widgetSelected(SelectionEvent e) {
					@SuppressWarnings("restriction")
					OpenResourceDialog ord = new OpenResourceDialog(parent
							.getShell(), AuroraPlugin.getActiveIFile()
							.getProject(), OpenResourceDialog.CARET_BEGINNING);
					ord.setInitialPattern("*.screen");
					ord.open();
					Object obj = ord.getFirstResult();
					if (!(obj instanceof IFile))
						return;
					IFile file = (IFile) obj;
					IPath path = file.getFullPath();
					IContainer web = Util.findWebInf(file).getParent();
					path = path.makeRelativeTo(web.getFullPath());
					tmpOpenPath = path.toString();
					text.setText(tmpOpenPath);
				}

				public void widgetDefaultSelected(SelectionEvent e) {

				}

			});
			setControl(com);
		}

	}

}
