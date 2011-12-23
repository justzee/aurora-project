package aurora.ide.dialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import aurora.ide.AuroraPlugin;
import aurora.ide.helpers.DialogUtil;

public class AddSxsdDialog extends Dialog {
	private Text txtNamespace;
	private Text txtTag;
	private String namespace;
	private String[] tags;
	private Map<String, List<String>> map;
	private Map<String, List<String>> customMap;
	private IPreferenceStore store = AuroraPlugin.getDefault().getPreferenceStore();

	public AddSxsdDialog(Shell parentShell, Map<String, List<String>> map) {
		super(parentShell);
		this.map = map;
	}

	@Override
	protected int getShellStyle() {
		return super.getShellStyle() | SWT.RESIZE | SWT.MAX | SWT.HELP;
	}

	@Override
	protected Point getInitialSize() {
		return new Point(500, 300);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		container.getShell().setText("Add Tags");
		GridLayout layou = new GridLayout();
		layou.numColumns = 2;
		container.setLayout(layou);
		Label lblNamespace = new Label(container, SWT.NONE);
		lblNamespace.setText("Namespace:");

		GridData gdNamespace = new GridData(GridData.FILL_HORIZONTAL);
		txtNamespace = new Text(container, SWT.BORDER);
		txtNamespace.setLayoutData(gdNamespace);

		GridData gdlblTag = new GridData();
		gdlblTag.verticalAlignment = SWT.TOP;
		Label lblTag = new Label(container, SWT.NONE);
		lblTag.setLayoutData(gdlblTag);
		lblTag.setText("Tag:");

		GridData gdTag = new GridData(GridData.FILL_BOTH);
		txtTag = new Text(container, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL);
		txtTag.setLayoutData(gdTag);

		return container;

	}

	@Override
	protected Control createButtonBar(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 0;
		layout.makeColumnsEqualWidth = true;
		composite.setLayout(layout);
		GridData data = new GridData(GridData.HORIZONTAL_ALIGN_END | GridData.VERTICAL_ALIGN_CENTER);
		composite.setLayoutData(data);
		composite.setFont(parent.getFont());
		// Add the buttons to the button bar.
		createButtonsForButtonBar(composite);
		return composite;
	}

	@Override
	protected void okPressed() {
		namespace = txtNamespace.getText().trim();
		tags = txtTag.getText().trim().split("\r\n");
		if ("".equals(namespace.trim())) {
			DialogUtil.showWarningMessageBox("请输入命名空间。");
			return;
		} else if ("".equals(txtTag.getText().trim())) {
			DialogUtil.showWarningMessageBox("请输入标签，以换行符分割。");
			return;
		} else {
			String pix = "";
			for (String s : tags) {
				if (s.indexOf(":") == -1) {
					DialogUtil.showWarningMessageBox("存在没有前缀的标签，请检查输入或者添加到\"No namespace\"。");
					return;
				} else {
					if (pix.equals("")) {
						pix = s.substring(0, s.indexOf(":"));
					} else if (!pix.equals(s.substring(0, s.indexOf(":")))) {
						DialogUtil.showWarningMessageBox("前缀不一致，请检查输入。");
						return;
					}
				}
			}
		}
		if (map.containsKey(namespace)) {
			if (DialogUtil.showConfirmDialogBox("命名空间已存在，是否在现有的命名空间里添加新标签？") == SWT.OK) {
				fillMap();
				super.okPressed();
			}
		} else {
			fillMap();
			super.okPressed();
		}
	}

	private void fillMap() {
		StringBuffer sb = new StringBuffer();
		customMap = new TreeMap<String, List<String>>();
		customMap.put(namespace, new ArrayList<String>());
		sb.append("*" + namespace);
		for (String s : tags) {
			if (!"".equals(s)) {
				customMap.get(namespace).add(s);
				sb.append("!" + s);
			}
		}
		sb.append("!");
		sb.append(store.getString("statistician.custom"));
		store.setValue("statistician.custom", sb.toString());
	}

	public Map<String, List<String>> getCustomMap() {
		return customMap;
	}
}
