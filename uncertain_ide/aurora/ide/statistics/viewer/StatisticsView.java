package aurora.ide.statistics.viewer;

import java.sql.Connection;
import java.sql.SQLException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ResourceListSelectionDialog;
import org.eclipse.ui.dialogs.ResourceSelectionDialog;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard;

import aurora.ide.AuroraPlugin;
import aurora.ide.helpers.ApplicationException;
import aurora.ide.statistics.DBManager;
import aurora.ide.statistics.wizard.dialog.LoadDataWizard;
import aurora.ide.statistics.wizard.dialog.SaveDataWizard;
import aurora.statistics.DatabaseAction;
import aurora.statistics.Statistician;
import aurora.statistics.map.StatisticsResult;
import aurora.statistics.model.StatisticsProject;

public class StatisticsView extends ViewPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "aurora.ide.viewer.statistics.StatisticsView";

	static final private String[] pViewColTitles = { "项目名称", " 值 ", "最大值", "最小值", "平均值" };
	static final private String[] pViewColTooltips = { "项目名称", "值或数量", "最大值", "最小值", "平均值" };

	static final private String[] oViewColTitles = { "类别", "文件名", "路径", "文件大小", "脚本大小", "标签数量", "引用次数", "被引用次数" };
	static final private String[] oViewColTooltips = { "类别", "文件名", "路径", "文件大小", "脚本大小", "标签数量", "引用次数", "被引用次数" };

	private TreeViewer projectViewer;
	private TreeViewer objectViewer;

	private Action fileSelectionAction;
	private Action projectSelectionAction;
	private Action doubleClickAction;
	private Action saveToDBAction;
	private Action dbLoadAction;
	private Action dropTable;

	private Statistician statistician;
	private StatisticsResult statisticsResult;

	class NameSorter extends ViewerSorter {
	}

	/**
	 * The constructor.
	 */
	public StatisticsView() {
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(Composite parent) {
		// createViewer(parent); /* Create the example widgets */
		TabFolder tabFolder = new TabFolder(parent, SWT.TOP);

		TabItem item = new TabItem(tabFolder, SWT.NONE);
		item.setText("Objects");
		item.setToolTipText("Project Objects:bm,screen,svc...");
		createObjectViewer(tabFolder);
		item.setControl(objectViewer.getControl());

		item = new TabItem(tabFolder, SWT.NONE);
		item.setText("Project");
		item.setToolTipText("Project Descripttion.");
		createProjectViewer(tabFolder);
		item.setControl(projectViewer.getControl());

		makeActions();
		hookContextMenu();
		hookDoubleClickAction();
		contributeToActionBars();
	}

	private void createObjectViewer(Composite parent) {
		objectViewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		objectViewer.setContentProvider(new ObjectViewContentProvider());
		objectViewer.setLabelProvider(new ObjectViewLabelProvider());
		// objectViewer.setSorter(new NameSorter());
		objectViewer.addTreeListener(new TreeViewerAutoFitListener());
		Tree tree = objectViewer.getTree();
		for (int i = 0; i < oViewColTitles.length; i++) {

			TreeColumn treeColumn = new TreeColumn(tree, SWT.NONE);
			treeColumn.setMoveable(true);
			// treeColumn.setImage(image)
			treeColumn.setResizable(true);
			treeColumn.setText(oViewColTitles[i]);
			treeColumn.setToolTipText(oViewColTooltips[i]);
			treeColumn.pack();
			// treeColumn.setWidth(100);

		}
		tree.setLinesVisible(true);
		tree.setHeaderVisible(true);
		objectViewer.expandAll();
	}

	private void createProjectViewer(Composite parent) {
		projectViewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		projectViewer.setContentProvider(new ProjectViewContentProvider());
		projectViewer.setLabelProvider(new ProjectViewLabelProvider());
		// projectViewer.setSorter(new NameSorter());
		projectViewer.addTreeListener(new TreeViewerAutoFitListener());
		Tree tree = projectViewer.getTree();
		for (int i = 0; i < pViewColTitles.length; i++) {

			TreeColumn treeColumn = new TreeColumn(tree, SWT.NONE);
			treeColumn.setMoveable(true);
			// treeColumn.setImage(image)
			treeColumn.setResizable(true);
			treeColumn.setText(pViewColTitles[i]);
			treeColumn.setToolTipText(pViewColTooltips[i]);
			treeColumn.pack();
			// treeColumn.setWidth(100);

		}
		tree.setLinesVisible(true);
		tree.setHeaderVisible(true);
	}

	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				StatisticsView.this.fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(objectViewer.getControl());
		this.objectViewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, objectViewer);
	}

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalPullDown(IMenuManager manager) {
		manager.add(fileSelectionAction);
		manager.add(new Separator());
		manager.add(projectSelectionAction);
	}

	private void fillContextMenu(IMenuManager manager) {
		manager.add(fileSelectionAction);
		manager.add(projectSelectionAction);
		manager.add(new Separator());
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(fileSelectionAction);
		manager.add(projectSelectionAction);
		manager.add(saveToDBAction);
		manager.add(dbLoadAction);
		manager.add(dropTable);
		manager.add(new Separator());
	}

	public void setInput(final StatisticsResult statisticsResult, Statistician statistician) {
		this.statistician = statistician;
		// this.statisticsResult = statisticsResult;
		this.getSite().getShell().getDisplay().asyncExec(new Runnable() {
			public void run() {
				objectViewer.setInput(statisticsResult);
				objectViewer.expandAll();
				TreeViewerAutoFitListener.packColumns(objectViewer);

				projectViewer.setInput(statisticsResult);
				projectViewer.expandAll();
				TreeViewerAutoFitListener.packColumns(projectViewer);
			}
		});
	}

	private void makeActions() {
		saveToDBAction = new Action() {
			public void run() {
				if (statistician == null || StatisticsProject.NONE_PROJECT.equals(statistician.getProject())) {
					showMessage("工程不存在，无法保存");
					return;
				}
				SaveDataWizard wizard = new SaveDataWizard(statistician);
				WizardDialog dialog = new WizardDialog(getSite().getShell(), wizard);
				if (WizardDialog.OK == dialog.open()) {
					statistician.setProject(wizard.getProject());
					SaveToDBJob job = new SaveToDBJob(statistician);
					job.setUser(true);
					job.schedule();
				}
			}
		};
		saveToDBAction.setToolTipText("保存到数据库");

		dbLoadAction = new Action() {
			public void run() {
				// TODO 选择，IProject关联的数据库设置，搜索所有保存的项目，然后根据选择进行加载。
				LoadDataWizard wizard = new LoadDataWizard();
				WizardDialog dialog = new WizardDialog(getSite().getShell(), wizard);
				if (WizardDialog.OK == dialog.open()) {
					LoadFromDBJob job = new LoadFromDBJob(wizard.getProject(), statistician, wizard.getStatisticsProject(), StatisticsView.this);
					job.setUser(true);
					job.schedule();
				}
			}
		};
		dbLoadAction.setToolTipText("从数据库读出");

		dropTable = new Action() {
			public void run() {
				IProject project = AuroraPlugin.getWorkspace().getRoot().getProject("hec2.0");
				DBManager dm = new DBManager(project);
				Connection connection = null;

				// TODO 未完 待续。。。。
				try {
					connection = dm.getConnection();
					DatabaseAction.dropTables(connection);
				} catch (ApplicationException e) {
					e.printStackTrace();
				} finally {
					try {
						if (connection != null)
							connection.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}
		};
		dropTable.setToolTipText("drop 数据库");

		fileSelectionAction = new Action() {
			public void run() {
				ResourceSelectionDialog dialog = new ResourceSelectionDialog(getSite().getShell(), AuroraPlugin.getWorkspace().getRoot(), "选择需要统计的文件：");

				dialog.setHelpAvailable(false);
				dialog.setTitle("文件选择");
				int open = dialog.open();

				if (open == Dialog.OK) {
					Object[] selected = dialog.getResult();
					StatisticianRunner runner = new StatisticianRunner(StatisticsView.this);
					runner.noProjectRun(selected);
				}
				// // ResourceListSelectionDialog dialog = new
				// // ResourceListSelectionDialog(
				// // getSite().getShell(), AuroraPlugin.getWorkspace()
				// // .getRoot(),IResource.PROJECT);//酷用
				// //
				// // dialog.open();
				// IProject project = AuroraPlugin.getWorkspace().getRoot()
				// .getProject("hr_aurora");
				// // ElementTreeSelectionDialog dialog = new
				// // ElementTreeSelectionDialog(
				// // getSite().getShell(), new WorkbenchLabelProvider(),
				// // new WorkbenchContentProvider());
				// // dialog.setInput(project);
				// //
				// // // dialog.addFilter(filter)
				// // dialog.open();//改projectpage 用
				//
				// // WorkbenchContentProvider
				// // WorkbenchLabelProvider

			}
		};
		fileSelectionAction.setText("文件选择");
		fileSelectionAction.setToolTipText("选择需要统计的文件");
		fileSelectionAction.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));

		projectSelectionAction = new Action() {
			public void run() {
				ResourceListSelectionDialog dialog = new ResourceListSelectionDialog(getSite().getShell(), AuroraPlugin.getWorkspace().getRoot(), IResource.PROJECT);
				dialog.setHelpAvailable(false);
				dialog.setTitle("工程选择");
				int open = dialog.open();
				if (open == Dialog.OK) {
					Object[] selected = dialog.getResult();
					StatisticianRunner runner = new StatisticianRunner(StatisticsView.this);
					runner.projectRun(selected);
				}

				// 酷用
				// //
				// // dialog.open();
				// IProject project = AuroraPlugin.getWorkspace().getRoot()
				// .getProject("hr_aurora");
				// // ElementTreeSelectionDialog dialog = new
				// // ElementTreeSelectionDialog(
				// // getSite().getShell(), new WorkbenchLabelProvider(),
				// // new WorkbenchContentProvider());
				// // dialog.setInput(project);
				// //
				// // // dialog.addFilter(filter)
				// // dialog.open();//改projectpage 用
				//
				// // WorkbenchContentProvider
				// // WorkbenchLabelProvider
			}
		};
		projectSelectionAction.setText("工程选择");
		projectSelectionAction.setToolTipText("选择需要统计的工程");
		projectSelectionAction.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));

		doubleClickAction = new Action() {
			public void run() {
				// ISelection selection = objectViewer.getSelection();
				// Object obj = ((IStructuredSelection) selection)
				// .getFirstElement();
				// showMessage("Double-click detected on " + obj.toString());
				// IWorkbenchWindow workbenchWindow =
				// AuroraPlugin.getActivePage()
				// .getWorkbenchWindow();
				// try {
				// IWorkbenchPage showPerspective = workbenchWindow
				// .getWorkbench().showPerspective(
				// "org.eclipse.jdt.ui.JavaPerspective",
				// workbenchWindow);
				//
				// } catch (WorkbenchException e) {
				// // TODO Auto-generated catch block
				// e.printStackTrace();
				// }
				IExtensionRegistry registry = Platform.getExtensionRegistry();
				IConfigurationElement[] configEle = registry.getConfigurationElementsFor("org.eclipse.ui.perspectives");
				IConfigurationElement ce = null;
				for (IConfigurationElement c : configEle) {
					if ("org.eclipse.jdt.ui.JavaPerspective".equals(c.getAttribute("id"))) {
						// c
						ce = c;
						break;
					}
				}
				if (ce != null) {
					BasicNewProjectResourceWizard.updatePerspective(ce);
					System.out.println(ce);
				}
			}
		};
	}

	private void hookDoubleClickAction() {
		objectViewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				doubleClickAction.run();
			}
		});
	}

	private void showMessage(String message) {
		MessageDialog.openInformation(objectViewer.getControl().getShell(), "Sample View", message);
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		objectViewer.getControl().setFocus();
	}
}
