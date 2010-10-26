package uncertain.ide.eclipse.editor.widgets;

import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.TreeViewer;
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
import uncertain.composite.QualifiedName;
import uncertain.ide.Activator;
import uncertain.ide.LoadSchemaManager;
import uncertain.ide.LocaleMessage;
import uncertain.ide.eclipse.action.ActionProperties;
import uncertain.ide.eclipse.action.ActionsFactory;
import uncertain.ide.eclipse.action.CompositeMapAction;
import uncertain.ide.eclipse.action.CopyElementAction;
import uncertain.ide.eclipse.action.ElementDoubleClickListener;
import uncertain.ide.eclipse.action.PasteAction;
import uncertain.ide.eclipse.action.RefreshAction;
import uncertain.ide.eclipse.action.RemoveElementAction;
import uncertain.ide.eclipse.action.ToolBarAddElementListener;
import uncertain.ide.eclipse.editor.AbstractCMViewer;
import uncertain.ide.eclipse.editor.IViewer;
import uncertain.ide.eclipse.wizards.DataSetWizard;
import uncertain.ide.eclipse.wizards.ProjectProperties;
import uncertain.schema.Element;

public class CompositeMapTreeViewer extends AbstractCMViewer {
	protected TreeViewer mTreeViewer;
	protected IViewer mParent;
	private CompositeMap mData;

	public CompositeMapTreeViewer(IViewer parent, CompositeMap data) {
		this.mParent = parent;
		this.mData = data;
	}

	public void create(Composite parent) {
		ViewForm viewForm = new ViewForm(parent, SWT.NONE);
		viewForm.setLayout(new FillLayout());

		Tree tree = new Tree(viewForm, SWT.NONE);

		mTreeViewer = new TreeViewer(tree);
		mTreeViewer.setLabelProvider(new CompositeMapTreeLabelProvider());
		
		CompositeMap parentData = mData.getParent();

		if (parentData == null) {
			CompositeMap root = new CompositeMap("root");
			root.addChild(mData);
			parentData = root;
		}
		mTreeViewer.setContentProvider(new CompositeMapTreeContentProvider(
				mData));
		
		mTreeViewer.setInput(parentData);
		


		fillContextMenu();
		fillDNDListener();
		fillKeyListener();
		mTreeViewer
				.addDoubleClickListener(new ElementDoubleClickListener(this));
		viewForm.setContent(mTreeViewer.getControl());
		fillElementToolBar(viewForm);
	}

	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		mTreeViewer.addSelectionChangedListener(listener);
	}

	public Control getControl() {
		return mTreeViewer.getControl();
	}

	public Object getViewer() {
		return mTreeViewer;
	}

	public void setSelection(Object data) {
		selectedData = (CompositeMap) data;

	}

	public void refresh() {
		mTreeViewer.refresh();
	}

	public void setFocus(Object data) {
		focusData = (CompositeMap) data;
	}

	public void setDirty(boolean dirty) {
		mParent.refresh(true);

	}

	public void refresh(boolean dirty) {
		if (dirty) {
			mParent.refresh(true);
		} else {
			mTreeViewer.refresh();
		}

	}

	public void setInput(CompositeMap data) {

		CompositeMap parent = data.getParent();

		if (parent == null) {
			CompositeMap root = new CompositeMap("root");
			root.addChild(data);
			parent = root;
		}
		
		mTreeViewer
		.setContentProvider(new CompositeMapTreeContentProvider(data));
		mTreeViewer.setInput(parent);
//		mTreeViewer.setInput(data);
	}

	public CompositeMap getInput() {
		return mData;
	}

	public TreeViewer getTreeViewer() {
		return mTreeViewer;
	}

	public void fillDNDListener() {
		final QualifiedName screenQN = new QualifiedName(
				"http://www.aurora-framework.org/application", "screen");

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
						if (element == null
								|| !element.getQName().equals(screenQN)) {
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
							if (!filePath.toLowerCase().endsWith("bm")) {
								continue;
							}
							String className = getClassName(new java.io.File(
									filePath), bmfile_dir);
							bmFiles = bmFiles + className + ",";
						}
						CompositeMap view = mData.getChild("view");
						if (view == null) {
							QualifiedName QName = new QualifiedName(
									"http://www.aurora-framework.org/application",
									"view");
							String prefix = CompositeMapAction.getContextPrefix(mData,
									QName);
							view = new CompositeMap(
									prefix,
									"http://www.aurora-framework.org/application",
									"dataSets");
							view.setParent(mData);
							mData.addChild(view);
						}
						CompositeMap dataSets = view.getChild("dataSets");
						if (dataSets == null) {
							QualifiedName QName = new QualifiedName(
									"http://www.aurora-framework.org/application",
									"dataSets");
							String prefix = CompositeMapAction.getContextPrefix(mData,
									QName);
							dataSets = new CompositeMap(
									prefix,
									"http://www.aurora-framework.org/application",
									"dataSets");
							dataSets.setParent(view);
						}
						DataSetWizard wizard = new DataSetWizard(
								dataSets, bmFiles);
						WizardDialog dialog = new WizardDialog(new Shell(),
								wizard);
						dialog.open();
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
		MenuManager childElementMenus = new MenuManager(LocaleMessage
				.getString("add.element.label"));
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
				manager.add(new PasteAction(CompositeMapTreeViewer.this,
						PasteAction.getDefaultImageDescriptor(), PasteAction
								.getDefaultText()));
				manager.add(new RemoveElementAction(
						CompositeMapTreeViewer.this, RemoveElementAction
								.getDefaultImageDescriptor(),
						RemoveElementAction.getDefaultText()));
				manager.add(new RefreshAction(CompositeMapTreeViewer.this,
						RefreshAction.getDefaultImageDescriptor(),
						LocaleMessage.getString("refresh")));
			}
		});

		Menu menu = menuManager.createContextMenu(getControl());
		// Menu menu = menuManager.createMenuBar(mColumnViewer.)
		getControl().setMenu(menu);

	}

	public void fillKeyListener() {
		mTreeViewer.getTree().addKeyListener(new KeyListener() {
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
				mTreeViewer.refresh();
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
}
