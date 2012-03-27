package aurora.ide.builder.processor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;

import uncertain.composite.CompositeMap;
import uncertain.composite.IterationHandle;
import uncertain.ocm.OCManager;
import uncertain.schema.Attribute;
import aurora.bm.BusinessModel;
import aurora.ide.api.composite.map.CommentXMLOutputter;
import aurora.ide.bm.ExtendModelFactory;
import aurora.ide.builder.AuroraBuilder;
import aurora.ide.builder.BuildContext;
import aurora.ide.builder.BuildMessages;
import aurora.ide.builder.CompositeMapInfo;
import aurora.ide.builder.SxsdUtil;
import aurora.ide.helpers.AuroraResourceUtil;
import aurora.ide.helpers.CompositeMapUtil;
import aurora.ide.preferencepages.BuildLevelPage;
import aurora.ide.search.cache.CacheManager;

public class LocalFieldProcessor extends AbstractProcessor {
	private int level;

	class LocalFieldCollect implements IterationHandle {

		private Set<String> set = new HashSet<String>();
		private CompositeMap map;

		public LocalFieldCollect(IFile file) {
			CompositeMap bm = null;
			try {
				bm = CacheManager.getCompositeMap(file);
				if (bm.get("extend") == null) {
					map = bm;
				} else {
					bm = AuroraResourceUtil.loadFromResource(file);
					BusinessModel r = createResult(bm, file);
					// String str = XMLOutputter.defaultInstance().toXML(
					// r.getObjectContext(), true);
					String str = CommentXMLOutputter.defaultInstance().toXML(
							r.getObjectContext(), true);
					map = CompositeMapUtil.loaderFromString(str);
				}

			} catch (Exception e) {
				AuroraBuilder.addMarker(file, e.getMessage(), 1,
						IMarker.SEVERITY_ERROR, AuroraBuilder.FATAL_ERROR);
			}
		}

		public Set<String> collect() {
			if (map != null)
				map.iterate(this, true);
			return set;
		}

		private BusinessModel createResult(CompositeMap config, IFile file) {
			ExtendModelFactory factory = new ExtendModelFactory(
					OCManager.getInstance(), file);
			return factory.getModel(config);
		}

		public int process(CompositeMap map) {
			try {
				List<Attribute> list = SxsdUtil.getAttributesNotNull(map);
				if (list == null)
					return 0;
				for (Attribute a : list) {
					if (map.get(a.getName()) != null) {
						if (SxsdUtil
								.isLocalFieldReference(a.getAttributeType())) {
							String name = a.getName();
							String value = map.getString(name);
							if (map.getName().equalsIgnoreCase("field")
									|| map.getName().equalsIgnoreCase(
											"ref-field")) {
								if (name.equalsIgnoreCase("name")) {
									set.add(value.toLowerCase());
									continue;
								}
							}
						}
					}
				}
			} catch (Exception e) {
			}
			return 0;
		}

	}

	private Set<Object[]> fieldTask = new HashSet<Object[]>();

	@Override
	public void processComplete(IFile file, CompositeMap map1, IDocument doc) {
		if (level == 0)
			return;
		Set<String> fieldSet = new LocalFieldCollect(file).collect();
		if (fieldSet.size() == 0)
			return;
		for (Object[] objs : fieldTask) {
			String name = (String) objs[0];
			String value = (String) objs[1];
			if (fieldSet.contains(value.toLowerCase()))
				continue;
			CompositeMap map = (CompositeMap) objs[2];
			CompositeMapInfo info = new CompositeMapInfo(map, doc);
			IRegion region = info.getAttrValueRegion2(name);
			int line = info.getLineOfRegion(region);
			String msg = String.format(
					BuildMessages.get("build.localfield.undefined"), name,
					value);
			AuroraBuilder.addMarker(file, msg, line + 1, region, level,
					AuroraBuilder.UNDEFINED_LOCALFIELD);
		}
	}

	@Override
	public void processMap(BuildContext bc) {
		String ext = "_" + bc.file.getFileExtension().toUpperCase();
		level = BuildLevelPage.getBuildLevel(AuroraBuilder.UNDEFINED_LOCALFIELD
				+ ext);
		if (level == 0)
			return;
		processAttribute(bc);
	}

	@Override
	public void visitAttribute(Attribute a, BuildContext bc) {
		if (SxsdUtil.isLocalFieldReference(a.getAttributeType())) {
			String name = a.getName();
			String value = bc.map.getString(name);
			if (bc.map.getName().equalsIgnoreCase("field")
					|| bc.map.getName().equalsIgnoreCase("ref-field")) {
				if (name.equalsIgnoreCase("name")) {
					// fieldSet.add(value.toLowerCase());
					return;
				}
			}
			fieldTask.add(new Object[] { name, value, bc.map });
		}
	}

}
