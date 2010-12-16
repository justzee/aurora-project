package uncertain.ide.eclipse.editor.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;

import org.eclipse.core.resources.IFile;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.CloseWindowListener;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.browser.LocationListener;
import org.eclipse.swt.browser.OpenWindowListener;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.browser.StatusTextEvent;
import org.eclipse.swt.browser.StatusTextListener;
import org.eclipse.swt.browser.TitleEvent;
import org.eclipse.swt.browser.TitleListener;
import org.eclipse.swt.browser.VisibilityWindowListener;
import org.eclipse.swt.browser.WindowEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ScrolledForm;

import uncertain.ide.Activator;
import uncertain.ide.eclipse.editor.widgets.CustomDialog;
import uncertain.ide.eclipse.editor.widgets.config.ProjectProperties;
import uncertain.ide.eclipse.preferencepages.BrowserPreferencePage;
import uncertain.ide.util.Common;
import uncertain.ide.util.LocaleMessage;

public class BrowserPage extends FormPage {
	protected static final String textPageId = "uncertain.ide.eclipse.editor.service.browser";
	protected static final String textPageTitle = LocaleMessage.getString("preview");
	static ResourceBundle resourceBundle = ResourceBundle
			.getBundle("uncertain");
	int index;
	boolean busy;
	Image icon = null;
	boolean title = false;
	Composite parent;
	Text locationBar;
	Browser browser;
	ToolBar toolbar;
	Canvas canvas;
	ToolItem itemBack, itemForward;
	Label status;
	ProgressBar progressBar;
	SWTError error = null;


	public BrowserPage(FormEditor editor) {
		super(editor, textPageId, textPageTitle);
	}

	public BrowserPage() {
		super(textPageId, textPageTitle);
	}

	public BrowserPage(Composite parent) {
		super(textPageId, textPageTitle);
		this.parent = parent;
	}

	public void createFormContent(IManagedForm managedForm) {
		ScrolledForm form = managedForm.getForm();
		Composite shell = form.getBody();
		createContent(shell, true);
	}

	protected void createContent(final Composite parent, boolean top) {
		this.parent = parent;
		try {
			browser = new Browser(parent, SWT.BORDER);
		} catch (SWTError e) {
			error = e;
			/* Browser widget could not be instantiated */
			parent.setLayout(new FillLayout());
			Label label = new Label(parent, SWT.CENTER | SWT.WRAP);
			label.setText(LocaleMessage.getString("BrowserNotCreated"));
			parent.layout(true);
			return;
		}
		final Display display = browser.getShell().getDisplay();
		browser.setData(
				textPageId,
				this);
		browser.addOpenWindowListener(new OpenWindowListener() {
			public void open(WindowEvent event) {
				Shell shell = new Shell(display);
				if (icon != null)
					shell.setImage(icon);
				shell.setLayout(new FillLayout());
				BrowserShell app = new BrowserShell(shell, false);
				app.setShellDecoration(icon, true);
				event.browser = app.getBrowser();
			}
		});
		if (top) {
			show(false, null, null, true, true, true, true);
		} else {
			browser.addVisibilityWindowListener(new VisibilityWindowListener() {
				public void hide(WindowEvent e) {
				}

				public void show(WindowEvent e) {
					Browser browser = (Browser) e.widget;
					BrowserPage app = (BrowserPage) browser.getData(textPageId);
					app.show(true, e.location, e.size, e.addressBar, e.menuBar,
							e.statusBar, e.toolBar);
				}
			});
			browser.addCloseWindowListener(new CloseWindowListener() {
				public void close(WindowEvent event) {
					Browser browser = (Browser) event.widget;
					Shell shell = browser.getShell();
					shell.close();
				}
			});
		}
	}

	/**
	 * Disposes of all resources associated with a particular instance of the
	 * BrowserApplication.
	 */
	public void dispose() {
	}

	public SWTError getError() {
		return error;
	}

	public Browser getBrowser() {
		return browser;
	}

	public void setShellDecoration(Image icon, boolean title) {
		this.icon = icon;
		this.title = title;
	}

	void show(boolean owned, Point location, Point size, boolean addressBar,
			boolean menuBar, boolean statusBar, boolean toolBar) {
		final Shell shell = browser.getShell();
		if (owned) {
			if (location != null)
				shell.setLocation(location);
			if (size != null)
				shell.setSize(shell.computeSize(size.x, size.y));
		}
		FormData data = null;
		if (toolBar) {
			toolbar = new ToolBar(parent, SWT.NONE);
			data = new FormData();
			data.top = new FormAttachment(0, 7);
			toolbar.setLayoutData(data);
			itemBack = new ToolItem(toolbar, SWT.PUSH);
			itemBack.setText(LocaleMessage.getString("Back"));
			itemForward = new ToolItem(toolbar, SWT.PUSH);
			itemForward.setText(LocaleMessage.getString("Forward"));
			final ToolItem itemStop = new ToolItem(toolbar, SWT.PUSH);
			itemStop.setText(LocaleMessage.getString("Stop"));
			final ToolItem itemRefresh = new ToolItem(toolbar, SWT.PUSH);
			itemRefresh.setText(LocaleMessage.getString("Refresh"));
			final ToolItem itemGo = new ToolItem(toolbar, SWT.PUSH);
			itemGo.setText(LocaleMessage.getString("Go"));

			createServerButtons(shell);

			itemBack.setEnabled(browser.isBackEnabled());
			itemForward.setEnabled(browser.isForwardEnabled());
			Listener listener = new Listener() {
				public void handleEvent(Event event) {
					ToolItem item = (ToolItem) event.widget;
					if (item == itemBack)
						browser.back();
					else if (item == itemForward)
						browser.forward();
					else if (item == itemStop)
						browser.stop();
					else if (item == itemRefresh)
						browser.refresh();
					else if (item == itemGo)
						browser.setUrl(locationBar.getText());
				}
			};
			itemBack.addListener(SWT.Selection, listener);
			itemForward.addListener(SWT.Selection, listener);
			itemStop.addListener(SWT.Selection, listener);
			itemRefresh.addListener(SWT.Selection, listener);
			itemGo.addListener(SWT.Selection, listener);

			canvas = new Canvas(parent, SWT.NO_BACKGROUND);
			data = new FormData();
			data.width = 24;
			data.height = 24;
			data.top = new FormAttachment(0, 7);
			data.right = new FormAttachment(100, -7);
			canvas.setLayoutData(data);
			canvas.addListener(SWT.MouseDown, new Listener() {
				public void handleEvent(Event e) {
					browser.setUrl(LocaleMessage.getString("Startup"));
				}
			});

			final Display display = parent.getDisplay();
			display.asyncExec(new Runnable() {
				public void run() {
					if (canvas.isDisposed())
						return;
					if (busy) {
						canvas.redraw();
					}
					display.timerExec(150, this);
				}
			});
		}
		if (addressBar) {
			locationBar = new Text(parent, SWT.BORDER);
			data = new FormData();
			if (toolbar != null) {
				data.top = new FormAttachment(toolbar, 0, SWT.TOP);
				data.left = new FormAttachment(toolbar, 5, SWT.RIGHT);
				data.right = new FormAttachment(canvas, -5, SWT.DEFAULT);
			} else {
				data.top = new FormAttachment(0, 0);
				data.left = new FormAttachment(0, 0);
				data.right = new FormAttachment(100, 0);
			}
			locationBar.setLayoutData(data);
			locationBar.addListener(SWT.DefaultSelection, new Listener() {
				public void handleEvent(Event e) {
					browser.setUrl(locationBar.getText());
				}
			});
		}
		if (statusBar) {
			status = new Label(parent, SWT.NONE);
			progressBar = new ProgressBar(parent, SWT.NONE);

			data = new FormData();
			data.left = new FormAttachment(0, 5);
			data.right = new FormAttachment(progressBar, 0, SWT.DEFAULT);
			data.bottom = new FormAttachment(100, -5);
			status.setLayoutData(data);

			data = new FormData();
			data.right = new FormAttachment(100, -5);
			data.bottom = new FormAttachment(100, -5);
			progressBar.setLayoutData(data);

			browser.addStatusTextListener(new StatusTextListener() {
				public void changed(StatusTextEvent event) {
					status.setText(event.text);
				}
			});
		}
		parent.setLayout(new FormLayout());

		Control aboveBrowser = toolBar ? (Control) canvas
				: (addressBar ? (Control) locationBar : null);
		data = new FormData();
		data.left = new FormAttachment(0, 0);
		data.top = aboveBrowser != null ? new FormAttachment(aboveBrowser, 5,
				SWT.DEFAULT) : new FormAttachment(0, 0);
		data.right = new FormAttachment(100, 0);
		data.bottom = status != null ? new FormAttachment(status, -5,
				SWT.DEFAULT) : new FormAttachment(100, 0);
		browser.setLayoutData(data);

		if (statusBar || toolBar) {
			browser.addProgressListener(new ProgressListener() {
				public void changed(ProgressEvent event) {
					if (event.total == 0)
						return;
					int ratio = event.current * 100 / event.total;
					if (progressBar != null){
						progressBar.setSelection(ratio);
						progressBar.setVisible(true);
					}
					busy = event.current != event.total;
					if (!busy) {
						index = 0;
						if (canvas != null)
							canvas.redraw();
					}
				}

				public void completed(ProgressEvent event) {
					if (progressBar != null){
						progressBar.setSelection(0);
						progressBar.setVisible(false);
					}
					busy = false;
					index = 0;
					if (canvas != null) {
						itemBack.setEnabled(browser.isBackEnabled());
						itemForward.setEnabled(browser.isForwardEnabled());
						canvas.redraw();
					}
				}
			});
		}
		if (addressBar || statusBar || toolBar) {
			browser.addLocationListener(new LocationListener() {
				public void changed(LocationEvent event) {
					busy = true;
					if (event.top && locationBar != null)
						locationBar.setText(event.location);
				}

				public void changing(LocationEvent event) {
				}
			});
		}
		if (title) {
			browser.addTitleListener(new TitleListener() {
				public void changed(TitleEvent event) {
					shell.setText(event.title + " - "
							+ LocaleMessage.getString("window.title"));
				}
			});
		}
		parent.layout(true);
		if (owned)
			shell.open();
	}

	private void createServerButtons(final Shell shell) {

		List menus = initServerActionMenu();
		Iterator it = menus.iterator();
		for (; it.hasNext();) {
			Object node = it.next();
			if (node instanceof ServerActionMenu) {
				ServerActionMenu sam = (ServerActionMenu) node;
				final ToolItem drop_down = new ToolItem(toolbar, SWT.DROP_DOWN);
				if (sam.getDisabled()) {
					drop_down.setEnabled(false);
					drop_down.setText(sam.getDisableLable());
					drop_down.setToolTipText(sam.getDisableToolTipText());
					continue;
				}
				final Menu menu = new Menu(shell, SWT.POP_UP);
				drop_down.addListener(SWT.Selection, new Listener() {
					public void handleEvent(Event event) {
						if (event.detail == SWT.ARROW) {
							Rectangle bounds = drop_down.getBounds();
							Point point = toolbar.toDisplay(bounds.x, bounds.y
									+ bounds.height);
							menu.setLocation(point);
							menu.setVisible(true);

						}
					}
				});
				drop_down.setText(sam.getLable());
				List actions = sam.getActions();

				Iterator it_actions = actions.iterator();
				for (; it_actions.hasNext();) {
					Object node_action = it_actions.next();
					if (node_action instanceof ServerAction) {
						final ServerAction sa = (ServerAction) node_action;
						final MenuItem item = new MenuItem(menu, SWT.PUSH);
						item.setText(sa.getLable());

						item.addListener(SWT.Selection, new Listener() {
							public void handleEvent(Event event) {
								MenuItem event_item = (MenuItem) event.widget;
								if (event_item == item) {
									browser.setUrl(sa.getUrl());
								}
							}
						});
					}
				}

			}
		}
	}

	private List initServerActionMenu() {
		List menus = new ArrayList();
		ServerActionMenu remoteMachine = new ServerActionMenu(LocaleMessage
				.getString("remote.server"), LocaleMessage
				.getString("undefined.remote.server.hint"), LocaleMessage
				.getString("go.preference.page"));
		String remoteMain = Activator.getDefault().getPreferenceStore()
				.getString(BrowserPreferencePage.BROWSER_REMOTE);
		if (remoteMain == null || remoteMain.equals("")) {
			remoteMachine.setDisabled(true);
		}
		String space = " ";
		new ServerAction(remoteMachine, LocaleMessage.getString("main.page") + space
				+ space + "(" + remoteMain + ")", remoteMain);
		String remoteUrl = remoteMain + getFileName();
		new ServerAction(remoteMachine, LocaleMessage.getString("this.page") + space
				+ "(" + remoteUrl + ")", remoteUrl);

		ServerActionMenu localMachine = new ServerActionMenu(LocaleMessage
				.getString("local.server"), LocaleMessage
				.getString("undefined.local.server.hint"), LocaleMessage
				.getString("go.preference.page"));
		String localMain = Activator.getDefault().getPreferenceStore()
				.getString(BrowserPreferencePage.BROWSER_LOCAL);
		if (localMain == null || localMain.equals("")) {
			localMachine.setDisabled(true);
		}
		new ServerAction(localMachine, LocaleMessage.getString("main.page") + space
				+ space + "(" + localMain + ")", localMain);
		String localUrl = localMain + getFileName();
		new ServerAction(localMachine, LocaleMessage.getString("this.page") + space
				+ "(" + localUrl + ")", localUrl);

		menus.add(remoteMachine);
		menus.add(localMachine);
		return menus;
	}

	class ServerActionMenu {

		private List actions = new ArrayList();

		public String getLable() {
			return lable;
		}

		public void setLable(String lable) {
			this.lable = lable;
		}

		public String getDisableLable() {
			return disableLable;
		}

		public void setDisableLable(String disableLable) {
			this.disableLable = disableLable;
		}

		public String getDisableToolTipText() {
			return disableToolTipText;
		}

		public void setDisableToolTipText(String disableToolTipText) {
			this.disableToolTipText = disableToolTipText;
		}

		public void add(ServerAction action) {
			actions.add(action);
		}

		public List getActions() {
			return actions;
		}

		/**
		 * @param lable
		 * @param disableLable
		 * @param disableToolTipText
		 */
		public ServerActionMenu(String lable, String disableLable,
				String disableToolTipText) {
			super();
			this.lable = lable;
			this.disableLable = disableLable;
			this.disableToolTipText = disableToolTipText;
		}

		private String lable;
		private String disableLable;
		private String disableToolTipText;
		private boolean disabled;

		public boolean getDisabled() {
			return disabled;
		}

		public void setDisabled(boolean disabled) {
			this.disabled = disabled;
		}

	}

	class ServerAction {
		public String getLable() {
			return lable;
		}

		public void setLable(String lable) {
			this.lable = lable;
		}

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		/**
		 * @param menu
		 * @param lable
		 * @param url
		 */
		public ServerAction(ServerActionMenu menu, String lable, String url) {
			super();
			this.lable = lable;
			this.url = url;
			this.menu = menu;
			menu.add(this);
		}

		private String lable;
		private String url;
		private ServerActionMenu menu;

		public ServerActionMenu getMenu() {
			return menu;
		}

		public void setMenu(ServerActionMenu menu) {
			this.menu = menu;
		}
	}

	/**
	 * Grabs input focus
	 */
	public void focus() {
		if (locationBar != null)
			locationBar.setFocus();
		else if (browser != null)
			browser.setFocus();
		else
			parent.setFocus();
	}


	protected String getFileName() {
		IFile ifile = ((IFileEditorInput) getEditor().getEditorInput())
				.getFile();
		String fileName = Common.getIfileLocalPath(ifile);
		String rootDir = null;
		try {
			rootDir = ProjectProperties.getWebBaseDir();
		} catch (Exception e) {
			CustomDialog.showExceptionMessageBox(e);
		}
		int webLocation = fileName.indexOf(rootDir);
		String registerPath = fileName.substring(webLocation+rootDir.length());
		return registerPath;
	}

	public static void main(String[] args) {
		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setLayout(new FillLayout());
		shell.setText(LocaleMessage.getString("window.title"));
		BrowserPage app = new BrowserPage();
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		app.dispose();
		display.dispose();
	}
}
