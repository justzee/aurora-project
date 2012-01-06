package aurora.ide.meta.gef.editors.property;

import aurora.ide.meta.gef.editors.figures.ColorConstants;
import aurora.ide.meta.gef.editors.models.AuroraComponent;
import aurora.ide.meta.gef.editors.models.ButtonClicker;
import aurora.ide.meta.gef.editors.models.Container;
import aurora.ide.meta.gef.editors.models.ViewDiagram;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

public class ButtonClickEditDialog extends EditDialog implements
		SelectionListener {

	protected Object result;
	protected Shell shell;
	private static final String[] names = { "查询", "重置", "保存", "打开", "关闭", "运行",
			"自定义" };
	private Button[] radios = new Button[names.length];
	private Composite[] stackComposites = new Composite[names.length];

	private StackLayout sl_composite_right = new StackLayout();
	private Composite composite_right;
	private ButtonClicker clicker = null;
	private Color SELECTION_BG = new Color(null, 200, 200, 200);

	/**
	 * Create the dialog.
	 * 
	 * @param parent
	 * @param style
	 */
	public ButtonClickEditDialog(Shell parent, Integer style) {
		super(parent, style | SWT.APPLICATION_MODAL);
		setText("Click");
	}

	/**
	 * Open the dialog.
	 * 
	 * @return the result
	 */
	public Object open() {
		createContents();
		shell.open();
		shell.layout();
		Display display = getParent().getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return result;
	}

	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {
		shell = new Shell(getParent(), getStyle());
		shell.setSize(450, 300);
		shell.setText(getText());
		shell.setLayout(new FillLayout(SWT.HORIZONTAL));

		SashForm sashForm = new SashForm(shell, SWT.NONE);
		sashForm.setBackground(ColorConstants.GRID_COLUMN_GRAY);
		sashForm.setSashWidth(1);

		Composite composite_left = new Composite(sashForm, SWT.NONE);
		composite_right = new Composite(sashForm, SWT.NONE);
		composite_right.setLayout(sl_composite_right);

		for (int i = 0; i < names.length; i++) {
			radios[i] = new Button(composite_left, SWT.RADIO);
			radios[i].setText(names[i]);
			radios[i].setBounds(10, 10 + 24 * i, 200, 24);
			radios[i].addSelectionListener(this);
			stackComposites[i] = new Composite(composite_right, SWT.NONE);
			if (names[i].equals(clicker.getActionText())) {
				radios[i].setSelection(true);
				radios[i].setBackground(SELECTION_BG);
				sl_composite_right.topControl = stackComposites[i];
			}
			// Label label = new Label(stackComposites[i], 0);
			// label.setText(names[i]);
			// label.setBounds(0, 0, 80, 30);
		}

		create_query();
		composite_right.layout();

		sashForm.setWeights(new int[] { 1, 3 });
	}

	public void widgetSelected(SelectionEvent e) {
		for (int i = 0; i < radios.length; i++) {
			if (radios[i] == e.getSource()) {
				if (radios[i].getSelection()) {
					sl_composite_right.topControl = stackComposites[i];
					clicker.setActionText(names[i]);
					composite_right.layout();
					radios[i].setBackground(SELECTION_BG);
				} else
					radios[i].setBackground(radios[i].getParent()
							.getBackground());
			}
		}

	}

	public void widgetDefaultSelected(SelectionEvent e) {
	}

	@Override
	public void setDialogEdiableObject(DialogEdiableObject obj) {
		clicker = (ButtonClicker) obj;
	}

	private void create_query() {
		aurora.ide.meta.gef.editors.models.AuroraComponent comp = (aurora.ide.meta.gef.editors.models.AuroraComponent) clicker
				.getContextInfo();
		ViewDiagram root = null;
		while (comp != null) {
			if (comp instanceof ViewDiagram) {
				root = (ViewDiagram) comp;
				break;
			}
			comp = comp.getParent();
		}
		if (root == null)
			throw new RuntimeException("Null root");
		stackComposites[0].setLayout(new FillLayout());
		Tree tree = new Tree(stackComposites[0], 0);
		tree.setData(root);
		for (AuroraComponent ac : root.getChildren()) {
			if (ac instanceof Container) {
				TreeItem ti = new TreeItem(tree, SWT.NONE);
				ti.setData(ac);
				ti.setText(ac.getClass().getSimpleName());
				createSubTree(ti, (Container) ac);
			}
		}
	}

	private void createSubTree(TreeItem ti, Container container) {
		for (AuroraComponent ac : container.getChildren()) {
			if (ac instanceof Container) {
				TreeItem t = new TreeItem(ti, SWT.NONE);
				t.setData(ac);
				t.setText(ac.getClass().getSimpleName());
				createSubTree(t, (Container) ac);
			}
		}
	}

}
