package aurora.statistics;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import uncertain.schema.ISchemaManager;
import aurora.statistics.map.ProjectObjectIteator;
import aurora.statistics.map.StatisticsMap;
import aurora.statistics.map.StatisticsResult;
import aurora.statistics.model.Dependency;
import aurora.statistics.model.ProjectObject;
import aurora.statistics.model.StatisticsProject;
import aurora.statistics.model.Tag;

public class Statistician implements IStatisticsReporter {

	private StatisticsProject project;
	private StatisticsResult result = new StatisticsResult();
	private List<ProjectObject> poList = new LinkedList<ProjectObject>();
	private ISchemaManager schemaManager;
	private boolean isDependecyContainJS;
	private List<IRunningListener> runnningListeners;

	public void setDependecyContainJS(boolean isDependecyContainJS) {
		this.isDependecyContainJS = isDependecyContainJS;
	}

	public Statistician(StatisticsProject project, ISchemaManager schemaManager) {
		this.project = project;
		result.setProject(project);
		this.schemaManager = schemaManager;
	}

	public void addProjectObject(ProjectObject po) {
		this.poList.add(po);
		result.addProjectObject(po);
		po.setProject(project);
	}

	public ISchemaManager getSchemaManager() {
		return schemaManager;
	}

	public void setSchemaManager(ISchemaManager schemaManager) {
		this.schemaManager = schemaManager;
	}

	public StatisticsProject getProject() {
		return project;
	}

	public void setProject(StatisticsProject project) {
		this.project = project;
	}

	public StatisticsResult getResult() {
		return result;
	}

	public void setResult(StatisticsResult result) {
		this.result = result;
	}

	public List<IRunningListener> getRunningListeners() {
		return runnningListeners;
	}

	public void addRuningListener(IRunningListener l) {
		if (runnningListeners == null) {
			runnningListeners = new ArrayList<IRunningListener>();
		}
		this.runnningListeners.add(l);
	}

	private boolean noticeRunning(ProjectObject po, int poIndex) {
		if (runnningListeners != null) {
			for (IRunningListener l : runnningListeners) {
				if (false == l.notice(po, poIndex)) {
					return false;
				}
			}
		}
		return true;
	}

	public List<ProjectObject> getPoList() {
		return poList;
	}

	public void setPoList(List<ProjectObject> poList) {
		this.poList = poList;
	}

	public StatisticsResult doStatistic() {
		int i = 0;
		for (ProjectObject po : this.poList) {
			if (false == noticeRunning(po, i)) {
				return result;
			}
			ProjectObjectIteator it = new ProjectObjectIteator(this, po);
			it.process(this);
			i++;
		}
		return result;
	}

	public Status save(Connection connection) {
		DatabaseAction action = new DatabaseAction(this);
		return action.saveAll(connection);
	}

	public StatisticsResult read(Connection connection) throws SQLException {
		DatabaseAction action = new DatabaseAction(this);
		return action.readAll(connection);
	}

	public void reportRoot(ProjectObject po, StatisticsMap sm) {
		po.setFileSize(sm.getSize());
	}

	public void reportDependency(ProjectObject po, StatisticsMap sm) {
		ProjectObject findDependencyObject = findDependencyObject(po, sm);
		if (findDependencyObject == null) {
			return;
		}
		Dependency dd = new Dependency();
		dd.setProject(project);
		dd.setObject(po);
		dd.setDependencyObject(findDependencyObject);
		po.addDependency(dd);
	}

	private ProjectObject findDependencyObject(ProjectObject po,
			StatisticsMap sm) {
		// TODO

		// po.getPath();
		// sm.getMap().getattribute.getpath
		//
		// screenpath relative web
		// bm relative classes
		// found path

		// private String isMatch(IFile file, String subString) {
		// IFile findScreenFile = Util.findScreenFile(file, subString);
		// boolean isScreen = this.getSources().contains(findScreenFile);
		// if (isScreen) {
		// return (String) this.createPattern(null, findScreenFile);
		// }
		// for (IFile f : this.getSources()) {
		// if ("bm".equalsIgnoreCase(f.getFileExtension())) {
		// Object createPattern = this.createPattern(null, f);
		// boolean bmRefMatch = Util.bmRefMatch(createPattern, subString);
		// if (bmRefMatch) {
		// return createPattern.toString();
		// }
		// }
		// }
		// return null;
		// }

		return null;
	}

	// public static IFile findScreenFile(IFile file, Object pkg) {
	// if (pkg instanceof String) {
	// IContainer webInf = findWebInf(file);
	// if (webInf == null)
	// return null;
	// IResource webRoot = webInf.getParent();
	// IContainer parent = file.getParent();
	// IPath parentPath = parent.getFullPath();
	// IPath rootPath = webRoot.getFullPath();
	// IPath path = new Path((String) pkg);
	// IPath requestPath = new Path("${/request/@context_path}");
	// boolean prefixOfRequest = requestPath.isPrefixOf(path);
	// if (prefixOfRequest) {
	// path = path.makeRelativeTo(requestPath);
	// }
	// String[] split = path.toString().split("\\?");
	// path = new Path(split[0]);
	// IPath relativePath = parentPath.makeRelativeTo(rootPath);
	// boolean prefixOf = relativePath.isPrefixOf(path);
	// if (prefixOf || prefixOfRequest) {
	// // fullpath
	// IPath sourceFilePath = rootPath.append(path);
	// IFile sourceFile = file.getProject().getParent()
	// .getFile(sourceFilePath);
	// if (sourceFile.exists())
	// return sourceFile;
	// } else {
	// // relativepath
	// IFile sourceFile = parent.getFile(path);
	// if (sourceFile.exists())
	// return sourceFile;
	// }
	// }
	// return null;
	// }

	// public static boolean bmRefMatch(Object bmPattern, String url) {
	// Path path = new Path(url);
	// String[] segments = path.segments();
	// for (String s : segments) {
	// String[] split = s.split("\\?");
	// s = split[0];
	// // TODO bug?
	// if (s.equals(bmPattern)) {
	// return true;
	// }
	// }
	// return false;
	// }

	public void reportTag(ProjectObject po, StatisticsMap sm) {
		Tag tag = new Tag(sm);
		tag.setProject(project);
		tag.setObject(po);
		po.addTag(tag);
	}

	public void reportScript(ProjectObject po, StatisticsMap sm) {
		int size = sm.getSize();
		po.appendScriptSize(size);
	}

	public boolean isDependecyContainJS() {

		return isDependecyContainJS;
	}
}
