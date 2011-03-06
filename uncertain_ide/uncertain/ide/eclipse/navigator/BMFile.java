package uncertain.ide.eclipse.navigator;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.internal.resources.File;
import org.eclipse.core.internal.resources.Workspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;

public class BMFile extends File{
	private IPath parentBMPath;
	private IPath bmPath;
	private List subBMFiles = new LinkedList();
	public BMFile(IPath parentBMPath,IPath bmPath){
		super(bmPath,(Workspace)ResourcesPlugin.getWorkspace());
		this.parentBMPath = parentBMPath;
		this.bmPath = bmPath;
	}
	public IPath getParentBMPath() {
		return parentBMPath;
	}
	public void setParentBMPath(IPath parentBMPath) {
		this.parentBMPath = parentBMPath;
	}
	public IPath getPath() {
		return bmPath;
	}
	public void setPath(IPath bmPath) {
		this.bmPath = bmPath;
	}
	public List getSubBMFiles() {
		return subBMFiles;
	}
	public void setSubBMFiles(List subBMFiles) {
		this.subBMFiles = subBMFiles;
	}
	public void addSubBMFile(BMFile subBMFile){
		subBMFiles.add(subBMFile);
	}
}
