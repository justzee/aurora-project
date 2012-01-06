package aurora.ide.meta.gef.editors.property;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Shell;

public class DialogCellEditor extends CellEditor implements SelectionListener,
		MouseListener {
	private Button button;
	private Label label;
	private DialogEdiableObject value;
	private Shell shell;
	private Class<EditDialog> clazz;

	public DialogCellEditor(Composite parent, Class<EditDialog> clazz) {
		super(parent, SWT.NONE);
		this.clazz = clazz;
	}

	@Override
	protected Control createControl(Composite parent) {
		shell = parent.getShell();
		Composite com = new Composite(parent, SWT.NONE);
		com.setBackground(parent.getBackground());
		com.setLayout(new SimpleLayout());
		label = new Label(com, SWT.NONE);
		label.addMouseListener(this);
		button = new Button(com, SWT.FLAT);
		button.setText("...");
		button.addSelectionListener(this);
		return com;
	}

	@Override
	protected Object doGetValue() {
		return value;
	}

	@Override
	protected void doSetFocus() {

	}

	@Override
	protected void doSetValue(Object value) {
		this.value = (DialogEdiableObject) value;
		label.setText(this.value.getDescripition());
	}

	public void activate(ColumnViewerEditorActivationEvent activationEvent) {
		if (activationEvent.eventType != ColumnViewerEditorActivationEvent.TRAVERSAL) {
			super.activate(activationEvent);
		}
	}

	private class SimpleLayout extends Layout {

		@Override
		protected Point computeSize(Composite composite, int wHint, int hHint,
				boolean flushCache) {
			return new Point(800, 20);
		}

		@Override
		protected void layout(Composite composite, boolean flushCache) {
			Point size = composite.getSize();
			label.setBounds(0, 0, size.x - 20, size.y);
			button.setBounds(size.x - 20, 0, 20, size.y);
		}

	}

	private void showDialog() {
		try {
			EditDialog dialog = clazz
					.getConstructor(Shell.class, Integer.class).newInstance(
							shell,
							SWT.TITLE | SWT.CLOSE | SWT.APPLICATION_MODAL);
			dialog.setDialogEdiableObject(value);
			dialog.open();
			label.setText(value.getDescripition());
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	public void widgetSelected(SelectionEvent e) {
		showDialog();
	}

	public void widgetDefaultSelected(SelectionEvent e) {

	}

	public void mouseDoubleClick(MouseEvent e) {
		showDialog();
	}

	public void mouseDown(MouseEvent e) {
	}

	public void mouseUp(MouseEvent e) {
	}

}
