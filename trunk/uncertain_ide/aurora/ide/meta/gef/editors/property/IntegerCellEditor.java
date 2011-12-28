package aurora.ide.meta.gef.editors.property;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Spinner;

public class IntegerCellEditor extends CellEditor implements FocusListener,
		KeyListener {
	private Spinner spinner;

	public IntegerCellEditor() {
		setStyle(SWT.NONE);
	}

	public IntegerCellEditor(Composite parent) {
		this(parent, SWT.NONE);
	}

	public IntegerCellEditor(Composite parent, int style) {
		super(parent, style);
	}

	public void activate() {
		spinner.setSelection(0);
		fireApplyEditorValue();
	}

	@Override
	protected Control createControl(Composite parent) {
		spinner = new Spinner(parent, SWT.NONE);
		spinner.setIncrement(5);
		spinner.setPageIncrement(30);
		spinner.setDigits(0);
		spinner.setMinimum(0);
		spinner.setMaximum(Integer.MAX_VALUE);
		spinner.addFocusListener(this);
		spinner.addKeyListener(this);
		// spinner.setCursor(Display.getCurrent()
		// .getSystemCursor(SWT.CURSOR_IBEAM));
		for (Control c : spinner.getChildren())
			System.out.println(c.getClass().getSimpleName());
		return spinner;
	}

	@Override
	protected Object doGetValue() {
		return spinner.getSelection();
	}

	@Override
	protected void doSetFocus() {

	}

	@Override
	protected void doSetValue(Object value) {
		spinner.setSelection((Integer) value);
	}

	public void activate(ColumnViewerEditorActivationEvent activationEvent) {
		if (activationEvent.eventType != ColumnViewerEditorActivationEvent.TRAVERSAL) {
			super.activate(activationEvent);
		}
	}

	public void focusGained(FocusEvent e) {

	}

	public void focusLost(FocusEvent e) {
		fireApplyEditorValue();
	}

	public void keyPressed(KeyEvent e) {
		// 回车
		if (e.keyCode == 13) {
			fireApplyEditorValue();
		}
	}

	public void keyReleased(KeyEvent e) {

	}

}
