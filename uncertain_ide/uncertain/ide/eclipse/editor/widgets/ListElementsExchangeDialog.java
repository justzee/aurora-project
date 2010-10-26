package uncertain.ide.eclipse.editor.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;

import uncertain.ide.LocaleMessage;

/**
 * This class demonstrates Lists
 */
public class ListElementsExchangeDialog {
	// Strings to use as list items
	private String dialogTitle;
	private String leftGroupTitle;
	private String rightGroupTitle;
	private String[] leftItems;
	private String[] rightItems;
	private List leftList;
	private List rightList;
	Shell shell;

	public ListElementsExchangeDialog(Shell shell, String dialogTitle,
			String leftGroupTitle, String rightGroupTitle, String[] leftItems,
			String[] rightItems) {
		this.shell = shell;
		this.dialogTitle = dialogTitle;
		this.leftGroupTitle = leftGroupTitle;
		this.rightGroupTitle = rightGroupTitle;
		this.leftItems = leftItems;
		this.rightItems = rightItems;
	}

	public ListElementsExchangeDialog(String dialogTitle,
			String leftGroupTitle, String rightGroupTitle, String[] leftItems,
			String[] rightItems) {
		this.dialogTitle = dialogTitle;
		this.leftGroupTitle = leftGroupTitle;
		this.rightGroupTitle = rightGroupTitle;
		this.leftItems = leftItems;
		this.rightItems = rightItems;
	}

	public void open() {
		if (shell == null)
			shell = new Shell(SWT.MIN | SWT.MAX | SWT.DIALOG_TRIM
					| SWT.APPLICATION_MODAL);

		GridLayout gridLayout = new GridLayout();
		shell.setLayout(gridLayout);
		if (dialogTitle != null)
			shell.setText(dialogTitle);

		Group mainGroup = new Group(shell, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL, GridData.FILL, true,
				true);
		mainGroup.setLayoutData(gridData);

		gridLayout = new GridLayout();
		gridLayout.numColumns = 10;
		mainGroup.setLayout(gridLayout);

		Group leftGroup = new Group(mainGroup, SWT.NONE);
		if (leftGroupTitle != null)
			leftGroup.setText(leftGroupTitle);
		gridData = new GridData(GridData.FILL, GridData.FILL, true, true);
		gridData.horizontalSpan = 4;
		leftGroup.setLayoutData(gridData);

		leftGroup.setLayout(new FillLayout());

		leftList = new List(leftGroup, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);
		// gridData = new GridData(GridData.FILL, GridData.CENTER, true, false);
		// gridData.horizontalSpan = 4;
		// leftList.setLayoutData(gridData);

		Group textGroup = new Group(mainGroup, SWT.NONE);
		gridData = new GridData(GridData.FILL, GridData.FILL, false, true);
		gridData.horizontalSpan = 2;
		textGroup.setLayoutData(gridData);

		RowLayout rowLayout = new RowLayout();
		rowLayout.wrap = false;
		rowLayout.pack = false;
		rowLayout.justify = false;
		rowLayout.type = SWT.VERTICAL;
		rowLayout.marginLeft = 15;
		rowLayout.marginTop = 150;
		rowLayout.marginRight = 15;
		rowLayout.marginBottom = 150;
		rowLayout.spacing = 5;
		textGroup.setLayout(rowLayout);

		if (leftItems != null)
			leftList.setItems(leftItems);
		// else
		// leftList.setItems(ITEMS);

		Button toRightAll = new Button(textGroup, SWT.NONE);
		toRightAll.setText("=>");
		Button toRight = new Button(textGroup, SWT.NONE);
		toRight.setText("->");
		Button toleft = new Button(textGroup, SWT.NONE);
		toleft.setText("<-");
		Button toleftAll = new Button(textGroup, SWT.NONE);
		toleftAll.setText("<=");

		Group rightGroup = new Group(mainGroup, SWT.NONE);
		if (rightGroupTitle != null)
			rightGroup.setText(rightGroupTitle);
		gridData = new GridData(GridData.FILL, GridData.FILL, true, true);
		gridData.horizontalSpan = 4;
		rightGroup.setLayoutData(gridData);

		rightGroup.setLayout(new FillLayout());
		// Create a multiple-selection list
		rightList = new List(rightGroup, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);
		// gridData = new GridData(GridData.FILL, GridData.CENTER, true, false);
		// gridData.horizontalSpan = 4;
		// multi.setLayoutData(gridData);

		// Add the items all at once
		if (rightItems != null)
			rightList.setItems(rightItems);
		// else
		// multi.setItems(ITEMS);

		Button enter = new Button(shell, SWT.PUSH);
		enter.setText(LocaleMessage.getString("OK"));
		gridData = new GridData(GridData.END, GridData.CENTER, false, false);
		enter.setLayoutData(gridData);

		toRightAll.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				int oldLength = rightList.getItems().length;
				String[] newItems = joinItems(rightList.getItems(), leftList
						.getItems());
				rightList.setItems(newItems);
				rightList.setSelection(oldLength, newItems.length);
				leftList.removeAll();

			}
		});

		toRight.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				int oldLength = rightList.getItems().length;
				String[] newItems = joinItems(rightList.getItems(), leftList
						.getSelection());
				rightList.setItems(newItems);
				rightList.setSelection(oldLength, newItems.length);
				leftList.remove(leftList.getSelectionIndices());
			}
		});

		toleft.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				int oldLength = leftList.getItems().length;
				String[] newItems = joinItems(leftList.getItems(), rightList
						.getSelection());
				leftList.setItems(newItems);
				leftList.setSelection(oldLength, newItems.length);
				rightList.remove(rightList.getSelectionIndices());
			}
		});

		toleftAll.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				int oldLength = leftList.getItems().length;
				String[] newItems = joinItems(leftList.getItems(), rightList
						.getItems());
				leftList.setItems(newItems);
				leftList.setSelection(oldLength, newItems.length);
				rightList.removeAll();
			}
		});

		enter.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				shell.close();
			}
		});
		// shell.pack();
		shell.open();
		shell.addDisposeListener(new DisposeListener() {

			public void widgetDisposed(DisposeEvent e) {
				leftItems = leftList.getItems();
				rightItems = rightList.getItems();

			}
		});

		Display display = shell.getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
	}

	public static void main(String[] args) {
		Display display = new Display();
		Shell shell = new Shell(display);
		// new DogShowRegistrationWindow().createShell(display);

		String[] ITEMS = { "Alpha", "Bravo", "Charlie", "Delta", "Echo",
				"Foxtrot", "Golf" };
		new ListElementsExchangeDialog(shell, "Dialog", "left ", "right", ITEMS, ITEMS)
				.open();
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
	}

	public static String[] joinItems(String[] left, String[] right) {
		String[] result = new String[left.length + right.length];
		int resultIndex = 0;
		for (int i = 0; i < left.length; i++) {
			result[resultIndex++] = left[i];
		}
		for (int i = 0; i < right.length; i++) {
			result[resultIndex++] = right[i];
		}
		return result;
	}

	public String getDialogTitle() {
		return dialogTitle;
	}

	public void setDialogTitle(String dialogTitle) {
		this.dialogTitle = dialogTitle;
	}

	public String getLeftGroupTitle() {
		return leftGroupTitle;
	}

	public void setLeftGroupTitle(String leftGroupTitle) {
		this.leftGroupTitle = leftGroupTitle;
	}

	public String getRightGroupTitle() {
		return rightGroupTitle;
	}

	public void setRightGroupTitle(String rightGroupTitle) {
		this.rightGroupTitle = rightGroupTitle;
	}

	public String[] getLeftItems() {
		return leftItems;
	}

	public void setLeftItems(String[] leftItems) {
		this.leftItems = leftItems;
	}

	public String[] getRightItems() {
		return rightItems;
	}

	public void setRightItems(String[] rightItems) {
		this.rightItems = rightItems;
	}
}
