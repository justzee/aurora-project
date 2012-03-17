package aurora.statistics;

import aurora.statistics.model.ProjectObject;

public interface IRunningListener {
	/**
	 * 
	 * 
	 * @return true,分析正常进行。false分析终止。
	 */
	boolean notice(ProjectObject po, int poIndex);
}
