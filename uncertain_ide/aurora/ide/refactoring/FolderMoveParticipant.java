package aurora.ide.refactoring;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext;
import org.eclipse.ltk.core.refactoring.participants.MoveParticipant;
import org.eclipse.text.edits.ReplaceEdit;

import aurora.ide.search.cache.CacheManager;
import aurora.ide.search.core.AbstractMatch;
import aurora.ide.search.core.Util;
import aurora.ide.search.reference.MultiSourceReferenceSearchService;
import aurora.ide.search.reference.ReferenceSearchService;

public class FolderMoveParticipant extends MoveParticipant {

	private IFolder currentFolder;
	private ScopeVisitor visitor;

	private Map<String, String> pkgMap;
	private TextFileChangeManager changeManager;

	private IFolder moveTO;
	private boolean check;

	public FolderMoveParticipant() {
	}

	protected boolean initialize(Object element) {

		Object destination = this.getArguments().getDestination();
		// ifnature in
		if (element instanceof IFolder && destination instanceof IFolder) {
			currentFolder = (IFolder) element;
			visitor = new ScopeVisitor();
			changeManager = new TextFileChangeManager();
			moveTO = (IFolder) destination;
			if (currentFolder.getParent().equals(moveTO)
					|| !currentFolder.getProject().equals(moveTO.getProject())) {
				// 目标目录相同，不参与
				// 工程不同不参与
				return false;
			}
			return isTakeIn(currentFolder);
		}
		return false;
	}

	private boolean isTakeIn(IFolder currentFolder) {
		try {
			currentFolder.accept(visitor);
			return visitor.isTakeIn();
		} catch (CoreException e) {
		}
		return false;
	}

	public String getName() {
		return "Folder Rename Participant";
	}

	public RefactoringStatus checkConditions(IProgressMonitor pm,
			CheckConditionsContext context) throws OperationCanceledException {
		this.check = true;
		RefactoringStatus result = new RefactoringStatus();
		Object destination = this.getArguments().getDestination();
		if (destination instanceof IResource) {
			IPath path = ((IResource) destination).getProjectRelativePath();
			String pkg = Util.toRelativeClassesPKG(path);
			if (pkg.length() == 0) {
				this.check = false;
				result.merge(RefactoringStatus
						.createInfoStatus("目标目录不属于classes,Aurora重构不会进行,请Cancel后重新选择。"));
			}
		}
		return result;
	}

	public Change createChange(IProgressMonitor pm) throws CoreException,
			OperationCanceledException {
		// bug:select->preview->back->select another->preview
		// getArguments().getDestination() did nont changed
		if (check) {
			createPKGMap();
			return createBMChange(pm);
		}
		return null;
	}

	private void createPKGMap() {
		pkgMap = new HashMap<String, String>();
		List<IFile> result = this.visitor.getResult();
		for (IFile file : result) {
			String oldPkg = Util.toBMPKG(file);
			IPath folderPath = this.currentFolder.getParent()
					.getProjectRelativePath();
			IPath filePath = file.getProjectRelativePath();
			IPath raelativeTo = filePath.makeRelativeTo(folderPath)
					.removeFileExtension();
			IPath toPath = this.moveTO.getProjectRelativePath();
			String newPkg = Util.toPKG(toPath)+"."
					+ Util.toPKG(raelativeTo);
			pkgMap.put(oldPkg, newPkg);
		}
	}

	private class ScopeVisitor implements IResourceVisitor {
		private List<IFile> result = new ArrayList<IFile>();

		private boolean isTakeIn = true;

		public boolean visit(IResource resource) throws CoreException {
			if (resource.getType() == IResource.FILE) {
				boolean checkExtension = checkExtension(resource);
				if (checkExtension) {
					result.add((IFile) resource);
				}
				return false;
			}
			if (resource.getType() == IResource.FOLDER) {
				IFolder f = (IFolder) resource;
				//
				// TODO bm folder in properties page
				if ("classes".equals(f.getName())) {
					isTakeIn = false;
				}
			}
			return true;
		}

		public boolean isTakeIn() {
			return isTakeIn && result.size() > 0;
		}

		public List<IFile> getResult() {
			return result;
		}

		private boolean checkExtension(IResource resource) {
			IFile file = (IFile) resource;
			String fileExtension = file.getFileExtension();
			return "bm".equalsIgnoreCase(fileExtension);
		}
	}

	private Change createBMChange(IProgressMonitor pm) throws CoreException {
		List<IFile> result = visitor.getResult();
		IResource scope = Util.getScope(currentFolder);
		ReferenceSearchService seachService = new MultiSourceReferenceSearchService(
				scope, result.toArray(new IFile[result.size()]), null);
		seachService.setPostException(false);
		List relations = seachService.service(pm);
		CompositeChange changes = new CompositeChange("aurora changes");
		changes.markAsSynthetic();
		for (int i = 0; i < relations.size(); i++) {
			AbstractMatch object = (AbstractMatch) relations.get(i);
			IFile file = (IFile) object.getElement();
			IDocument document = getDocument(file);
			TextFileChange textFileChange = changeManager
					.getTextFileChange(file);

			int offset = object.getOriginalOffset();
			int length = object.getOriginalLength();
			try {
				String string = document.get(offset, length);
				ReplaceEdit edit = new ReplaceEdit(offset, length,
						this.pkgMap.get(string));
				textFileChange.addEdit(edit);
			} catch (BadLocationException e) {
				continue;
			}
		}
		changes.addAll(changeManager.getAllChanges());
		return changes;
	}

	public IDocument getDocument(IFile file) throws CoreException {
		return CacheManager.getDocumentCacher().getDocument(file);
	}
}
