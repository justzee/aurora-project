package aurora.bpmn.designer.rcp.viewer;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.bpmn2.modeler.ui.Bpmn2DiagramEditorInput;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.emf.common.util.URI;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.util.SafeRunnable;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import aurora.bpmn.designer.rcp.action.TestBPMN;
import aurora.bpmn.designer.ws.BPMNDefineModel;
import aurora.bpmn.designer.ws.ServiceModel;
import aurora.ide.designer.editor.AuroraBpmnEditor;
import aurora.ide.designer.editor.BPMServiceInputStreamEditorInput;

public class BPMServiceViewer extends ViewPart {
	public static final String ID = "aurora.bpmn.designer.rcp.viewer.BPMServiceViewer";
	private TreeViewer viewer;

	class TreeObject {
		private String name;
		private TreeParent parent;

		public TreeObject(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public void setParent(TreeParent parent) {
			this.parent = parent;
		}

		public TreeParent getParent() {
			return parent;
		}

		public String toString() {
			return getName();
		}
	}

	class TreeParent extends TreeObject {
		private ArrayList children;

		public TreeParent(String name) {
			super(name);
			children = new ArrayList();
		}

		public void addChild(TreeObject child) {
			children.add(child);
			child.setParent(this);
		}

		public void removeChild(TreeObject child) {
			children.remove(child);
			child.setParent(null);
		}

		public TreeObject[] getChildren() {
			return (TreeObject[]) children.toArray(new TreeObject[children
					.size()]);
		}

		public boolean hasChildren() {
			return children.size() > 0;
		}
	}

	/**
	 * We will set up a dummy model to initialize tree heararchy. In real code,
	 * you will connect to a real model and expose its hierarchy.
	 */
	private TreeObject createDummyModel() {
		TreeObject to1 = new TreeObject("报销申请");
		TreeObject to2 = new TreeObject("休假申请");
		TreeObject to3 = new TreeObject("预算申请");
		TreeParent p1 = new TreeParent("HEC");
		p1.addChild(to1);
		p1.addChild(to2);
		p1.addChild(to3);

		TreeObject to4 = new TreeObject("某某申请");
		TreeParent p2 = new TreeParent("SRM");
		p2.addChild(to4);

		TreeParent root = new TreeParent("");
		root.addChild(p1);
		root.addChild(p2);
		return root;
	}

	class ViewContentProvider implements IStructuredContentProvider,
			ITreeContentProvider {

		public void inputChanged(Viewer v, Object oldInput, Object newInput) {
		}

		public void dispose() {
		}

		public Object[] getElements(Object parent) {
			if (parent instanceof ServiceModel[])
				return (ServiceModel[]) parent;
			return null;
		}

		public Object getParent(Object child) {
			if (child instanceof BPMNDefineModel) {
				return ((BPMNDefineModel) child).getServiceModel();
			}
			return null;
		}

		public Object[] getChildren(Object parent) {
			if (parent instanceof ServiceModel) {
				List<BPMNDefineModel> defines = ((ServiceModel) parent)
						.getDefines();
				return defines.toArray(new BPMNDefineModel[defines.size()]);
			}
			return new Object[0];
		}

		public boolean hasChildren(Object parent) {
			if (parent instanceof ServiceModel) {
				return ((ServiceModel) parent).getDefines().isEmpty() == false;
			}
			return false;
		}
	}

	class ViewLabelProvider extends LabelProvider {

		public String getText(Object obj) {
			if (obj instanceof ServiceModel)
				return ((ServiceModel) obj).getServiceName();
			else if (obj instanceof BPMNDefineModel) {
				return ((BPMNDefineModel) obj).getName();
			}
			return obj.toString();
		}

		public Image getImage(Object obj) {
			String imageKey = ISharedImages.IMG_OBJ_ELEMENT;
			if (obj instanceof ServiceModel)
				imageKey = ISharedImages.IMG_OBJ_FOLDER;
			return PlatformUI.getWorkbench().getSharedImages()
					.getImage(imageKey);
		}
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(Composite parent) {
		viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL
				| SWT.BORDER);
		viewer.setContentProvider(new ViewContentProvider());
		viewer.setLabelProvider(new ViewLabelProvider());
		viewer.setSorter(new ViewerSorter());
		viewer.setInput(createServiceModel());
		BPMServiceViewMenu menu = new BPMServiceViewMenu(viewer, this);
		menu.initContextMenu();
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(final DoubleClickEvent event) {
				SafeRunner.run(new SafeRunnable() {
					public void run() throws Exception {
						handleDoubleClick(event);
					}

				});
			}
		});
	}

	private ServiceModel[] createServiceModel() {

		return new ServiceModel[] { new ServiceModel() };
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		viewer.getControl().setFocus();
	}

	private void handleDoubleClick(DoubleClickEvent event) {
		// open editor

		try {
			// URI modelUri = URI
			// .createURI("platform:/plugin/aurora.bpmn.designer.rcp/test.bpmn#/0");
			// URI diagramUri = URI
			// .createURI("platform:/plugin/aurora.bpmn.designer.rcp/test.bpmn#/1");
			// Bpmn2DiagramEditorInput input = new Bpmn2DiagramEditorInput(
			// modelUri, diagramUri,
			// "org.eclipse.bpmn2.modeler.ui.diagram.MainBPMNDiagramType");
			// diagramComposite.setInput(new DiagramEditorInput(uri,
			// "org.eclipse.graphiti.examples.tutorial.diagram.TutorialDiagramTypeProvider"));
			this.getSite()
					.getPage()
					.openEditor(
							new BPMServiceInputStreamEditorInput(
									TestBPMN.getStream()), AuroraBpmnEditor.ID,
							true);
		} catch (PartInitException e) {
			MessageDialog.openError(this.getSite().getShell(), "Error",
					"Error opening view:" + e.getMessage());
		}

	}

}