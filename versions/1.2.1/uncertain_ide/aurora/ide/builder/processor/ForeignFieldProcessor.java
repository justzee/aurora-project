package aurora.ide.builder.processor;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;

import uncertain.composite.CompositeMap;
import uncertain.schema.Attribute;
import aurora.ide.builder.AuroraBuilder;
import aurora.ide.builder.BuildContext;
import aurora.ide.builder.BuildMessages;
import aurora.ide.builder.ResourceUtil;
import aurora.ide.builder.SxsdUtil;
import aurora.ide.helpers.ApplicationException;
import aurora.ide.preferencepages.BuildLevelPage;
import aurora.ide.search.cache.CacheManager;
import aurora.ide.search.core.AbstractSearchService;
import aurora.ide.search.core.Util;
import aurora.ide.search.reference.MapFinderResult;
import aurora.ide.search.reference.ReferenceTypeFinder;

public class ForeignFieldProcessor extends AbstractProcessor {
	private int level;

	@Override
	public void processMap(BuildContext bc) {
		String ext = "_" + bc.file.getFileExtension().toUpperCase();
		level = BuildLevelPage
				.getBuildLevel(AuroraBuilder.UNDEFINED_FOREIGNFIELD + ext);
		if (level == 0)
			return;
		processAttribute(bc);
	}

	@Override
	public void visitAttribute(Attribute a, BuildContext bc) {
		if (!SxsdUtil.isForeignFieldReference(a.getAttributeType()))
			return;
		String name = a.getName();
		String value = bc.map.getString(name);
		IRegion region = bc.info.getAttrValueRegion2(name);
		String refModel = (String) Util
				.getReferenceModelPKG(bc.map.getParent());
		if (refModel == null)
			refModel = (String) Util.getReferenceModelPKG(bc.map.getParent()
					.getParent());
		if (refModel == null) {
			// AuroraBuilder.addMarker(file, a.getName() + " : " + value +
			// " 没有参照BM", line, region,
			// IMarker.SEVERITY_WARNING, AuroraBuilder.UNDEFINED_FOREIGNFIELD);
			return;
		}
		IFile bmfile = ResourceUtil.getBMFile(bc.file.getProject(), refModel);
		if (bmfile != null) {
			CompositeMap bmmap = null;
			try {
				bmmap = CacheManager.getCompositeMapCacher().getCompositeMap(
						bmfile);
			} catch (CoreException e) {
				e.printStackTrace();
			} catch (ApplicationException e) {
				e.printStackTrace();
			}
			ReferenceTypeFinder rtf = new ReferenceTypeFinder(
					AbstractSearchService.localFieldReference);
			bmmap.iterate(rtf, true);

			List<MapFinderResult> res = rtf.getResult();
			for (MapFinderResult mfr : res) {
				CompositeMap mfrMap = mfr.getMap();
				if (!mfrMap.getName().equals("field"))
					continue;
				String field = mfrMap.getString("name");
				if (field != null) {
					if (field.equalsIgnoreCase(value)) {
						// 在参照BM总找到了field
						return;
					}
				}
			}
			int line = bc.info.getLineOfRegion(region);
			// 未在参照bm中找到field
			String msg = String.format(
					BuildMessages.get("build.foreignfield.undefined"),
					a.getName(), value, refModel);
			AuroraBuilder.addMarker(bc.file, msg, line + 1, region, level,
					AuroraBuilder.UNDEFINED_FOREIGNFIELD);
		} else {
			// 参照BM不存在,此处不作处理,在BmProcessor中处理
		}
	}

	@Override
	public void processComplete(IFile file, CompositeMap map, IDocument doc) {

	}

}
