package aurora.ide.meta.gef.editors.wizard.page;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.List;

import aurora.ide.AuroraPlugin;
import aurora.ide.editor.widgets.core.IUpdateMessageDialog;
import aurora.ide.meta.gef.extension.ExtensionBean;
import aurora.ide.meta.gef.extension.ExtensionManager;

public class TemplateWizardPage extends WizardPage implements IUpdateMessageDialog {

	private ExtensionBean selected;
	private Image emptyImage;

	public TemplateWizardPage() {
		super("aurora.wizard.template.Page");
		setTitle("模版选择");
	}

	public void createControl(Composite parent) {
		final Composite composite = new Composite(parent, SWT.NULL);
		setControl(composite);
		composite.setLayout(new GridLayout(2, false));
		String loc = AuroraPlugin.getDefault().getBundle().getLocation();
		final String path = loc.substring(loc.indexOf("/") + 1);

		GridData gd = new GridData(GridData.FILL_VERTICAL);
		List list = new List(composite, SWT.BORDER | SWT.DROP_DOWN | SWT.READ_ONLY);
		list.setLayoutData(gd);

		gd = new GridData(GridData.FILL_BOTH);
		final Canvas canvas = new Canvas(composite, SWT.BORDER);
		canvas.setLayoutData(gd);

		java.util.List<ExtensionBean> beans = ExtensionManager.getInstance().getBeans();
		for (int i = 0; i < beans.size(); i++) {
			list.add((i + 1) + "：" + beans.get(i).getName() + "  ");
			list.setData(Integer.toString(i), beans.get(i));
		}

		canvas.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				try {
					Image image = new Image(composite.getDisplay(), path + selected.getThumbnail());
					e.gc.drawImage(image, (e.width - image.getImageData().width) / 2, (e.height - image.getImageData().height) / 2);
					image.dispose();
				} catch (SWTException e1) {
					if (emptyImage != null) {
						e.gc.drawImage(emptyImage, (e.width - emptyImage.getImageData().width) / 2, (e.height - emptyImage.getImageData().height) / 2);
					}
				}
			}
		});

		list.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				List list = (List) e.getSource();
				selected = (ExtensionBean) list.getData(Integer.toString(list.getSelectionIndex()));
				setPageComplete(selected == null ? false : (selected.getWizard().getPageCount() > 0));
				setDescription(selected == null ? "" : selected.getDescription());
				canvas.redraw();
				getWizard().canFinish();
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		list.select(0);
		selected = (ExtensionBean) list.getData("0");
		try {
			emptyImage = new Image(composite.getDisplay(), path + "templates/image/empty.png");
		} catch (SWTException e) {
			emptyImage = null;
		}
		setPageComplete(selected == null ? false : (selected.getWizard().getPageCount() > 0));
		setDescription(selected == null ? "" : selected.getDescription());
		canvas.redraw();
		getWizard().canFinish();
	}

	public void updateStatus(String message) {
		setErrorMessage(message);
		setPageComplete(message == null);
	}

	public IWizardPage getNextPage() {
		return selected == null ? null : selected.getWizard().getStartingPage();
	}

	public void dispose() {
		super.dispose();
		if (!(emptyImage == null || emptyImage.isDisposed())) {
			emptyImage.dispose();
		}
		ExtensionManager.getInstance().dispose();
	}

	public ExtensionBean getSelected() {
		return selected;
	}
}