package aurora.ide.preferencepages;

import java.io.IOException;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.jface.text.templates.ContextTypeRegistry;
import org.eclipse.jface.text.templates.Template;
import org.eclipse.jface.text.templates.persistence.TemplatePersistenceData;
import org.eclipse.jface.text.templates.persistence.TemplateStore;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.forms.widgets.ColumnLayout;

import aurora.ide.AuroraPlugin;

public class FunctionRegisterPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {



	/** The table presenting the templates. */
	private CheckboxTableViewer fTableViewer;

	/* buttons */
	private Button fAddButton;
	private Button fEditButton;
	private Button fImportButton;
	private Button fExportButton;
	private Button fRemoveButton;
	private Button fRestoreButton;
	private Button fRevertButton;

	/** The viewer displays the pattern of selected template. */
	private SourceViewer fPatternViewer;
	/** Format checkbox. This gets conditionally added. */
	private Button fFormatButton;
	/** The store for our templates. */
	private TemplateStore fTemplateStore;
	/** The context type registry. */
	private ContextTypeRegistry fContextTypeRegistry;


	/**
	 * Creates a new template preference page.
	 */
	public FunctionRegisterPreferencePage() {
		super();
		setDescription("功能注册SQL摸版定制");
	}

	/*
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
	}

	/*
	 * @see PreferencePage#createContents(Composite)
	 */
	protected Control createContents(Composite ancestor) {
		Composite parent= new Composite(ancestor, SWT.NONE);
		GridLayout layout= new GridLayout();
		layout.numColumns= 2;
		layout.marginHeight= 0;
		layout.marginWidth= 0;
		parent.setLayout(layout);

        Composite innerParent= new Composite(parent, SWT.NONE);
        GridLayout innerLayout= new GridLayout();
        innerLayout.numColumns= 2;
        innerLayout.marginHeight= 0;
        innerLayout.marginWidth= 0;
        innerParent.setLayout(innerLayout);
        GridData gd= new GridData(GridData.FILL_BOTH);
        gd.horizontalSpan= 2;
        innerParent.setLayoutData(gd);

        Composite tableComposite= new Composite(innerParent, SWT.NONE);
        GridData data= new GridData(GridData.FILL_BOTH);
        data.widthHint= 360;
        data.heightHint= convertHeightInCharsToPixels(10);
        tableComposite.setLayoutData(data);

        ColumnLayout columnLayout= new ColumnLayout();
        tableComposite.setLayout(columnLayout);
		Table table= new Table(tableComposite, SWT.CHECK | SWT.BORDER | SWT.MULTI | SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL);

		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		GC gc= new GC(getShell());
		gc.setFont(JFaceResources.getDialogFont());

		TableColumn column1= new TableColumn(table, SWT.NONE);
		column1.setText("Name");
//		int minWidth= computeMinimumColumnWidth(gc, TemplatesMessages.TemplatePreferencePage_column_name);
//		columnLayout.addColumnData(new ColumnWeightData(2, minWidth, true));

		TableColumn column2= new TableColumn(table, SWT.NONE);
		column2.setText("Description");
//		minWidth= computeMinimumContextColumnWidth(gc);
//		columnLayout.addColumnData(new ColumnWeightData(1, minWidth, true));


		gc.dispose();

		fTableViewer= new CheckboxTableViewer(table);
//		fTableViewer.setLabelProvider(new TemplateLabelProvider());
//		fTableViewer.setContentProvider(new TemplateContentProvider());

		fTableViewer.setComparator(new ViewerComparator() {
			public int compare(Viewer viewer, Object object1, Object object2) {
				if ((object1 instanceof TemplatePersistenceData) && (object2 instanceof TemplatePersistenceData)) {
					Template left= ((TemplatePersistenceData) object1).getTemplate();
					Template right= ((TemplatePersistenceData) object2).getTemplate();
					int result= Collator.getInstance().compare(left.getName(), right.getName());
					if (result != 0)
						return result;
					return Collator.getInstance().compare(left.getDescription(), right.getDescription());
				}
				return super.compare(viewer, object1, object2);
			}

			public boolean isSorterProperty(Object element, String property) {
				return true;
			}
		});

		fTableViewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent e) {
//				edit();
			}
		});

		fTableViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent e) {
				selectionChanged1();
			}
		});

		fTableViewer.addCheckStateListener(new ICheckStateListener() {
			public void checkStateChanged(CheckStateChangedEvent event) {
				TemplatePersistenceData d= (TemplatePersistenceData) event.getElement();
				d.setEnabled(event.getChecked());
			}
		});

		Composite buttons= new Composite(innerParent, SWT.NONE);
		buttons.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
		layout= new GridLayout();
		layout.marginHeight= 0;
		layout.marginWidth= 0;
		buttons.setLayout(layout);

		fAddButton= new Button(buttons, SWT.PUSH);
		fAddButton.setText("New...");
		fAddButton.setLayoutData(getButtonGridData(fAddButton));
		fAddButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				add();
			}
		});

		fEditButton= new Button(buttons, SWT.PUSH);
		fEditButton.setText("Edit...");
		fEditButton.setLayoutData(getButtonGridData(fEditButton));
		fEditButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
//				edit();
			}
		});

		fRemoveButton= new Button(buttons, SWT.PUSH);
		fRemoveButton.setText("Remove");
		fRemoveButton.setLayoutData(getButtonGridData(fRemoveButton));
		fRemoveButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
//				remove();
			}
		});

		createSeparator(buttons);


		fPatternViewer= doCreateViewer(parent);


//		fTableViewer.setInput(fTemplateStore);
//		fTableViewer.setAllChecked(false);
//		fTableViewer.setCheckedElements(getEnabledTemplates());

//		updateButtons();
		Dialog.applyDialogFont(parent);
		innerParent.layout();

		return parent;
	}

	/*
	 * @since 3.2
	 */
	private int computeMinimumColumnWidth(GC gc, String string) {
		return gc.stringExtent(string).x + 10; // pad 10 to accommodate table header trimmings
	}


	/**
	 * Creates a separator between buttons.
	 *
	 * @param parent the parent composite
	 * @return a separator
	 */
	private Label createSeparator(Composite parent) {
		Label separator= new Label(parent, SWT.NONE);
		separator.setVisible(false);
		GridData gd= new GridData();
		gd.horizontalAlignment= GridData.FILL;
		gd.verticalAlignment= GridData.BEGINNING;
		gd.heightHint= 4;
		separator.setLayoutData(gd);
		return separator;
	}


	/**
	 * Returns whether the formatter preference checkbox should be shown.
	 *
	 * @return <code>true</code> if the formatter preference checkbox should
	 *         be shown, <code>false</code> otherwise
	 */
	protected boolean isShowFormatterSetting() {
		return true;
	}

	private TemplatePersistenceData[] getEnabledTemplates() {
		List enabled= new ArrayList();
		TemplatePersistenceData[] datas= fTemplateStore.getTemplateData(false);
		for (int i= 0; i < datas.length; i++) {
			if (datas[i].isEnabled())
				enabled.add(datas[i]);
		}
		return (TemplatePersistenceData[]) enabled.toArray(new TemplatePersistenceData[enabled.size()]);
	}

	private SourceViewer doCreateViewer(Composite parent) {
		Label label= new Label(parent, SWT.NONE);
		label.setText("Preview:");
		GridData data= new GridData();
		data.horizontalSpan= 2;
		label.setLayoutData(data);

		SourceViewer viewer= createViewer(parent);

		viewer.setEditable(false);
		Cursor arrowCursor= viewer.getTextWidget().getDisplay().getSystemCursor(SWT.CURSOR_ARROW);
		viewer.getTextWidget().setCursor(arrowCursor);

		// Don't set caret to 'null' as this causes https://bugs.eclipse.org/293263
//		viewer.getTextWidget().setCaret(null);

		Control control= viewer.getControl();
		data= new GridData(GridData.FILL_BOTH);
		data.horizontalSpan= 2;
		data.heightHint= convertHeightInCharsToPixels(5);
		control.setLayoutData(data);

		return viewer;
	}

	/**
	 * Creates, configures and returns a source viewer to present the template
	 * pattern on the preference page. Clients may override to provide a custom
	 * source viewer featuring e.g. syntax coloring.
	 *
	 * @param parent the parent control
	 * @return a configured source viewer
	 */
	protected SourceViewer createViewer(Composite parent) {
		SourceViewer viewer= new SourceViewer(parent, null, null, false, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		SourceViewerConfiguration configuration= new SourceViewerConfiguration();
		viewer.configure(configuration);
		IDocument document= new Document();
		viewer.setDocument(document);
		return viewer;
	}

	/**
	 * Return the grid data for the button.
	 *
	 * @param button the button
	 * @return the grid data
	 */
	private static GridData getButtonGridData(Button button) {
		GridData data= new GridData(GridData.FILL_HORIZONTAL);
		// TODO replace SWTUtil
//		data.widthHint= SWTUtil.getButtonWidthHint(button);
//		data.heightHint= SWTUtil.getButtonHeightHint(button);

		return data;
	}

	private void selectionChanged1() {
		updateViewerInput();
		updateButtons();
	}

	/**
	 * Updates the pattern viewer.
	 */
	protected void updateViewerInput() {
		IStructuredSelection selection= (IStructuredSelection) fTableViewer.getSelection();

		if (selection.size() == 1) {
			TemplatePersistenceData data= (TemplatePersistenceData) selection.getFirstElement();
			Template template= data.getTemplate();
			fPatternViewer.getDocument().set(template.getPattern());
		} else {
			fPatternViewer.getDocument().set(""); //$NON-NLS-1$
		}
	}

	/**
	 * Updates the buttons.
	 */
	protected void updateButtons() {
		IStructuredSelection selection= (IStructuredSelection) fTableViewer.getSelection();
		int selectionCount= selection.size();
		int itemCount= fTableViewer.getTable().getItemCount();
		boolean canRestore= fTemplateStore.getTemplateData(true).length != fTemplateStore.getTemplateData(false).length;
		boolean canRevert= false;
		for (Iterator it= selection.iterator(); it.hasNext();) {
			TemplatePersistenceData data= (TemplatePersistenceData) it.next();
			if (data.isModified()) {
				canRevert= true;
				break;
			}
		}

		fEditButton.setEnabled(selectionCount == 1);
		fExportButton.setEnabled(selectionCount > 0);
		fRemoveButton.setEnabled(selectionCount > 0 && selectionCount <= itemCount);
		fRestoreButton.setEnabled(canRestore);
		fRevertButton.setEnabled(canRevert);
	}

	private void add() {}

	

	/*
	 * @see Control#setVisible(boolean)
	 */
	public void setVisible(boolean visible) {
		super.setVisible(visible);
	}

	/*
	 * @see PreferencePage#performDefaults()
	 */
	protected void performDefaults() {
		if (isShowFormatterSetting()) {
			IPreferenceStore prefs= getPreferenceStore();
//			fFormatButton.setSelection(prefs.getDefaultBoolean(getFormatterPreferenceKey()));
		}

		fTemplateStore.restoreDefaults(false);

		// refresh
		fTableViewer.refresh();
		fTableViewer.setAllChecked(false);
		fTableViewer.setCheckedElements(getEnabledTemplates());
	}

	/*
	 * @see PreferencePage#performOk()
	 */
	public boolean performOk() {
		if (isShowFormatterSetting()) {
			IPreferenceStore prefs= getPreferenceStore();
//			prefs.setValue(getFormatterPreferenceKey(), fFormatButton.getSelection());
		}

		try {
			fTemplateStore.save();
		} catch (IOException e) {
			openWriteErrorDialog(e);
		}

		return super.performOk();
	}


	/*
	 * @see PreferencePage#performCancel()
	 */
	public boolean performCancel() {
		try {
			fTemplateStore.load();
		} catch (IOException e) {
			openReadErrorDialog(e);
			return false;
		}
		return super.performCancel();
	}

	/*
	 * @since 3.2
	 */
	private void openReadErrorDialog(IOException ex) {
		IStatus status= new Status(IStatus.ERROR, AuroraPlugin.PLUGIN_ID, IStatus.OK, "Failed to read templates.", ex); //$NON-NLS-1$
		AuroraPlugin.getDefault().getLog().log(status);
		String title= ".TemplatePreferencePage_error_read_title";
		String message= "TemplatesMessages.TemplatePreferencePage_error_read_message";
		MessageDialog.openError(getShell(), title, message);
	}

	/*
	 * @since 3.2
	 */
	private void openWriteErrorDialog(IOException ex) {
		IStatus status= new Status(IStatus.ERROR, AuroraPlugin.PLUGIN_ID, IStatus.OK, "Failed to write templates.", ex); //$NON-NLS-1$
		AuroraPlugin.getDefault().getLog().log(status);
		String title= "TemplatePreferencePage_error_write_title";
		String message= "TemplatePreferencePage_error_write_message";
		MessageDialog.openError(getShell(), title, message);
	}

	protected SourceViewer getViewer() {
		return fPatternViewer;
	}

	protected TableViewer getTableViewer() {
		return fTableViewer;
	}
}