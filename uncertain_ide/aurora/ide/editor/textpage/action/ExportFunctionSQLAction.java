package aurora.ide.editor.textpage.action;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;

import uncertain.composite.CompositeMap;
import aurora.ide.editor.textpage.TextPage;
import aurora.ide.helpers.ApplicationException;
import aurora.ide.helpers.DialogUtil;
import aurora.ide.search.cache.CacheManager;
import aurora.ide.search.core.AbstractSearchService;
import aurora.ide.search.reference.MapFinderResult;
import aurora.ide.search.reference.MultiReferenceTypeFinder;

public class ExportFunctionSQLAction extends Action implements
		IEditorActionDelegate {

	private String functionCode;
	private String functionName;
	private String functionOrder;

	private String modulesCode;
	private String modulesName;

	private String hostPage;

	private List<IFile> pages;
	private List<IFile> models;
	private IEditorPart activeEditor;

	// public class ITerationHandle implements IterationHandle {
	//
	// public int process(CompositeMap map) {
	//
	// return IterationHandle.IT_CONTINUE;
	// }
	//
	// protected boolean isReferenceType(IType attributeType) {
	// if (attributeType instanceof SimpleType) {
	// return referenceType.equals(((SimpleType) attributeType)
	// .getReferenceTypeQName());
	// }
	// return false;
	// }
	//
	// }

	public String getFunctionCode() {
		return functionCode;
	}

	public void setFunctionCode(String functionCode) {
		this.functionCode = functionCode;
	}

	public String getFunctionName() {
		return functionName;
	}

	public void setFunctionName(String functionName) {
		this.functionName = functionName;
	}

	public String getFunctionOrder() {
		return functionOrder;
	}

	public void setFunctionOrder(String functionOrder) {
		this.functionOrder = functionOrder;
	}

	public String getModulesCode() {
		return modulesCode;
	}

	public void setModulesCode(String modulesCode) {
		this.modulesCode = modulesCode;
	}

	public String getModulesName() {
		return modulesName;
	}

	public void setModulesName(String modulesName) {
		this.modulesName = modulesName;
	}

	public String getHostPage() {
		return hostPage;
	}

	public void setHostPage(String hostPage) {
		this.hostPage = hostPage;
	}

	public void addModel(IFile model) {
		models.add(model);
	}

	public void addPage(IFile page) {
		pages.add(page);
	}

	public ExportFunctionSQLAction() {
		this.setActionDefinitionId("aurora.ide.text.editor.export.function.register.sql");
	}

	public void run() {
		run(null);
	}

	public void run(IAction action) {
		if (activeEditor == null || !(activeEditor instanceof TextPage)) {
			DialogUtil.showErrorMessageBox("找不到功能文件，不能继续");
			return;
		}
		IFile file = (IFile) activeEditor.getAdapter(IFile.class);
		if (file.exists()
				&& getFilesExtensions().contains(
						file.getFileExtension().toLowerCase())) {
			try {
				models = new ArrayList<IFile>();
				pages = new ArrayList<IFile>();
				fetchAll(file);
			} catch (CoreException e) {
				DialogUtil.logErrorException(e);
				e.printStackTrace();
			} catch (ApplicationException e) {
				DialogUtil.logErrorException(e);
				e.printStackTrace();
			}
		} else {
			DialogUtil.showErrorMessageBox("找不到功能文件，不能继续");
		}
	}

	private void fetchAll(IFile host) throws CoreException,
			ApplicationException {
		CompositeMap hostMap = CacheManager.getCompositeMap(host);
		MultiReferenceTypeFinder mrtf = new MultiReferenceTypeFinder(
				AbstractSearchService.bmReference).addReferenceType(
				AbstractSearchService.screenReference).addReferenceType(
				AbstractSearchService.urlReference);
		hostMap.iterate(mrtf, true);
		List<MapFinderResult> results = mrtf.getResult();
		for (MapFinderResult r : results) {
			r.getAttributes();
		}
		this.addPage(host);

	}

	private String getPagePath(IFile host) {
		return null;
	}

	public void selectionChanged(IAction action, ISelection selection) {
	}

	public void setActiveEditor(IAction action, IEditorPart targetEditor) {
		activeEditor = targetEditor;
	}

	private List<String> getFilesExtensions() {
		return Arrays.asList("bm", "screen", "svc");
	}

}
