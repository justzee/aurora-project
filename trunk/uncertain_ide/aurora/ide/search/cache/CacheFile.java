package aurora.ide.search.cache;

import org.eclipse.core.resources.IFile;

import uncertain.composite.CompositeMap;

public class CacheFile {
	private IFile file;
	private long modificationStamp;
	private CompositeMap compositeMap;

	public IFile getFile() {
		return file;
	}

	public long getModificationStamp() {
		return modificationStamp;
	}

	public CompositeMap getCompositeMap() {
		return compositeMap;
	}

	public CacheFile(IFile file, CompositeMap compositeMap) {
		super();
		this.file = file;
		this.modificationStamp = file.getModificationStamp();
		this.compositeMap = compositeMap;
	}

	public boolean checkModification() {
		return file.getModificationStamp() != this.modificationStamp;
	}

}
