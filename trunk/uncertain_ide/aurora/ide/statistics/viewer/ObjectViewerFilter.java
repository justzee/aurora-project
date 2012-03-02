package aurora.ide.statistics.viewer;

import java.util.regex.Pattern;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

public class ObjectViewerFilter extends ViewerFilter {

	private String fileName;

	public ObjectViewerFilter(String fileName) {
		fileName = fileName.replaceAll("\\\\", "\\\\\\\\");
		fileName = fileName.replaceAll("\\.", "\\\\.");
		fileName = fileName.replaceAll("\\?", ".");
		fileName = fileName.replaceAll("\\*", ".*");
		fileName += ".*";
		this.fileName = fileName;
	}

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if (element instanceof ObjectNode) {
			ObjectNode o = (ObjectNode) element;
			if (fileName.length() == 0) {
				return true;
			} else if (Pattern.matches("^" + fileName, o.fileName)) {
				return true;
			} else {
				return false;
			}
		}
		return true;
	}

}
