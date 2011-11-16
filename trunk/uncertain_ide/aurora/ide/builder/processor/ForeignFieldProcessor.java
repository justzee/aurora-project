package aurora.ide.builder.processor;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;

import uncertain.composite.CompositeMap;
import uncertain.schema.Attribute;
import uncertain.schema.IType;
import uncertain.schema.SimpleType;
import aurora.ide.bm.BMUtil;
import aurora.ide.builder.AuroraBuilder;
import aurora.ide.builder.validator.AbstractValidator;
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
	public void processMap(IFile file, CompositeMap map, IDocument doc) {
		String ext = "_" + file.getFileExtension().toUpperCase();
		level = BuildLevelPage
				.getBuildLevel(AuroraBuilder.UNDEFINED_FOREIGNFIELD + ext);
		if (level == 0)
			return;
		processAttribute(file, map, doc);
	}

	@Override
	public void visitAttribute(Attribute a, IFile file, CompositeMap map,
			IDocument doc) {
		if (!isForeignFieldReference(a.getAttributeType()))
			return;
		String name = a.getName();
		String value = map.getString(name);
		int line = map.getLocationNotNull().getStartLine();
		IRegion region = AbstractValidator.getValueRegion(doc, line - 1, name,
				value);
		String refModel = (String) Util.getReferenceModelPKG(map.getParent());
		if (refModel == null)
			refModel = (String) Util.getReferenceModelPKG(map.getParent()
					.getParent());
		if (refModel == null) {
			// AuroraBuilder.addMarker(file, a.getName() + " : " + value +
			// " 没有参照BM", line, region,
			// IMarker.SEVERITY_WARNING, AuroraBuilder.UNDEFINED_FOREIGNFIELD);
			return;
		}
		IResource bmfile = null;
		try {
			bmfile = BMUtil.getBMResourceFromClassPath(file.getProject(),
					refModel);
		} catch (ApplicationException e) {
			e.printStackTrace();
		}
		if (bmfile instanceof IFile) {
			// System.out.println("find bm:" +
			// refModel.toString());
			CompositeMap bmmap = null;
			try {
				bmmap = CacheManager.getCompositeMapCacher().getCompositeMap(
						(IFile) bmfile);
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
			// 未在参照bm中找到field
			AuroraBuilder.addMarker(file, a.getName() + " : " + value
					+ " 没有在参照BM : " + refModel + " 中定义", line, region, level,
					AuroraBuilder.UNDEFINED_FOREIGNFIELD);
		} else {
			// 参照BM不存在,此处不作处理
		}
	}

	private boolean isForeignFieldReference(IType attributeType) {
		if (attributeType instanceof SimpleType) {
			return AbstractSearchService.foreignFieldReference
					.equals(((SimpleType) attributeType)
							.getReferenceTypeQName());
		}
		return false;
	}

	@Override
	public void processComplete(IFile file, CompositeMap map, IDocument doc) {

	}

}
