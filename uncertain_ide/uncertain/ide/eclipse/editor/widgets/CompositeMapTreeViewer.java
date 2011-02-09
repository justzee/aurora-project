package uncertain.ide.eclipse.editor.widgets;

import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ViewForm;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceAdapter;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ResourceTransfer;

import uncertain.composite.CompositeMap;
import uncertain.ide.Activator;
import uncertain.ide.eclipse.action.ActionListener;
import uncertain.ide.eclipse.action.ActionProperties;
import uncertain.ide.eclipse.action.ActionsFactory;
import uncertain.ide.eclipse.action.CompositeMapAction;
import uncertain.ide.eclipse.action.CopyElementAction;
import uncertain.ide.eclipse.action.DataSetWizard;
import uncertain.ide.eclipse.action.ElementDoubleClickListener;
import uncertain.ide.eclipse.action.PasteAction;
import uncertain.ide.eclipse.action.RefreshAction;
import uncertain.ide.eclipse.action.RemoveElementAction;
import uncertain.ide.eclipse.action.ToolBarAddElementListener;
import uncertain.ide.eclipse.editor.AbstractCMViewer;
import uncertain.ide.eclipse.editor.core.IViewer;
import uncertain.ide.eclipse.editor.widgets.config.ProjectProperties;
import uncertain.ide.util.LoadSchemaManager;
import uncertain.ide.util.LocaleMessage;
import uncertain.schema.Element;
import aurora.ide.AuroraConstant;

public class CompositeMapTreeViewer extends AbstractCMViewer {
	protected TreeViewer treeViewer;
	protected IViewer parentViewer;
	private CompositeMap input;
	public final static String VirtualNode = "VirtualNode";
	public CompositeMapTreeViewer(IViewer parentViewer, CompositeMap data) {
		this.parentViewer = parentViewer;
		this.input = data;
	}

	public void create(Composite parent) {
		ViewForm viewForm = new ViewForm(parent, SWT.NONE);
		viewForm.setLayout(new FillLayout());
		Tree tree = new Tree(viewForm, SWT.NONE);
		treeViewer = new TreeViewer(tree);
		treeViewer.setLabelProvider(new CompositeMapTreeLabelProvider());
		CompositeMap parentData = input.getParent();
		if (parentData == null) {
			parentData = createVirtualParentNode(input);
		}
		treeViewer.setContentProvider(new CompositeMapTreeContentProvider(input));
		treeViewer.setInput(parentData);

		fillContextMenu();
		fillDNDListener();
		fillKeyListener();
		treeViewer.addDoubleClickListener(new ElementDoubleClickListener(this));
		viewForm.setContent(treeViewer.getControl());
		fillElementToolBar(viewForm);
	}

	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		treeViewer.addSelectionChangedListener(listener);
	}

	public Control getControl() {
		return treeViewer.getControl();
	}

	public Object getViewer() {
		return treeViewer;
	}

	public void setSelection(Object data) {
		selectedData = (CompositeMap) data;

	}

	public void refresh() {
		treeViewer.refresh();
	}

	public void setFocus(Object data) {
		focusData = (CompositeMap) data;
	}

	public void setDirty(boolean dirty) {
		parentViewer.refresh(true);

	}

	public void refresh(boolean dirty) {
		if (dirty) {
			parentViewer.refresh(true);
		} else {
			treeViewer.refresh();
		}

	}

	public void setInput(CompositeMap data) {

		CompositeMap parent = data.getParent();
		if (parent == null) {
			parent = createVirtualParentNode(data);
		}
		treeViewer.setContentProvider(new CompositeMapTreeContentProvider(data));
		treeViewer.setInput(parent);
	}

	public CompositeMap getInput() {
		return input;
	}

	public TreeViewer getTreeViewer() {
		return treeViewer;
	}

	public void fillDNDListener() {
		DragSource ds = new DragSource(getControl(), DND.DROP_COPY
				| DND.DROP_MOVE);
		ds.setTransfer(new Transfer[] { LocalSelectionTransfer.getTransfer() });
		ds.addDragListener(new DragSourceAdapter() {
			public void dragSetData(DragSourceEvent event) {
			}
		});

		DropTarget dt = new DropTarget(getControl(), DND.DROP_COPY
				| DND.DROP_MOVE);
		final LocalSelectionTransfer localSelectionTransfer = LocalSelectionTransfer
				.getTransfer();
		final ResourceTransfer resourceTransfer = ResourceTransfer
				.getInstance();
		dt.setTransfer(new Transfer[] { localSelectionTransfer,
				resourceTransfer });
		dt.addDropListener(new DropTargetAdapter() {
			public void dragEnter(DropTargetEvent event) {
				if (event.detail == DND.DROP_DEFAULT) {
					if ((event.operations & DND.DROP_COPY) != 0) {
						event.detail = DND.DROP_COPY;
					} else {
						event.detail = DND.DROP_NONE;
					}
				}
				for (int i = 0; i < event.dataTypes.length; i++) {
					if (resourceTransfer.isSupportedType(event.dataTypes[i])) {
						event.currentDataType = event.dataTypes[i];
						// files should only be copied
						if (event.detail != DND.DROP_COPY) {
							event.detail = DND.DROP_NONE;
						}
						break;
					}
				}
			}

			public void drop(DropTargetEvent event) {
				if (resourceTransfer.isSupportedType(event.currentDataType)) {
					Object data = event.data;
					if (data != null) {
						Element element = LoadSchemaManager.getSchemaManager()
								.getElement(getInput());
						if (element == null|| !element.getQName().equals(AuroraConstant.ScreenQN)) {
							CustomDialog.showErrorMessageBox("this.is.not.screen.file");
							return;
						}
						IResource[] resources = (IResource[]) data;
						String bmfile_dir = null;
						String bmFiles = "";
						try {
							bmfile_dir = getFullPath();
						} catch (Exception e) {
							CustomDialog.showExceptionMessageBox(e);
						}
						for (int i = 0; i < resources.length; i++) {
							IResource resource = resources[i];
							String filePath = resource.getLocation()
									.toOSString();
							if (!filePath.toLowerCase().endsWith(AuroraConstant.BMFileExtension)) {
								continue;
							}
							String className = getClassName(new java.io.File(
									filePath), bmfile_dir);
							bmFiles = bmFiles + className + ",";
						}
						CompositeMap view = input.getChild(AuroraConstant.ViewQN.getLocalName());
						if (view == null) {
							String prefix = CompositeMapAction.getContextPrefix(input,AuroraConstant.ViewQN);
							view = new CompositeMap(prefix,AuroraConstant.ViewQN.getNameSpace(),AuroraConstant.ViewQN.getLocalName());
							view.setParent(input);
							input.addChild(view);
						}
						CompositeMap dataSets = view.getChild(AuroraConstant.DataSetSQN.getLocalName());
						if (dataSets == null) {
							String prefix = CompositeMapAction.getContextPrefix(input,AuroraConstant.DataSetSQN);
							dataSets = new CompositeMap(prefix,AuroraConstant.DataSetSQN.getNameSpace(),AuroraConstant.DataSetSQN.getLocalName());
							view.addChild(dataSets);
							dataSets.setParent(view);
						}
						DataSetWizard wizard = new DataSetWizard(
								dataSets, bmFiles);
						WizardDialog dialog = new WizardDialog(new Shell(),
								wizard);
						if(dialog.open()==Window.OK)
							refresh(true);
					}

				} else if (localSelectionTransfer
						.isSupportedType(event.currentDataType)) {

					CompositeMap sourceCm = focusData;
					if (sourceCm == null)
						return;
					CompositeMap objectCm = (CompositeMap) event.item.getData();
					if (objectCm == null)
						return;
					if (objectCm.equals(sourceCm)
							&& objectCm.toXML().equals(sourceCm.toXML())) {
						return;
					}
					if (!CompositeMapAction.validNextNodeLegalWithAction(
							objectCm, sourceCm)) {
						return;
					}
					CompositeMap childCm = new CompositeMap(sourceCm);

					if (childCm != null) {
						objectCm.addChild(childCm);
						if (sourceCm.getParent() != null)
							sourceCm.getParent().removeChild(sourceCm);
					}
					refresh(true);
				}
			}
		});
	}

	private String getClassName(File file, String fullpath) {
		String path = file.getPath();
		int end = path.indexOf(".");
		path = path.substring(fullpath.length() + 1, end);
		path = path.replace(File.separatorChar, '.');
		return path;
	}

	private String getFullPath() throws Exception {
		IEditorInput input = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage().getActiveEditor()
				.getEditorInput();
		IFile ifile = ((IFileEditorInput) input).getFile();
		IProject project = ifile.getProject();
		String bmFilesDir = ProjectProperties.getBMBaseDir(project);
		java.io.File baseDir = new java.io.File(bmFilesDir);
		String fullPath = baseDir.getAbsolutePath();
		return fullPath;
	}

	public MenuManager addChildElements() {
		String text = LocaleMessage.getString("add.element.label");
		ImageDescriptor imageDes = Activator.getImageDescriptor(LocaleMessage.getString("add.icon"));
		MenuManager childElementMenus = new MenuManager(text,imageDes,null);
		final CompositeMap comp = focusData;
		
		ActionProperties actionProperties = new ActionProperties(this,comp);
		try {
			ActionsFactory.getInstance().addActionsToMenuManager(childElementMenus, actionProperties);
		} catch (Exception e) {
			CustomDialog.showExceptionMessageBox(e);
		}
		return childElementMenus;
	}

	public void fillContextMenu() {
		MenuManager mgr = new MenuManager("#PopupMenu");
		MenuManager menuManager = (MenuManager) mgr;
		mgr.setRemoveAllWhenShown(true);

		menuManager.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				manager.add(new Separator(
						IWorkbenchActionConstants.MB_ADDITIONS));
				MenuManager childElements = addChildElements();
				manager.add(childElements);
				manager.add(new CopyElementAction(CompositeMapTreeViewer.this,
						CopyElementAction.getDefaultImageDescriptor(),
						CopyElementAction.getDefaultText()));
				manager.add(new PasteAction(CompositeMapTreeViewer.this,ActionListener.DefaultImage|ActionListener.DefaultTitle));
				manager.add(new RemoveElementAction(CompositeMapTreeViewer.this,ActionListener.DefaultImage|ActionListener.DefaultTitle));
				manager.add(new RefreshAction(CompositeMapTreeViewer.this,ActionListener.DefaultImage|ActionListener.DefaultTitle));
			}
		});

		Menu menu = menuManager.createContextMenu(getControl());
		// Menu menu = menuManager.createMenuBar(mColumnViewer.)
		getControl().setMenu(menu);

	}

	public void fillKeyListener() {
		treeViewer.getTree().addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent e) {
				if (e.stateMask == SWT.CTRL && e.keyCode == 'c') {
					copyElement();
				} else if (e.stateMask == SWT.CTRL && e.keyCode == 'v') {
					pasteElement();
				} else if (e.keyCode == SWT.DEL) {
					removeElement();
				}
			}

			public void keyReleased(KeyEvent e) {
			}
		});

	}

	private void fillElementToolBar(Composite shell) {

		ToolBar toolBar = new ToolBar(shell, SWT.RIGHT | SWT.FLAT);
		Menu menu = new Menu(shell);

		ToolItem addItem = new ToolItem(toolBar, SWT.DROP_DOWN);
		setToolItemShowProperty(addItem, LocaleMessage
				.getString("add.element.label"), LocaleMessage
				.getString("add.icon"));
		addItem.addListener(SWT.Selection, new ToolBarAddElementListener(
				toolBar, menu, addItem, this));

		final ToolItem cutItem = new ToolItem(toolBar, SWT.PUSH);
		setToolItemShowProperty(cutItem, LocaleMessage.getString("cut"),
				LocaleMessage.getString("cut.icon"));
		cutItem.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				cutElement();
			}
		});

		final ToolItem copyItem = new ToolItem(toolBar, SWT.PUSH);
		setToolItemShowProperty(copyItem, LocaleMessage.getString("copy"),
				LocaleMessage.getString("copy.icon"));
		copyItem.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				copyElement();
			}
		});

		final ToolItem pasteItem = new ToolItem(toolBar, SWT.PUSH);
		setToolItemShowProperty(pasteItem, LocaleMessage.getString("paste"),
				LocaleMessage.getString("paste.icon"));
		pasteItem.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				pasteElement();
			}
		});
		final ToolItem refreshItem = new ToolItem(toolBar, SWT.PUSH);
		setToolItemShowProperty(refreshItem,
				LocaleMessage.getString("refresh"), LocaleMessage
						.getString("refresh.icon"));
		refreshItem.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				treeViewer.refresh();
				LoadSchemaManager.refeshSchemaManager();
			}
		});
		final ToolItem removeItem = new ToolItem(toolBar, SWT.PUSH);
		setToolItemShowProperty(removeItem, LocaleMessage.getString("delete"),
				LocaleMessage.getString("delete.icon"));
		removeItem.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				removeElement();

			}
		});
		toolBar.pack();
		((ViewForm) shell).setTopLeft(toolBar);
	}

	private void setToolItemShowProperty(ToolItem toolItem, String text,
			String iconPath) {
		if (text != null && !text.equals(""))
			toolItem.setToolTipText(text);
		if (iconPath != null && !iconPath.equals("")) {
			Image icon = Activator.getImageDescriptor(iconPath).createImage();
			toolItem.setImage(icon);
		}

	}
	private CompositeMap createVirtualParentNode(CompositeMap node){
		if(node == null)
			return null;
		CompositeMap parentNode = node.getParent();
		if (parentNode != null)
			return parentNode;
		CompositeMap virtualNode = new CompositeMap(VirtualNode);
		virtualNode.addChild(node);
		return virtualNode;
	}
}
