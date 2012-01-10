package aurora.ide.meta.gef.editors.property;

import aurora.ide.meta.gef.editors.figures.ColorConstants;
import aurora.ide.meta.gef.editors.models.AuroraComponent;
import aurora.ide.meta.gef.editors.models.ButtonClicker;
import aurora.ide.meta.gef.editors.models.Container;
import aurora.ide.meta.gef.editors.models.ViewDiagram;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

public class ButtonClickEditDialog extends EditWizard {

	protected Object result;
	protected Shell shell;
	private static final String[] descriptions = { "查询,选择一个带有查询功能的组件",
			"重置,选择一个带有重置功能的组件", "保存", "打开", "关闭", "运行", "自定义" };
	private Button[] radios = new Button[ButtonClicker.action_texts.length];
	private Composite[] stackComposites = new Composite[radios.length];

	private Composite composite_right;
	private ButtonClicker clicker = null;
	private Color SELECTION_BG = new Color(null, 109, 187, 242);
	private WizardPage page;

	public ButtonClickEditDialog() {
		setWindowTitle("Click");
	}

	@Override
	public void setDialogEdiableObject(DialogEditableObject obj) {
		clicker = (ButtonClicker) obj;
	}

	public void addPages() {
		page = new InnerPage();
		addPage(page);
	}

	@Override
	public boolean performFinish() {
		if (result instanceof AuroraComponent)
			clicker.setTargetComponent((AuroraComponent) result);
		else
			page.setErrorMessage("请选择一个");
		return true;
	}

	private class InnerPage extends WizardPage implements SelectionListener {
		private StackLayout slLayout = new StackLayout();

		public InnerPage() {
			super("button_click");
			setTitle("设置button的'click'");
		}

		public void createControl(Composite parent) {
			SashForm sashForm = new SashForm(parent, SWT.NONE);
			sashForm.setBackground(ColorConstants.GRID_COLUMN_GRAY);
			sashForm.setSashWidth(1);

			Composite composite_left = new Composite(sashForm, SWT.NONE);
			RowLayout rw = new RowLayout(SWT.VERTICAL);
			rw.fill = true;
			rw.spacing = 5;
			composite_left.setLayout(rw);
			composite_right = new Composite(sashForm, SWT.NONE);
			composite_right.setLayout(slLayout);

			for (int i = 0; i < ButtonClicker.action_texts.length; i++) {
				radios[i] = new Button(composite_left, SWT.RADIO);
				radios[i].setText(ButtonClicker.action_texts[i]);
				// radios[i].setBounds(10, 10 + 24 * i, 200, 24);
				radios[i].addSelectionListener(this);
				stackComposites[i] = new Composite(composite_right, SWT.NONE);
				if (ButtonClicker.action_ids[i].equals(clicker.getActionID())) {
					radios[i].setSelection(true);
					radios[i].setBackground(SELECTION_BG);
					slLayout.topControl = stackComposites[i];
					setDescription(descriptions[i]);
				}
			}

			create_query(0);// 0
			create_reset(1);// 1
			composite_right.layout();
			if (slLayout.topControl != null) {
				slLayout.topControl.forceFocus();
			}

			sashForm.setWeights(new int[] { 1, 3 });
			setControl(sashForm);
		}

		private void create_query(int index) {
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
			stackComposites[index].setLayout(new FillLayout());
			final Tree tree = new Tree(stackComposites[index], 0);
			tree.setData(root);
			for (AuroraComponent ac : root.getChildren()) {
				if (ac instanceof Container) {
					TreeItem ti = new TreeItem(tree, SWT.NONE);
					ti.setData(ac);
					ti.setText(ac.getClass().getSimpleName());
					if (ac == clicker.getTargetComponent())
						tree.setSelection(ti);
					ti.setExpanded(true);
					createSubTree(tree, ti, (Container) ac);
				}
			}
			for (TreeItem ti : tree.getItems())
				ti.setExpanded(true);
			tree.addSelectionListener(new SelectionListener() {

				public void widgetSelected(SelectionEvent e) {
					TreeItem ti = tree.getSelection()[0];
					result = ti.getData();
				}

				public void widgetDefaultSelected(SelectionEvent e) {

				}
			});
		}

		private void create_reset(int index) {
			create_query(1);
		}

		private void createSubTree(Tree tree, TreeItem ti, Container container) {
			for (AuroraComponent ac : container.getChildren()) {
				if (ac instanceof Container) {
					TreeItem t = new TreeItem(ti, SWT.NONE);
					t.setData(ac);
					if (ac == clicker.getTargetComponent())
						tree.setSelection(t);
					t.setText(ac.getClass().getSimpleName());
					createSubTree(tree, t, (Container) ac);
				}
			}
			for (TreeItem t : ti.getItems())
				t.setExpanded(true);
		}

		public void widgetSelected(SelectionEvent e) {
			for (int i = 0; i < radios.length; i++) {
				if (radios[i] == e.getSource()) {
					if (radios[i].getSelection()) {
						slLayout.topControl = stackComposites[i];
						clicker.setActionText(ButtonClicker.action_texts[i]);
						clicker.setActionID(ButtonClicker.action_ids[i]);
						composite_right.layout();
						radios[i].setBackground(SELECTION_BG);
						setDescription(descriptions[i]);
					} else
						radios[i].setBackground(radios[i].getParent()
								.getBackground());
				}
			}
		}

		public void widgetDefaultSelected(SelectionEvent e) {

		}
	}
}
