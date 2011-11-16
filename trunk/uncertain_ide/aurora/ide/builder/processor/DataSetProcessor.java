package aurora.ide.builder.processor;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;

import uncertain.composite.CompositeMap;
import uncertain.schema.Attribute;
import aurora.ide.builder.AuroraBuilder;
import aurora.ide.builder.SxsdUtil;
import aurora.ide.builder.validator.AbstractValidator;
import aurora.ide.preferencepages.BuildLevelPage;

public class DataSetProcessor extends AbstractProcessor {
	private Set<String> datasetSet = new HashSet<String>();
	private Set<Object[]> dataSetTask = new HashSet<Object[]>();
	private int level;

	@Override
	public void processMap(IFile file, CompositeMap map, IDocument doc) {
		level = BuildLevelPage.getBuildLevel(AuroraBuilder.UNDEFINED_DATASET);
		if (level == 0)
			return;
		processAttribute(file, map, doc);
	}

	@Override
	public void visitAttribute(Attribute a, IFile file, CompositeMap map,
			IDocument doc) {
		if (SxsdUtil.isDataSetReference(a.getAttributeType())) {
			String name = a.getName();
			String value = map.getString(name);
			if (map.getName().equalsIgnoreCase("dataSet")) {
				if (name.equalsIgnoreCase("id")) {
					datasetSet.add(value);
					return;
				}
			}
			dataSetTask.add(new Object[] { name, value, map });
		}
	}

	@Override
	public void processComplete(IFile file, CompositeMap map1, IDocument doc) {
		if (level == 0)
			return;
		for (Object[] objs : dataSetTask) {
			String name = (String) objs[0];
			String value = (String) objs[1];
			if (datasetSet.contains(value))
				continue;
			CompositeMap map = (CompositeMap) objs[2];
			int line = map.getLocationNotNull().getStartLine();
			IRegion region = AbstractValidator.getValueRegion(doc, line - 1,
					name, value);
			IMarker marker = AuroraBuilder.addMarker(file, name + " : " + value
					+ " 未在本页面中定义过", line, region, level,
					AuroraBuilder.UNDEFINED_DATASET);
			if (marker != null) {
				try {
					marker.setAttribute("ATTRIBUTE_NAME", name);
					marker.setAttribute("ATTRIBUTE_VALUE", value);
				} catch (CoreException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
