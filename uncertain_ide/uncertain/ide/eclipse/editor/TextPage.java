package uncertain.ide.eclipse.editor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

import uncertain.composite.CompositeLoader;
import uncertain.ide.Common;
import uncertain.ide.eclipse.action.IViewer;

public class TextPage extends FormPage implements IViewer {
	protected static final String textPageId = "textPage";
	protected static final String textPageTitle = "Source File";
	private StyledText mInnerText;
	private JavaScriptLineStyler lineStyler = new JavaScriptLineStyler();
	private String originalContent;
	private boolean modify = false;
	public TextPage(String id, String title) {
		super(id, title);
	}

	public TextPage(FormEditor editor, String id, String title) {
		super(editor, id, title);
	}

	public TextPage(FormEditor editor) {
		super(editor, textPageId, textPageTitle);
	}

	protected void createFormContent(IManagedForm managedForm) {
		ScrolledForm form = managedForm.getForm();
		FormToolkit toolkit = managedForm.getToolkit();
		Composite shell = form.getBody();

		FillLayout layout = new FillLayout();
		shell.setLayout(layout);
		//当此页面没有显示前，而其他编辑器对文件内容作了更改，此时可以直接使用originalContent，否则用原始的文件内容。
		if (originalContent == null)
			try {
				initOriginalContent(getFile());
			} catch (IOException e) {
				e.printStackTrace();
			}
		createContent(shell, toolkit);

	}

	private void initOriginalContent(File file) throws IOException {
			FileInputStream fileInputStream = new FileInputStream(file);
			InputStreamReader inputStramReader = new InputStreamReader(
					fileInputStream, "utf-8");
//			InputStreamReader inputStramReader = new InputStreamReader(
//					fileInputStream);
			BufferedReader bufferedReader = new BufferedReader(
					inputStramReader);
			originalContent = "";
			String line = null;
			while ((line = bufferedReader.readLine()) != null) {
				originalContent += line + "\n";
			}
	}

	protected void createContent(Composite shell, FormToolkit toolkit) {

		createStyledText(shell);
		mInnerText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if (!originalContent.equals(mInnerText.getText())) {
					refresh(true);
				}
			}
		});
	}

	private void createStyledText(Composite parent) {
		mInnerText = new StyledText(parent, SWT.MULTI | SWT.V_SCROLL
				| SWT.H_SCROLL);
		mInnerText.addLineStyleListener(lineStyler);
		Color bg = Display.getDefault().getSystemColor(SWT.COLOR_WHITE);
		mInnerText.setBackground(bg);
		Display display = mInnerText.getDisplay();
		display.asyncExec(new Runnable() {
			public void run() {
				mInnerText.setText(originalContent);
			}
		});

		// parse the block comments up front since block comments can go across
		// lines - inefficient way of doing this
		// mInnerText.setText(originalContent);
		lineStyler.parseBlockComments(originalContent);
	}

	protected File getFile() {
		IFile ifile = ((IFileEditorInput) getEditor().getEditorInput())
				.getFile();
		String fileName = Common.getIfileLocalPath(ifile);
		return new File(fileName);
	}

	public void doSave(IProgressMonitor monitor) {
		try {
			if (mInnerText != null) {
				File file = getFile();
				PrintStream ps = new PrintStream( new FileOutputStream(file));
				ps.print(mInnerText.getText());
				ps.close();
			}
			// setDirty(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	//本页面改动时调用
	public void setDirty(boolean dirty) {
		getEditor().editorDirtyStateChanged();
	}

	public void refresh(boolean dirty) {
//		setModify(true);
		setDirty(dirty);

	}

	public void refresh(String newContent) {
		originalContent = newContent;
		if (mInnerText != null) {
			mInnerText.setText(originalContent);
		}
	}

	public boolean isModify() {
		return modify;
	}

	public void setModify(boolean modify) {
		this.modify = modify;
	}

	public String getOriginalContent() {
		return mInnerText.getText();
	}

	public void setOriginalContent(String originalContent) {
		this.originalContent = originalContent;
	}
	public boolean canLeaveThePage() {
		if(!checkContentFormat()){
			return false;
		}
		if(!originalContent.equals(mInnerText.getText())){
			setModify(true);
			setDirty(true);
		}
		return true;
	}
	private boolean checkContentFormat(){
		CompositeLoader loader = new CompositeLoader();
		try {
			loader.loadFromString(getOriginalContent(),"utf-8");
		} catch (Exception e) {
			Shell shell = new Shell();
			MessageBox messageBox = new MessageBox(shell, SWT.ICON_WARNING
					| SWT.OK);
			messageBox.setText("Error");
			messageBox.setMessage(e.getLocalizedMessage());
			messageBox.open();
			return false;
		}
		return true;
	}
}
