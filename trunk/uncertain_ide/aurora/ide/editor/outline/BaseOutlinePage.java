package aurora.ide.editor.outline;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.IPageBookViewPage;
import org.eclipse.ui.part.Page;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

import aurora.ide.editor.textpage.TextPage;

public class BaseOutlinePage extends Page implements IContentOutlinePage {

	private IContentOutlinePage activePage;
	private IEditorPart activeEditor;
	private Composite control;

	/**
	 * Set the active editor. This outline page shows the outline which is
	 * provided by given editor.
	 * 
	 * @param editor
	 *            the active editor
	 */
	public void setActiveEditor(IEditorPart editor) {
		if (activePage != null && activePage.getControl() != null) {
			activePage.getControl().dispose();
		}
		if (editor instanceof TextPage) {
			this.activeEditor = editor;
			activePage = (IContentOutlinePage) editor.getAdapter(IContentOutlinePage.class);
			if (control != null && activePage != null) {
				initActivePage();
				activePage.createControl(control);
				getSite().getActionBars().updateActionBars();
				control.layout();
			}
		}
	}

	public void createControl(Composite parent) {
		control = new Composite(parent, SWT.NULL);
		control.setLayout(new FillLayout());
		if (activePage != null) {
			initActivePage();
			activePage.createControl(control);
		}
	}

	private void initActivePage() {
		getSite().getActionBars().getToolBarManager().removeAll();
		getSite().getActionBars().getMenuManager().removeAll();

		if (activePage instanceof IPageBookViewPage) {
			IPageBookViewPage pageBook = (IPageBookViewPage) activePage;
			if (pageBook.getSite() == null) {
				try {
					pageBook.init(getSite());
				} catch (PartInitException ex) {
					ex.printStackTrace();
				}
			}
		}
	}

	public Control getControl() {
		/*
		 * if(activePage!=null){ activePage.getControl(); }
		 */
		return control;
	}

	public void setFocus() {
		if (activePage != null && activePage.getControl() != null && (!activePage.getControl().isDisposed())) {
			activePage.setFocus();
		}
	}

	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		if (activePage != null) {
			activePage.addSelectionChangedListener(listener);
		}
	}

	public ISelection getSelection() {
		if (activePage != null) {
			return activePage.getSelection();
		}
		return null;
	}

	public void removeSelectionChangedListener(ISelectionChangedListener listener) {
		if (activePage != null) {
			activePage.removeSelectionChangedListener(listener);
		}
	}

	public void setSelection(ISelection selection) {
		if (activePage != null) {
			activePage.setSelection(selection);
		}
	}

	public IContentOutlinePage getActivePage() {
		return activePage;
	}

	public IEditorPart getActiveEditor() {
		return activeEditor;
	}

}
