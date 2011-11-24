package aurora.ide.statistics.viewer;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Image;

import aurora.statistics.map.ObjectStatisticsResult;
import aurora.statistics.map.StatisticsResult;
import aurora.statistics.model.StatisticsProject;

class ProjectNode {
	// 0
	String name;
	// 1
	String value;
	// 2
	String max;
	// 3
	String min;
	// 4
	String average;
	Object parent;
}

class ProjectViewContentProvider implements IStructuredContentProvider,
		ITreeContentProvider {

	public void dispose() {

	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

	}

	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof StatisticsProject) {
			String[] ps = StatisticsProject.PROPERTIES;
			ProjectNode[] nodes = new ProjectNode[ps.length];
			for (int i = 0; i < ps.length; i++) {
				nodes[i] = new ProjectNode();
				// TODO i18n
				nodes[i].name = ps[i];
				nodes[i].value = ((StatisticsProject) parentElement)
						.getProperty(i);
				nodes[i].parent = parentElement;
			}
			return nodes;
		}
		if (parentElement instanceof ObjectStatisticsResult) {
			ObjectStatisticsResult osr = (ObjectStatisticsResult) parentElement;
			ProjectNode fileSize = createProjectNode("file size", osr,
					osr.getMaxFileSize(), osr.getMinFileSize(),
					osr.getTotalFileSize(), osr.getAverageFileSize());
			ProjectNode scriptSize = createProjectNode("script size", osr,
					osr.getMaxScriptSize(), osr.getMinScriptSize(),
					osr.getTotalScriptSize(), osr.getAverageScriptSize());

			ProjectNode tagCount = createProjectNode("tags", osr,
					osr.getMaxTagCount(), osr.getMinTagCount(),
					osr.getTotalTagCount(), osr.getAverageTagCount());
			return new ProjectNode[] { fileSize, scriptSize, tagCount };
		}
		return null;
	}

	private ProjectNode createProjectNode(String nodeName, Object parent,
			int max, int min, int total, int average) {
		ProjectNode node = new ProjectNode();
		node.name = nodeName;
		node.parent = parent;
		node.max = toString(max);
		node.min = toString(min);
		node.value = toString(total);
		node.average = toString(average);
		return node;
	}

	private String toString(int i) {
		return String.valueOf(i);
	}

	public Object getParent(Object element) {
		if (element instanceof ProjectNode)
			return ((ProjectNode) element).parent;
		return null;
	}

	public boolean hasChildren(Object element) {
		return element instanceof StatisticsProject
				|| element instanceof ObjectStatisticsResult;
	}

	public Object[] getElements(Object inputElement) {
		if (inputElement instanceof StatisticsResult) {
			ObjectStatisticsResult bmStatisticsResult = ((StatisticsResult) inputElement)
					.getBMStatisticsResult();
			ObjectStatisticsResult sreenStatisticsResult = ((StatisticsResult) inputElement)
					.getSreenStatisticsResult();
			ObjectStatisticsResult svcStatisticsResult = ((StatisticsResult) inputElement)
					.getSVCStatisticsResult();

			StatisticsProject project = ((StatisticsResult) inputElement)
					.getProject();
			List<Object> result = new ArrayList<Object>();
			if (project != null)
				result.add(project);
			if (bmStatisticsResult != null)
				result.add(bmStatisticsResult);
			if (sreenStatisticsResult != null)
				result.add(sreenStatisticsResult);
			if (svcStatisticsResult != null)
				result.add(svcStatisticsResult);
			return result.toArray(new Object[result.size()]);
		}
		return null;
	}

}

class ProjectViewLabelProvider implements ITableLabelProvider {

	public void addListener(ILabelProviderListener listener) {

	}

	public void dispose() {

	}

	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	public void removeListener(ILabelProviderListener listener) {

	}

	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}

	public String getColumnText(Object element, int columnIndex) {
		if (element instanceof StatisticsProject && columnIndex == 0) {
			return ((StatisticsProject) element).getProjectName();
		}
		if (element instanceof ObjectStatisticsResult && columnIndex == 0) {
			return ((ObjectStatisticsResult) element).getType();
		}
		if (element instanceof ProjectNode) {
			ProjectNode node = (ProjectNode) element;
			switch (columnIndex) {
			case 0:
				return node.name;
			case 1:
				return node.value;
			case 2:
				return node.max;
			case 3:
				return node.min;
			case 4:
				return node.average;
			}
		}
		return null;
	}

}
