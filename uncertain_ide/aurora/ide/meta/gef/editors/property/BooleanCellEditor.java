package aurora.ide.meta.gef.editors.property;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class BooleanCellEditor extends CellEditor implements SelectionListener {

	private static final int defaultStyle = SWT.NONE;
	private Button checkButton;

	public BooleanCellEditor() {
		setStyle(defaultStyle);
	}

	public BooleanCellEditor(Composite parent) {
		this(parent, defaultStyle);
	}

	public BooleanCellEditor(Composite parent, int style) {
		super(parent, style);
	}

	public void activate() {
		checkButton.setSelection(!checkButton.getSelection());
		fireApplyEditorValue();
	}

	protected Control createControl(Composite parent) {
		checkButton = new Button(parent, SWT.CHECK);
		checkButton.setCapture(true);
		checkButton.addSelectionListener(this);
		return checkButton;
	}

	protected Object doGetValue() {
		return checkButton.getSelection();
	}

	protected void doSetFocus() {
		// Ignore
	}

	protected void doSetValue(Object value) {
		Assert.isTrue(value instanceof Boolean);
		checkButton.setSelection(((Boolean) value).booleanValue());
	}

	public void activate(ColumnViewerEditorActivationEvent activationEvent) {
		if (activationEvent.eventType != ColumnViewerEditorActivationEvent.TRAVERSAL) {
			super.activate(activationEvent);
		}
	}

	public void widgetSelected(SelectionEvent e) {
		fireApplyEditorValue();
	}

	public void widgetDefaultSelected(SelectionEvent e) {

	}
}
