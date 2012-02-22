package aurora.statistics;

import aurora.statistics.map.StatisticsMap;
import aurora.statistics.model.ProjectObject;

public interface IStatisticsReporter {
	void reportRoot(ProjectObject po, StatisticsMap sm);

	void reportDependency(ProjectObject po, StatisticsMap sm);

	void reportTag(ProjectObject po, StatisticsMap sm);

	void reportScript(ProjectObject po, StatisticsMap sm);

}
