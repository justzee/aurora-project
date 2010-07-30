package uncertain.ide.eclipse.editor;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import uncertain.composite.CompositeMap;
import uncertain.ide.LoadSchemaManager;
import uncertain.ide.LocaleMessage;
import uncertain.ide.eclipse.editor.textpage.JavaScriptLineStyler;
import uncertain.ide.eclipse.editor.widgets.CompositeMapTreeViewer;
import uncertain.ide.eclipse.editor.widgets.PropertyGridViewer;
import uncertain.ide.eclipse.editor.widgets.PropertyHashViewer;
import uncertain.schema.Element;

public class BaseCompositeMapViewer implements IViewer {

	private CompositeMapTreeViewer treeViewer;
	private PropertySection propertySection;
	private CompositeMap data;
	IViewer viewer;
	SashForm control;
	public BaseCompositeMapViewer(IViewer viewer, CompositeMap data) {
		this.viewer = viewer;
		this.data = data;
	}

	public void createFormContent(Composite parent) {
		parent.setLayout(new FillLayout());
		control = new SashForm(parent, SWT.NONE);
		createElementContent(control);
		createPropertyContent(control);
		treeViewer.addSelectionChangedListener(new ElementSelectionListener());
		control.setWeights(new int[] { 40, 60 });
	}

	private void createElementContent(Composite mContent) {
		treeViewer = new CompositeMapTreeViewer(this, data);
		treeViewer.create(mContent);

	}
	protected void createPropertyContent(Composite mContent) {
		propertySection = new PropertySection(this);
		propertySection.create(mContent);

	}

	public void refresh(CompositeMap data) {
		this.data = data;
		treeViewer.setInput(data);
	}

	public CompositeMap getData() {
		return data;
	}

	public void setData(CompositeMap data) {
		this.data = data;
	}

	public TreeViewer getTreeViewer() {
		return treeViewer.getTreeViewer();
	}

	public CompositeMap getSelection() {
		TreeSelection selection = (TreeSelection) treeViewer.getTreeViewer()
				.getSelection();
		Object selected = selection.getFirstElement();
		if (selected == null)
			return null;
		CompositeMap data = (CompositeMap) selected;
		return data;
	}

	public CompositeMap getContent() {
		return data;

	}
	public Control getControl(){
		return control;
	}

	public String getFullContent() {
		String encoding = "UTF-8";
		String xml_decl = "<?xml version=\"1.0\" encoding=\"" + encoding
				+ "\"?>\n";
		return xml_decl + data.toXML();
	}

	public void setContent(CompositeMap content) {
		this.data = content;
		treeViewer.setInput(data);
	}

	public void refresh(boolean isDirty) {
		if(isDirty){
			viewer.refresh(isDirty);
		}
		else{
			treeViewer.refresh();
			propertySection.refresh(false);
		}
	}

	class PropertySection implements IViewer{
		private CTabFolder mTabFolder;
		private PropertyHashViewer mPropertyEditor;
		private PropertyGridViewer mPropertyArrayEditor;
		private StyledText mInnerText;
		private JavaScriptLineStyler lineStyler = new JavaScriptLineStyler();

		IViewer viewer;

		PropertySection(IViewer viewer) {
			this.viewer = viewer;
		}
		public void create(Composite parent) {

			createTabFolder(parent);
			createPropertyHashTab(mTabFolder);
			createPropertyGridTab(mTabFolder);
			createTextTab(mTabFolder);

		}
		private void createPropertyHashTab(Composite parent) {
			mPropertyEditor = new PropertyHashViewer(viewer, parent);
			mPropertyEditor.createEditor();
			mTabFolder.getItem(0).setControl(mPropertyEditor.getControl());
		}
		private void createPropertyGridTab(Composite parent) {
			mPropertyArrayEditor = new PropertyGridViewer(viewer);
			mPropertyArrayEditor.createEditor(parent);
			mTabFolder.getItem(1).setControl(mPropertyArrayEditor.viewForm);
		}
		private void createTextTab(Composite parent) {
			mInnerText = new StyledText(parent, SWT.MULTI | SWT.V_SCROLL
					| SWT.H_SCROLL);
			GridData spec = new GridData();
			spec.horizontalAlignment = GridData.FILL;
			spec.grabExcessHorizontalSpace = true;
			spec.verticalAlignment = GridData.FILL;
			spec.grabExcessVerticalSpace = true;
			mInnerText.setLayoutData(spec);
			mInnerText.addLineStyleListener(lineStyler);
			mInnerText.setFont(new Font(mTabFolder.getDisplay(), "Courier New", 10,
					SWT.NORMAL));
			
			mInnerText.addFocusListener(new FocusListener() {

				public void focusGained(FocusEvent e) {
				}

				public void focusLost(FocusEvent e) {

					String newText = mInnerText.getText();
					if (newText == null) {
						newText = "";
					}
					newText = newText.trim();
					String oldText = ((CompositeMap) treeViewer.getFocus())
							.getText();
					if (oldText == null) {
						oldText = "";
					}
					oldText = oldText.trim();
					if (!newText.equals(oldText)) {
						((CompositeMap) treeViewer.getFocus()).setText(newText);
						refresh(true);
					}

				}

			});
			mTabFolder.getItem(2).setControl(mInnerText);
		}
		public void setInput(CompositeMap data){
			Element em = LoadSchemaManager.getSchemaManager().getElement(data);
			if (em != null && em.isArray()) {
				mPropertyArrayEditor.createEditor(mTabFolder, data);
				mTabFolder.getItem(1).setControl(
						mPropertyArrayEditor.getControl());
				mTabFolder.setSelection(1);
				mTabFolder.layout(true);

			} else {
				mPropertyEditor.setData(data);
				mTabFolder.setSelection(0);
				mTabFolder.layout(true);

			}
			String a = data.getText();
			if (a != null && !a.trim().equals("")) {
				mInnerText.setText(data.getText());
				lineStyler.parseBlockComments(data.getText());
				mTabFolder.setSelection(2);
				mTabFolder.layout(true);
			} else {
				mInnerText.setText("");
			}
		}
		
		public void clear() throws Exception{
			mPropertyEditor.clear();
			mPropertyArrayEditor.clearAll();
		}
		public void refresh(boolean isDirty) {
			if(isDirty)
				viewer.refresh(isDirty);
			else{	
				mPropertyEditor.refresh();
				mPropertyArrayEditor.refresh(false);
			}
		}
		private void createTabFolder(final Composite parent) {
			mTabFolder = new CTabFolder(parent, SWT.TOP);
			mTabFolder.setMaximizeVisible(true);
			mTabFolder.setBorderVisible(true);
			mTabFolder.setSimple(false);
			mTabFolder.setTabHeight(23);

			CTabItem tabItem1 = new CTabItem(mTabFolder, SWT.None | SWT.MULTI
					| SWT.V_SCROLL);
			String tab = "         ";
			tabItem1.setText(tab + LocaleMessage.getString("property.name") + tab);

			CTabItem tabItem2 = new CTabItem(mTabFolder, SWT.None | SWT.MULTI
					| SWT.V_SCROLL);
			tabItem2.setText(tab + LocaleMessage.getString("child.list") + tab);

			CTabItem tabItem3 = new CTabItem(mTabFolder, SWT.None | SWT.MULTI
					| SWT.V_SCROLL);
			tabItem3.setText(tab + LocaleMessage.getString("value") + tab);

//			CTabItem tabItem4 = new CTabItem(mTabFolder, SWT.None | SWT.MULTI
//					| SWT.V_SCROLL);
//			tabItem4.setText(tab + LocaleMessage.getString("editor") + tab);
/*			mTabFolder.addCTabFolder2Listener(new CTabFolder2Adapter() {
				public void minimize(CTabFolderEvent event) {
					mTabFolder.setMinimized(true);
					mTabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
							false));
					parent.layout(true);
				}

				public void maximize(CTabFolderEvent event) {
					mTabFolder.setMaximized(true);
					sashForm.setMaximizedControl(mTabFolder);
					parent.layout(true);
				}

				public void restore(CTabFolderEvent event) {
					mTabFolder.setMaximized(false);
					sashForm.setMaximizedControl(null);
					parent.layout(true);
				}
			});*/
		}
	}

	class ElementSelectionListener implements ISelectionChangedListener {
		private boolean validError = false;
		public void selectionChanged(SelectionChangedEvent event) {
			if (validError) {
				validError = false;
				return;
			}
			try {
				propertySection.clear();
			} catch (Exception e) {
				validError = true;
				treeViewer.getTreeViewer().setSelection(
						new StructuredSelection(treeViewer.getFocus()));
				return;
			}
			TreeSelection selection = (TreeSelection) event.getSelection();
			CompositeMap data = (CompositeMap) selection.getFirstElement();
			if (data == null)
				return;
			treeViewer.setFocus(data);
			propertySection.setInput(data);
		}

	}

}